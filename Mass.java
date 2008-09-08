import java.awt.*;
public class Mass extends PhysObject{
    private int x = 20, y = 20, fX = 0, fY = 0, vX = 0, vY = 0;
    private int[] env = new int[2];
    private boolean mouseOver = false;
    public void interact(PhysObject p) {
        if(p instanceof Mass) {
        }
    }
    public void paintObject(Graphics g) {
        g.setColor(Color.black);
        g.fillOval(x,y,6,6);
        if(mouseOver) {
            g.drawOval(x-2,y-2,9,9);
        }
    }
    public void setEnv(int[] e) {
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