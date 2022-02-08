package renderer.shapes;

import renderer.rendering.shaders.LightVector;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CellBox {

    private CellCube[] cells;
    private CellCube[] cellsCopy;
    private CellCube outline;
    private int cellSize;

    private int amountX;
    private int amountY;
    private int amountZ;
    private int totalCells;

    private double xDeg;
    private double yDeg;
    private double zDeg;

    private int globalCounter = 1;


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
        cellsCopy = new CellCube[totalCells];

        int cellIndex = 0;

        int centerX = (amountX / 2) * cellSize;
        int centerY = (amountY / 2) * cellSize;
        int centerZ = (amountZ / 2) * cellSize;

        Random random = new Random();


        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountX; y++) {
                for (int z = 0; z < amountX; z++) {
                    boolean isAlive = false;

                    CellCube cellCube = new CellCube((x * cellSize) - centerX, (y * cellSize) - centerY, (z * cellSize) - centerZ, cellSize, isAlive, colorAdjust(x, y, z));

                    cells[cellIndex] = cellCube;
                    cellsCopy[cellIndex] = cellCube;
                    cellIndex++;
                }
            }
        }

//        outline = new CellCube(-cellSize/2, -cellSize/2, -cellSize/2, (amountX * cellSize), true); // FOR EVEN
        outline = new CellCube(0, 0, 0, (amountX * cellSize), true);

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
//        this.cells = sortCellCubes(this.cells);
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

    //
    // BEGIN OF RULE TESTING
    //

    public void testAnimation() {
        int cellIndex = 0;

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {

                    if ((x + y + z) % globalCounter == 0) {
                        cells[cellIndex].revive();
                    } else {
                        cells[cellIndex].kill();
                    }

                    cellIndex++;
                }
            }
        }
        globalCounter++;
    }


    public Color colorAdjust(int x, int y, int z) {

        int center = cells.length / 2;
        int edge = amountX / 2;

        int colorDif = (int) Math.sqrt((Math.pow(x - center, 2)) + (Math.pow(y - center, 2)) + (Math.pow(z - center, 2)));
        int maxDif = (int) Math.sqrt((Math.pow(center - edge, 2)) + (Math.pow(center - edge, 2)) + (Math.pow(center - edge, 2)));
        int newColorVal = Math.abs(maxDif - colorDif);

        Color color = new Color(100, 0,  newColorVal * 5);

        return color;
    }

    public void populateCenter() {

        int length = amountX;
        int face = length * length;
        int center = cells.length / 2;

        for (int layer = -1; layer < 2; layer++) {
            cells[center - layer - face - length].revive();
            cells[center - layer - face].revive();
            cells[center - layer - face + length].revive();
            cells[center - layer - length].revive();
            cells[center - layer].revive();
            cells[center - layer + length].revive();
            cells[center - layer + face - length].revive();
            cells[center - layer + face].revive();
            cells[center - layer + face + length].revive();
        }

    }

    public void populateAll() {
        for (int i = 0; i < cells.length; i++) {
            cells[i].revive();
        }
    }

    public void populateRandom() {
        for (int i = 0; i < cells.length; i++) {

            Random random = new Random();

            if (random.nextBoolean()) {
                if (random.nextBoolean()) {
                    cells[i].revive();
                }
            }

        }
    }

    public void populateEach() {

        cells[globalCounter].revive();
        globalCounter++;
    }



    // RULES

    public void setCells() {

        this.cellsCopy = new CellCube[cells.length];

        for (int i = 0; i < cells.length; i++) {
            this.cellsCopy[i] = cells[i];
        }

    }

    public void copyCells() {
        for (int i = 0; i < cells.length; i++) {
            this.cells[i] = this.cellsCopy[i];
        }
    }

    public void updateLife() {
        copyCells();

        int amtAlive = 0;
        int length = amountX;
        int face = length * length;
        int center = cells.length / 2;

        for (int i = 0; i < cells.length; i++) {
            // For each cell, calculate amount of alive neighbors
            try {

                for (int layer = -1; layer < 2; layer++) {

                    if (cells[center - layer - face - length].isAlive()) {
                        amtAlive++;
                    }

                    if (cells[center - layer - face].isAlive()) {
                        amtAlive++;
                    }

                    if (cells[center - layer - face + length].isAlive()) {
                        amtAlive++;
                    }

                    if (cells[center - layer - length].isAlive()) {
                        amtAlive++;
                    }

                    if (cells[center - layer].isAlive()) {

                        if (layer != 0) {
                            amtAlive++;
                        }
                    }

                    if (cells[center - layer + length].isAlive()) {
                        amtAlive++;
                    }

                    if (cells[center - layer + face - length].isAlive()) {
                        amtAlive++;
                    }

                    if (cells[center - layer + face].isAlive()) {
                        amtAlive++;
                    }

                    if (cells[center - layer + face + length].isAlive()) {
                        amtAlive++;
                    }

                }


            } catch (Exception e) {
            }



            if (cells[i].isAlive()) {
                System.out.println(amtAlive);
                if (!(amtAlive == 2 || amtAlive == 3)) {
                    cellsCopy[i].kill();
                }
            } else {
                if (amtAlive == 3) {
                    cellsCopy[i].revive();
                }
            }

            amtAlive = 0;

        }


    }





}
