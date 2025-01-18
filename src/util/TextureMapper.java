package src.util;

public class TextureMapper {
    public static double[] calculateQuadTextureCoordinates(int x, int y, Point3D[] points, double[][] uvCoords, int width, int height) {
        // Convert screen coordinates to normalized device coordinates (-1 to 1)
        double normalizedX = (2.0 * x / width) - 1.0;
        double normalizedY = (2.0 * y / height) - 1.0;

        // Inverse bilinear interpolation
        double[] bilinearCoor = solveInverseBilinear(
            normalizedX, normalizedY,
            points[0].x / (double)width * 2 - 1, points[0].y / (double)height * 2 - 1,
            points[1].x / (double)width * 2 - 1, points[1].y / (double)height * 2 - 1,
            points[2].x / (double)width * 2 - 1, points[2].y / (double)height * 2 - 1,
            points[3].x / (double)width * 2 - 1, points[3].y / (double)height * 2 - 1
        );

        if(bilinearCoor == null){
            return null;
        }

        double u = bilinearCoor[0];
        double v = bilinearCoor[1];

        double uz = bilinearInterpolation(uvCoords, 0, u, v);
        double uv = bilinearInterpolation(uvCoords, 1, u, v);
        double inverse_z = bilinearInterpolation(uvCoords, 2, u, v);

        if(Math.abs(inverse_z) < 1e-6){
            return null;
        }
        double perspU = uz / inverse_z;
        double perspV = uv / inverse_z;

        return new double[]{
            Math.max(0, Math.min(1, perspU)),
            Math.max(0, Math.min(1, perspV))
        };
    }

    public static double bilinearInterpolation(double[][] values, int index, double u, double v){
        return (1-u) * (1-v) * values[0][index] + 
            u*(1-v) * values[1][index] + 
            u * v * values[2][index] + 
            (1-u) * v * values[3][index];
    }

    public static double[] solveInverseBilinear(double x, double y, 
                                         double x0, double y0, double x1, double y1,
                                         double x2, double y2, double x3, double y3) {
        // Newton-Raphson method to solve the inverse bilinear interpolation
        double u = 0.5, v = 0.5;  // Initial guess
        double epsilon = 0.0001;
        int maxIterations = 20;

        for (int i = 0; i < maxIterations; i++) {
            // Compute the bilinear interpolation at current (u,v)
            double bx = (1-u)*(1-v)*x0 + u*(1-v)*x1 + u*v*x2 + (1-u)*v*x3;
            double by = (1-u)*(1-v)*y0 + u*(1-v)*y1 + u*v*y2 + (1-u)*v*y3;

            // Compute the error
            double ex = x - bx;
            double ey = y - by;

            if (Math.abs(ex) < epsilon && Math.abs(ey) < epsilon) {
                // Check if result is within valid range
                if (u >= -epsilon && u <= 1+epsilon && v >= -epsilon && v <= 1+epsilon) {
                    return new double[]{
                        Math.max(0, Math.min(1, u)),
                        Math.max(0, Math.min(1, v))
                    };
                }
                break;
            }

            // Compute Jacobian
            double dxdu = (1-v)*(-x0+x1) + v*(-x3+x2);
            double dxdv = (1-u)*(-x0+x3) + u*(-x1+x2);
            double dydu = (1-v)*(-y0+y1) + v*(-y3+y2);
            double dydv = (1-u)*(-y0+y3) + u*(-y1+y2);

            // Compute determinant
            double det = dxdu*dydv - dxdv*dydu;
            if (Math.abs(det) < epsilon) break;

            // Update u and v
            u += (dydv*ex - dxdv*ey) / det;
            v += (-dydu*ex + dxdu*ey) / det;
        }

        return null;
    }


}
