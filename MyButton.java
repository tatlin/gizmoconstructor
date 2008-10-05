import java.awt.*;

public class MyButton {
    private int x,y,w,h, startX, startY;
    private boolean isPressed = false;
    private String lbl;
    public MyButton(int nX, int nY, int nW, int nH, String label) {
        x = nX;
        y = nY;
        w = nW;
        h = nH;
        lbl = label;
    }
    public boolean inRect(int mx, int my) {
        return (startX < x + w && startX > x && startY < y + h && startY > y && mx < x + w && mx > x && my < y+h && my > y);
    }
    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(x,y,w,h);
        int textX = (int)((x+w/2)-7*lbl.length()/2);
        int textY = (int)((y+h/2)+6);
        if(!isPressed) {
            g.setColor(Color.gray);
            g.fillRect(x+1, y+1, w-1, h-1);
            g.setColor(Color.white);
            g.drawString(lbl, textX, textY);
        } else {
            g.setColor(Color.black);
            g.drawString(lbl, textX, textY);
        }
    }
    public void mousePress(int mx, int my) {
        startX = mx;
        startY = my;
        if(inRect(mx,my)) {
            isPressed = true;
        }
    }
    public void mouseRelease(int mx, int my) {
        isPressed = false;
    }
    public void mouseMove(int mx, int my) {
        if(!inRect(mx,my)) {
            isPressed = false;
        }
    }
    public boolean getPressed() {
        return isPressed;
    }
}