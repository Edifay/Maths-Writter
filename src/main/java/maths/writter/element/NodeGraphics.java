package maths.writter.element;

import maths.writter.controller.FrameListener;
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

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g2d.setClip(-100, -100, 1920, 1080);

        this.contener.draw(g2d);
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
