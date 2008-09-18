import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
/**
 * Simulator applet inspired by Ed Burton's sodaconstructor
 * @author Colin Stanfill
 * @version 3.1.4
 */
public class Simulator extends  JApplet implements  Runnable, MouseListener, MouseMotionListener, KeyListener{
    /**
     * Class fields defined in Simulator:
     * simthread = refresh thread
     * iters = frame count
     * canvas = drawing surface for everything
     * buff and buffG = buffered graphics variables
     */
    private Thread simthread;
    private int iters = 0;
    private Canvas canvas;
    private Graphics buffG;
    private Image buff;
    /**
     * Initialize the applet.
     */
    public void init() {
        canvas = new Canvas(getWidth(), getHeight());
        getContentPane().add(canvas);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        buff = createImage(getWidth(), getHeight());
        buffG = buff.getGraphics();
    }
    /**
     * Start the refresh thread.
     */
    public void start() {
        simthread = new Thread(this);
        simthread.start();
    }
    /**
     * Iterate the physics, call repaint().
     */
    public void run() {
        while(true) {
            repaint();
            double[] env = new double[4]; //env = {gravity, friction, height, width}
            env[0] = 0.3;
            env[1] = 0.0;
            env[2] = getHeight();
            env[3] = getWidth();
            canvas.iterate(env);
            canvas.iters = this.iters;
            repaint();
            try {
                simthread.sleep(10);
                this.iters++;
            } catch(InterruptedException ie) {System.out.println("!!");}
        }
    }
    /**
     * Paint the drawing surface on the applet.
     * @param   g   A graphics object
     */
    public void paint(Graphics g) {
        buffG.clearRect(0,0,getWidth(), getHeight());
        canvas.paint(buffG);
        g.drawImage(buff,0,0,this);
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
}    