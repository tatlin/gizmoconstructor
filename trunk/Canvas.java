import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class Canvas extends JComponent {
    private Vector objects;
    public int iters;
    private int startX, startY, endX, endY;
    private boolean dragging = false, draggingMass, pressed = false;
    private boolean isRight = false;
    private boolean shift, ctrl = false;
    private int massDragged;
    private int mouX = 0, mouY = 0;
    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        objects = new Vector();
    }
    public void iterate(double[] env) {
        if(shift && pressed && !draggingMass) {
            int i = 0;
            int min = 0;
            boolean closeEnough = false;
            for(Object o: objects) {
                if( ((PhysObject)o).dist(mouX, mouY) < 20) {
                    closeEnough = true;
                    if ( ((PhysObject)o).dist(mouX, mouY) < ((PhysObject)objects.elementAt(min)).dist(mouX, mouY)) {
                        min = i;
                    }
                }
                i++;
            }
            if(closeEnough) {
                massDragged = min;                    
                draggingMass = true;
                ((Mass)objects.elementAt(min)).selected = true;          
            }
        }                    
        for(Object o:objects) {
            PhysObject obj = (PhysObject)o;
            obj.setEnv(env);
            try {
                obj.move();
            } catch (Exception e) {}
        }
    }
    public void paintComponent(Graphics g){
        g.clearRect(0,0, getWidth(), getHeight());
        for(Object o:objects) {
            ((PhysObject)o).paintObject(g);
        }
        if(dragging && !isRight && !shift) {
            g.setColor(new Color(200, 200, 200));
            g.drawLine(startX, startY, endX, endY);
            g.drawOval(startX-3, startY-3, 6, 6);
        }
    }
    public void mouseMove(int x, int y) {
        mouX = x;
        mouY = y;
    }
    public void mousePress(int x, int y, boolean isR) {
        startX = x;
        startY = y;
        isRight = isR;
        pressed = true;
    }
    public void mouseDrag(int x, int y, boolean isR) {
        dragging = true;
        endX = x;
        endY = y;
        if(shift) {
            if(draggingMass) {
                ((Mass)objects.elementAt(massDragged)).x = endX;
                ((Mass)objects.elementAt(massDragged)).y = endY;
            }
        }
    }
    public void mouseRelease(int x, int y, boolean isR) {
        dragging = false;
        pressed = false;
        if(shift) {
            if(draggingMass) {
                ((Mass)objects.elementAt(massDragged)).selected = false;
                draggingMass = false;
            }
        } else {
            if(isRight) {
                Mass mass = new Mass(x, y);
                objects.add(mass);
            } else {
                Mass mass = new Mass(x, y, (endX-startX)*0.1, (endY-startY)*0.1);
                objects.add(mass);
            }
        }
    }
    public void keyPress(int k) {
        if(k == KeyEvent.VK_SHIFT) {
            shift = true;
        }
        if(k == KeyEvent.VK_CONTROL) {
            ctrl = true;
        }
    }
    public void keyRelease(int k) {
        if(k == KeyEvent.VK_SHIFT) {
            shift = false;
        }
        if(k == KeyEvent.VK_CONTROL) {
            ctrl = false;
        }
    }
}