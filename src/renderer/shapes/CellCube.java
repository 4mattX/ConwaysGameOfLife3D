package renderer.shapes;

import renderer.rendering.CellPoint;
import renderer.rendering.shaders.LightVector;

import java.awt.*;

public class CellCube {


    private CellPolygon[] polygons;
    private Color color;
    private boolean alive;
    private int indexOfCellBox;
    private int age;
    private int MAX_AGE;
    private double x;
    private double y;
    private double z;
    private int size;

    public CellCube(double x, double y, double z, int size, boolean alive) {
        this.alive = alive;
        this.color = Color.CYAN;
        createCellCube(x, y, z, size);
    }

    public CellCube(double x, double y, double z, int size, boolean alive, Color color) {

        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setSize(size);


        this.alive = alive;
        this.color = color;
        createCellCube(x, y, z, size);
        this.MAX_AGE = 4;
    }



    public void render(Graphics g) {
//        for (CellPolygon poly : this.polygons) {
//            poly.render(g);
//        }

        for (int i = this.polygons.length - 1; i > 2; i--) {
            this.polygons[i].render(g);
        }

    }

    public void renderFrontOutline(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 5; i < polygons.length; i++) {
            this.polygons[i].renderOutline(g);
        }
    }

    public void renderBackOutline(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < 4; i++) {
            this.polygons[i].renderOutline(g);
        }
    }

    // Sorts the polygons such that faces in front are displayed in front of rear faces
    public void sortPolygons() {
        CellPolygon.sortPolygons(this.polygons);
    }

    private void createCellCube(double x, double y, double z, int size) {

        CellPoint p1 = new CellPoint(size / 2 + x, -size / 2 + y, -size / 2 + z);
        CellPoint p2 = new CellPoint(size / 2 + x, size / 2 + y, -size / 2 + z);
        CellPoint p3 = new CellPoint(size / 2 + x, size / 2 + y, size / 2 + z);
        CellPoint p4 = new CellPoint(size / 2 + x, -size / 2 + y, size / 2 + z);
        CellPoint p5 = new CellPoint(-size / 2 + x, -size / 2 + y, -size / 2 + z);
        CellPoint p6 = new CellPoint(-size / 2 + x, size / 2 + y, -size / 2 + z);
        CellPoint p7 = new CellPoint(-size / 2 + x, size / 2 + y, size / 2 + z);
        CellPoint p8 = new CellPoint(-size / 2 + x, -size / 2 + y, size / 2 + z);

        polygons = new CellPolygon[6];

        polygons[0] = new CellPolygon(color, p5,p6,p7,p8);
        polygons[1] = new CellPolygon(color, p1,p2,p6,p5);
        polygons[2] = new CellPolygon(color, p1,p5,p8,p4);
        polygons[3] = new CellPolygon(color, p2,p6,p7,p3);
        polygons[4] = new CellPolygon(color, p4,p3,p7,p8);
        polygons[5] = new CellPolygon(color, p1,p2,p3,p4);

    }

    private void setPolygonColor() {
        for (CellPolygon poly : this.polygons) {
            poly.setColor(this.color);
        }
    }

    public void rotate(boolean clockWise, double xDegrees, double yDegrees, double zDegrees, LightVector lightVector) {
        for (CellPolygon polygon : this.polygons) {
            polygon.rotate(clockWise, xDegrees, yDegrees, zDegrees, lightVector);
        }
        if (this.isAlive()) {
            this.sortPolygons();
        }
    }

    public void translateZ(double z) {
        for (CellPolygon polygon : this.polygons) {
            for (CellPoint point : polygon.getPoints()) {
                point.addZ(z);
            }
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getAverageX() {
        double sum = 0;

        for (CellPolygon polygon : this.polygons) {
            for (CellPoint point : polygon.getPoints()) {
                sum += point.getX();
            }
        }
        return sum;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void kill() {
        this.age = 0;
        this.alive = false;
    }

    public void revive() {
        this.age = this.MAX_AGE;
        this.alive = true;
    }

    public void age() {
        this.age -= 1;
        if (this.age < 1) {
            this.age = 0;
            this.kill();
            return;
        }
    }

    public int getIndexOfCellBox() {
        return indexOfCellBox;
    }

    public void setIndexOfCellBox(int indexOfCellBox) {
        this.indexOfCellBox = indexOfCellBox;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMaxAge() {
        return this.MAX_AGE;
    }

    public void setMaxAge(int age) {
        this.MAX_AGE = age;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
