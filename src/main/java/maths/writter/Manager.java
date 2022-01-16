package maths.writter;

import dependences.Location;
import dependences.Size;
import maths.writter.controller.FrameListener;
import maths.writter.element.FrameContenerNode;
import maths.writter.element.Node;
import maths.writter.graphics.Frame;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Manager {

    public static Manager manager_last_Manager;

    protected final FrameContenerNode contener;

    protected Frame frame;

    public Manager() {
        contener = new FrameContenerNode(new Location(0, 0), new Size(1280, 720), this);
        frame = new Frame(contener);
        frame.graphics.setFrameListener(contener);
        manager_last_Manager = this;
    }

    public void changeListener(FrameListener frameListener) {
        this.frame.graphics.setFrameListener(frameListener);
    }

    public void setCursor(Cursor cursor) {
        frame.setCursor(cursor);
    }

    public static boolean isCollision(Node node, Location location) {
        return !(node.getLocation().getX() > location.getX() || node.getLocation().getY() > location.getY()
                || node.getSize().getWidth() + node.getLocation().getX() < location.getX()
                || node.getSize().getHeight() + node.getLocation().getY() < location.getY());
    }

    public FrameContenerNode getContener() {
        return this.contener;
    }

    private static ExecutorService exe = Executors.newCachedThreadPool();

    public static void startAway(Runnable runnable) {
        exe.submit(runnable);
    }

}