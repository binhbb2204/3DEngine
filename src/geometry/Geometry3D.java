package src.geometry;

import src.util.Point3D;

public class Geometry3D {
    private static final double CAMERA_DISTANCE = 1000;

    public static double[] rotatePoint(double[] point, double posX, double posY, double posZ, double angleX, double angleY){
        double[] translate = new double[3];

        translate[0] = point[0] + posX;
        translate[1] = point[1] + posY;
        translate[2] = point[2] + posZ;

        double[] rotated = new double[3];

        // Rotate around Y axis
        double cosY = Math.cos(angleY);
        double sinY = Math.sin(angleY);
        double tempX = translate[0] * cosY + translate[2] * sinY;
        double tempZ = -translate[0] * sinY + translate[2] * cosY;

        // Rotate around X axis
        double cosX = Math.cos(angleX);
        double sinX = Math.sin(angleX);
        rotated[0] = tempX;
        rotated[1] = translate[1] * cosX - tempZ * sinX;
        rotated[2] = translate[1] * sinX + tempZ * cosX;

        return rotated;
    }

    public static Point3D project3D(double[] point3D, int width, int height, double scale){
        double z = CAMERA_DISTANCE - 400;
        if (z < 1) z = 1;
        double baseScale = Math.min(width, height) * 0.625;
        double projZ = z - point3D[2];
        int x = (int) (point3D[0] * baseScale * scale / projZ + width / 2);
        int y = (int) (point3D[1] * baseScale * scale / projZ + height / 2);
        return new Point3D(x, y, projZ);
    }

    public static boolean isPointInPolygon(int x, int y, Point3D[] points) {
        int i, j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > y) != (points[j].y > y) &&
                (x < (points[j].x - points[i].x) * (y - points[i].y) / 
                    (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;
    }
}
