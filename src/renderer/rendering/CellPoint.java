package renderer.rendering;

public class CellPoint {

    public double x;
    public double y;
    public double z;

    public CellPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public void addZ(double value) {
        this.z += value;
    }
}
