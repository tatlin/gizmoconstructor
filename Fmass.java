import java.awt.*;
public class Fmass extends Mass {
    public Fmass(double x, double y) {
        this.x = x-6;
        this.y = y-6;
    }
    public Fmass(double x, double y, double vX, double vY) {
        this.x = x-6;
        this.y = y-6;
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
