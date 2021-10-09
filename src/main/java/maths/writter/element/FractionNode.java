package maths.writter.element;

import dependences.Location;
import dependences.Size;

import java.awt.*;

public class FractionNode extends Node {
    protected Node numerator;
    protected Node denominator;

    public FractionNode(Location location, Size size, TextNode numerator, TextNode denominator, Node parent) {
        super(location, size, parent);
        this.numerator = numerator;
        this.denominator = denominator;
        this.numerator.setParent(this);
        this.denominator.setParent(this);
        this.addNode(this.numerator);
        this.addNode(this.denominator);
        this.canChangeChild = true;
    }

    @Override
    public void draw(Graphics2D g) {
        int biggest_width = 0;
        for (Node node : this.nodes)
            if (node.getSize().getWidth() > biggest_width)
                biggest_width = node.getSize().getWidth();

        g.fillRoundRect(this.size.getWidth() / 2 - biggest_width / 2, this.numerator.getSize().getHeight(), biggest_width, 2, 8, 8);
        super.draw(g);
    }

    public FractionNode(Location location, Size size, String numerator, String denominator, Node parent) {
        super(location, size, parent);
        this.numerator = new TextNode(new Location(10, 10), new Size(20, 20), numerator, this);
        this.denominator = new TextNode(new Location(10, 50), new Size(20, 20), denominator, this);
        this.addNode(this.numerator);
        this.addNode(this.denominator);
        this.canChangeChild = true;
    }

    public FractionNode(Location location, Size size, TextNode numerator, String denominator, Node parent) {
        super(location, size, parent);
        this.numerator = numerator;
        this.denominator = new TextNode(new Location(10, 50), new Size(20, 20), denominator, this);
        this.numerator.setParent(this);
        this.addNode(this.numerator);
        this.addNode(this.denominator);
        this.canChangeChild = true;
    }

    public FractionNode(Location location, Size size, String numerator, TextNode denominator, Node parent) {
        super(location, size, parent);
        this.numerator = new TextNode(new Location(10, 50), new Size(20, 20), numerator, parent);
        this.denominator = denominator;
        this.denominator.setParent(this);
        this.addNode(this.numerator);
        this.addNode(this.denominator);
        this.canChangeChild = true;
    }

    public FractionNode(Location location, Size size, Node parent) {
        this(location, size, "num", "deno", parent);
    }

    @Override
    public Size getPreferredSize() {
        if (denominator != null & this.numerator != null) {
            Size size_deno = this.denominator.getPreferredSize();
            Size size_num = this.numerator.getPreferredSize();
            return new Size(Math.max(size_deno.getWidth(), size_num.getWidth()), size_deno.getHeight() + size_num.getHeight() + 2);
        }
        return this.size;
    }

    @Override
    protected void resizeChild() {
        if (this.denominator == null || this.numerator == null) return;

        Size denominator_size = this.denominator.getPreferredSize().clone();

        if (denominator_size.getWidth() > this.size.getWidth())
            denominator_size.setWidth(this.size.getWidth());
        if (denominator_size.getHeight() > this.size.getHeight())
            denominator_size.setHeight(this.size.getHeight());

        Size numerator_size = this.numerator.getPreferredSize().clone();

        if (numerator_size.getWidth() > this.size.getWidth())
            numerator_size.setWidth(this.size.getWidth());
        if (numerator_size.getHeight() > this.size.getHeight())
            numerator_size.setHeight(this.size.getHeight());


        this.numerator.setLocation(new Location((int) (this.size.getWidth() / 2d - numerator_size.getWidth() / 2d), 0));
        this.denominator.setLocation(new Location((int) (this.size.getWidth() / 2d - denominator_size.getWidth() / 2d), numerator_size.getHeight() + 2));
        this.numerator.setSize(numerator_size);
        this.denominator.setSize(denominator_size);
    }

    public void selectDeno() {
        this.addNodeSelected(this.denominator);
    }

    @Override
    public void removeNode(Node node) {
        if (this.numerator == node)
            this.numerator = null;
        else if (this.denominator == node)
            this.denominator = null;

        super.removeNode(node);
    }

    @Override
    public void addNode(Node node) {
        if (this.nodes.size() >= 2)
            return;

        if (this.numerator == null)
            this.numerator = node;
        else if (this.denominator == null)
            this.denominator = node;

        super.addNode(node);
    }
}