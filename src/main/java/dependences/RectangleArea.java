package dependences;

public class RectangleArea {

    protected Location location;
    protected Size size;

    public RectangleArea(Location location, Size size) {
        this.location = location;
        this.size = size;
    }

    public RectangleArea() {
        this.location = new Location(0, 0);
        this.size = new Size(0, 0);
    }

    public Location getLocation() {
        return location;
    }

    public Size getSize() {
        return size;
    }
}
