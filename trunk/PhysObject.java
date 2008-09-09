import java.awt.*;
public abstract class PhysObject {
    public abstract void setEnv(double[] e);
    public abstract void paintObject(Graphics g);
    public abstract void interact(PhysObject p);
    public abstract void move();
    public abstract boolean isOver(int x, int y);
}