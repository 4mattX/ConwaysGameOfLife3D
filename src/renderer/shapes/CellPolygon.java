package renderer.shapes;

import renderer.rendering.CellPoint;
import renderer.rendering.PointConverter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CellPolygon {


    private Color color;
    private CellPoint[] points;
    private int location;

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
        this.location = location;
    }

    public void render(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Polygon poly = new Polygon();
        for (int i = 0; i < points.length; i++) {
            Point p = PointConverter.convertPoint(points[i]);
            poly.addPoint(p.x, p.y);
        }

        g.setColor(this.color);
        g.fillPolygon(poly);


        BasicStroke stroke = new BasicStroke(2.0f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);

        g2.setStroke(stroke);

        Color edgeColor = new Color(30, 30, 30);
        g2.setColor(edgeColor);
        g2.drawPolygon(poly);
    }

    public void rotate(boolean clockWise, double xDegrees, double yDegrees, double zDegrees) {
        for (CellPoint point : points) {
            PointConverter.rotateAxisX(point, clockWise, xDegrees);
            PointConverter.rotateAxisY(point, clockWise, yDegrees);
            PointConverter.rotateAxisZ(point, clockWise, zDegrees);
        }
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


}
