import java.awt.*;
public class Fmass extends Mass {
    public Fmass(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Fmass(int x, int y, double vX, double vY) {
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
    }
    public void move() {}
    public void paintObject(Graphics g) {
        g.setColor(Color.black);
        if(selected) {
            g.setColor(Color.blue);
            g.drawRect((int)x-3, (int)y-3, 12, 12);
        }
        g.fillRect((int)x,(int)y,6,6);
    }
}
