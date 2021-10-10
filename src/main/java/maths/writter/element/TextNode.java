package maths.writter.element;

import dependences.Location;
import dependences.Size;
import maths.writter.element.special.TextAreaModified;

import java.awt.event.KeyEvent;


public class TextNode extends Node {

    protected TextAreaModified textArea;

    public TextNode(Location location, Size size, String text, Node parent) {
        super(location, size, parent);
        this.textArea = new TextAreaModified(new Location(0, 0), new Size(100, 100), text, this);
        this.addNode(this.textArea);
    }

    public TextNode(Location location, Size size, Node parent) {
        this(location, size, "Zone de texte", parent);
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == '/' && this.parent.isCanChangeChild()) {
            //System.err.println("____________________________________________________________________");
            this.parent.removeNode(this);
            FractionNode fractionNode = new FractionNode(this.location, this.size, this.textArea.getText_manager().getBeforeCaret(), this.textArea.getText_manager().getAfterCaret(), this.parent);
            this.parent.addNode(fractionNode);
            this.parent.addNodeSelected(fractionNode);
            fractionNode.selectDeno();
            this.parent.update(fractionNode);
            return;
        }
        super.keyPressed(e);
    }

}