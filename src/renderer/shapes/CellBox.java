package renderer.shapes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CellBox {

    private CellCube[] cells;
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

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    cells[cellIndex] = new CellCube((x * cellSize), (y * cellSize), (z * cellSize), cellSize, true);
                    cellIndex++;
                }
            }
        }

    }

    public void render(Graphics g) {
        for (CellCube cube : this.cells) {
            cube.render(g);
        }
    }

    public void rotate(double xDegrees, double yDegrees, double zDegrees) {
        for (CellCube cube : this.cells) {
            cube.rotate(true, xDegrees, yDegrees, zDegrees);
        }
        this.cells = sortCellCubes(this.cells);
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
