import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class Simulator extends JApplet implements Runnable{
    private Thread simthread;
    private final int CONSTRUCT = 0, SIMULATE = 1, FREE_MASS = 0;
    private int mode = CONSTRUCT, massMode = FREE_MASS;
    private int iters = 0;
    private Canvas canvas;
    public void init() {
        canvas = new Canvas(getWidth(), getHeight());
        getContentPane().add(canvas);
    }
    public void start() {
        simthread = new Thread(this);
        simthread.start();
    }
    public void run() {
        while(true) {
            repaint();
            double[] env = new double[2];
            env[0] = 0.75;
            env[1] = getHeight();
            canvas.iterate(env);
            canvas.iters = this.iters;
            canvas.repaint();
            try {
                simthread.sleep(10);
                this.iters++;
            } catch(InterruptedException ie) {System.out.println("!!");}
        }
    }
        
}    