package maths.writter.element;

import dependences.Location;
import dependences.Size;
import maths.writter.controller.FrameListener;
import maths.writter.element.special.TextAreaModified;
import maths.writter.graphics.Frame;

import javax.swing.*;
import java.awt.*;


public class NodeGraphics extends JPanel {

    public static final Font default_font = new Font("", Font.PLAIN, 12);

    private FrameListener lastFrameListener;
    protected Frame frame;

    protected FrameContenerNode contener;

    public NodeGraphics(Frame frame, FrameContenerNode contener) {
        super();
        this.frame = frame;
        this.setFocusable(true);
        this.requestFocus();

        this.contener = contener;
/*
        this.addKeyListener(this.textAreaModified);
        this.addMouseListener(this.textAreaModified);
        this.addMouseMotionListener(this.textAreaModified);*/
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        /*this.textAreaModified.draw(g.create(this.textAreaModified.getLocation().getX(), this.textAreaModified.getLocation().getY(),
                this.textAreaModified.getSize().getWidth() + 1, this.textAreaModified.getSize().getHeight() + 1));*/
        this.contener.draw((Graphics2D) g.create(this.contener.location.getX(), this.contener.location.getY(),
                this.contener.size.getWidth(), this.contener.size.getHeight()));
        /*
        for (Node node : this.frame.getNodes())
            node.draw((Graphics2D) g.create(node.real_location.getX(), node.real_location.getY(), node.real_size.getWidth(), node.real_size.getHeight()), null);*/
    }

    public synchronized void setFrameListener(FrameListener frameListener) {
        if (lastFrameListener != null) {
            this.removeMouseListener(this.lastFrameListener);
            this.removeKeyListener(this.lastFrameListener);
            this.removeMouseMotionListener(this.lastFrameListener);
            this.lastFrameListener.dispose();
        }
        this.addMouseListener(frameListener);
        this.addKeyListener(frameListener);
        this.addMouseMotionListener(frameListener);
        this.lastFrameListener = frameListener;
    }
}
