package renderer.shapes;

import renderer.rendering.shaders.LightVector;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CellBox {

    private CellCube[] cells;
    private CellCube outline;
    private int cellSize;

    private int amountX;
    private int amountY;
    private int amountZ;
    private int totalCells;

    private double xDeg;
    private double yDeg;
    private double zDeg;


    public CellBox(int amountX, int amountY, int amountZ, int cellSize) {
        this.cellSize = cellSize;
        this.amountZ = amountZ;
        this.amountY = amountY;
        this.amountX = amountX;
        this.totalCells = amountX * amountY * amountZ;

        createBox();
    }

    private void createBox() {
        cells = new CellCube[totalCells];

        int cellIndex = 0;

        int centerX = (amountX / 2) * cellSize;
        int centerY = (amountY / 2) * cellSize;
        int centerZ = (amountZ / 2) * cellSize;

        Random random = new Random();


        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    boolean isAlive;

                    if ((x + y + z) % 19 == 0) {
                        isAlive = true;
                    } else {
                        isAlive = false;
                    }

                    cells[cellIndex] = new CellCube((x * cellSize) - centerX, (y * cellSize) - centerY, (z * cellSize) - centerZ, cellSize, isAlive);
                    cellIndex++;
                }
            }
        }

        outline = new CellCube(0, 0, 0, amountX * cellSize, true);

    }

    public void render(Graphics g) {
        renderBackOutline(g);
        for (CellCube cube : this.cells) {
            if (!cube.isAlive()) {
                continue;
            }
            cube.render(g);
        }
        renderFrontOutline(g);
    }

    public void renderFrontOutline(Graphics g) {
        outline.renderFrontOutline(g);
    }

    public void renderBackOutline(Graphics g) {
        outline.renderBackOutline(g);
    }



    public void rotate(double xDegrees, double yDegrees, double zDegrees, LightVector lightVector) {
        for (CellCube cube : this.cells) {
            cube.rotate(true, xDegrees, yDegrees, zDegrees, lightVector);
        }
        this.cells = sortCellCubes(this.cells);
        outline.rotate(true, xDegrees, yDegrees, zDegrees, lightVector);
    }

    public static CellCube[] sortCellCubes(CellCube[] cellCubeArray) {
        List<CellCube> cubieList = new ArrayList<>();

        for (CellCube c : cellCubeArray) {
            cubieList.add(c);
        }

        Collections.sort(cubieList, new Comparator<CellCube>() {
            @Override
            public int compare(CellCube c1, CellCube c2) {

                double c1AverageX = c1.getAverageX();
                double c2AverageX = c2.getAverageX();
                double diff = c2AverageX - c1AverageX;

                if (diff == 0) {
                    return 0;
                }

                return c2.getAverageX() - c1.getAverageX() < 0 ? 1 : -1;
            }
        });

        for (int i = 0; i < cellCubeArray.length; i++) {
            cellCubeArray[i] = cubieList.get(i);
        }

        return cellCubeArray;
    }





}
