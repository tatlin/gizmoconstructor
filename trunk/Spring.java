import java.awt.*;
import java.math.*;
public class Spring extends PhysObject {
    private Mass ma, mb;
    private int bX = 15, bY = 45;
    private double restlength;
    private double k = 1;
    private double[] env = new double[4];
    public double dist(int mx, int my) {
        try {
            double ya = ma.getY();
            double yb = mb.getY();
            double xa = ma.getX();
            double xb = mb.getX();
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
        return Math.sqrt((ma.getX()-mb.getX())*(ma.getX()-mb.getX()) + (ma.getY()-mb.getY())*(ma.getY()-mb.getY()));
    }
    public void move() {
        ForceVector fv = new ForceVector(ma.getX()-mb.getX(), ma.getY()-mb.getY());
        fv.setM(.5*k*(massdist()-restlength)/stepmult);
        ForceVector fvb = new ForceVector(mb.getX()-ma.getX(), mb.getY()-ma.getY());
        fvb.setM(.5*k*(massdist()-restlength)/stepmult);
        ma.addSpring(fvb);
        mb.addSpring(fv);
    }
    public void interact(PhysObject po) {}
    public void paintObject(Graphics g) {
        double drawXa = ma.getX();
        double drawYa = ma.getY();
        if(drawXa > env[3]-bX) {
            drawXa = env[3]-bX;
        } else if (drawXa < 0 ) {
            drawXa = 0;
        }
        if(drawYa > env[2]-bY) {
            drawYa = env[2]-bY;
        } else if(drawYa < 0) {
            drawYa = 0;
        }
        double drawXb = mb.getX();
        double drawYb = mb.getY();
        if(drawXb > env[3]-bX) {
            drawXb = env[3]-bX;
        } else if (drawXb < 0 ) {
            drawXb = 0;
        }
        if(drawYb > env[2]-bY) {
            drawYb = env[2]-bY;
        } else if(drawYa < 0) {
            drawYb = 0;
        }
        g.setColor(Color.black);
        g.drawLine((int)drawXa+3, (int)drawYa+3,(int)drawXb+3,(int)drawYb+3);
    }
    public void setEnv(double[] e) {
        for(int i=0;i<e.length;i++) {
            env[i] = e[i];
        }
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
}