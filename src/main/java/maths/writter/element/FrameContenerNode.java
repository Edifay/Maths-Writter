package maths.writter.element;

import dependences.Location;
import dependences.Size;
import maths.writter.Manager;
import maths.writter.controller.frame_controllers.NewNodeListener;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class FrameContenerNode extends Node {

    protected boolean multiple_select = false;
    protected Manager manager;

    public FrameContenerNode(Location location, Size size, Manager manager) {
        super(location, size);
        this.manager = manager;
    }

    public ArrayList<Node> getSelectedNode() {
        return this.nodes_selected;
    }

    public ArrayList<Node> getNodes() {
        return this.nodes;
    }


    @Override
    public void dispose() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (this.manager.getContener().getSelectedNode().size() != 0) {
            ArrayList<Node> nodes_at_remove = new ArrayList<>();
            for (Node node : this.manager.getContener().getSelectedNode()) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE && !node.haveChildSelected()) {
                    nodes_at_remove.add(node);
                } else if (node.haveChildSelected())
                    node.keyPressed(new KeyEvent(
                            e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getKeyCode(), e.getKeyChar(), e.getKeyLocation()
                    ));
            }
            for (Node node : nodes_at_remove) {
                this.manager.getContener().removeNode(node);
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_SPACE)
                this.manager.changeListener(new NewNodeListener(this.manager));
            if (e.getKeyCode() == 17)
                this.multiple_select = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.manager.getContener().getSelectedNode().size() != 0) {
            for (Node node : this.manager.getContener().getSelectedNode()) {
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
        Node node = this.manager.getContener().getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null) {
            node.mouseClicked(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.getLocation().getX(), e.getY() - node.getLocation().getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = this.manager.getContener().getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null) {
            node.mousePressed(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.getLocation().getX(), e.getY() - node.getLocation().getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            return;
        }

        node = this.manager.getContener().getCollision(new Location(e.getX(), e.getY()));
        if (node != null) {
            if (!multiple_select)
                this.manager.getContener().clearSelected();
            this.manager.getContener().addNodeSelected(node);
            this.mouseMoved(new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                    e.getX() - node.getLocation().getX(), e.getY() - node.getLocation().getY(),
                    e.getClickCount(), e.isPopupTrigger(), e.getButton()));
        } else
            this.manager.getContener().clearSelected();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Node node = this.manager.getContener().getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null)
            node.mouseReleased(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.getLocation().getX(), e.getY() - node.getLocation().getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
    }

    @Override
    public void mouseEntered(MouseEvent e) { // Genered by Moved !

    }

    @Override
    public void mouseExited(MouseEvent e) { // Genered by Moved !

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Node node = this.manager.getContener().getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null)
            node.mouseDragged(
                    new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            e.getX() - node.getLocation().getX(), e.getY() - node.getLocation().getY(),
                            e.getClickCount(), e.isPopupTrigger(), e.getButton()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Node node = this.manager.getContener().getCollisionWithSelectedNodes(new Location(e.getX(), e.getY()));
        if (node != null) {
            if (!node.isOver()) {
                node.setOver(true);
                node.mouseEntered(
                        new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                                e.getX(), e.getY(),
                                e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            } else
                node.mouseMoved(new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                        e.getX() - node.getLocation().getX(), e.getY() - node.getLocation().getY(),
                        e.getClickCount(), e.isPopupTrigger(), e.getButton()));
        } else
            for (Node no : this.manager.getContener().getSelectedNode())
                if (no.isOver()) {
                    no.setOver(false);
                    no.mouseExited(
                            new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                                    e.getX() - no.getLocation().getX(), e.getY() - no.getLocation().getY(),
                                    e.getClickCount(), e.isPopupTrigger(), e.getButton()));
                }

    }

    @Override
    protected void resizeChild() {
        for (Node node : nodes) {
            if (node == null) return;

            int width = node.getPreferredSize().getWidth();
            int height = node.getPreferredSize().getHeight();

            int base_width = width;
            int base_height = height;

            if (width > this.size.getWidth())
                width = this.size.getWidth();
            if (height > this.size.getHeight())
                height = this.size.getHeight();


            node.setLocation(new Location(node.getLocation().getX() - (base_width - width), node.getLocation().getY() - (base_height - height)));
            node.setSize(new Size(width, height));
        }
    }
}
