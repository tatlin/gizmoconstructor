import java.awt.*;
public abstract class PhysObject {
    public abstract void setEnv(int[] e);
    public abstract void paintObject(Graphics g);
    public abstract void interact(PhysObject p);
    public abstract void move();
}