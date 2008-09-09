import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Canvas extends JComponent {
    private Vector objects;
    public int iters;
    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        objects = new Vector();
        objects.add(new Mass());
    }
    public void iterate(double[] env) {
        for(Object o:objects) {
            PhysObject obj = (PhysObject)o;
            obj.setEnv(env);
            try {
                obj.move();
            } catch (Exception e) {}
        }
    }
    public void paintComponent(Graphics g){
        g.clearRect(0,0, getWidth(), getHeight());
        for(Object o:objects) {
            ((PhysObject)o).paintObject(g);
        }
    }
}