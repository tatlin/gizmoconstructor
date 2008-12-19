import java.awt.*;
import java.math.*;
import java.util.*;
public class Mass extends PhysObject{
    public double x = 0, y = 0, oX=0, oY=0;
    private ForceVector forces,  motion;
    private Vector springforces = new Vector();
    private Vector barsprings = new Vector();
    public boolean fixed = false;
    public Mass() {}
    public Mass(double x, double y) {
        this.x = x-6;
        this.y = y-6;
        motion = new ForceVector(0,0);
        forces = new ForceVector(0,0);
    }
    public Mass(double x, double y, double vX, double vY) {
        this.x = x-6;
        this.y = y-6;
        motion = new ForceVector(vX,vY);
        forces = new ForceVector(0,0);
    }
    public void interact(PhysObject p) {
    }
    public void paintObject(Graphics g) {
        g.setColor(Color.black);
        double drawX = x;
        double drawY = y;
        if(drawX > env[1]) {
            drawX = env[1];
        } else if (drawX < env[0] ) {
            drawX = env[0];
        }
        if(drawY > env[3]) {
            drawY = env[3];
        } else if(drawY < env[2]) {
            drawY = env[2];
        }
        if(selected) {
            g.setColor(Color.red);
        }
        if(mouseOver) {
            g.setColor(Color.blue);
        }
        if(fixed) {
            g.fillRect((int)drawX,(int)drawY,6,6);
        } else {
            g.fillOval((int)drawX,(int)drawY,6,6);
        }
    }
    public void setEnv(double[] e) {
        env = e;
    }
    public void moveTo(int mx, int my) {
        x = mx;
        y = my;
        if(x > env[1]) {
            x = env[1];
        } else if (x < env[0] ) {
            x = env[0];
        }
        if(y > env[3]) {
            y = env[3];
        } else if(y < env[2]) {
            y = env[2];
        }
        motion = new ForceVector(0,0);
    }
    public void addSpring(ForceVector fv) {
        springforces.add(fv);
    }
    public void gravity() {        
        try {
            forces = forces.add(new ForceVector(0, env[4]/stepmult));
        } catch (Exception e) { System.out.println(e.getMessage());}
    }
    public void bounce() {
        if(y > env[3]) {
            y -= 2*(y-env[3]);
            motion.reverseY();
        }
        if(y < env[2]) {
            y -= 2*(y - env[2]);
            motion.reverseY();
        }
        if(x > env[1]) {
            x -= 2*(x-env[1]);
            motion.reverseX();
        }
        if(x < env[0]) {
            x -= 2*(x - env[0]);
            motion.reverseX();
        }
    }
    public void friction() {
        motion.applyFriction(Math.pow(1-env[5],1/stepmult));
    }
    public void springs() {
        Iterator iter = springforces.iterator();
        while(iter.hasNext()) {
            forces = forces.add((ForceVector)iter.next());
        }
    }
    public void barsprings() {}
    public void move() {
        if(selected || fixed) {
            if(y > env[3]) {
                y = env[3];
            }
            if(y < env[2]) {
                y = env[2];
            }
            if(x > env[1]) {
                x = env[1];
            }
            if(x < env[0]) {
                x = env[0];
            }
            motion = new ForceVector(0,0);
            forces = new ForceVector(0,0);
            springforces = new Vector();
            barsprings = new Vector();
            return;
        }
        gravity();
        springs();
        motion = motion.add(forces);
        friction();
        x += motion.getX()*motion.getM()/stepmult;
        y += motion.getY()*motion.getM()/stepmult;
        bounce();
        barsprings();
        forces = new ForceVector(0,0);
        springforces = new Vector();
        barsprings = new Vector();
    }
    public double dist(int x, int y) {
        double X = this.x+3;
        double Y = this.y+3;
        return Math.pow((((X-x)*(X-x) + (Y-y)*(Y-y))),0.5);
    }
}