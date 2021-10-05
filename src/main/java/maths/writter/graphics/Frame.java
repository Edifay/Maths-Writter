package maths.writter.graphics;

import maths.writter.element.FrameContenerNode;
import maths.writter.element.NodeGraphics;

import javax.swing.*;

public class Frame extends JFrame {

    public NodeGraphics graphics;

    public Frame(FrameContenerNode contener) {

        graphics = new NodeGraphics(this, contener);

        this.setSize(contener.getSize().getWidth(), contener.getSize().getHeight());
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(graphics);
        this.setUndecorated(true);
        this.setVisible(true);

        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(3);
                    repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
