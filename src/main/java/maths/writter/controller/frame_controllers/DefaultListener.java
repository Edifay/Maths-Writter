package maths.writter.controller.frame_controllers;

import dependences.Location;
import maths.writter.Manager;
import maths.writter.controller.FrameListener;
import maths.writter.element.Node;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DefaultListener implements FrameListener {

    protected boolean multiple_select = false;
    protected Manager manager;

    public DefaultListener(Manager manager) {
        this.manager = manager;
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
}
