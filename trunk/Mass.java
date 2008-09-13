import java.awt.*;
import java.math.*;
public class Mass extends PhysObject{
    public double x = 0, y = 0, fX = 0, fY = 0, vX = 0, vY = 0, ofX, ofY;
    private double[] env = new double[4];
    private boolean mouseOver = false;
    public boolean selected = false;
    private double stepmult = 1;
    public Mass() {}
    public Mass(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Mass(int x, int y, double vX, double vY) {
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
    }
    public void interact(PhysObject p) {
        if(p instanceof Mass) {
        }
    }
    public void paintObject(Graphics g) {
        g.setColor(Color.black);
        if(selected) {
            g.setColor(Color.blue);
            g.drawOval((int)x-3, (int)y-3, 12, 12);
        }
        g.fillOval((int)x,(int)y,6,6);
    }
    public void setEnv(double[] e) {
        for(int i=0;i<e.length;i++) {
            env[i] = e[i];
        }
    }
    public void gravity() {
        fY += env[0]/stepmult;
    }
    public void bounce() {
        if(y > env[2] - 30) {
            y -= 2*(y-env[2] + 30);
            y = env[2] - 30;
            vY = 0-vY;
        }
        if(y < 0) {
            y = 0-y;
            vY = 0-vY;
        }
        if(x > env[3]) {
            x -= 2*(x-env[3]);
            vX = 0-vX;
        }
        if(x < 0) {
            x = 0-x;
            vX = 0-vX;
        }
    }
    public void friction() {
        vX *= Math.pow(1-env[1],1/stepmult);
        vY *= Math.pow(1-env[1],1/stepmult);
        ofY = fY;
    }
    public void move() {
        if(selected) {
            vX = 0;
            vY = 0;
            return;
        }
        gravity();
        vX += fX;
        vY += fY;
        friction();
        x += vX/stepmult;
        y += vY/stepmult;
        bounce();
        fX = 0;
        fY = 0;
    }
    public double dist(int x, int y) {
        double X = this.x+3;
        double Y = this.y+3;
        return Math.pow((((X-x)*(X-x) + (Y-y)*(Y-y))),0.5);
    }
}