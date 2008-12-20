import java.awt.*;
import java.math.*;
public class BarSpring extends PhysObject {
    public Mass ma, mb;
    public double restlength;
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
            System.out.println(ptx + " " + pty + " ");
            return Math.sqrt((mx-ptx)*(mx-ptx)+(my-pty)*(my-pty));
        } catch(Exception e) {System.out.println(e.getMessage());}
        return 0;
    }
    public double massdist() {
        return Math.sqrt((ma.x-mb.x)*(ma.x-mb.x) + (ma.y-mb.y)*(ma.y-mb.y));
    }
    public void move() {/*
        ForceVector fv = new ForceVector(ma.x-mb.x, ma.y-mb.y);
        fv.setM(.5*env[4]*(massdist()-restlength)/stepmult);
        ForceVector fvb = new ForceVector(mb.x-ma.x, mb.y-ma.y);
        fvb.setM(.5*env[4]*(massdist()-restlength)/stepmult);
        ma.addBarSpring(fvb);
        mb.addBarSpring(fv);*/
    }
    public void interact(PhysObject po) {}
    public void paintObject(Graphics g) {
        if(!ma.exists || !mb.exists) {
            this.exists = false;
            return;
        }
        double drawXa = ma.x;
        double drawYa = ma.y;
        if(drawXa > env[3]) {
            drawXa = env[3];
        } else if (drawXa < 0 ) {
            drawXa = 0;
        }
        if(drawYa > env[2]) {
            drawYa = env[2];
        } else if(drawYa < 0) {
            drawYa = 0;
        }
        double drawXb = mb.x;
        double drawYb = mb.y;
        if(drawXb > env[3]) {
            drawXb = env[3];
        } else if (drawXb < 0 ) {
            drawXb = 0;
        }
        if(drawYb > env[2]) {
            drawYb = env[2];
        } else if(drawYa < 0) {
            drawYb = 0;
        }
        g.setColor(Color.blue);
        if(selected) {
            g.setColor(Color.red);
        }
        g.drawLine((int)drawXa+3, (int)drawYa+3,(int)drawXb+3,(int)drawYb+3);
    }
    public void setEnv(double[] input) {
        env = input;
    }
    public BarSpring(int xc, int xd, int yc, int yd) {
        ma = new Mass(xc, yc);
        mb = new Mass(xd, yd);
    }
    public BarSpring(Mass a, Mass b) {
        ma = a;
        mb = b;
        restlength = massdist();
    }
    public BarSpring() {}
    public void updateExists() {
        try {
            if(!ma.exists || !mb.exists) {
                this.exists = false;
                return;
            }
        } catch (Exception e) {this.exists = false;}
    }
        
}