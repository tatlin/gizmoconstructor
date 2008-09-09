import java.awt.*;
public class Mass extends PhysObject{
    private double x = 20, y = 20, fX = 0, fY = 0, vX = 0, vY = 0;
    private double[] env = new double[2];
    private boolean mouseOver = false;
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