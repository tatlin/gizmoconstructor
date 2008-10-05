import java.awt.*;
import java.math.*;
import java.util.*;
public class Mass extends PhysObject{
    public double x = 0, y = 0, oX=0, oY=0;
    private ForceVector forces,  motion;
    private double[] env = new double[4];
    private boolean mouseOver = false;
    public boolean selected = false;
    private int bX = 15, bY = 45;
    private Vector springforces = new Vector();
    public Mass() {}
    public Mass(double x, double y) {
        this.x = x;
        this.y = y;
        motion = new ForceVector(0,0);
        forces = new ForceVector(0,0);
    }
    public Mass(double x, double y, double vX, double vY) {
        this.x = x;
        this.y = y;
        motion = new ForceVector(vX,vY);
        forces = new ForceVector(0,0);
    }
    public void interact(PhysObject p) {
        if(p instanceof Mass) {
        }
    }
    public void paintObject(Graphics g) {
        g.setColor(Color.black);
        double drawX = x;
        double drawY = y;
        if(x > env[3]-bX) {
            drawX = env[3]-bX;
        } else if (x < 0 ) {
            drawX = 0;
        }
        if(y > env[2]-bY) {
            drawY = env[2]-bY;
        } else if(y < 0) {
            drawY = 0;
        }
        if(selected) {
            g.setColor(Color.blue);
            g.drawOval((int)drawX-3, (int)drawY-3, 12, 12);
        }
        g.fillOval((int)drawX,(int)drawY,6,6);
    }
    public void setEnv(double[] e) {
        for(int i=0;i<e.length;i++) {
            env[i] = e[i];
        }
    }
    public void gravity() {        
        try {
            forces = forces.add(new ForceVector(0, env[0]/stepmult));
        } catch (Exception e) { System.out.println(e.getMessage());}
    }
    public void bounce() {
        if(y > env[2] - bY) {
            y -= 2*(y-env[2] + bY);
            motion.reverseY();
        }
        if(y < 0) {
            y = 0;
            motion.reverseY();
        }
        if(x > env[3]-bX) {
            x -= 2*(x-env[3]+bX);
            motion.reverseX();
        }
        if(x < 0) {
            x = 0;
            motion.reverseX();
        }
    }
    public void friction() {
        motion.applyFriction(Math.pow(1-env[1],1/stepmult));
    }
    public void springs() {
        Iterator iter = springforces.iterator();
        while(iter.hasNext()) {
            forces = forces.add((ForceVector)iter.next());
        }
    }
    public void move() {
        if(selected) {
            motion = new ForceVector(0,0);
            return;
        }
        gravity();
        springs();
        motion = motion.add(forces);
        friction();
        x += motion.getX()*motion.getM()/stepmult;
        y += motion.getY()*motion.getM()/stepmult;
        bounce();
        forces = new ForceVector(0,0);
        springforces = new Vector();
    }
    public double dist(int x, int y) {
        double X = this.x+3;
        double Y = this.y+3;
        return Math.pow((((X-x)*(X-x) + (Y-y)*(Y-y))),0.5);
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getVX() {
        return motion.getX()*motion.getM();
    }
    public double getVY() {
        return motion.getY()*motion.getM();
    }
    public void addSpring(ForceVector fv) {
        springforces.add(fv);
    }
}