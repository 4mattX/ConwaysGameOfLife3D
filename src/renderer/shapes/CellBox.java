package renderer.shapes;

import renderer.Display;
import renderer.rendering.shaders.LightVector;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CellBox {


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


        int cellIndex = 0;

        int centerX = (amountX / 2) * cellSize;
        int centerY = (amountY / 2) * cellSize;
        int centerZ = (amountZ / 2) * cellSize;

        Random random = new Random();


        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountX; y++) {
                for (int z = 0; z < amountX; z++) {
                    boolean isAlive = false;

                    CellCube cellCube1 = new CellCube((x * cellSize) - centerX, (y * cellSize) - centerY, (z * cellSize) - centerZ, cellSize, isAlive, colorAdjust(x, y, z));
                    CellCube cellCube2 = new CellCube((x * cellSize) - centerX, (y * cellSize) - centerY, (z * cellSize) - centerZ, cellSize, isAlive, colorAdjust(x, y, z));
                    cellCube1.setMaxAge(MAX_AGE);
                    cellCube1.setAge(MAX_AGE);
                    cellCube2.setMaxAge(MAX_AGE);
                    cellCube2.setAge(MAX_AGE);

                    cellsArray[x][y][z] = cellCube1;
                    cellsArrayCopy[x][y][z] = cellCube2;
                }
            }
        }

