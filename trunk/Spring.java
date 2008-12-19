import java.awt.*;
import java.math.*;
public class Spring extends PhysObject {
    public Mass ma, mb;
    public double restlength;
    private double k = 1;
    public double dist(int mx, int my) {
        try {
            double ya = ma.y;
            double yb = mb.y;
            double xa = ma.x;
            double xb = mb.x;
            double S = (yb-ya)/(xb-xa);
            double ptx = (my-ya+mx/S + S*xa)/(S+1/S);
            double pty = ya + S*(ptx-xa);
            double maxx = xa;
            double minx = xb;
            double maxy = ya;
            double miny = yb;
            if(maxx < minx) {
                maxx = xb;
                minx = xa;
            }
            if(maxy < miny) {
                maxy = yb;
                miny = ya;
            }
            if(ptx > maxx) {
                ptx = maxx;
            }
            if(ptx < minx) {
                ptx = minx;
            }
            if(pty > maxy) {
                pty = maxy;
            }
            if(pty < miny) {
                pty = miny;
            }
            return Math.sqrt((mx-ptx)*(mx-ptx)+(my-pty)*(my-pty));
        } catch(Exception e) {System.out.println(e.getMessage());}
        return 0;
    }
    public double massdist() {
        return Math.sqrt((ma.x-mb.x)*(ma.x-mb.x) + (ma.y-mb.y)*(ma.y-mb.y));
    }
    public void move() {
    }
    public void interact(PhysObject po) {
        if(po instanceof Mass) {
            if(po == ma) {
                ForceVector fv = new ForceVector(mb.x-ma.x, mb.y-ma.y);
                fv.setM(.5*env[6]*(massdist()-restlength)/stepmult);
                ma.addSpring(fv);
            }
            if(po == mb) {
                ForceVector fv = new ForceVector(ma.x-mb.x, ma.y-mb.y);
                fv.setM(.5*env[6]*(massdist()-restlength)/stepmult);
                mb.addSpring(fv);
            }
        }
    }
    public void paintObject(Graphics g) {
        if(!ma.exists || !mb.exists) {
            this.exists = false;
            return;
        }
        double drawXa = ma.x;
        double drawYa = ma.y;
        if(drawXa > env[1]) {
            drawXa = env[1];
        } else if (drawXa < env[0] ) {
            drawXa = env[0];
        }
        if(drawYa > env[3]) {
            drawYa = env[3];
        } else if(drawYa < env[2]) {
            drawYa = env[2];
        }
        double drawXb = mb.x;
        double drawYb = mb.y;
        if(drawXb > env[1]) {
            drawXb = env[1];
        } else if (drawXb < env[0] ) {
            drawXb = env[0];
        }
        if(drawYb > env[3]) {
            drawYb = env[3];
        } else if(drawYa < env[2]) {
            drawYb = env[2];
        }
        g.setColor(Color.black);
        if(selected) {
            g.setColor(Color.red);
        }
        g.drawLine((int)drawXa+3, (int)drawYa+3,(int)drawXb+3,(int)drawYb+3);
    }
    public void setEnv(double[] input) {
        env = input;
    }
    public Spring(int xc, int xd, int yc, int yd) {
        ma = new Mass(xc, yc);
        mb = new Mass(xd, yd);
    }
    public Spring(Mass a, Mass b) {
        ma = a;
        mb = b;
        restlength = massdist();
    }
    public Spring() {}
    public void updateExists() {
        try {
            if(!ma.exists || !mb.exists) {
                this.exists = false;
                return;
            }
        } catch (Exception e) {this.exists = false;}
    }
        
}