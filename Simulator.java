import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
/**
 * Simulator applet inspired by Ed Burton's sodaconstructor
 * @author Colin Stanfill
 * @version 3.1.4
 */
public class Simulator extends JPanel implements  Runnable, MouseListener, MouseMotionListener, KeyListener{
    private double[] env = new double[5];
    /**
     * Class fields defined in Simulator:
     * simthread = refresh thread
     * iters = frame count
     * canvas = drawing surface for everything
     * buff and buffG = buffered graphics variables
     */
    private Thread simthread, canvasthread;
    private int iters = 0;
    private Canvas canvas;
    private Graphics2D buffG;
    private BufferedImage buff;
    private double lastTime = 0;
    private int sinceReset = 0;
    private double fps = 0;
    private boolean dispFPS = false;
    /**
     * Initialize the applet.
     */
    public Simulator() {   
        setFocusable(true);
        canvas = new Canvas(700, 500);
        add(canvas);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        simthread = new Thread(this);
        canvasthread = new Thread(canvas);
        simthread.start();
        this.setBackground(Color.white);
        lastTime = System.currentTimeMillis();
    }
    /**
     * Start the refresh thread.
     */
    /**
     * Iterate the physics, call repaint().
     */
    public void run() {
        while(true) {
            canvas.setEnv(env);
            repaint();
            env[2] = getHeight();
            env[3] = getWidth();
            try {
                simthread.sleep(10);
                canvasthread.run();
                canvasthread.join();
                
                //this.iters++;
            } catch(Exception ie) {System.out.println(ie.getMessage() + "!!!");}
            //System.out.println(iters + "!!");
        }
    }
    /**
     * Paint the drawing surface on the applet.
     * @param   g   A graphics object
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        sinceReset++;
        if(dispFPS) {
            g.drawString(""+fps,50,50);
        }
        if(sinceReset == 50) {
            fps = ((int)(sinceReset*100000/(System.currentTimeMillis()-lastTime)))/100.0;
            lastTime = System.currentTimeMillis();
            sinceReset = 0;
        }
    }
    /**
     * Update the graphics by just painting
     * @param   g   A graphics object
     */
    public void update(Graphics g) {
        paint(g);
    }
    /**
     * Does nothing
     * @param   me  A mouse event.
     */
    public void mouseExited(MouseEvent me) {}
    /**
     * Does nothing
     * @param   me  A mouse event.
     */
    public void mouseEntered(MouseEvent me) {}
    /**
     * Wrapper for the canvas' mousePress method
     * @param   me  A mouse event.
     */
    public void mousePressed(MouseEvent me) {
        canvas.mousePress(me.getX(), me.getY(), (me.getButton() == me.BUTTON3));
    }
    /**
     * Wrapper for the canvas' mouseRelease method
     * @param   me  A mouse event.
     */
    public void mouseReleased(MouseEvent me) {
        canvas.mouseRelease(me.getX(), me.getY(), (me.getButton() == me.BUTTON3));
    }
    /**
     * Does nothing
     * @param   me  A mouse event.
     */
    public void mouseClicked(MouseEvent me) {}
    
    /**
     * Wrapper for the canvas' mouseMoved method
     * @param   me  A mouse motion event.
     */
    public void mouseMoved(MouseEvent me) {
        canvas.mouseMove(me.getX(), me.getY());
    }
    /**
     * Wrapper for the canvas' mouseDragged method
     * @param   me  A mouse motion event.
     */
    public void mouseDragged(MouseEvent me) {
        canvas.mouseDrag(me.getX(), me.getY(), (me.getButton() == me.BUTTON3));
    }
    /**
     * Wrapper for the canvas' keyPress method
     * @param   ke  A keyboard event.
     */
    public void keyPressed(KeyEvent ke) {
        canvas.keyPress(ke.getKeyCode());
    }
    /**
     * Wrapper for the canvas' keyRelease method
     * @param   ke  A keyboard event.
     */
    public void keyReleased(KeyEvent ke) {
        canvas.keyRelease(ke.getKeyCode());
    }
    /**
     * Wrapper for the canvas' keyType method
     * @param   ke  A keyboard event.
     */
    public void keyTyped(KeyEvent ke) {
        canvas.keyType(ke.getKeyCode());
    }    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Gizmoconstructor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Simulator c = new Simulator();
        c.setOpaque(true); 
        frame.setContentPane(c);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}