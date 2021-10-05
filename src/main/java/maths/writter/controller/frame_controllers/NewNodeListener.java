package maths.writter.controller.frame_controllers;

import dependences.Location;
import dependences.Size;
import maths.writter.Manager;
import maths.writter.controller.FrameListener;
import maths.writter.element.Node;
import maths.writter.element.TextNode;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class NewNodeListener implements FrameListener {

    protected Manager manager;

    protected final Object key = new Object();
    protected Node actual = null;
    protected Size size;

    protected Location default_location;

    public NewNodeListener(Manager manager) {
        this.manager = manager;
        this.manager.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void dispose() {
        synchronized (key) {
            if (actual != null)
                this.manager.getContener().removeNode(actual);
            this.manager.setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.manager.changeListener(new DefaultListener(this.manager));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (key) {
            default_location = new Location(e.getX(), e.getY());
            actual = new TextNode(default_location.clone(), new Size(100, 100));
            this.manager.getContener().addNode(actual);
            this.manager.getContener().addNodeSelected(actual);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (key) {
            if (actual != null)
                actual.setSize(new Size(e.getX() - actual.getLocation().getX(), e.getY() - actual.getLocation().getY()));
            actual = null;
            this.manager.changeListener(new DefaultListener(this.manager));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        synchronized (key) {
            if (actual != null) {
                Size size = new Size(e.getX() - actual.getLocation().getX(), e.getY() - actual.getLocation().getY());
                if (size.getWidth() < 0) {
                    actual.setLocation(default_location);
                } else {
                }
                actual.setSize(new Size(1500, 050));
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
