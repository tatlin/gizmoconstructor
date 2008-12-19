import java.awt.*;
public abstract class PhysObject {
    public double stepmult = 2;
    public boolean exists = true, selected = false, mouseOver = false;
    public double[] env = new double[5];
    public abstract void paintObject(Graphics g);
    public abstract void interact(PhysObject p);
    public abstract void move();
    public abstract double dist(int x, int y);
}