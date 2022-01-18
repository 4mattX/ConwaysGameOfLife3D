package renderer.shapes;

import renderer.rendering.CellPoint;

import java.awt.*;

public class CellCube {


    private CellPolygon[] polygons;
    private Color color;
    private int location; // The index value of which the CellCube is located on the cube

    public CellCube(double x, double y, double z, int size, int location, int[] position) {
        this.color = Color.WHITE;
        this.location = location;
        createCellCube(x, y, z, size, position);
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

    private void createCellCube(double x, double y, double z, int size, int[] position) {
        CellPoint p1 = new CellPoint(size / 2 + x, -size / 2 + y, -size / 2 + z);
        CellPoint p2 = new CellPoint(size / 2 + x, size / 2 + y, -size / 2 + z);
        CellPoint p3 = new CellPoint(size / 2 + x, size / 2 + y, size / 2 + z);
        CellPoint p4 = new CellPoint(size / 2 + x, -size / 2 + y, size / 2 + z);
        CellPoint p5 = new CellPoint(-size / 2 + x, -size / 2 + y, -size / 2 + z);
        CellPoint p6 = new CellPoint(-size / 2 + x, size / 2 + y, -size / 2 + z);
        CellPoint p7 = new CellPoint(-size / 2 + x, size / 2 + y, size / 2 + z);
        CellPoint p8 = new CellPoint(-size / 2 + x, -size / 2 + y, size / 2 + z);


        polygons = new CellPolygon[6];
        int transparent = 150;

        Color orange = new Color(255, 162, 0, transparent);
        Color white = new Color(252, 254, 255, transparent);
        Color blue = new Color(0, 145, 255, transparent);
        Color green = new Color(63, 252, 20, transparent);
        Color yellow = new Color(245, 245, 32, transparent);
        Color red = new Color(250, 30, 10, transparent);

        polygons[0] = new CellPolygon(orange, this.location, p5,p6,p7,p8);
        polygons[1] = new CellPolygon(white, this.location, p1,p2,p6,p5);
        polygons[2] = new CellPolygon(blue, this.location, p1,p5,p8,p4);
        polygons[3] = new CellPolygon(green, this.location, p2,p6,p7,p3);
        polygons[4] = new CellPolygon(yellow, this.location, p4,p3,p7,p8);
        polygons[5] = new CellPolygon(red, this.location, p1,p2,p3,p4);

        Color middleColor = new Color(10, 10, 10);

        if (position[0] < 1) {
            polygons[4] = new CellPolygon(middleColor, this.location, p4,p3,p7,p8);
        }
        if (position[0] == 1 || position[0] == 0) {
            polygons[1] = new CellPolygon(middleColor, this.location, p1,p2,p6,p5);
        }
        if (position[2] < 1) {
            polygons[3] = new CellPolygon(middleColor, this.location, p2,p6,p7,p3);
        }
        if (position[2] == 0 || position[2] == 1) {
            polygons[2] = new CellPolygon(middleColor, this.location, p1,p5,p8,p4);
        }
        if (position[1] == 0 || position[1] == -1) {
            polygons[5] = new CellPolygon(middleColor, this.location, p1,p2,p3,p4);
        }
        if (position[1] == 0 || position[1] == 1) {
            polygons[0] = new CellPolygon(middleColor, this.location, p5,p6,p7,p8);
        }
    }

    private void setPolygonColor() {
        for (CellPolygon poly : this.polygons) {
            poly.setColor(this.color);
        }
    }

    public void rotate(boolean clockWise, double xDegrees, double yDegrees, double zDegrees) {
        for (CellPolygon polygon : this.polygons) {
            polygon.rotate(clockWise, xDegrees, yDegrees, zDegrees);
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

}
