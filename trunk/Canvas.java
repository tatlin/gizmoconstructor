import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Canvas extends JComponent {
    private Vector objects;
    public Canvas(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.white);
        objects = new Vector();
        objects.add(new Mass());
    }
    public void paintComponent(Graphics g){
        System.out.println("!");
        Rectangle rect = g.getClipBounds();
        g.setColor(Color.white);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        for(Object o:objects) {
            System.out.println("!");
            ((PhysObject)o).paintObject(g);
        }
    }
}