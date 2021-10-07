package maths.writter.controller.frame_controllers;

import dependences.Location;
import dependences.RectangleArea;
import dependences.Size;
import maths.writter.Manager;
import maths.writter.controller.FrameListener;
import maths.writter.element.FractionNode;
import maths.writter.element.Node;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class NewNodeListener implements FrameListener {

    protected final Object key = new Object();
    protected Manager manager;
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
            actual = new FractionNode(default_location.clone(), new Size(100, 100));
            this.manager.getContener().addNode(actual);
            this.manager.getContener().addNodeSelected(actual);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (key) {
            if (actual != null) {
                RectangleArea rectangleArea = getWidth(new Location(e.getX(), e.getY()));
                actual.setLocation(rectangleArea.getLocation());
                actual.setSize(rectangleArea.getSize());
                actual = null;
            }
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
                RectangleArea rectangleArea = getWidth(new Location(e.getX(), e.getY()));
                actual.setLocation(rectangleArea.getLocation());
                actual.setSize(rectangleArea.getSize());
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    private RectangleArea getWidth(Location location) {
        RectangleArea rectangleArea = new RectangleArea();
        if (location.getX() > this.default_location.getX()) {
            rectangleArea.getLocation().setX(this.default_location.getX());
            rectangleArea.getSize().setWidth(location.getX() - this.default_location.getX());
        } else {
            rectangleArea.getLocation().setX(location.getX());
            rectangleArea.getSize().setWidth(this.default_location.getX() - location.getX());
        }

        if (location.getY() > this.default_location.getY()) {
            rectangleArea.getLocation().setY(this.default_location.getY());
            rectangleArea.getSize().setHeight(location.getY() - this.default_location.getY());
        } else {
            rectangleArea.getLocation().setY(location.getY());
            rectangleArea.getSize().setHeight(this.default_location.getY() - location.getY());
        }
        return rectangleArea;
    }
}
