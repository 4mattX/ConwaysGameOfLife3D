package renderer;// Display Class is responsible for all things related with window display
// This also includes any User Controller(s) involved

import renderer.rendering.shaders.LightVector;
import renderer.shapes.CellBox;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Display extends Canvas implements Runnable {

    public static Thread thread; // Rendering is ran on separate single thread
    private JFrame frame;
    private static String title = "Conway's Game of Life 3D";
    private static JPanel rightBigPanel = new JPanel();
    private static JPanel leftBigPanel = new JPanel();
    private static JPanel leftPanel = new JPanel();
    private static JPanel rightPanel = new JPanel();
    private static JPanel leftPanelGhost = new JPanel();
    private static JPanel rightPanelGhost = new JPanel();
    private static JLabel bornLabel = new JLabel();
    private static JLabel surviveLabel = new JLabel();
    private static JLabel rightLabel = new JLabel();

    private static JToggleButton[] bornButtons = new JToggleButton[26];
    private static JToggleButton[] surviveButtons = new JToggleButton[26];

    private static List<String> organismNames = Arrays.asList("RandomBlob", "4-4Star", "4-4Dancer", "Pillars", "4-4Worm", "Single Block", "Big Blob");

    private static JSlider ageSlider = new JSlider();
    private static JSlider worldSlider = new JSlider();
    private static JSlider colorSlider = new JSlider();
    private static JSlider leftSlider4 = new JSlider();
    private static JToggleButton outlineToggleButton = new JToggleButton();
    private static JToggleButton vonToggleButton = new JToggleButton();

    private static boolean open = false;
    private static boolean rOpen = false;
    private static boolean pause = false;
    public static boolean toggleOutline = true;
    public static boolean toggleVon = false;
    private static int organismID = 0;

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;
    private static final double NANO_SECOND = 1000000000.0 / 60;
    private static final double SECOND = 1000;

    // Slider and Button Values
    public static int MAX_AGE = 5;
    public static int WORLD_SIZE = 71;
    public static int CELL_SIZE = 5;
    public static List<Integer> bornList = new ArrayList<>();
    public static List<Integer> surviveList = new ArrayList<>();

    private static CellBox cellBox;
    private static LightVector lightVector = LightVector.normalize(new LightVector(-1, 1, -1));

    private static boolean running = false;

    public Display() {
        this.frame = new JFrame();

        Dimension size = new Dimension(WIDTH, HEIGHT);
        this.setPreferredSize(size);
    }

    public synchronized void start() {
        running = true;
        this.thread = new Thread(this, "Display");
        this.thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            this.thread.join(); // quits the thread
        } catch (Exception e) {
        }
    }

    // This method renders the simulation and updates it
    // The update function is compressed to give smooth animations
    public void run() {

        int smoothVar = 8; // smaller number = faster

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = NANO_SECOND;
        double delta = smoothVar;
        int frames = 0;


        // Initializes objects in display
        init();


        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= smoothVar) {

                update();
                delta = 0;
                render();
                frames++;
            }

            // Updates frame title every second to display Frames Per Second
            if (System.currentTimeMillis() - timer > SECOND) {
                timer += SECOND;
                this.frame.setTitle(title + " | " + frames + " fps");
                frames = 0; // resets frames to 0 to properly calculate updated fps
            }

        }
        stop();
    }

    private void render() {

        // This buffer creates a good mechanism to organize all memory required for this program
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        // Draws background of display
        drawBackground(g);
        cellBox.render(g);

        g.dispose();
        bs.show();
    }

    private void drawBackground(Graphics g) {
        // If we decide to use a background image, use this structure
//        Image image = Toolkit.getDefaultToolkit().getImage("background.jpg");

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH * 2, HEIGHT * 2);
        g.setColor(new Color(71, 71, 71));
        g.drawString("[SPACE] = Pause    [N] = Delete Center     [M] = Next Life Iteration", (WIDTH / 2) - 160, HEIGHT - 5);
    }

    private static void leftSliderMouseEnter(MouseEvent event) {

        if (open) {
            return;
        }

        leftPanel.setBackground(Color.WHITE);

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    for (int j = 0; j < 215; j++) {
                        Thread.sleep(1);
                        leftPanel.setSize(j, HEIGHT);
                    }
                    leftPanelGhost.setSize(220, HEIGHT);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };
        th.start();

    }

    private static void rightSliderMouseEnter(MouseEvent event) {

        if (rOpen) {
            return;
        }

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    for (int j = 0; j < 155; j++) {
                        Thread.sleep(1);
                        rightPanel.setBounds(225 - j, 0, j, HEIGHT);
                    }
                    rightPanelGhost.setBounds(65, 0, 160, HEIGHT);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };
        th.start();

    }

    private static void leftSliderMouseExit(MouseEvent event) {

        bornList.clear();
        surviveList.clear();

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 155; i > 0; i--) {
                        Thread.sleep(1);
                        leftPanel.setSize(i, HEIGHT);
                    }
                    leftPanelGhost.setSize(60, HEIGHT);
                    leftPanel.setBackground(Color.BLACK);

                    for (int i = 0; i < bornButtons.length; i++) {
                        if (bornButtons[i].isSelected()) {
                            bornList.add(i + 1);
                        }
                    }

                    for (int i = 0; i < surviveButtons.length; i++) {
                        if (surviveButtons[i].isSelected()) {
                            surviveList.add(i + 1);
                        }
                    }

                    resetLife();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };
        th.start();

    }

    private static void rightSliderMouseExit(MouseEvent event) {
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 155; i++) {
                        Thread.sleep(1);

                        rightPanel.setBounds(95 + i, 0, 155 - i, HEIGHT);
                    }
                    rightPanelGhost.setBounds(165, 0, 60, HEIGHT);

                    resetLife();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };
        th.start();

    }

    private static void initComponents(Display display) {

        display.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'm') {
                    cellBox.updateLife();
                }

                if (e.getKeyChar() == 'n') {
                    cellBox.clearMiddleArea();
                }

                if (e.getKeyChar() == ' ') {
                    if (pause) {
                        pause = false;
                    } else {
                        pause = true;
                    }
                }



            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        // BIG PANEL
        leftBigPanel.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        leftBigPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        leftBigPanel.setPreferredSize(new Dimension(WIDTH / 4, HEIGHT));
        leftBigPanel.setBackground(new Color(0, 0, 0));

        leftBigPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (open) {
                    leftSliderMouseExit(event);
                }
                open = false;
            }
        });

        //Right Big Panel
        rightBigPanel.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        rightBigPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rightBigPanel.setBounds(775, 0, 225, HEIGHT);
        rightBigPanel.setBackground(Color.BLACK);

        rightBigPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (rOpen) {
                    rightSliderMouseExit(event);
                }
                rOpen = false;
            }
        });

        // LEFT PANEL
        leftPanelGhost.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                leftSliderMouseEnter(event);
                open = true;
            }

            public void mouseExited(MouseEvent event) {
                //leftSliderMouseExit(event);
            }
        });

        //RIGHT PANEL
        rightPanelGhost.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                rightSliderMouseEnter(event);
                rOpen = true;
            }

            public void mouseExited(MouseEvent event) {
//                rightSliderMouseExit(event);
            }
        });


        leftPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        leftPanelGhost.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanelGhost.setBackground(Color.BLACK);
        rightPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rightPanelGhost.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanelGhost.setBackground(Color.BLACK);

        createSliderValues();

        leftPanel.add(ageSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 10, 205, -1));
        leftPanel.add(worldSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 90, 205, -1));

        // Outline Toggle Button
        leftPanel.add(outlineToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 160, 205, -1));

        // Von Neuman Life Toggle Button
        leftPanel.add(vonToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 190, 205, -1));

        bornLabel.setText("Born Rules");
        bornLabel.setForeground(Color.BLUE);
        leftPanel.add(bornLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 90, 64));

        surviveLabel.setText("Survive Rules");
        surviveLabel.setForeground(Color.RED);
        leftPanel.add(surviveLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 200, 90, 64));

        for (int i = 0; i < bornButtons.length; i++) {
            bornButtons[i] = new JToggleButton(i +  1 + "");
            bornButtons[i].setBorder(BorderFactory.createEmptyBorder());
            bornButtons[i].setFont(new Font("Times New Roman", Font.BOLD, 10));
            leftPanel.add(bornButtons[i], new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 245 + (i * 13), 24, 12));
        }

        for (int i = 0; i < surviveButtons.length; i++) {
            surviveButtons[i] = new JToggleButton(i +  1 + "");
            surviveButtons[i].setBorder(BorderFactory.createEmptyBorder());
            surviveButtons[i].setFont(new Font("Times New Roman", Font.BOLD, 10));
            leftPanel.add(surviveButtons[i], new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 245 + (i * 13), 24, 12));
        }

        JRadioButton[] jRadioButtons = new JRadioButton[7];
        JLabel[] organismLabels = new JLabel[7];
        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < jRadioButtons.length; i++) {
            jRadioButtons[i] = new JRadioButton();
            organismLabels[i] = new JLabel();
            organismLabels[i].setText(organismNames.get(i));
            organismLabels[i].setIcon(new ImageIcon("life" + i + ".png"));
            rightPanel.add(organismLabels[i],  new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100 + (i * 50), 150, 50));
            rightPanel.add(jRadioButtons[i], new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100 + (i * 50), 20, 50));
            buttonGroup.add(jRadioButtons[i]);
            int finalI = i;
            jRadioButtons[i].addActionListener(e -> organismID = finalI);
        }

        // Left Arrow Icon
        ImageIcon leftArrowIcon = new ImageIcon("left_arrow.png");
        JLabel leftArrowLabel = new JLabel(leftArrowIcon, JLabel.CENTER);
        leftPanelGhost.add(leftArrowLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, (HEIGHT / 2) - leftArrowIcon.getIconHeight(), leftArrowIcon.getIconHeight() - 15, -1));

        // Right Arrow Icon
        ImageIcon rightArrowIcon = new ImageIcon("right_arrow.png");
        JLabel rightArrowLabel = new JLabel(rightArrowIcon, JLabel.CENTER);
        rightPanelGhost.add(rightArrowLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, (HEIGHT / 2) - rightArrowIcon.getIconHeight(), 50, -1));

        leftBigPanel.add(leftPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, HEIGHT));
        leftBigPanel.add(leftPanelGhost, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, HEIGHT));

        rightPanel.add(rightLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 268, 109, 64));
        rightBigPanel.add(rightPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 0, 0, HEIGHT));
        rightBigPanel.add(rightPanelGhost, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, 0, 60, HEIGHT));

        display.frame.add(rightBigPanel);
        display.frame.add(leftBigPanel);
        display.frame.pack();

    }

    private static void createSliderValues() {
        // Left Panel Sliders
        // AGE SLIDER
        ageSlider.setMinimum(0);
        ageSlider.setMaximum(26);
        ageSlider.setValue(2);

        ageSlider.setPaintTicks(true);
        ageSlider.setMinorTickSpacing(1);

        ageSlider.setPaintTrack(true);
        ageSlider.setMajorTickSpacing(5);
        ageSlider.setPaintLabels(true);

        ageSlider.setBorder(BorderFactory.createTitledBorder("Max Age: " + ageSlider.getValue() + ""));

        ageSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ageSlider.setBorder(BorderFactory.createTitledBorder("Max Age: " + ageSlider.getValue() + ""));
                MAX_AGE = ageSlider.getValue();
            }
        });

        // WORLD SIZE SLIDER
        worldSlider.setMinimum(20);
        worldSlider.setMaximum(100);
        worldSlider.setValue(71);

        worldSlider.setPaintTicks(true);
        worldSlider.setMinorTickSpacing(5);

        worldSlider.setPaintTrack(true);
        worldSlider.setMajorTickSpacing(20);
        worldSlider.setPaintLabels(true);

        worldSlider.setBorder(BorderFactory.createTitledBorder("World Size: " + worldSlider.getValue()));

        worldSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if (worldSlider.getValue() == 20) {
                    worldSlider.setValue(21);
                } else if (worldSlider.getValue() % 2 == 0) {
                    worldSlider.setValue(worldSlider.getValue() - 1);
                }
                worldSlider.setBorder(BorderFactory.createTitledBorder("World Size: " + worldSlider.getValue()));
                WORLD_SIZE = worldSlider.getValue();
                CELL_SIZE = 390 / WORLD_SIZE;

            }
        });

        outlineToggleButton.setSelected(true);
        outlineToggleButton.setText("Toggle Outline");
        outlineToggleButton.addActionListener(e -> {
            if (toggleOutline) {
                toggleOutline = false;
            } else {
                toggleOutline = true;
            }
        });

        vonToggleButton.setText("Toggle Von Neumann Life");
        vonToggleButton.addActionListener(e -> {
            toggleVon = !toggleVon;
        });

    }

    // Initializer used to create first instances of cells
    private void init() {
        cellBox = new CellBox(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, CELL_SIZE);
        cellBox.rotate(0, 0, 60, lightVector);
    }

    private static void resetLife() {
        cellBox = new CellBox(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, CELL_SIZE);
        cellBox.rotate(0, 0, 60, lightVector);
        switch (organismID) {
            case 0:
                cellBox.populateRandom();
                break;
            case 1:
                cellBox.createGlider(0);
                break;
            case 2:
                cellBox.createGlider(1);
                break;
            case 3:
                cellBox.populateOdd();
                break;
            case 4:
                cellBox.createGlider(2);
                break;
            case 5:
                cellBox.createGlider(3);
                break;
            case 6:
                cellBox.populateBigBlob();
                break;
        }
    }

    private void update() {
        if (!pause) {
            cellBox.updateLife();
        }
    }

    public static void main(String[] args) {

        Display display = new Display();
        initComponents(display);

        display.frame.setTitle(title);
        display.frame.add(display); // Adding display to frame
        display.frame.pack();
        display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.frame.setLocationRelativeTo(null); // This centers display on user's screen

        display.frame.setVisible(true);
        display.start();
    }

    public Thread getThread() {
        return this.thread;
    }

}
