import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class Simulator extends JApplet implements Runnable{
    private Thread simthread;
    private final int CONSTRUCT = 0, SIMULATE = 1, FREE_MASS = 0;
    private int mode = CONSTRUCT, massMode = FREE_MASS;
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
            canvas.repaint();
            try {
                simthread.sleep(50);
            } catch(InterruptedException ie) {System.out.println("!!");}
        }
    }
}    