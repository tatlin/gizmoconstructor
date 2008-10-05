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
public class Canvas extends JComponent implements Runnable{
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
    private int iters = 0;
    private String debug = "";
    private Vector springs, masses;
    private double[] env;
    private Color lightblue = new Color(128, 128, 255);
    private int startX, startY, endX, endY;
    private boolean dragging = false, draggingMass, pressed = false;
    private boolean isRight = false;
    private boolean shift, ctrl, shiftb = false, ctrlb = false;
    private boolean deleted = false;
    private int massDragged;
    private int mouX = 0, mouY = 0;
    private final int CONSTRUCT = 0, SIMULATE = 1, FREE_MASS = 0, FIXED_MASS = 1, STAT = 0, MOVE = 1, GRAB = 2, DEL = 3, NONE = -1, RIGHTCL = 0, LEFTCL = 1, SHIFTL = 2, CTRLL = 3;
    private int mode = SIMULATE, massMode = FREE_MASS, leftcl = STAT, rightcl = MOVE, shiftl = GRAB, ctrll = DEL, mouseb = -1, mask = -1;
    private int key = 0;
    private int width, height;
    private double gravity=0.3, friction = 0.0;
    private MySelector stat = new MySelector(350,469,50,15,0, "stat",0);
    private MySelector move = new MySelector(350,484,50,15,2, "move",1);
    private MySelector grab = new MySelector(400,469,50,15,1, "grab",2);
    private MySelector del  = new MySelector(400,484,50,15,3, "del",3);
    private MyToggleable modetoggle = new MyToggleable(300, 469, 50, 15, "SIM", "CON");
    private MyToggleable masstoggle = new MyToggleable(300, 484, 50, 15, "FREE", "FIX");
    private MySlider grav = new MySlider(5,474,25,100,30, "G");
    private MySlider fric = new MySlider(120,474,25,100,0, "F");
    private MyButton save = new MyButton(240,469,50,15, "SAVE");
    private MyButton load = new MyButton(240,484,50,15, "LOAD");
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
        masses = new Vector();
        springs = new Vector();
        Mass m = new Mass(40,40, 10,10);
        Mass n = new Mass(70,70,15,15);
        Spring s = new Spring(m,n);
        springs.add(s);
        masses.add(m);
        masses.add(n);
    }
    /**
     * Iterate the physmasses in the masses vector
     * 
     * @param   env     The environmental variables - gravity, friction, height, width
     */
    public void setEnv(double [] env) {
        this.env = env;
    }
    public void run() {
        this.iterate(env);
    }
    public void iterate(double[] env) {
        //iters++;
        //System.out.println(iters + "!");
        if(modetoggle.getState() == modetoggle.STATE_A) {
            mode = SIMULATE;
        } else {
            mode = CONSTRUCT;
        }
        if(masstoggle.getState() == masstoggle.STATE_A) {
            massMode = FREE_MASS;
        } else {
            massMode = FIXED_MASS;
        }
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
        env[0] = (Math.pow(2,env[0])-1)*4;
        //env[1] = Math.pow(2,env[1])-1;
        Vector mas = (Vector)masses.clone();
        Vector sps = (Vector)springs.clone();
        gravity = (double)(grav.getValue())/100;
        friction = (double)(fric.getValue())/100;
        if(startY > height - 30) {
        } else if(mouseb == GRAB && !draggingMass) {
            int i = 0;
            int min = 0;
            boolean closeEnough = false;
            for(Object o: mas) {
                if( ((PhysObject)o).dist(mouX, mouY) < 20) {
                    closeEnough = true;
                    if ( ((PhysObject)o).dist(mouX, mouY) < ((PhysObject)masses.elementAt(min)).dist(mouX, mouY)) {
                        min = i;
                    }
                }
                i++;
            }
            if(closeEnough) {
                massDragged = min;                    
                draggingMass = true;
                ((Mass)mas.elementAt(min)).selected = true;          
            }
        }  else if(mouseb == DEL && !deleted) {
            int i = 0;
            int min = 0;
            boolean closeEnough = false;
            for(Object o: mas) {
                if( ((PhysObject)o).dist(mouX, mouY) < 20) {
                    closeEnough = true;
                    if ( ((PhysObject)o).dist(mouX, mouY) < ((PhysObject)masses.elementAt(min)).dist(mouX, mouY)) {
                        min = i;
                    }
                }
                i++;
            }
            if(closeEnough) {
                mas.remove(masses.elementAt(min));
                deleted = true;
            }
        }  
        if(draggingMass) {
            ((Mass)masses.elementAt(massDragged)).x = mouX-3;
            ((Mass)masses.elementAt(massDragged)).y = mouY-3;
        }      
        for(Object o:sps) {
            ((PhysObject)o).move();
        }
        for(Object o:mas) {
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
        masses = mas;
            
    }
    /**
     * Paints the canvas and all of the masses on it
     * 
     * @param   g       A graphics object
     */
    public void paintComponent(Graphics g){
        //iterate(env);
        for(Object o:masses) {
            ((PhysObject)o).paintObject(g);
        }
        for(Object o:springs) {
            ((PhysObject)o).paintObject(g);
        }
        if(mouseb == MOVE && mouY < height - 30) {
            g.setColor(new Color(200, 200, 200));
            g.drawLine(startX, startY, mouX, mouY);
            g.drawOval(startX-3, startY-3, 6, 6);
            g.drawOval(mouX-3, mouY-3, 6, 6);
        }
        g.setColor(Color.black);
        g.drawRect(0,0, this.width-1, this.height-31);
        masstoggle.paintComponent(g);
        modetoggle.paintComponent(g);
        stat.paintComponent(g);
        move.paintComponent(g);
        grab.paintComponent(g);
        del.paintComponent(g);
        grav.paintComponent(g);
        fric.paintComponent(g);
        save.paintComponent(g);
        load.paintComponent(g);
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
        save.mouseMove(x,y);
        load.mouseMove(x,y);
    }
    public void mousePress(int x, int y, boolean isR) {   
        modetoggle.mousePressed(x, y);
        masstoggle.mousePressed(x, y);
        stat.mousePress(x,y);
        move.mousePress(x,y);
        grab.mousePress(x,y);
        del.mousePress(x,y);
        grav.mousePress(x,y);
        fric.mousePress(x,y);
        save.mousePress(x,y);
        load.mousePress(x,y);
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
        grav.mouseDrag(x,y);
        fric.mouseDrag(x,y);
        dragging = true;
        mouX = x;
        mouY = y;
    }
    public boolean inRect(int x, int y, int w, int h, int mx, int my) {
        return (startX < x + w && startX > x && startY < y + h && startY > y && mx < x + w && mx > x && my < y+h && my > y);
    }
    public void mouseRelease(int x, int y, boolean isR) {   
        modetoggle.mouseReleased(x,y);
        masstoggle.mouseReleased(x,y);
        if(save.getPressed()) {
            saveF();
        }
        save.mouseRelease(x,y);
        if(load.getPressed()) { 
            loadF();
        }
        load.mouseRelease(x,y);
        int[] sofar = new int[4];
        if((stat.inRect(x,y) || move.inRect(x,y)) || grab.inRect(x,y) || del.inRect(x,y)) {
            stat.mouseRelease(x,y,mask);
            sofar = stat.getButtons(sofar);
            move.mouseRelease(x,y,mask);
            sofar = move.getButtons(sofar);
            grab.mouseRelease(x,y,mask);
            sofar = grab.getButtons(sofar);
            del.mouseRelease(x,y,mask);
            sofar = del.getButtons(sofar);
            leftcl = sofar[0];
            rightcl = sofar[1];
            shiftl = sofar[2];
            ctrll = sofar[3];
            return;
        }
        dragging = false;
        pressed = false;
        if(startY > height - 30) {
            draggingMass = false;
            if(draggingMass) {
                ((Mass)masses.elementAt(massDragged)).selected = false;
            }
            shiftb = false;
            ctrlb = false;
            deleted = false;
            return;
        }
        if(mouseb == GRAB) {
            if(draggingMass) {
                ((Mass)masses.elementAt(massDragged)).selected = false;
                draggingMass = false;
            }
        }
        if(mouseb == STAT) {
            if(massMode == FIXED_MASS) {
                Fmass mass = new Fmass(x,y);
                masses.add(mass);
            } else {
                Mass mass = new Mass(x, y);
                masses.add(mass);
            }           
        }
        if (mouseb == MOVE) {
            if(massMode == FIXED_MASS) {
                Fmass mass = new Fmass(x,y);
                masses.add(mass);
            } else {
                Mass mass = new Mass(x, y, (x-startX)*0.1, (y-startY)*0.1);
                masses.add(mass);
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
            masses = new Vector();
            mode = CONSTRUCT;
            massMode = FREE_MASS;
        }
        if(k == KeyEvent.VK_S) {  
            saveF();
        }
        if(k == KeyEvent.VK_L) {
            loadF();
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
    public void loadF() {
        boolean tempm = modetoggle.getState();
        if(!modetoggle.getState()) {
            modetoggle.toggle();
        }
        Frame f = new Frame();
        FileDialog fd = new FileDialog(f, "Load a file...", FileDialog.LOAD);
        fd.setVisible(true);
        if(fd.getFile() != null) {
            loadFile(fd.getDirectory() + fd.getFile());
        }
        if(!tempm) {
            modetoggle.toggle();
        }
    }        
    public void loadFile(String name) {
        try {
            masses = new Vector();
            InputStream fin = new FileInputStream(name);
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
                                masses.add(m);
                            } else {
                                Fmass m = new Fmass(x,y);
                                masses.add(m);
                            }
                            
                            j = 0;
                        }
                    }
                }  
            }
        } catch(Exception e) {
            System.out.println("!");
            System.out.println(e.getMessage());
        }
    }
    public void saveF() {
        boolean tempm = modetoggle.getState();
        if(!modetoggle.getState()) {
            modetoggle.toggle();
        }
        Frame f = new Frame();
        FileDialog fd = new FileDialog(f, "Save a file...", FileDialog.SAVE);
        fd.setVisible(true);
        if(fd.getFile() != null) {
            saveFile(fd.getDirectory() + fd.getFile());
        }
        if(!tempm) {
            modetoggle.toggle();
        }
    }
    public void saveFile(String name) {
        try {
            OutputStream fout = new FileOutputStream(name);
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            OutputStreamWriter out = new OutputStreamWriter(bout, "8859_1");
            out.write("<?xml version=\"1.0\" ");
            out.write("encoding=\"ISO-8859-1\"?>\r\n");
            out.write("<Objects g=" + gravity + " f=" + friction + ">\r\n"); 
            for(Object o: masses) {
                if(o instanceof Mass) {
                    out.write("  <mass x=" + ((Mass)o).getX() + " y=" + ((Mass)o).getY() + " vX=" + ((Mass)o).getVX() + " vY=" + ((Mass)o).getVY() + " fixed=" + (o instanceof Fmass) + ">" );
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