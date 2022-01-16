package dependences;

import java.util.Objects;

public class Size {

    protected int width;
    protected int height;

    public Size(final int x, final int y) {
        this.height = y;
        this.width = x;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(Size location) {
        this.setSize(location.width, location.height);
    }

    public Size clone() {
        return new Size(this.width, this.height);
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return width == size.width && height == size.height;
    }

    public boolean canContain(Size size) {
        return size.width <= this.width && size.height <= this.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
