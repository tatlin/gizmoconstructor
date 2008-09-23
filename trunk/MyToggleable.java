import java.awt.*;

public class MyToggleable {
    private int x, y, h, w, textX, textY, startX, startY;
    private String stateA, stateB;
    public final boolean STATE_A = false, STATE_B = true;
    private boolean state = STATE_A;
    public MyToggleable(int x, int y, int w, int h, String stateA, String stateB) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
        this.stateA = stateA;
        this.stateB = stateB;
        textX = (int)((x+w/2)-7*stateA.length()/2);
        textY = (int)((y+h/2)+6);
    }
    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(x,y,w,h);
        if(!state) {
            g.setColor(Color.gray);
            g.fillRect(x+1, y+1, w-1, h-1);
            g.setColor(Color.white);
            g.drawString(stateA, textX, textY);
        } else {
            g.setColor(Color.black);
            g.drawString(stateB, textX, y+h-2);
        }
    }
    public boolean inRect(int mx, int my) {
        return (startX < x + w && startX > x && startY < y + h && startY > y && mx < x + w && mx > x && my < y+h && my > y);
    }
    public void mousePressed(int mx, int my) {
        startX = mx;
        startY = my;
    }
    public void mouseReleased(int mx, int my) {
        if(inRect(mx, my)) {
            state = !state;
        }
    }
    public boolean getState() {
        return state;
    }
}