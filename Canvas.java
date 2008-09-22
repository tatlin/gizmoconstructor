import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.regex.*;
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
    private final int CONSTRUCT = 0, SIMULATE = 1, FREE_MASS = 0, FIXED_MASS = 1, STAT = 0, MOVE = 1, GRAB = 2, DEL = 3, NONE = -1, RIGHTCL = 0, LEFTCL = 1, SHIFTL = 2, CTRLL = 3;
    private int mode = SIMULATE, massMode = FREE_MASS, leftcl = MOVE, rightcl = STAT, shiftl = GRAB, ctrll = DEL, mouseb = -1, mask = -1;
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
        if(!pressed) {
            mouseb = NONE;
            mask = NONE;
        } else {
            mouseb = leftcl;
            mask = LEFTCL;
            if(isRight) {
                mouseb = rightcl;
                mask = RIGHTCL;
            }
            if(shiftb) {
                mouseb = shiftl;
                mask = SHIFTL;
            }
            if(ctrlb) {
                mouseb = ctrll;
                mask = CTRLL;
            }
        }
        this.env = env;
        env[0] = gravity;
        env[1] = friction;
        //env[0] = (Math.pow(2,env[0])-1)*2;
        //env[1] = Math.pow(2,env[1])-1;
        Vector objs = (Vector)objects.clone();
        if(startY > height - 30) {
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
        } else if(mouseb == GRAB && !draggingMass) {
            int i = 0;
            int min = 0;
            boolean closeEnough = false;
            for(Object o: objs) {
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
                ((Mass)objs.elementAt(min)).selected = true;          
            }
        }  else if(mouseb == DEL && !deleted) {
            int i = 0;
            int min = 0;
            boolean closeEnough = false;
            for(Object o: objs) {
                if( ((PhysObject)o).dist(mouX, mouY) < 20) {
                    closeEnough = true;
                    if ( ((PhysObject)o).dist(mouX, mouY) < ((PhysObject)objects.elementAt(min)).dist(mouX, mouY)) {
                        min = i;
                    }
                }
                i++;
            }
            if(closeEnough) {
                objs.remove(objects.elementAt(min));
                deleted = true;
            }
        }  
        if(draggingMass) {
            ((Mass)objects.elementAt(massDragged)).x = mouX-3;
            ((Mass)objects.elementAt(massDragged)).y = mouY-3;
        }       
        for(Object o:objs) {
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
        objects = objs;
            
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
        if(mouseb == MOVE && mouX > height - 30) {
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
        g.drawString("F",265,this.height-14);
        g.drawRect(300,this.height-31, 50, 15);
        g.drawRect(300,this.height-16, 50, 15);
        g.setColor(Color.white);
        if(mode == SIMULATE) {
            g.setColor(Color.gray);
        }
        g.fillRect(301, this.height-30, 49, 14);
        g.setColor(Color.gray);
        if(massMode == FIXED_MASS) {
            g.setColor(Color.white);
        }
        g.fillRect(301, this.height-15, 49, 14);
        if(mode == SIMULATE) {
            g.setColor(Color.white);
            g.drawString("SIM", 315, this.height-18);
        } else {
            g.setColor(Color.black);
            g.drawString("CON", 312, this.height-18);
        }
        if(massMode == FIXED_MASS) {
            g.setColor(Color.black);
            g.drawString("FIX",315, this.height-3);
        } else {
            g.setColor(Color.white);
            g.drawString("FRE",315, this.height-3);
        }
        g.setColor(Color.black);
        g.drawRect(350, this.height-31, 50,15);
        g.drawRect(350, this.height-16, 50,15);
        g.drawRect(400, this.height-31, 50,15);
        g.drawRect(400, this.height-16, 50,15);
        g.setColor(Color.red);
        if(leftcl == STAT) {
            g.fillOval(351,height-30, 5, 5);
        }
        if(leftcl == MOVE) {
            g.fillOval(351,height-15, 5, 5);
        }
        if(leftcl == GRAB) {
            g.fillOval(401,height-30, 5, 5);
        }
        if(leftcl == DEL) {
            g.fillOval(401,height-15, 5, 5);
        }
        g.setColor(Color.blue);
        if(rightcl == STAT) {
            g.fillOval(395,height-30, 5, 5);
        }
        if(rightcl == MOVE) {
            g.fillOval(395,height-15, 5, 5);
        }
        if(rightcl == GRAB) {
            g.fillOval(445,height-30, 5, 5);
        }
        if(rightcl == DEL) {
            g.fillOval(445,height-15, 5, 5);
        }
        g.setColor(Color.green);
        if(shiftl == STAT) {
            g.fillOval(351,height-20, 5, 5);
        }
        if(shiftl == MOVE) {
            g.fillOval(351,height-5, 5, 5);
        }
        if(shiftl == GRAB) {
            g.fillOval(401,height-20, 5, 5);
        }
        if(shiftl == DEL) {
            g.fillOval(401,height-5, 5, 5);
        }
        g.setColor(Color.yellow);        
        if(ctrll == STAT) {
            g.fillOval(395,height-20, 5, 5);
        }
        if(ctrll == MOVE) {
            g.fillOval(395,height-5, 5, 5);
        }
        if(ctrll == GRAB) {
            g.fillOval(445,height-20, 5, 5);
        }
        if(ctrll == DEL) {
            g.fillOval(445,height-5, 5, 5);
        }
        g.setColor(Color.black);
        g.drawString("stat",364,this.height-19);
        g.drawString("move",360,this.height-3);
        g.drawString("grab",413,this.height-19);
        g.drawString("del",416, this.height-3);
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
    public boolean inRect(int x, int y, int w, int h, int mx, int my) {
        return (startX < x + w && startX > x && startY < y + h && startY > y && mx < x + w && mx > x && my < y+h && my > y);
    }
    public void mouseRelease(int x, int y, boolean isR) {
        dragging = false;
        pressed = false;
        if(startY > height - 30) {
            draggingMass = false;
            if(draggingMass) {
                ((Mass)objects.elementAt(massDragged)).selected = false;
            }
            shiftb = false;
            ctrlb = false;
            deleted = false;
            if(inRect(255,height-31,100,15,x,y)) {
                mode = 1-mode;
            }
            if(inRect(350, height-31, 50, 15, x, y)) {
                if(mask == LEFTCL) {
                    leftcl = STAT;
                } else if(mask == SHIFTL) {
                    shiftl = STAT;
                } else if(mask == CTRLL) {
                    ctrll = STAT;
                } else {
                    rightcl = STAT;
                }
            }
            if(inRect(350, height-16, 50, 15, x, y)) {
                if(mask == LEFTCL) {
                    leftcl = MOVE;
                } else if(mask == SHIFTL) {
                    shiftl = MOVE;
                } else if(mask == CTRLL) {
                    ctrll = MOVE;
                } else {
                    rightcl = MOVE;
                }
            }
            if(inRect(400, height-31, 50, 15, x, y)) {
                if(mask == LEFTCL) {
                    leftcl = GRAB;
                } else if(mask == SHIFTL) {
                    shiftl = GRAB;
                } else if(mask == CTRLL) {
                    ctrll = GRAB;
                } else {
                    rightcl = GRAB;
                }
            }
            if(inRect(400, height-16, 50, 15, x, y)) {
                if(mask == LEFTCL) {
                    leftcl = DEL;
                } else if(mask == SHIFTL) {
                    shiftl = DEL;
                } else if(mask == CTRLL) {
                    ctrll = DEL;
                } else {
                    rightcl = DEL;
                }
            }
            if(inRect(300, height-16, 50, 15, x, y)) {
                massMode = 1 - massMode;
            }
            return;
        }
        if(mouseb == GRAB) {
            if(draggingMass) {
                ((Mass)objects.elementAt(massDragged)).selected = false;
                draggingMass = false;
            }
        }
        if(mouseb == STAT) {
            if(massMode == FIXED_MASS) {
                Fmass mass = new Fmass(x,y);
                objects.add(mass);
            } else {
                Mass mass = new Mass(x, y);
                objects.add(mass);
            }           
        }
        if (mouseb == MOVE) {
            if(massMode == FIXED_MASS) {
                Fmass mass = new Fmass(x,y);
                objects.add(mass);
            } else {
                Mass mass = new Mass(x, y, (x-startX)*0.1, (y-startY)*0.1);
                objects.add(mass);
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
            objects = new Vector();
            mode = CONSTRUCT;
            massMode = FREE_MASS;
        }
        if(k == KeyEvent.VK_S) {            
            save("test");
        }
        if(k == KeyEvent.VK_L) {
            load("test");
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
    public void load(String name) {
        try {
            objects = new Vector();
            InputStream fin = new FileInputStream(name + ".xml");
            InputStreamReader in = new InputStreamReader(fin, "8859_1");    
            StringBuffer sb = new StringBuffer();
            Reader reader = new InputStreamReader(fin, "8859_1");
            int c;
            while ((c = fin.read()) != -1) sb.append((char) c);
            String s = sb.toString();
            in.close();
            Pattern info = Pattern.compile("Objects g=([0-9\\.]+) f=([0-9\\.]+)");
            Pattern mass = Pattern.compile("mass x=([0-9\\.\\-E]+) y=([0-9\\.\\-E]+) vX=([0-9\\.\\-E]+) vY=([0-9\\.\\-E]+) fixed=([falsetru]+)");
            for(String k:s.split("\n")) {
                Matcher f = info.matcher(k);
                Matcher fit = mass.matcher(k);
                if (f.find()) {
                    gravity = Double.parseDouble(f.group(1));
                    friction = Double.parseDouble(f.group(2));
                }
                if (fit.find()) {
                    int j = 0;
                    double x = 0, y = 0, vX = 0, vY = 0;
                    boolean fixed = false;
                    for (int i=0; i<=fit.groupCount(); i++) {
                        if(j == 1) {
                            x = Double.parseDouble(fit.group(i));
                        } else if(j == 2) {
                            y = Double.parseDouble(fit.group(i));
                        } else if(j == 3) {
                            vX = Double.parseDouble(fit.group(i));
                        } else if(j == 4) {
                            vY = Double.parseDouble(fit.group(i));
                        } else if(j == 5) {
                            fixed = Boolean.parseBoolean(fit.group(i));
                        }
                        j += 1;
                        if(j == 6) {
                            if(!fixed) {
                                Mass m = new Mass(x, y, vX, vY);
                                objects.add(m);
                            } else {
                                Fmass m = new Fmass(x,y);
                                objects.add(m);
                            }
                            
                            j = 0;
                        }
                        System.out.println(fit.group(i));
                    }
                }  
            }
        } catch(Exception e) {
            System.out.println("!");
            System.out.println(e.getMessage());
        }
    }
    public void save(String name) {
        try {
            OutputStream fout = new FileOutputStream(name + ".xml");
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            OutputStreamWriter out = new OutputStreamWriter(bout, "8859_1");
            out.write("<?xml version=\"1.0\" ");
            out.write("encoding=\"ISO-8859-1\"?>\r\n");
            out.write("<Objects g=" + gravity + " f=" + friction + ">\r\n"); 
            for(Object o: objects) {
                if(o instanceof Mass) {
                    out.write("  <mass x=" + ((Mass)o).x + " y=" + ((Mass)o).y + " vX=" + ((Mass)o).vX + " vY=" + ((Mass)o).vY + " fixed=" + (o instanceof Fmass) + ">" );
                    out.write("</mass>\n");
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