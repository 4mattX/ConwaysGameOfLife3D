package renderer;// Display Class is responsible for all things related with window display
// This also includes any User Controller(s) involved

import renderer.rendering.shaders.LightVector;
import renderer.shapes.CellBox;
import renderer.shapes.CellCube;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class Display extends Canvas implements Runnable{

    public Thread thread; // Rendering is ran on separate single thread
    private JFrame frame;
    private static String title = "Conway's Game of Life 3D";

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;
    private static final double NANO_SECOND = 1000000000.0 / 60;
    private static final double SECOND = 1000;

    private static CellBox cellBox;
    private LightVector lightVector = LightVector.normalize(new LightVector(1, 1, 1));

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

        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = NANO_SECOND;
        double delta = 0;
        int frames = 0;

        // Initializes objects in display
        init();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {

                update();
                delta--;
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

    // Initializer used to create first instances of cells
    private void init() {
        cellBox = new CellBox(15, 15, 15, 10);
    }


    private void update() {
        cellBox.rotate(3, 1, 2, lightVector);
    }

    public static void main(String[] args) {
        Display display = new Display();

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
