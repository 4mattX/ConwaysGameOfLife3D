package renderer.rendering.shaders;

import renderer.rendering.CellPoint;

public class LightVector {

    public double x;
    public double y;
    public double z;

    public LightVector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public LightVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LightVector(CellPoint p1, CellPoint p2) {
        this.x = p2.x - p1.x;
        this.y = p2.y - p1.y;
        this.z = p2.z - p1.z;
    }

    public static double dotProduct(LightVector v1, LightVector v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    // finds perpendicular vector from the plane of which two other vectors lie
    public static LightVector crossProduct(LightVector v1, LightVector v2) {
        return new LightVector(
                (v1.y * v2.z - v1.z * v2.y),
                (v1.z * v2.x - v1.x * v2.z),
                (v1.x * v2.y - v1.y * v2.x)
        );
    }

    public static LightVector normalize(LightVector vector) {
        double magnitude = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
        return new LightVector(vector.x / magnitude, vector.y / magnitude, vector.z / magnitude);
    }




}
