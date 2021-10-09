package maths.writter.element;

import dependences.Location;
import dependences.Size;
import maths.writter.Manager;
import maths.writter.controller.FrameListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class Node implements FrameListener {

    protected boolean multiple_select = false;

    protected final ArrayList<Node> nodes = new ArrayList<>();
    protected final ArrayList<Node> nodes_selected = new ArrayList<>();

    protected boolean movable;

    protected final Location location;
    protected final Size size;

    protected boolean haveToBeDraw = true;

    protected boolean selected;

    protected boolean over = false;

    protected Node parent;

    protected boolean canChangeChild = true;

    public Node(final Location location, final Size size, Node parent) {
        this(location, size, parent, false);
    }

    public Node(final Location location, final Size size, Node parent, boolean movable) {
        this.movable = movable;
        this.location = location.clone();
        this.size = new Size(0, 0);
        this.parent = parent == null ? this : parent;
        this.setSize(size);
        this.update(this);
    }

    public Location getLocation() {
        synchronized (this.location) {
            return this.location;
        }
    }

    public Location setLocation(final Location location) {
        synchronized (this.location) {
            this.location.setLocation(location);
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
            } else
                this.size.setWidth(size.getWidth());

            if (size.getHeight() < 0) {
                this.location.setY(this.location.getY() + size.getHeight());
                this.size.setHeight(this.size.getHeight() - size.getHeight());
            } else
                this.size.setHeight(size.getHeight());

            this.update(this, false);
            return this.size;
        }
    }

    public void draw(Graphics2D g) {
        synchronized (this.size) {
            for (Node node : this.nodes)
                if (node.isHaveToBeDraw())
                    node.draw((Graphics2D) g.create(node.location.getX(), node.location.getY(), node.size.getWidth(), node.size.getHeight()));
            if (selected)
                g.drawRect(0, 0, size.getWidth() - 1, size.getHeight() - 1);
        }
    }

    public void update(Node parent) {
        this.update(parent, true);
    }

    public void update(Node parent, boolean canCallParent) {
        if (!this.parent.equals(parent) && this.parent != this && canCallParent) { // Si un parent existe !
            //System.out.println(this.getClass().getSimpleName() + " call this parent : " + this.parent.getClass().getSimpleName());
            this.parent.update(this);
        } else { // Si le retour vers le parent n'est pas possible !
            resizeChild();
            if (!this.nodes.contains(parent)) { // Si le caller n'est pas un enfant direct
                if (this.nodes.size() == 0) {
                    //System.out.println(this.getClass().getSimpleName() + " : Update but don't have child !");
                }
                for (Node node : this.nodes) {
                    //System.out.println(this.getClass().getSimpleName() + " call this child : " + node.getClass().getSimpleName());
                    node.update(this);
                }
            } else { // si l'enfant est un caller direct alors on update que l'enfant direct !
                //System.out.println(this.getClass().getSimpleName() + " :  to direct : " + parent.getClass().getSimpleName());
                parent.update(this);
            }
        }
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

    protected boolean moveInAction;
    protected Location locationInAction;

    @Override
    public void mousePressed(MouseEvent e) {
        if (this.nodes_selected.size() == 0 && this.movable)
            if (e.getX() < 5 || e.getY() < 5 || this.size.getWidth() - 5 < e.getX() || this.getSize().getHeight() - 5 < e.getY()) {
                this.moveInAction = true;
                this.locationInAction = new Location(e.getX(), e.getY());
                return;
            }
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
            node.mousePressed(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            node.mouseMoved(new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                    e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                    e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            this.mouseMoved(new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                    e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                    e.getClickCount(), e.isPopupTrigger(), e.getButton()));
        } else
            this.clearSelected();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.moveInAction)
            this.moveInAction = false;
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
        Manager.manager_last_Manager.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.moveInAction) {
            this.location.setLocation(this.location.getX() - (this.locationInAction.getX() - e.getX()), this.location.getY() - (this.locationInAction.getY() - e.getY()));
        } else {
            Node node = this.getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
            if (node != null)
                node.mouseDragged(
                        new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                                e.getX() - node.location.getX(), e.getY() - node.location.getY(),
                                e.getClickCount(), e.isPopupTrigger(), e.getButton()));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.nodes_selected.size() == 0 && this.movable)
            if (e.getX() < 5 || e.getY() < 5 || this.size.getWidth() - 5 < e.getX() || this.getSize().getHeight() - 5 < e.getY())
                Manager.manager_last_Manager.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            else
                Manager.manager_last_Manager.setCursor(Cursor.getDefaultCursor());

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

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isMovable() {
        return movable;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isCanChangeChild() {
        return canChangeChild;
    }

    public void setCanChangeChild(boolean canChangeChild) {
        this.canChangeChild = canChangeChild;
    }
}
