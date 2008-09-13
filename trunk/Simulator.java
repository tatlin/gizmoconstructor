import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

public class Simulator extends JApplet implements Runnable, MouseListener, MouseMotionListener, KeyListener{
    private Thread simthread;
    private final int CONSTRUCT = 0, SIMULATE = 1, FREE_MASS = 0;
    private int mode = CONSTRUCT, massMode = FREE_MASS;
    private int iters = 0;
    private Canvas canvas;
    private Graphics buffG;
    private Image buff;
    public void init() {
        canvas = new Canvas(getWidth(), getHeight());
        getContentPane().add(canvas);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        buff = createImage(getWidth(), getHeight());
        buffG = buff.getGraphics();
    }
    public void start() {
        simthread = new Thread(this);
        simthread.start();
    }
    public void run() {
        while(true) {
            repaint();
            double[] env = new double[4];
            env[0] = Math.pow(2,0.3)-1;
            env[1] = Math.pow(2,0.0)-1;
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
    public void paint(Graphics g) {
        buffG.clearRect(0,0,getWidth(), getHeight());
        canvas.paint(buffG);
        g.drawImage(buff,0,0,this);
    }
    public void update(Graphics g) {
        paint(g);
    }
    public void mouseExited(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mousePressed(MouseEvent me) {
        canvas.mousePress(me.getX(), me.getY(), (me.getButton() == me.BUTTON3));
    }
    public void mouseReleased(MouseEvent me) {
        canvas.mouseRelease(me.getX(), me.getY(), (me.getButton() == me.BUTTON3));
    }
    public void mouseClicked(MouseEvent me) {}
    public void mouseMoved(MouseEvent me) {
        canvas.mouseMove(me.getX(), me.getY());
    }
    public void mouseDragged(MouseEvent me) {
        canvas.mouseDrag(me.getX(), me.getY(), (me.getButton() == me.BUTTON3));
    }
    public void keyPressed(KeyEvent ke) {
        canvas.keyPress(ke.getKeyCode());
    }
    public void keyReleased(KeyEvent ke) {
        canvas.keyRelease(ke.getKeyCode());
    }
    public void keyTyped(KeyEvent ke) {}
}    