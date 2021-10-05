package maths.writter.element;

import dependences.Location;
import dependences.Size;
import maths.writter.element.special.TextAreaModified;


public class TextNode extends Node {

    protected TextAreaModified textArea;

    public TextNode(Location location, Size size, String text) {
        super(location, size);
        this.textArea = new TextAreaModified(new Location(0, 0), new Size(100, 100), text);
        this.addNode(this.textArea);
    }

    public TextNode(Location location, Size size) {
        this(location, size, "Zone de texte");
    }

    @Override
    public Size getPreferredSize() {
        Size biggest = new Size(10, 10);
        for (Node node : nodes) {
            Size preferred_size = node.getPreferredSize();
            if (preferred_size.getWidth() > biggest.getWidth())
                biggest.setWidth(preferred_size.getWidth() + 5);
            if (preferred_size.getHeight() > biggest.getHeight())
                biggest.setHeight(preferred_size.getHeight() + 5);
        }
        return biggest;
    }

    @Override
    public void setSelected(boolean selected) {
        this.addNodeSelected(this.textArea);
        super.setSelected(selected);
    }
}