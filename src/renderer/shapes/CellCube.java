package renderer.shapes;

import renderer.rendering.CellPoint;
import renderer.rendering.shaders.LightVector;

import java.awt.*;

public class CellCube {


    private CellPolygon[] polygons;
    private Color color;
    private boolean alive;
    private int indexOfCellBox;

    public CellCube(double x, double y, double z, int size, boolean alive) {
        this.alive = alive;
        this.color = Color.WHITE;
        createCellCube(x, y, z, size);
    }

    public void render(Graphics g) {
        for (CellPolygon poly : this.polygons) {
            poly.render(g);
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

//        if (!alive) {
//            Color invisible = new Color(0, 0, 0, 0);
////            Color invisible = null;
//            polygons[0] = new CellPolygon(invisible, p5,p6,p7,p8);
//            polygons[1] = new CellPolygon(invisible, p1,p2,p6,p5);
//            polygons[2] = new CellPolygon(invisible, p1,p5,p8,p4);
//            polygons[3] = new CellPolygon(invisible, p2,p6,p7,p3);
//            polygons[4] = new CellPolygon(invisible, p4,p3,p7,p8);
//            polygons[5] = new CellPolygon(invisible, p1,p2,p3,p4);
//            return;
//        }

        Color orange = new Color(255, 162, 0);
        Color white = new Color(252, 254, 255);
        Color blue = new Color(0, 145, 255);
        Color green = new Color(63, 252, 20);
        Color yellow = new Color(245, 245, 32);
        Color red = new Color(250, 30, 10);

        polygons[0] = new CellPolygon(red, p5,p6,p7,p8);
        polygons[1] = new CellPolygon(red, p1,p2,p6,p5);
        polygons[2] = new CellPolygon(red, p1,p5,p8,p4);
        polygons[3] = new CellPolygon(red, p2,p6,p7,p3);
        polygons[4] = new CellPolygon(red, p4,p3,p7,p8);
        polygons[5] = new CellPolygon(red, p1,p2,p3,p4);

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
        this.sortPolygons();
    }

    public void translateZ(double z) {
        for (CellPolygon polygon : this.polygons) {
            for (CellPoint point : polygon.getPoints()) {
                point.addZ(z);
            }
        }
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
        this.alive = false;
    }

    public void revive() {
        this.alive = true;
    }

    public int getIndexOfCellBox() {
        return indexOfCellBox;
    }

    public void setIndexOfCellBox(int indexOfCellBox) {
        this.indexOfCellBox = indexOfCellBox;
    }
}