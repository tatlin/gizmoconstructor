import java.awt.*;

public class MySelector {
    private boolean ul = false, ur = false, bl = false, br = false;
    private Color lightblue = new Color(153,214,255);
    private final int RIGHTCL = 0, LEFTCL = 1, SHIFTL = 2, CTRLL = 3, STAT = 0, MOVE = 1, GRAB = 2, DEL = 3;
    public int x,y,h,w, startX, startY, func = STAT;
    private String label;
    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        int textX = (int)((x+w/2)-7*label.length()/2);
        int textY = (int)((y+h/2)+6);
        g.drawRect(x,y,w,h);
        g.drawRect(x,y,6,6);
        g.drawRect(x+w-6,y,6,6);
        g.drawRect(x,y+h-6,6,6);
        g.drawRect(x+w-6,y+h-6,6,6);
        g.drawString(label, textX, textY);
        g.setColor(lightblue);
        if(ul) {
            g.fillRect(x+1,y+1,5,5);
        }
        if(ur) {
            g.fillRect(x+w-5,y+1,5,5);
        }
        if(bl) {
            g.fillRect(x+1,y+h-5,5,5);;
        }
        if(br) {
            g.fillRect(x+w-5, y+h-5,5,5);
        }
    }
    public boolean inRect(int mx, int my) {
        return (startX < x + w && startX > x && startY < y + h && startY > y && mx < x + w && mx > x && my < y+h && my > y);
    }
    public void mousePress(int mx, int my) {
        startX = mx;
        startY = my;
    }
    public void mouseRelease(int mx, int my, int mask) {
        switch(mask) {
            case LEFTCL: ul = false; break;
            case RIGHTCL: bl = false; break;
            case SHIFTL: ur = false; break;
            case CTRLL: br = false; break;
            default: break;
        }
        if(inRect(mx, my)) {
            switch(mask) {
                case LEFTCL: ul = true; break;
                case RIGHTCL: bl = true; break;
                case SHIFTL: ur = true; break;
                case CTRLL: br = true; break;
                default: break;
            }
        }
    }
    public MySelector(int nX, int nY, int nW, int nH, int state, String lbl, int fnc) {
        x = nX;
        y = nY;
        h = nH;
        w = nW;
        label = lbl;
        func = fnc;
        switch(state) {
            case 0:  ul = true; break;
            case 1:  ur = true; break;
            case 2:  bl = true; break;
            case 3:  br = true; break;
            default: break;
        }
    }
    public int[] getButtons(int[] sofar) {
        if(ul) {
            sofar[0] = func;
        }
        if(ur) {
            sofar[2] = func;
        }
        if(bl) {
            sofar[1] = func;
        }
        if(br) {
            sofar[3] = func;
        }
        return sofar;
    }
}