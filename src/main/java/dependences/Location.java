package dependences;

import java.util.Objects;

public class Location {

    protected int x;
    protected int y;

    public Location(final int x, final int y) {
        this.y = y;
        this.x = x;
    }


    public synchronized int getX() {
        return this.x;
    }

    public synchronized void setX(final int x) {
        this.x = x;
    }

    public synchronized int getY() {
        return this.y;
    }

    public synchronized void setY(final int y) {
        this.y = y;
    }

    public synchronized void setLocation(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void setLocation(Location location) {
        this.setLocation(location.x, location.y);
    }

    public Location clone() {
        return new Location(x, y);
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x && y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
