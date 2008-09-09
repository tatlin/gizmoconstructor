import java.awt.*;
public class Mass extends PhysObject{
    private double x = 0, y = 0, fX = 0, fY = 0, vX = 0, vY = 0;
    private double[] env = new double[3];
    private boolean mouseOver = false;
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
        g.fillOval((int)x,(int)y,6,6);
    }
    public void setEnv(double[] e) {
        env[0] = e[0];
        env[1] = e[1];
        env[2] = e[2];
    }
    public void gravity() {
        fY += env[0];
    }
    public void bounce() {
        if(y > env[1]) {
            y -= 2*(y-env[1]);
            vY = 0-vY;
        }
        if(y < 0) {
            y = 0-y;
            vY = 0-vY;
        }
        if(x > env[2]) {
            x -= 2*(x-env[2]);
            vX = 0-vX;
        }
        if(x < 0) {
            x = 0-x;
            vX = 0-vX;
        }
    }
    public void move() {
        gravity();
        vX += fX;
        vY += fY;
        x += vX;
        y += vY;
        bounce();
        fX = 0;
        fY = 0;
    }
}