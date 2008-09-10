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
    private boolean shift, ctrl, shiftb = false;
    private int massDragged;
    private int mouX = 0, mouY = 0;
    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        objects = new Vector();
    }
    public void iterate(double[] env) {
        if(shiftb && pressed && !draggingMass) {
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
        if(draggingMass) {
            ((Mass)objects.elementAt(massDragged)).x = mouX-3;
            ((Mass)objects.elementAt(massDragged)).y = mouY-3;
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
        for(Object o:objects) {
            ((PhysObject)o).paintObject(g);
        }
        if(dragging && !isRight && !shiftb) {
            g.setColor(new Color(200, 200, 200));
            g.drawLine(startX, startY, mouX, mouY);
            g.drawOval(startX-3, startY-3, 6, 6);
            g.drawOval(mouX-3, mouY-3, 6, 6);
        }
    }
    public void mouseMove(int x, int y) {
        if(pressed) {
            dragging = true;
        }
        mouX = x;
        mouY = y;
    }
    public void mousePress(int x, int y, boolean isR) {
        startX = x;
        startY = y;
        isRight = isR;
        pressed = true;
        if(shift) {
            shiftb = true;
        }
    }
    public void mouseDrag(int x, int y, boolean isR) {
        dragging = true;
        mouX = x;
        mouY = y;
    }
    public void mouseRelease(int x, int y, boolean isR) {
        dragging = false;
        pressed = false;
        if(shiftb) {
            if(draggingMass) {
                ((Mass)objects.elementAt(massDragged)).selected = false;
                draggingMass = false;
            }
        } else {
            if(isRight) {
                Mass mass = new Mass(x, y);
                objects.add(mass);
            } else {
                Mass mass = new Mass(x, y, (x-startX)*0.1, (y-startY)*0.1);
                objects.add(mass);
            }
        }
        shiftb = false;
    }
    public void keyPress(int k) {
        if(k == KeyEvent.VK_SHIFT) {
            shift = true;
            if(pressed) {
                shiftb = true;
            }
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