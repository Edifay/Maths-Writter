package maths.writter.element;

import dependences.Location;
import dependences.Size;

import java.awt.*;

public class FractionNode extends Node {
    protected TextNode numerator;
    protected TextNode denominator;

    public FractionNode(Location location, Size size, TextNode numerator, TextNode denominator) {
        super(location, size);
        this.numerator = numerator;
        this.denominator = denominator;
        this.addNode(this.numerator);
        this.addNode(this.denominator);
    }

    @Override
    public void draw(Graphics2D g, Node parent) {
        int biggest_width = 0;
        for (Node node : this.nodes)
            if (node.getSize().getWidth() > biggest_width)
                biggest_width = node.getSize().getWidth();

        g.fillRoundRect(this.size.getWidth() / 2 - biggest_width / 2, this.size.getHeight() / 2 - 1, biggest_width, 2, 8, 8);
        super.draw(g, parent);
    }

    public FractionNode(Location location, Size size, String numerator, String denominator) {
        this(location, size,
                new TextNode(new Location(10, 10), new Size(20, 20), numerator),
                new TextNode(new Location(10, 50), new Size(20, 20), denominator)
        );
    }

    public FractionNode(Location location, Size size, TextNode numerator, String denominator) {
        this(location, size,
                numerator,
                new TextNode(new Location(10, 50), new Size(20, 20), denominator)
        );
    }

    public FractionNode(Location location, Size size, String numerator, TextNode denominator) {
        this(location, size,
                new TextNode(new Location(10, 50), new Size(20, 20), numerator),
                denominator
        );
    }

    public FractionNode(Location location, Size size) {
        this(location, size, "num", "deno");
    }

    @Override
    public Size getPreferredSize() {
        Size biggest = new Size(10, 10);
        for (Node node : nodes) {
            Size preferred_size = node.getPreferredSize();
            if (preferred_size.getWidth() > biggest.getWidth())
                biggest.setWidth(preferred_size.getWidth());
            if (preferred_size.getHeight() > biggest.getHeight())
                biggest.setHeight(preferred_size.getHeight() + 30);
        }
        return biggest;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    protected void resizeChild() {
        if (this.denominator == null || this.numerator == null) return;

        Size denominator_size = this.denominator.getPreferredSize().clone();

        if (denominator_size.getWidth() > this.size.getWidth())
            denominator_size.setWidth(this.size.getWidth());
        if (denominator_size.getHeight() > this.size.getHeight())
            denominator_size.setHeight(this.size.getHeight());


        Size numerator_size = this.numerator.getPreferredSize();

        if (numerator_size.getWidth() > this.size.getWidth())
            numerator_size.setWidth(this.size.getWidth());
        if (numerator_size.getHeight() > this.size.getHeight())
            numerator_size.setHeight(this.size.getHeight());


        this.numerator.setLocation(new Location((int) (this.size.getWidth() / 2d - numerator_size.getWidth() / 2d), (int) (this.size.getHeight() / 4d - numerator_size.getHeight() / 2d)));
        this.denominator.setLocation(new Location((int) (this.size.getWidth() / 2d - denominator_size.getWidth() / 2d), (int) (this.size.getHeight() / 4d * 3 - denominator_size.getHeight() / 2d)));
        this.numerator.setSize(numerator_size);
        this.denominator.setSize(denominator_size);
    }
}
