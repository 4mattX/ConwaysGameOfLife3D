package renderer;// Display Class is responsible for all things related with window display
// This also includes any User Controller(s) involved

import renderer.rendering.shaders.LightVector;
import renderer.shapes.CellBox;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

public class Display extends Canvas implements Runnable {

    public static Thread thread; // Rendering is ran on separate single thread
    private JFrame frame;
    private static String title = "Conway's Game of Life 3D";
    private static JPanel rightBigPanel = new JPanel();
    private static JPanel leftBigPanel = new JPanel();
    private static JPanel leftPanel = new JPanel();
    private static JPanel rightSlider = new JPanel();
    private static JPanel leftPanelGhost = new JPanel();
    private static JPanel rightPanelGhost = new JPanel();
    private static JLabel bornLabel = new JLabel();
    private static JLabel surviveLabel = new JLabel();
    private static JLabel rightLabel = new JLabel();

    private static JToggleButton[] bornButtons = new JToggleButton[26];
    private static JToggleButton[] surviveButtons = new JToggleButton[26];

    private static JSlider ageSlider = new JSlider();
    private static JSlider worldSlider = new JSlider();
    private static JSlider colorSlider = new JSlider();
    private static JSlider leftSlider4 = new JSlider();

    private static boolean open = false;
    private static boolean rOpen = false;

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
                        rightSlider.setBounds(225 - j, 0, j, HEIGHT);
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
                    leftPanelGhost.setSize(25, HEIGHT);
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

                        rightSlider.setBounds(95 + i, 0, 155 - i, HEIGHT);
                    }
                    rightPanelGhost.setBounds(200, 0, 25, HEIGHT);
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
        rightSlider.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rightPanelGhost.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rightSlider.setBackground(Color.WHITE);
        rightPanelGhost.setBackground(Color.BLACK);

        createSliderValues();


        leftPanel.add(ageSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 10, 205, -1));
        leftPanel.add(worldSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 90, 205, -1));
        leftPanel.add(colorSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 170, 205, -1));

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

        leftBigPanel.add(leftPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, HEIGHT));
        leftBigPanel.add(leftPanelGhost, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 25, HEIGHT));

        rightLabel.setText("Organisms");
        rightLabel.setForeground(Color.BLUE);
        rightSlider.add(rightLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 268, 109, 64));
        rightBigPanel.add(rightSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 0, 0, HEIGHT));
        rightBigPanel.add(rightPanelGhost, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 25, HEIGHT));

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

        // COLOR SLIDER
        colorSlider.setMinimum(0);
        colorSlider.setMaximum(255);
        colorSlider.setValue(125);

        colorSlider.setPaintTrack(true);
        colorSlider.setMajorTickSpacing(50);
        colorSlider.setPaintLabels(true);
        colorSlider.setBorder(BorderFactory.createTitledBorder("Color Key: " + colorSlider.getValue()));

        colorSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                colorSlider.setBorder(BorderFactory.createTitledBorder("Color Key: " + colorSlider.getValue()));
            }
        });
    }

    // Initializer used to create first instances of cells
    private void init() {
        cellBox = new CellBox(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, CELL_SIZE);
        cellBox.rotate(0, 0, 60, lightVector);
//        cellBox.populateRandom();
        cellBox.createGlider();
    }

    private static void resetLife() {
        cellBox = new CellBox(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, CELL_SIZE);
        cellBox.rotate(0, 0, 60, lightVector);

        cellBox.createGlider();
    }

    private void update() {
//        cellBox.updateLife();
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
