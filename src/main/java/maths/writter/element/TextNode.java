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
        this.update(this);
    }

    public TextNode(Location location, Size size, Node parent) {
        this(location, size, "Zone de texte", parent);
    }

    @Override
    public Size getPreferredSize() {
        return this.textArea.getPreferredSize();
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
            FractionNode fractionNode = new FractionNode(this.location, this.size, this.textArea.getText_manager().getBeforeCaret(), this.textArea.getText_manager().getAfterCaret(), this.parent);
            fractionNode.setMovable(false);
            this.parent.replace(this, fractionNode);
            fractionNode.selectDeno();
            return;
        }
        super.keyPressed(e);
    }

    @Override
    public void elementAfter(Node node_caller) {
        this.parent.elementAfter(this);
    }

    @Override
    public void elementBefore(Node node_caller) {
        this.parent.elementBefore(this);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}