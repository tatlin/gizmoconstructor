import java.awt.*;

public class MySlider {
    private int x, y, h, w, value, startX, startY;
    private String label;
    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(x,y,w,h);
        g.drawLine(x+value, y, x+value, y+h);
        g.drawString(label, x+w+5, (int)(y+h/2));
        g.setColor(Color.blue);
        g.drawLine(x,(int)(y+h/2), x+value, (int)(y+h/2));
        g.setColor(Color.red);
        g.drawLine(x+value,(int)(y+h/2), x+w, (int)(y+h/2));
    }
    public boolean inRect(int mx, int my) {
        return (startX < x + w && startX > x && startY < y + h && startY > y && mx < x + w && mx > x && my < y+h && my > y);
    }
    public void mousePress(int mx, int my) {
        startX = mx;
        startY = my;
        if(inRect(mx, my)) {
            value = mx-x;
        }
    }
    public void mouseDrag(int moux, int mouy) {
        int mx = moux - 5;
        int my = mouy;
        if(inRect(mx, my)) {
            value = mx-x;
        }
        if(inRect(startX, startY)) {
            if(mx > x + w) {
                value = w;
            } else if(mx < x) {
                value = 0;
            }
        }
    }
    public int getValue() {
        return value;
    }
    public MySlider(int nX, int nY, int nH, int nW, int initial, String lbl) {
        x = nX;
        y = nY;
        h = nH;
        w = nW;
        value = initial;
        label = lbl;
    }
}