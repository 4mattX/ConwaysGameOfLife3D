package renderer.shapes;

import renderer.rendering.shaders.LightVector;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CellBox {

    private CellCube[] cells;
    private CellCube[] cellsCopy;

    private CellCube[][][] cellsArray;
    private CellCube[][][] cellsArrayCopy;

    private int[][][] surrounding = new int[3][3][3];

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


        cellsArray = new CellCube[amountX][amountY][amountZ];
        cellsArrayCopy = new CellCube[amountX][amountY][amountZ];

        createBox();
    }

    private void createSurrounding() {
        for (int x = -1; x < amountX; x++) {
            for (int y = -1; y < amountY; y++) {
                for (int z = -1; z < amountZ; z++) {

                }
            }
        }
    }

    private void createBox() {
        cells = new CellCube[totalCells];

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

//                    cells[cellIndex] = cellCube;
//                    cellsCopy[cellIndex] = cellCube;
//                    cellIndex++;

                    cellsArray[x][y][z] = cellCube;
                    cellsArrayCopy[x][y][z] = cellCube;
                }
            }
        }

//        outline = new CellCube(-cellSize/2, -cellSize/2, -cellSize/2, (amountX * cellSize), true); // FOR EVEN
        outline = new CellCube(0, 0, 0, (amountX * cellSize), true);

    }

    public void render(Graphics g) {
        renderBackOutline(g);
//        for (CellCube cube : this.cells) {
//            if (!cube.isAlive()) {
//                continue;
//            }
//            cube.render(g);
//        }

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    if (!cellsArray[x][y][z].isAlive()) {
                        continue;
                    }
                    cellsArray[x][y][z].render(g);
                }
            }
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
//        for (CellCube cube : this.cells) {
//            cube.rotate(true, xDegrees, yDegrees, zDegrees, lightVector);
//        }

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    cellsArray[x][y][z].rotate(true, xDegrees, yDegrees, zDegrees, lightVector);
                }
            }
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
                        cellsArray[x][y][z].revive();
                    } else {
                        cellsArray[x][y][z].kill();
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

        int radius = 6;

        for (int x = (amountX / 2) - radius; x < (amountX / 2) + radius; x++) {
            for (int y = (amountY / 2) - radius; y < (amountY / 2) + radius; y++) {
                for (int z = (amountZ / 2) - radius; z < (amountZ / 2) + radius; z++) {

                    Random random = new Random();

                    if (random.nextBoolean()) {
//                        if (random.nextBoolean()) {

                            if (random.nextBoolean()) {
                                cellsArray[x][y][z].revive();
                                cellsArray[x][y][z].setMaxAge(1);
                                cellsArrayCopy[x][y][z].revive();
                                cellsArrayCopy[x][y][z].setMaxAge(1);
                            }

//                        }

                    }
                }
            }
        }
    }

    public void createGlider() {
        // 4555 Glider
        int x = amountX / 2;
        int y = amountY / 2;
        int z = amountZ / 2;

//        cellsArray[x][y][z].revive();
//        cellsArray[x][y][z + 1].revive();
//        cellsArray[x][y - 1][z].revive();
//        cellsArray[x][y - 1][z + 1].revive();
//        cellsArray[x - 1][y + 1][z].revive();
//        cellsArray[x - 1][y + 1][z + 1].revive();
//        cellsArray[x - 1][y][z - 1].revive();
//        cellsArray[x - 1][y - 1][z - 1].revive();
//        cellsArray[x - 1][y][z + 1].revive();
//        cellsArray[x - 1][y - 1][z + 1].revive();

        cellsArray[x][y][z].revive();
        cellsArray[x][y - 1][z].revive();
        cellsArray[x - 1][y][z].revive();
        cellsArray[x - 1][y - 1][z].revive();
        cellsArray[x - 1][y][z + 1].revive();
        cellsArray[x - 1][y - 1][z + 1].revive();

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

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    this.cellsArray[x][y][z] = this.cellsArrayCopy[x][y][z];
                }
            }
        }
    }

    public void updateLife() {
        copyCells();

        int amountAlive = 0;

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {

                    try {
                        for (int layer = -1; layer <= 1; layer++) {

                            if (cellsArray[x + 1][y + layer][z - 1].isAlive()) {
                                amountAlive++;
                            }

                            if (cellsArray[x + 1][y + layer][z].isAlive()) {
                                amountAlive++;
                            }

                            if (cellsArray[x + 1][y + layer][z + 1].isAlive()) {
                                amountAlive++;
                            }

                            if (cellsArray[x][y + layer][z - 1].isAlive()) {
                                amountAlive++;
                            }

                            if (cellsArray[x][y + layer][z].isAlive()) {
                                if (layer != 0) {
                                    amountAlive++;
                                }
                            }

                            if (cellsArray[x][y + layer][z + 1].isAlive()) {
                                amountAlive++;
                            }

                            if (cellsArray[x - 1][y + layer][z - 1].isAlive()) {
                                amountAlive++;
                            }

                            if (cellsArray[x - 1][y + layer][z].isAlive()) {
                                amountAlive++;
                            }

                            if (cellsArray[x - 1][y + layer][z + 1].isAlive()) {
                                amountAlive++;
                            }
                        }


//                        if (cellsArray[x][y][z].isAlive()) {
//                            cellsArrayCopy[x][y][z].setAge(cellsArrayCopy[x][y][z].getAge() - 1);
//
//                            if (cellsArrayCopy[x][y][z].getAge() < 1) {
//                                cellsArrayCopy[x][y][z].kill();
//                            }
//
//                            if (amountAlive != 4) {
//                                if (cellsArrayCopy[x][y][z].getAge() != 1) {
//                                    cellsArrayCopy[x][y][z].kill();
//                                }
//                            }
//                        } else {
//                            if (amountAlive == 4) {
//                                cellsArrayCopy[x][y][z].revive();
//                                cellsArrayCopy[x][y][z].setAge(4);
//                            }
//                        }

                        CellCube cell = cellsArray[x][y][z];
                        CellCube cellCopy = cellsArrayCopy[x][y][z];


                        if (cell.isAlive()) {
                            cellCopy.age();

                            if (!cellCopy.isAlive()) {
                                continue;
                            }
                        }

                        if (cell.isAlive()) {
                            if (amountAlive == 4 || amountAlive == 5) {
                                cellCopy.kill();
                                continue;
                            }
                        }


                        if (!cell.isAlive()) {
                            if (amountAlive == 5) {
                                cellCopy.revive();
                                continue;
                            }
                        }



                    } catch (ArrayIndexOutOfBoundsException exception) {
                        cellsArrayCopy[x][y][z].kill();
                        continue;
                    }

                    amountAlive = 0;

                }
            }
        }

    }





}
