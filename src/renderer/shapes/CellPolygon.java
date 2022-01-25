package renderer.shapes;

import renderer.Display;
import renderer.rendering.CellPoint;
import renderer.rendering.PointConverter;
import renderer.rendering.shaders.LightVector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CellPolygon {


    private Color color;
    private Color lightingColor;
    private CellPoint[] points;
    private Boolean isAlive;

    private static final double AMBIENT_LIGHTING = 0.05;

    public CellPolygon(CellPoint... points) { // ... means any amount of arguments of type CellPoint
        this.color = Color.WHITE; // default color
        this.points = new CellPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            CellPoint p = points[i];
            this.points[i] = new CellPoint(p.x, p.y, p.z);
        }
    }

    public CellPolygon(Color color, CellPoint... points) {
        this.color = color;
        this.lightingColor = color;
        this.points = new CellPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            CellPoint p = points[i];
            this.points[i] = new CellPoint(p.x, p.y, p.z);
        }
    }

    public CellPolygon(Color color, int location, CellPoint... points) {
        this.color = color;
        this.points = new CellPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            CellPoint p = points[i];
            this.points[i] = new CellPoint(p.x, p.y, p.z);
        }
    }


    public void render(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Polygon poly = new Polygon();
        for (int i = 0; i < points.length; i++) {
            Point p = PointConverter.convertPoint(points[i]);
            poly.addPoint(p.x, p.y);
        }



        g2.setColor(this.lightingColor);
        g2.fillPolygon(poly);

    }

    public void renderOutline(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        Polygon poly = new Polygon();
        for (int i = 0; i < points.length; i++) {
            Point p = PointConverter.convertPoint(points[i]);
            poly.addPoint(p.x, p.y);
        }

        g2d.setColor(Color.GREEN);
        g2d.drawPolygon(poly);
    }

    private void updateLightingColor(double lightRatio) {
        int red = (int) (this.color.getRed() * lightRatio);
        int green = (int) (this.color.getGreen() * lightRatio);
        int blue = (int) (this.color.getBlue() * lightRatio);

        this.lightingColor = new Color(red, green, blue);
    }

    public void rotate(boolean clockWise, double xDegrees, double yDegrees, double zDegrees, LightVector lightVector) {
        for (CellPoint point : points) {
            PointConverter.rotateAxisX(point, clockWise, xDegrees);
            PointConverter.rotateAxisY(point, clockWise, yDegrees);
            PointConverter.rotateAxisZ(point, clockWise, zDegrees);
        }

        this.updateLighting(lightVector);
    }

    private void updateLighting(LightVector lightVector) {

        // Prevents crash if you draw a line
        if (this.points.length < 3) {
            return;
        }

        LightVector v1 = new LightVector(this.points[0], this.points[1]);
        LightVector v2 = new LightVector(this.points[1], this.points[2]);
        LightVector normalVector = LightVector.normalize(LightVector.crossProduct(v2, v1));

        double dotProduct = LightVector.dotProduct(normalVector, lightVector);
        double sign = dotProduct < 0 ? -1 : 1;
        dotProduct = sign * (dotProduct * dotProduct);

        dotProduct = (dotProduct + 1) / 2 * 0.8; // Yeh stackoverflow

        // Caps lighting between 0 and 1
        double lightRatio = Math.min(1, Math.max(0, AMBIENT_LIGHTING + dotProduct));
        this.updateLightingColor(lightRatio);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Gets average x value for all points in polygon
    public double getAverageX() {
        double sum = 0;

        for (CellPoint point : this.points) {
            sum += point.x;
        }

        return sum / this.points.length;
    }

    // Sorts polygons such that x distance (distance from camera) is sorted properly
    public static CellPolygon[] sortPolygons(CellPolygon[] polygons) {
        List<CellPolygon> polygonList = new ArrayList<CellPolygon>();

        for (CellPolygon poly : polygons) {
            polygonList.add(poly);
        }

        // This will compare whether a polygon is in front of another based on their average x value
        // where the x value is the distance from the camera
        Collections.sort(polygonList, new Comparator<CellPolygon>() {
            @Override
            public int compare(CellPolygon p1, CellPolygon p2) {

                double p1AverageX = p1.getAverageX();
                double p2AverageX = p2.getAverageX();
                double diff = p2AverageX - p1AverageX;

                // Happens when multiple faces are on same x plane
                if (diff == 0) {
                    return 0;
                }

                return p2.getAverageX() - p1.getAverageX() < 0 ? 1 : -1;
            }
        });

        for (int i = 0; i < polygons.length; i++) {
            polygons[i] = polygonList.get(i);
        }

        return polygons;
    }

    public CellPoint[] getPoints() {
        return this.points;
    }


    public Boolean getAlive() {
        return isAlive;
    }

    public void setAlive(Boolean alive) {
        isAlive = alive;
    }
}
