import java.awt.*;
import java.math.*;
public class Spring extends PhysObject {
    private Mass ma, mb;
    private double restlength;
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
        fv.setM(0.05*(massdist()-restlength)/stepmult);
        ForceVector fvb = new ForceVector(mb.getX()-ma.getX(), mb.getY()-ma.getY());
        fv.setM(0.05*(massdist()-restlength)/stepmult);
        ma.addSpring(fvb);
        mb.addSpring(fv);
    }
    public void interact(PhysObject po) {}
    public void paintObject(Graphics g) {
        g.drawLine((int)ma.getX(), (int)ma.getY(),(int)mb.getX(),(int)mb.getY());
    }
    public void setEnv(double[] env) {}
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