public class ForceVector {
    private double x, y, magnitude, angle;
    public ForceVector(double xD, double yD) {
        double total = xD + yD;
        if(total == 0) {
            x = y = magnitude = 0;
        } else {
            angle = Math.atan2(yD,xD);
            magnitude = Math.sqrt(xD*xD+yD*yD);
            x = Math.cos(angle);
            y = Math.sin(angle);
        }
        
            
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getM() {
        return magnitude;
    }
    public void setM(double m) {
        magnitude = m;
    }
    public void reverseX() {
        x = 0-x;
    }
    public void reverseY() {
        y = 0-y;
    }
    public void applyFriction(double friction) {
        x *= friction;
        y *= friction;
    }
    public ForceVector add(ForceVector v) {
        return new ForceVector(x*magnitude + v.getX()*v.getM(), y*magnitude+v.getY()*v.getM());
    }
}