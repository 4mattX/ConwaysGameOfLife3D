package renderer.shapes;

import renderer.Display;
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

    public int MAX_AGE = Display.MAX_AGE;
    public List<Integer> bornList = new ArrayList<>();
    public List<Integer> surviveList = new ArrayList<>();

    private int globalCounter = 1;


    public CellBox(int amountX, int amountY, int amountZ, int cellSize) {
        this.cellSize = cellSize;
        this.amountZ = amountZ;
        this.amountY = amountY;
        this.amountX = amountX;
        this.totalCells = amountX * amountY * amountZ;

        this.bornList = Display.bornList;
        this.surviveList = Display.surviveList;


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
                    cellCube.setMaxAge(MAX_AGE);
                    cellCube.setAge(MAX_AGE);

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

        int center = amountX / 2;

        int distance = (int) (Math.sqrt(Math.pow(center - x, 2) + Math.pow(center - y, 2) + Math.pow(center - z, 2)));

        int multiplier = (int) 255 / (distance + 1);

        int redDif = (Math.abs(center - x) + 20);
        int greenDif = (Math.abs(center - y) + 20) * 2;
        int blueDif = (Math.abs(center - z) + 20) * 4;

        Color color = null;

//        color = new Color((distance + 10) * 4 + 20, greenDif + 20, (distance) * 4 + 10);
        color = new Color(100, greenDif, blueDif);

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

        int radius = 10;

        for (int x = (amountX / 2) - radius; x < (amountX / 2) + radius; x++) {
            for (int y = (amountY / 2) - radius; y < (amountY / 2) + radius; y++) {
                for (int z = (amountZ / 2) - radius; z < (amountZ / 2) + radius; z++) {

                    Random random = new Random();

                    if (random.nextBoolean()) {
//                        if (random.nextBoolean()) {

//                            if (random.nextBoolean()) {
                                cellsArray[x][y][z].revive();
//                                cellsArrayCopy[x][y][z].revive();
//                            }

//                        }

                    }
                }
            }
        }
    }

    public void populateOdd() {
        int radius = 5;

        for (int x = (amountX / 2) - radius; x < (amountX / 2) + radius; x++) {
            for (int y = (amountY / 2) - radius; y < (amountY / 2) + radius; y++) {
                for (int z = (amountZ / 2) - radius; z < (amountZ / 2) + radius; z++) {

                    if ((x + y) % 2 == 0) {
//                        if ((x + y) % 2 == 0) {
//                            cellsArray[x][y][z].setMaxAge(MAX_AGE);
                            cellsArray[x][y][z].revive();
                            cellsArrayCopy[x][y][z].revive();
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

        int[] glider = {0, 0, 0,
                        0, 1, 0,
                        0, 0, 0,

                        0, 1, 0,
                        1, 0, 1,
                        1, 0, 1,

                        0, 0, 0,
                        0, 1, 0,
                        0, 0, 0};

        int cellCounter = 0;

        for (int i = -1; i <= 1; i++) {
            if (glider[cellCounter] == 1) {
                cellsArray[x + 1][y + i][z - 1].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x + 1][y + i][z].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x + 1][y + i][z + 1].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x][y + i][z - 1].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x][y + i][z].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x][y + i][z + 1].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x - 1][y + i][z - 1].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x - 1][y + i][z].revive();
            }
            cellCounter++;
            if (glider[cellCounter] == 1) {
                cellsArray[x - 1][y + i][z + 1].revive();
            }
            cellCounter++;
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

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    this.cellsArray[x][y][z] = this.cellsArrayCopy[x][y][z];
                }
            }
        }
    }

    public void updateLife() {

        int amountAlive = 0;

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {

                    CellCube cell = cellsArray[x][y][z];
                    CellCube cellCopy = cellsArrayCopy[x][y][z];
                    amountAlive = 0;

                    if (x == amountX-1 || y == amountY-1 || z == amountZ-1 || x == 0 || y == 0 || z == 0) {
                        continue;
                    }

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

                    if (cell.isAlive()) {
                        boolean shouldSurvive = false;
                        for (int i = 0; i < surviveList.size(); i++) {
                            if (amountAlive == surviveList.get(i)) {
                                shouldSurvive = true;
                            }
                        }

                        if (!shouldSurvive) {
                            cellCopy.kill();
                        }
                    }

                    if (!cell.isAlive()) {
                        boolean shouldRevive = false;
                        for (int i = 0; i < bornList.size(); i++) {
                            if (amountAlive == bornList.get(i)) {
                                shouldRevive = true;
                            }
                        }
                        if (shouldRevive) {
                            cellCopy.revive();
                        }
                    }

                    if (cell.isAlive()) {
                        cellCopy.age();
                    }



                }
            }
        }
        copyCells();
    }





}
