import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
/**
 * Canvas - a drawing surface for Simulator
 * 
 * @author Colin Stanfill
 */
public class Canvas extends JComponent {
    /**
     * Class fields defined in Canvas
     * 
     * objects = a Vector with all the PhysObjects created so far
     * iters = frame count
     * startX, startY = start position of a moving mouse
     * endX, endY = end position of a moving mouse
     * dragging = if the mouse is dragging as opposed to moving
     * draggingMass = if the user is dragging a mass around
     * pressed = if a mouse button is pressed
     * isRight = which mouse button is pressed
     * shift, ctrl = if shift/control are pressed
     * shiftb = if shift was pressed at the beginning of the mouse dragging event
     * massDragged = id # of mass being dragged
     * mouX, mouY = mouse X, mouse Y coordinates. Not called mouseX because some overzealous censors change it to mou***
     * CONSTRUCT, SIMULATE, etc. = final ids representing different modes
     * mode, massMode = current mode and what kind of mass are being made
     * key = what key was most recently pressed
     */
    private String debug = "";
    private Vector objects;
    private double[] env;
    public int iters;
    private int startX, startY, endX, endY;
    private boolean dragging = false, draggingMass, pressed = false;
    private boolean isRight = false;
    private boolean shift, ctrl, shiftb = false, ctrlb = false;
    private boolean deleted = false;
    private int massDragged;
    private int mouX = 0, mouY = 0;
    private final int CONSTRUCT = 0, SIMULATE = 1, FREE_MASS = 0, FIXED_MASS = 1;
    private int mode = SIMULATE, massMode = FREE_MASS;
    private int key = 0;
    private int width, height;
    private double gravity=0.3, friction = 0.0;
    /**
     * Makes a new Canvas with given height and width
     * 
     * @param   width   The width of the new Canvas
     * @param   height  The height of the new Canvas
     */
    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        objects = new Vector();
    }
    /**
     * Iterate the physobjects in the objects vector
     * 
     * @param   env     The environmental variables - gravity, friction, height, width
     */
    public void iterate(double[] env) {
        this.env = env;
        env[0] = gravity;
        env[1] = friction;
        env[0] = (Math.pow(2,env[0])-1)*2;
        env[1] = Math.pow(2,env[1])-1;
        if(startY > env[2] - 30) {
            if(dragging) {
                if(startX >= 5 && startX <= 105) {
                    gravity = ((double)mouX-5)/100;
                    if(gravity > 1) {
                        gravity = 1;
                    }
                    if(gravity < 0) {
                        gravity = 0;
                    }
                }
                if(startX >= 155 && startX <= 255) {
                    friction = ((double)mouX-155)/100;
                    if(friction > 1) {
                        friction = 1;
                    }
                    if(friction < 0) {
                        friction = 0;
                    }
                }
            }
        } else if(shiftb && pressed && !draggingMass) {
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
        }  else if(ctrlb && pressed && !deleted) {
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
                objects.remove(objects.elementAt(min));
                deleted = true;
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
                if(obj instanceof Mass) {
                    if(mode==SIMULATE || ((Mass)obj).selected) {
                        obj.move();
                    }
                } else if(obj instanceof Fmass) {
                    obj.move();
                }
            } catch (Exception e) {}
        }
            
    }
    /**
     * Paints the canvas and all of the objects on it
     * 
     * @param   g       A graphics object
     */
    public void paintComponent(Graphics g){
        iterate(env);
        for(Object o:objects) {
            ((PhysObject)o).paintObject(g);
        }
        if(dragging && !isRight && !shiftb && !ctrlb && (mouY < env[2]-30) && (startY < env[2]-30)) {
            g.setColor(new Color(200, 200, 200));
            g.drawLine(startX, startY, mouX, mouY);
            g.drawOval(startX-3, startY-3, 6, 6);
            g.drawOval(mouX-3, mouY-3, 6, 6);
        }
        g.setColor(Color.black);
        g.drawRect(0,0, this.width-1, this.height-31);
        g.drawRect(5,this.height-26,100,25);
        g.setColor(Color.blue);
        g.drawLine(5,this.height-13,(int)(100*gravity)+5, this.height-13);
        g.setColor(Color.red);        
        g.drawLine(105,this.height-13,(int)(100*gravity)+5, this.height-13);
        g.setColor(Color.black);
        g.drawLine((int)(100*gravity)+5, this.height-26,(int)(100*gravity)+5, this.height-1);
        g.drawRect(155,this.height-26,100,25);
        g.setColor(Color.blue);
        g.drawLine(155,this.height-13,(int)(100*friction)+155, this.height-13);
        g.setColor(Color.red);
        g.drawLine(255,this.height-13,(int)(100*friction)+155, this.height-13);
        g.setColor(Color.black);
        g.drawLine((int)(100*friction)+155, this.height-26,(int)(100*friction)+155, this.height-1);
        g.drawString("G",110,this.height-14);
    }
    /**
     * React to the mouse being moved
     * 
     * @param   x       The x coordinate of the mouse
     * @param   y       The y coordinate of the mouse
     */
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
        if(ctrl) {
            ctrlb = true;
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
        if(mouY > env[2] - 30) {
            draggingMass = false;
            if(draggingMass) {
                ((Mass)objects.elementAt(massDragged)).selected = false;
            }
            shiftb = false;
            ctrlb = false;
            deleted = false;
            return;
        }
        if(shiftb) {
            if(draggingMass) {
                ((Mass)objects.elementAt(massDragged)).selected = false;
                draggingMass = false;
            }
        } else if (!ctrlb) {
            if(isRight) {
                if(massMode == FIXED_MASS) {
                    Fmass mass = new Fmass(x,y);
                    objects.add(mass);
                } else {
                    Mass mass = new Mass(x, y);
                    objects.add(mass);
                }
            } else {
                if(massMode == FIXED_MASS) {
                    Fmass mass = new Fmass(x,y);
                    objects.add(mass);
                } else {
                    Mass mass = new Mass(x, y, (x-startX)*0.1, (y-startY)*0.1);
                    objects.add(mass);
                }
            }
        }
        shiftb = false;
        ctrlb = false;
        deleted = false;
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
            if(pressed) {
                ctrlb = true;
            }
        }
    }
    public void keyRelease(int k) {
        key = k;
        if(k == KeyEvent.VK_SHIFT) {
            shift = false;
        }
        if(k == KeyEvent.VK_CONTROL) {
            ctrl = false;
            deleted = false;
        }
        if(k == KeyEvent.VK_F) {
            massMode = FREE_MASS;
        }
        if(k == KeyEvent.VK_D) {
            massMode = FIXED_MASS;
        }
        if(k == KeyEvent.VK_SPACE) {
            mode = 1-mode;
        }
        if(k == KeyEvent.VK_C) {
            save("test");
            objects = new Vector();
            mode = CONSTRUCT;
            massMode = FREE_MASS;
        }
    }
    public void keyType(int k) {
        if(k == KeyEvent.VK_F) {
            if(massMode == FIXED_MASS) {
                massMode = FREE_MASS; 
            } else {
                massMode = FIXED_MASS;
            }
        }
    }
    public void save(String name) {
        try {
            OutputStream fout = new FileOutputStream("test.xml");
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            OutputStreamWriter out = new OutputStreamWriter(bout, "8859_1");
            out.write("<?xml version=\"1.0\" ");
            out.write("encoding=\"ISO-8859-1\"?>\r\n");
            out.write("<Objects>\r\n"); 
            for(Object o: objects) {
                if(o instanceof Mass) {
                    out.write("  <mass x=\"" + ((Mass)o).x + "\" y=\"" + ((Mass)o).y + "\" vX=\"" + ((Mass)o).vX + "\" vY=\"" + ((Mass)o).vY + "\" fixed=\"" + (o instanceof Fmass) + "\">" );
                    out.write("</mass>");
                }
            }
            out.write("</Objects>\r\n"); 
            out.flush();
            out.close();      
        } catch (Exception e) {
            debug = e.getMessage();
        }
        
    }
}