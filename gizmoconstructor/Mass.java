import java.awt.*;
public class Mass extends PhysObject{
    private int x = 20, y = 20;
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
    public void setEnv(int[] e) {}
    public void move() {}
}