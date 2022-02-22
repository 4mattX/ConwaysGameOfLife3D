package renderer;// Display Class is responsible for all things related with window display
// This also includes any User Controller(s) involved

import renderer.rendering.shaders.LightVector;
import renderer.shapes.CellBox;
import renderer.shapes.CellCube;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Display extends Canvas implements Runnable{

    public static Thread thread; // Rendering is ran on separate single thread
    private JFrame frame;
    private static String title = "Conway's Game of Life 3D";
    private static JPanel rightBigPanel = new JPanel();
    private static JPanel bigPanel = new JPanel();
    private static JPanel leftSlider = new JPanel();
    private static JPanel rightSlider = new JPanel();
    private static JPanel leftSliderGhost = new JPanel();
    private static JPanel rightSliderGhost = new JPanel();
    private static JLabel leftLabel1 = new JLabel();
    private static JLabel rightLabel = new JLabel();

    private static JSlider jSlider1 = new JSlider();
    private static JSlider jSlider2 = new JSlider();
    private static JSlider jSlider3 = new JSlider();
    private static JSlider jSlider4 = new JSlider();

    private static boolean open = false;
    private static boolean rOpen = false;

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;
    private static final double NANO_SECOND = 1000000000.0 / 60;
    private static final double SECOND = 1000;

    private static CellBox cellBox;
    private LightVector lightVector = LightVector.normalize(new LightVector(-1, 1, -1));

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

        Thread th = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    for(int j = 0; j < 155; j++)
                    {
                        Thread.sleep(1);
                        leftSlider.setSize(j, HEIGHT);
                    }
                    leftSliderGhost.setSize(160, HEIGHT);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };th.start();

    }

    private static void rightSliderMouseEnter(MouseEvent event) {

        if (rOpen) {
            return;
        }

        Thread th = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    for(int j = 0; j < 155; j++)
                    {
                        Thread.sleep(1);
                        rightSlider.setBounds(225-j, 0, j, HEIGHT);
                    }
                    rightSliderGhost.setBounds(65, 0, 160, HEIGHT);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };th.start();

    }

    private static void leftSliderMouseExit(MouseEvent event) {

        Thread th = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    for(int i = 155; i > 0; i--)
                    {
                        Thread.sleep(1);
                        leftSlider.setSize(i, HEIGHT);
                    }
                    leftSliderGhost.setSize(25, HEIGHT);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };th.start();

    }

    private static void rightSliderMouseExit(MouseEvent event) {
        Thread th = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    for(int i = 0; i < 155; i++)
                    {
                        Thread.sleep(1);

                        rightSlider.setBounds(95+i, 0, 155-i, HEIGHT);
                    }
                    rightSliderGhost.setBounds(200, 0, 25, HEIGHT);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };th.start();

    }

    private static void mouseEnterMiddle(MouseEvent event) {
        System.out.println("HERE");

        Thread th = new Thread()
        {
            @Override
            public void run()
            {
                try {
                    for(int i = 155; i > 0; i--)
                    {
                        Thread.sleep(1);
                        leftSlider.setSize(i, 590);
                    }
                    leftSliderGhost.setSize(25, HEIGHT);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        };th.start();
    }

    private static void initComponents(Display display) {

        // BIG PANEL
        bigPanel.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        bigPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        bigPanel.setPreferredSize(new Dimension(WIDTH / 4, HEIGHT));
        bigPanel.setBackground(new Color(0, 0, 0));

        bigPanel.addMouseListener(new MouseAdapter() {
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
                if (rOpen){
                    rightSliderMouseExit(event);
                }
                rOpen = false;
            }
        });

        // LEFT PANEL
        leftSliderGhost.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                leftSliderMouseEnter(event);
                open = true;
            }
            public void mouseExited(MouseEvent event) {
                //leftSliderMouseExit(event);
            }
        });

        //RIGHT PANEL
        rightSliderGhost.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                rightSliderMouseEnter(event);
                rOpen = true;
            }
            public void mouseExited(MouseEvent event) {
//                rightSliderMouseExit(event);
            }
        });


        leftSlider.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        leftSliderGhost.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        leftSlider.setBackground(Color.WHITE);
        leftSliderGhost.setBackground(Color.BLACK);
        rightSlider.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rightSliderGhost.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        rightSlider.setBackground(Color.WHITE);
        rightSliderGhost.setBackground(Color.BLACK);

        leftSlider.add(jSlider1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 40, 145, -1));
        leftSlider.add(jSlider2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 70, 145, -1));
        leftSlider.add(jSlider3, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 100, 145, -1));
        leftSlider.add(jSlider4, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 130, 145, -1));

        leftLabel1.setText("Rules");
        leftLabel1.setForeground(Color.RED);
        leftSlider.add(leftLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 268, 109, 64));
        bigPanel.add(leftSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, HEIGHT));
        bigPanel.add(leftSliderGhost, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 25, HEIGHT));

        rightLabel.setText("Organisms");
        rightLabel.setForeground(Color.BLUE);
        rightSlider.add(rightLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 268, 109, 64));
        rightBigPanel.add(rightSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 0, 0, HEIGHT));
        rightBigPanel.add(rightSliderGhost, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 25, HEIGHT));

        display.frame.add(rightBigPanel);
        display.frame.add(bigPanel);
        display.frame.pack();

    }

    // Initializer used to create first instances of cells
    private void init() {
//        initComponents();
        cellBox = new CellBox(51, 51, 51, 7);
        cellBox.rotate(0, 0, 60, lightVector);
        cellBox.populateRandom();
//        cellBox.createGlider();
//        cellBox.populateCenter();
//        cellBox.updateLife();

    }


    private void update() {
//        cellBox.rotate(0, 0, 2, lightVector);
//        cellBox.testAnimation();
        cellBox.updateLife();

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
