import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class Canvas extends JComponent {
    private Vector objects;
    public int iters;
    private int startX, startY, endX, endY;
    private boolean dragging = false, draggingMass;
    private boolean isRight = false;
    private boolean shift, ctrl = false;
    private int massDragged;
    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        objects = new Vector();
    }
    public void iterate(double[] env) {
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
    public void mouseClick(int x, int y, boolean isR) {
    }
    public void mousePress(int x, int y, boolean isR) {
        if(shift) {
            int i = 0;
            for(Object o: objects) {
                if( ((PhysObject)o).isOver(x, y)) {
                    massDragged = i;                    
                    draggingMass = true;
                    ((Mass)o).selected = true;
                    break;
                }
                i++;
            }
        }                    
        startX = x;
        startY = y;
        isRight = isR;
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