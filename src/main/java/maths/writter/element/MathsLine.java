package maths.writter.element;

import dependences.Location;
import dependences.Size;
import maths.writter.element.special.MultiComponent;

import java.awt.event.KeyEvent;

public class MathsLine extends Node implements MultiComponent {

    public MathsLine(Location location, Size size, Node parent) {
        this(location, size, parent, true);
    }

    public MathsLine(Location location, Size size, Node parent, boolean movable) {
        super(location, size, parent, movable);
        this.linear_component = true;
    }

    @Override
    protected void resizeChild() {
        int actual_x = 0;
        for (int i = 0; i < this.nodes.size(); i++) {
            Node node = this.nodes.get(i);

            Size size = node.getPreferredSize();

            node.getLocation().setLocation(actual_x, this.size.getHeight() / 2 - size.getHeight() / 2);
            node.setSize(size);

            actual_x += node.getSize().getWidth();
        }
    }

    @Override
    public Size getPreferredSize() {
        Size size = new Size(0, 0);
        for (Node node : this.nodes) {
            size.setWidth(size.getWidth() + node.getSize().getWidth());
            if (size.getHeight() < node.getSize().getHeight()) {
                size.setHeight(node.getSize().getHeight());
            }
        }
        return size;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("IN ! : " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_TAB && this.nodes_selected.size() != 0 && this.getLastMultiComponentSelected() == null) {
            System.out.println("ADD !");
            TextNode textNode = new TextNode(new Location(0, 0), new Size(0, 0), "nothing", this);
            textNode.setMovable(false);
            this.insertNode(this.nodes.indexOf(this.nodes_selected.get(0)), textNode);
            this.clearSelected();
            this.addNodeSelected(textNode);
            return;
        }
        super.keyPressed(e);
    }

    @Override
    public Node getLastMultiComponentSelected() {
        for (Node node : this.nodes_selected)
            if (node instanceof MultiComponent)
                if (((MultiComponent) node).getLastMultiComponentSelected() != null)
                    return ((MultiComponent) node).getLastMultiComponentSelected();
                else
                    return node;
        return null;
    }

    @Override
    public void elementAfter(Node node_caller) {
        System.out.println("Maths line get !");
        super.elementAfter(node_caller);
    }

    @Override
    public void elementBefore(Node node_caller) {
        System.out.println("Maths line get !");
        super.elementBefore(node_caller);
    }
}
