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
    private Vector springs, masses, objects;
    private int startX, startY, endX, endY;
    private boolean dragging = false, draggingMass, pressed = false, selectedMass = false;
    private boolean isRight = false;
    private boolean shift, ctrl, shiftb = false, ctrlb = false;
    private boolean deleted = false;
    private int massDragged = 0, massSelected = 0;
    private int mouX = 0, mouY = 0;
    private final int CONSTRUCT = 0, SIMULATE = 1, FREE_MASS = 0, FIXED_MASS = 1, STAT = 0, MOVE = 1, GRAB = 2, DEL = 3, NONE = -1, RIGHTCL = 0, LEFTCL = 1, SHIFTL = 2, CTRLL = 3;
    private int mode = SIMULATE, massMode = FREE_MASS, leftcl = STAT, rightcl = MOVE, shiftl = GRAB, ctrll = DEL, mouseb = -1, mask = -1, LEFT_MARGIN = 5, RIGHT_MARGIN = 15, TOP_MARGIN = 5, BOTTOM_MARGIN = 55;
    private int key = 0;
    private int width, height;    
    private MySelector stat = new MySelector(465,469,50,15,0, "stat",0);
    private MySelector move = new MySelector(465,484,50,15,2, "move",1);
    private MySelector grab = new MySelector(515,469,50,15,1, "grab",2);
    private MySelector del  = new MySelector(515,484,50,15,3, "del",3);
    private MyToggleable modetoggle = new MyToggleable(415, 469, 50, 15, "SIM", "CON");
    private MyToggleable masstoggle = new MyToggleable(415, 484, 50, 15, "FREE", "FIX");
    private MySlider grav = new MySlider(5,474,25,100,30, "G");
    private MySlider fric = new MySlider(120,474,25,100,1, "F");
    private MySlider hooke = new MySlider(235,474,25,100,30, "K");
    private MyButton save = new MyButton(355,469,50,15, "SAVE");
    private MyButton load = new MyButton(355,484,50,15, "LOAD");
    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        objects = new Vector();
        objects.add(new Mass(10,10,5,5));
    }
    public void changeSize(int height, int width) {
        this.height = height;
        this.width = width;
    }
    public void run() {
        this.iterate();
    }
    public void iterate() {
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
        if(mode == SIMULATE) {
        	selectedMass = false;
        }
        double[] env = new double[7];
        env[0] = LEFT_MARGIN+4;
        env[1] = width - RIGHT_MARGIN-4;
        env[2] = TOP_MARGIN+4;
        env[3] = height - BOTTOM_MARGIN-4;
        env[4] = (double)(grav.getValue())/100;
        env[5] = (double)(fric.getValue())/100;
        env[6] = ((double)(hooke.getValue())/100);
        env[6] *= env[6];
        env[4] = (Math.pow(2,env[4])-1)*4;
        for(Object o: objects) {
            ((PhysObject)o).env = env;
            ((PhysObject)o).mouseOver = false;
            if(!draggingMass) {
                ((PhysObject)o).selected = false;
            }
        }
        if(!pressed) {
            int i = 0;
            int min = 0;
            boolean closeEnough = false;
            for(Object o: objects) {
                if(((PhysObject)o).dist(mouX, mouY) < 20) {
                    closeEnough = true;
                    if (((PhysObject)o).dist(mouX, mouY) < ((PhysObject)objects.elementAt(min)).dist(mouX, mouY)) {
                        min = i;
                    }
                }
                i++;
            }
            if(closeEnough) {  
                ((PhysObject)(objects.elementAt(min))).mouseOver = true;
            }
        }
        if(startY > height) {
        } else if(mouseb == GRAB && !draggingMass) {
            int i = 0;
            int min = 0;
            boolean closeEnough = false;
            for(Object o: objects) {
                if(o instanceof Mass) {
                    if(((PhysObject)o).dist(mouX, mouY) < 20) {
                        closeEnough = true;
                        if (((PhysObject)o).dist(mouX, mouY) < ((PhysObject)objects.elementAt(min)).dist(mouX, mouY)) {
                            min = i;
                        }
                    }
                }
                i++;
            }
            if(closeEnough) {
                ((PhysObject)(objects.elementAt(min))).selected = true;
                draggingMass = true;
                massDragged = min;
                
            }
        }  else if(mouseb == DEL && !deleted) {
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
                if(((PhysObject)(objects.elementAt(min))).selected) {
                    draggingMass = false;
                }
                objects.remove((PhysObject)(objects.elementAt(min)));
                
                
            }
        }
        if(draggingMass) {
            ((Mass)(objects.elementAt(massDragged))).moveTo(mouX-6, mouY-6);
        }            
        if(mode == CONSTRUCT) {
            return;
        }
        for(Object o: objects) {
            for(Object p: objects) {
                if(o != p) {
                    ((PhysObject)o).interact(((PhysObject)p));
                }
            }
        }
        for(Object o: objects) {
            ((PhysObject)o).move();
        }
     }
    public void paintComponent(Graphics g){
        for(Object o: objects) {
            ((PhysObject)o).paintObject(g);
        }
        if(mouseb == MOVE && mouY < height - 30 && startY < height - 30) {
            g.setColor(new Color(200, 200, 200));
            g.drawLine(startX, startY, mouX, mouY);
            g.drawOval(startX-3, startY-3, 6, 6);
            g.drawOval(mouX-3, mouY-3, 6, 6);
        }
        if(selectedMass) {
            g.setColor(Color.blue);
            Mass m = ((Mass)objects.elementAt(massSelected));
            if(mouY < height - BOTTOM_MARGIN && mouY > TOP_MARGIN && mouX < width - LEFT_MARGIN && mouX > RIGHT_MARGIN) {
                g.drawLine((int)m.x+3, (int)m.y+3, mouX, mouY);            	
            }
        }
        g.setColor(Color.black);
        g.drawRect(LEFT_MARGIN, TOP_MARGIN, width-RIGHT_MARGIN-LEFT_MARGIN, height-BOTTOM_MARGIN-TOP_MARGIN);
        masstoggle.paintComponent(g);
        modetoggle.paintComponent(g);
        stat.paintComponent(g);
        move.paintComponent(g);
        grab.paintComponent(g);
        del.paintComponent(g);
        grav.paintComponent(g);
        fric.paintComponent(g);
        hooke.paintComponent(g);
        save.paintComponent(g);
        load.paintComponent(g);
    }
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
        hooke.mousePress(x,y);
        save.mousePress(x,y);
        load.mousePress(x,y);
        startX = x;
        startY = y;
        mouX = x;
        mouY = y;
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
        hooke.mouseDrag(x,y);
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
        int[] sofar = new int[4];
        dragging = false;
        pressed = false;
        shiftb = false;
        ctrlb = false;
        deleted = false;
        if(draggingMass) {
            ((Mass)objects.elementAt(massDragged)).selected = false;
        }
        draggingMass = false;
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
        if(y > height - BOTTOM_MARGIN) {
            return;
        }
        if(mouseb == STAT) {
            if(mode == CONSTRUCT) {
                int i = 0;
                int min = 0;
                boolean closeEnough = false;
                for(Object o: objects) {
                    if(o instanceof Mass) {
                        if( ((PhysObject)o).dist(mouX, mouY) < 20 ) {
                            closeEnough = true;
                            if ( ((PhysObject)o).dist(mouX, mouY) < ((PhysObject)objects.elementAt(min)).dist(mouX, mouY)) {
                                min = i;
                            }
                        }
                    }
                    i++;
                }
                if(closeEnough) {
                    if(selectedMass) {
                        Spring s = new Spring((Mass)objects.elementAt(min), (Mass)objects.elementAt(massSelected));
                        objects.add(s);
                        selectedMass = false;
                        ((Mass)objects.elementAt(massSelected)).selected = false;
                    } else {
                        massSelected = min;
                        selectedMass = true;
                        ((Mass)objects.elementAt(min)).selected = true;
                    }
                } else {
                    if(selectedMass) {
                        Mass mass = new Mass(x, y);
                        if(massMode == FIXED_MASS) {
                            mass.fixed = true;
                        }
                        Spring s = new Spring(mass, (Mass)objects.elementAt(massSelected));
                        objects.add(s);
                        objects.add(mass);
                        selectedMass = false;
                        ((Mass)objects.elementAt(massSelected)).selected = false;
                    } else {
                        Mass mass = new Mass(x,y);
                        if(massMode == FIXED_MASS) {
                            mass.fixed = true;
                        }
                        objects.add(mass);
                    } 
                }
            } else {
                Mass mass = new Mass(x,y);
                if(massMode == FIXED_MASS) {
                    mass.fixed = true;
                }
                objects.add(mass);
            }
        }
        if (mouseb == MOVE) {
            if(mode == CONSTRUCT) {
                if(selectedMass) {
                    selectedMass = false;
                    ((Mass)objects.elementAt(massSelected)).selected = false; 
                } else {
                    Mass mass = new Mass(x, y, (x-startX)*0.1, (y-startY)*0.1);
                    if(massMode == FIXED_MASS) {
                        mass.fixed = true;
                    }
                    objects.add(mass);
                }
            } else {
                Mass mass = new Mass(x, y, (x-startX)*0.1, (y-startY)*0.1);
                if(massMode == FIXED_MASS) {
                    mass.fixed = true;
                }
                objects.add(mass);
            }
         }
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
        }
        if(k == KeyEvent.VK_F) {
            massMode = FREE_MASS;
        }
        if(k == KeyEvent.VK_D) {
            massMode = FIXED_MASS;
        }
        if(k == KeyEvent.VK_SPACE) {
            modetoggle.toggle();
        }
        if(k == KeyEvent.VK_C) {
            masses = new Vector();
            mode = CONSTRUCT;
            massMode = FREE_MASS;
        }
        if(k == KeyEvent.VK_S) {  
        }
        if(k == KeyEvent.VK_L) {
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
}