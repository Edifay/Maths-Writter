package maths.writter.controller;

import maths.writter.Manager;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface FrameListener extends KeyListener, MouseListener, MouseMotionListener {

    void dispose();

}