//        outline = new CellCube(-cellSize/2, -cellSize/2, -cellSize/2, (amountX * cellSize), true); // FOR EVEN
        outline = new CellCube(0, 0, 0, (amountX * cellSize), true);

    }

    public void render(Graphics g) {
        if (Display.toggleOutline) {
            renderBackOutline(g);
        }

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    if (cellsArray[x][y][z].isAlive()) {
                        cellsArray[x][y][z].render(g);
                    }
                }
            }
        }

        if (Display.toggleOutline) {
            renderFrontOutline(g);
        }
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

        double xD = x;
        double yD = y;
        double zD = z;

        double amount = amountX;


        double newX = (xD / amount) * 255.0;
        double newY = (yD / amount) * 255.0;
        double newZ = (zD / amount) * 255.0;

        Color color = new Color((int) newX, (int) newY, (int) newZ);

        return color;
    }

    public void populateRandom() {

        int radius = 5;

        for (int x = (amountX / 2) - radius; x < (amountX / 2) + radius; x++) {
            for (int y = (amountY / 2) - radius; y < (amountY / 2) + radius; y++) {
                for (int z = (amountZ / 2) - radius; z < (amountZ / 2) + radius; z++) {

                    Random random = new Random();
                    if (random.nextBoolean()) {
                        cellsArray[x][y][z].revive();
                    }
                }
            }
        }
    }

    public void populateBigBlob() {

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {
                    Random random = new Random();
                    if (random.nextBoolean()) {
                        cellsArray[x][y][z].revive();
                    }
                }
            }
        }

    }

    public void populateCenter() {
        cellsArray[amountX / 2][amountY / 2][amountZ / 2].revive();
    }

    public void populateOdd() {
        int radius = 7;

        for (int x = (amountX / 2) - radius; x < (amountX / 2) + radius; x++) {
            for (int y = (amountY / 2) - radius; y < (amountY / 2) + radius; y++) {
                for (int z = (amountZ / 2) - radius; z < (amountZ / 2) + radius; z++) {

                    if ((x + y) % 2 == 0) {
//                        if ((x + y) % 2 == 0) {
//                            cellsArray[x][y][z].setMaxAge(MAX_AGE);
                            cellsArray[x][y][z].revive();
//                            cellsArrayCopy[x][y][z].revive();
//                        }
                    }


                }
            }
        }
    }

    public void clearMiddleArea() {

        int radius = amountX / 2;

        for (int x = (amountX / 2) - radius; x < (amountX / 2) + radius; x++) {
            for (int y = (amountY / 2) - radius; y < (amountY / 2) + radius; y++) {
                for (int z = (amountZ / 2) - radius; z < (amountZ / 2) + radius; z++) {

                    cellsArray[x][y][z].kill();
                    cellsArrayCopy[x][y][z].kill();

                }
            }
        }
    }

    public void createGlider(int gliderType) {
        // 4555 Glider
        int x = amountX / 2;
        int y = amountY / 2;
        int z = amountZ / 2;

        int[] glider = new int[27];

        if (gliderType == 0) {
            glider = new int[] {1, 1, 0,
                                1, 1, 0,
                                0, 0, 0,

                                0, 1, 0,
                                1, 1, 1,
                                0, 1, 0,

                                0, 0, 0,
                                0, 1, 1,
                                0, 1, 1};
        } else if (gliderType == 1) {
            glider = new int[] {0, 0, 0,
                                1, 0, 1,
                                0, 0, 0,

                                0, 0, 0,
                                1, 0, 1,
                                0, 0, 0,

                                0, 0, 0,
                                1, 0, 1,
                                0, 0, 0};
        } else if (gliderType == 2) {
            glider = new int[] {1, 1, 0,
                                1, 1, 0,
                                0, 0, 0,

                                0, 0, 0,
                                0, 0, 0,
                                0, 0, 0,

                                1, 1, 0,
                                1, 1, 0,
                                0, 0, 0};
        } else if (gliderType == 3) {
            glider = new int[] {0, 0, 0,
                                0, 0, 0,
                                0, 0, 0,

                                0, 0, 0,
                                0, 1, 0,
                                0, 0, 0,

                                0, 0, 0,
                                0, 0, 0,
                                0, 0, 0};
        }

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

    // RULES
    public void copyCells() {

        int centerX = (amountX / 2) * cellSize;
        int centerY = (amountY / 2) * cellSize;
        int centerZ = (amountZ / 2) * cellSize;

        for (int x = 0; x < amountX; x++) {
            for (int y = 0; y < amountY; y++) {
                for (int z = 0; z < amountZ; z++) {

                    if (cellsArrayCopy[x][y][z].isAlive()) {
                        if (!cellsArray[x][y][z].isAlive()) {
                            cellsArray[x][y][z].revive();
                        }
                    }

                    if (!cellsArrayCopy[x][y][z].isAlive()) {
                        cellsArray[x][y][z].kill();
                    }

                    cellsArray[x][y][z].setAge(cellsArrayCopy[x][y][z].getAge());


//                    this.cellsArray[x][y][z] = this.cellsArrayCopy[x][y][z];
                }
            }
        }
    }

    public void updateLife() {

        int amountAlive = 0;

        int cellCounter = 0;

        for (int xOriginal = 0; xOriginal < amountX; xOriginal++) {
            for (int yOriginal = 0; yOriginal < amountY; yOriginal++) {
                for (int zOriginal = 0; zOriginal < amountZ; zOriginal++) {

                    int x = xOriginal;
                    int y = yOriginal;
                    int z = zOriginal;

                    CellCube cell = this.cellsArray[x][y][z];
                    CellCube cellCopy = this.cellsArrayCopy[x][y][z];

                    if (xOriginal > amountX - 2) {
                        x = 1;
                    }

                    if (yOriginal > amountY - 2) {
                        y = 1;
                    }

                    if (zOriginal > amountZ - 2) {
                        z = 1;
                    }

                    if (xOriginal < 1) {
                        x = amountX - 2;
                    }

                    if (yOriginal < 1) {
                        y = amountY - 2;
                    }

                    if (zOriginal < 1) {
                        z = amountZ - 2;
                    }


                    amountAlive = 0;

                    // If Von Neumann use separate logic
                    if (Display.toggleVon) {

                        if (cellsArray[x + 1][y + 0][z].isAlive()) {
                            amountAlive++;
                        }

                        if (cellsArray[x][y + 0][z - 1].isAlive()) {
                            amountAlive++;
                        }

                        if (cellsArray[x][y + 0][z + 1].isAlive()) {
                            amountAlive++;
                        }

                        if (cellsArray[x - 1][y + 0][z].isAlive()) {
                            amountAlive++;
                        }

                        if (cellsArray[x][y + 1][z].isAlive()) {
                            amountAlive++;
                        }

                        if (cellsArray[x][y - 1][z].isAlive()) {
                            amountAlive++;
                        }
                    }

                    // Logic for Moore Neighbor group
                    else {
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
                        cellCopy.age();
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
                        if (cell.getMaxAge() != 0) {
                            cellCopy.age();
                        }
                    }



                }
            }
        }
        copyCells();

    }





}
