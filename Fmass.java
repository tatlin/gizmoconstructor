public class Fmass extends Mass {
    public Fmass(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Fmass(int x, int y, double vX, double vY) {
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
    }
    public void move() {}
}
