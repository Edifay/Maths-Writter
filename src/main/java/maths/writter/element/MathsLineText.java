package maths.writter.element;

import dependences.Location;
import dependences.Size;

import java.awt.event.KeyEvent;

public class MathsLineText extends MathsLine {

    public MathsLineText(Location location, Size size, Node parent) {
        this(location, size, "default", parent, true);
    }

    public MathsLineText(Location location, Size size, String text, Node parent) {
        this(location, size, text, parent, true);
    }

    public MathsLineText(Location location, Size size, String text, Node parent, boolean movable) {
        super(location, size, parent, movable);
        TextNode textNode = new TextNode(new Location(0, 0), new Size(0, 0), text, this);
        textNode.setMovable(false);
        this.addNode(textNode);
    }

}