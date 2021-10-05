package maths.writter.element;

import dependences.Location;
import dependences.Size;
import maths.writter.Manager;
import maths.writter.controller.FrameListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public abstract class Node implements FrameListener {

    protected boolean multiple_select = false;

    protected final ArrayList<Node> nodes = new ArrayList<>();
    protected final ArrayList<Node> nodes_selected = new ArrayList<>();


    protected final Location location;
    protected final Size size;

    protected boolean haveToBeDraw = true;

    protected boolean selected;

    protected boolean over = false;

    public Node(final Location location, final Size size) {
        this.location = location;
        this.size = new Size(0, 0);
        this.setSize(size);
        this.update();
    }

    public Location getLocation() {
        synchronized (this.location) {
            return this.location;
        }
    }

    public Location setLocation(final Location location) {
        synchronized (this.location) {
            this.location.setLocation(location);
            this.update();
            return this.location;
        }
    }

    public Size getSize() {
        synchronized (this.size) {
            return this.size;
        }
    }

    public Size setSize(final Size size) {
        synchronized (this.size) {
            if (size.getWidth() < 0) {
                this.location.setX(this.location.getX() + size.getWidth());
                this.size.setWidth(this.size.getWidth() - size.getWidth());
            } else {
                this.size.setWidth(size.getWidth());
            }

            if (size.getHeight() < 0) {
                this.location.setY(this.location.getY() + size.getHeight());
                this.size.setHeight(this.size.getHeight() - size.getHeight());
            } else {
                this.size.setHeight(size.getHeight());
            }
            //this.size.setSize(size);
            this.update();
            return this.size;
        }
    }

    public void draw(Graphics2D g, Node parent) {
        synchronized (this.size) {
            for (Node node : this.nodes)
                if (node.isHaveToBeDraw())
                    node.draw((Graphics2D) g.create(node.location.getX(), node.location.getY(), node.size.getWidth(), node.size.getHeight()), parent == null ? this : parent);
            if (selected)
                g.drawRect(0, 0, size.getWidth() - 1, size.getHeight() - 1);
        }
    }

    public void update() {
        resizeChild();
        for (Node node : this.nodes)
            node.update();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected)
            this.clearSelected();
    }

    @Override
    public String toString() {
        return "Node{" +
                "location=" + location +
                ", size=" + size +
                ", selected=" + selected +
                '}';
    }

    public void addNode(Node node) {
        synchronized (this.nodes) {
            this.nodes.add(node);
        }
    }

    public void removeNode(Node node) {
        synchronized (this.nodes) {
            this.nodes.remove(node);
            this.removeNodeSelected(node);
        }
    }

    public void addNodeSelected(Node node) {
        synchronized (this.nodes_selected) {
            this.nodes_selected.add(node);
            node.setSelected(true);
        }
    }

    public void removeNodeSelected(Node node) {
        synchronized (this.nodes_selected) {
            node.setSelected(false);
            this.nodes_selected.remove(node);
        }
    }

    public void clearSelected() {
        synchronized (this.nodes_selected) {
            for (Node node : this.nodes_selected)
                node.setSelected(false);
            this.nodes_selected.clear();
        }
    }

    public boolean isChildNodeSelected(Node node) {
        synchronized (this.nodes_selected) {
            return this.nodes_selected.contains(node);
        }
    }

    public boolean isHaveToBeDraw() {
        return haveToBeDraw;
    }

    public void setHaveToBeDraw(boolean haveToBeDraw) {
        this.haveToBeDraw = haveToBeDraw;
    }

    public Node getCollision(Location location) {
        for (Node node : nodes)
            if (Manager.isCollision(node, location))
                return node;
        return null;
    }

    public Node getCollisionWithSelectedNodes(Location location) {
        for (Node node : this.nodes_selected)
            if (Manager.isCollision(node, location))
                return node;
        return null;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (this.nodes_selected.size() != 0) {
            for (Node node : this.nodes_selected) {
                node.keyPressed(new KeyEvent(
                        e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                        e.getKeyCode(), e.getKeyChar(), e.getKeyLocation()
                ));
            }
        } else {
            if (e.getKeyCode() == 17)
                this.multiple_select = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.nodes_selected.size() != 0) {
            for (Node node : this.nodes_selected) {
                node.keyReleased(new KeyEvent(
                        e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                        e.getKeyCode(), e.getKeyChar(), e.getKeyLocation()
                ));
            }
        } else {
            if (e.getKeyCode() == 17)
                this.multiple_select = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Node node = this.getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null)
            node.mouseClicked(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = this.getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null) {
            node.mousePressed(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            return;
        }

        node = this.getCollision(new Location(e.getX(), e.getY()));
        if (node != null) {
            if (!multiple_select)
                this.clearSelected();
            this.addNodeSelected(node);
            this.mouseMoved(new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                    e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                    e.getClickCount(), e.isPopupTrigger(), e.getButton()));
        } else
            this.clearSelected();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Node node = this.getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null)
            node.mouseReleased(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Node node = this.getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null)
            node.mouseDragged(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Node node = this.getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null) {
            if (!node.isOver()) {
                node.setOver(true);
                node.mouseEntered(
                        new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                                e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                                e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            } else
                node.mouseMoved(new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                        e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                        e.getClickCount(), e.isPopupTrigger(), e.getButton()));
        } else
            for (Node no : this.nodes_selected)
                if (no.isOver()) {
                    no.setOver(false);
                    no.mouseExited(
                            new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                                    e.getX() - no.location.getX(), e.getY() - no.location.getY(),
                                    e.getClickCount(), e.isPopupTrigger(), e.getButton()));
                }

    }

    public boolean isSelected() {
        return selected;
    }

    public Size getPreferredSize() {
        Size biggest = new Size(10, 10);
        for (Node node : nodes) {
            Size preferred_size = node.getPreferredSize();
            if (preferred_size.getWidth() > biggest.getWidth())
                biggest.setWidth(preferred_size.getWidth());
            if (preferred_size.getHeight() > biggest.getHeight())
                biggest.setHeight(preferred_size.getHeight());
        }
        return biggest;
    }

    public boolean haveChildSelected() {
        return this.nodes_selected.size() != 0;
    }

    protected void resizeChild() {
        for (Node node : nodes) {
            if (node == null) return;

            int width = node.getPreferredSize().getWidth();
            int height = node.getPreferredSize().getHeight();

            if (width > this.size.getWidth())
                width = this.size.getWidth();
            if (height > this.size.getHeight())
                height = this.size.getHeight();


            node.setLocation(new Location((int) (this.size.getWidth() / 2d - width / 2d), (int) (this.size.getHeight() / 2d - height / 2d)));
            node.setSize(new Size(width, height));
        }
    }

    @Override
    public void dispose() {

    }

}
