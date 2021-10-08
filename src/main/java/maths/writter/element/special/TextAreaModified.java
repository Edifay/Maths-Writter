package maths.writter.element.special;

import dependences.Location;
import dependences.Size;
import maths.writter.Manager;
import maths.writter.element.Node;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class TextAreaModified extends Node {

    protected final AffineTransform affinetransform = new AffineTransform();
    protected final FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

    protected TextManager text_manager;
    protected Font font = new Font("Menlo", Font.PLAIN, 20);

    protected Size preferredSize;

    protected int caret_height;

    protected boolean moving_text = false;

    public TextAreaModified(Location location, Size size, Node parent) {
        this(location, size, "Empty", parent);
    }

    public TextAreaModified(Location location, Size size, String text, Node parent) {
        super(location, size, parent);
        this.text_manager = new TextManager(text);
        this.preferredSize = size.clone();
        remakePreferredSize();
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D tempo = (Graphics2D) g.create();

        g.setFont(font);
        this.text_manager.forEachLine((text, line_number) -> g.drawString(text, 0, this.caret_height * (line_number + 1)));
        if (selected) {
            if (this.text_manager.isCaretVisible())
                this.text_manager.onCaretLocation((x, y, text) -> {
                    int x_Caret = (int) font.getStringBounds((String) text.subSequence(0, x), frc).getWidth();
                    g.fillRect(x_Caret - 1, y * this.caret_height + 5, 2, this.caret_height);
                });

            if (this.text_manager.isSelect()) {
                g.setColor(new Color(55, 160, 241, 140));

                this.text_manager.forEachLineSelected((text, line_number, line_number_selected, caret_x_min, caret_x_max, min_y, max_y) -> {

                    int x = (int) font.getStringBounds((String) text.subSequence(0, Math.min(caret_x_min, text.length())), frc).getWidth();
                    int y = this.caret_height * line_number;
                    int width = (int) font.getStringBounds((String) text.subSequence(caret_x_min, Math.min(caret_x_max, text.length())), frc).getWidth();
                    int height = this.caret_height;
                    if (width == 0)
                        width = 10;
                    g.fillRoundRect(x, y + 5, width, height, 5, 5);

                });
            }
        }
        //super.draw(tempo, parent);
    }

    protected void remakePreferredSize() {
        if (text_manager != null) {
            AtomicInteger max_width = new AtomicInteger();
            this.text_manager.forEachLine((text, line_number) -> {
                int width = (int) (font.getStringBounds(text, frc).getWidth()) + 3;
                if (width > max_width.get())
                    max_width.set(width);
            });
            if (max_width.get() < 15)
                max_width.set(15);
            this.caret_height = (int) (font.getStringBounds("", frc).getHeight()) - 5;
            this.preferredSize.setSize(max_width.get(), (int) (this.caret_height * this.text_manager.size()) + 10);
        }
    }

    protected Location getCharacterIndexForWidth(int positionX, int positionY) {
        int line = getCharacterLineForHeight(positionY);
        if (positionX == 0) return new Location(0, line);
        double total = 0;
        String actual_line = this.text_manager.getLine(line);

        for (int i = 0; i < actual_line.length(); i++) {
            if (total >= positionX) {
                double caretBefore = (int) (font.getStringBounds(actual_line.substring(0, i - 1), frc).getWidth());
                double positionOnCharacter = positionX - caretBefore;

                if (positionOnCharacter > font.getStringBounds(actual_line.charAt(i - 1) + "", frc).getWidth() / 2d)
                    return new Location(i, line);
                else
                    return new Location(i - 1, line);
            }
            total += font.getStringBounds(actual_line.charAt(i) + "", frc).getWidth();
        }
        return new Location(actual_line.length(), line);
    }

    protected int getCharacterLineForHeight(int positionY) {
        return Math.min(this.text_manager.size() - 1, positionY / this.caret_height);
    }


    @Override
    public void update() {
        this.remakePreferredSize();
        super.update();
    }

    @Override
    public Size getPreferredSize() {
        System.out.println("TextArea PrefferedSize : " + this.preferredSize);
        return this.preferredSize;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    protected boolean ctrl_down = false;
    protected boolean shift_down = false;

    @Override
    public void keyPressed(KeyEvent e) {

        if (!ctrl_down) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SHIFT:
                    shift_down = true;
                    break;
                case KeyEvent.VK_CAPS_LOCK:
                case KeyEvent.VK_ENTER:
                    if (this.text_manager.isSelect())
                        this.text_manager.deleteSelect();
                    this.text_manager.insertLineAtCaret(this.text_manager.getCaretLine().substring(this.text_manager.getCaretLocation().getX()));
                    this.text_manager.deleteInLine(this.text_manager.getCaretLocation().getY(), this.text_manager.caret_location.getX(),
                            this.text_manager.getCaretLine().length() - this.text_manager.getCaretLocation().getX());
                    this.text_manager.getCaretLocation().setLocation(0, this.text_manager.getCaretLocation().getY() + 1);
                    break;
                case KeyEvent.VK_UP:
                    if (!shift_down) {
                        if (this.text_manager.isSelect())
                            this.text_manager.resetSelection();
                    } else {
                        if (this.text_manager.isNotSelect()) {
                            this.text_manager.defineSelectionPointToCaretLocation();
                        }
                    }
                    if (this.text_manager.getCaretLocation().getY() != 0) {
                        this.text_manager.getCaretLocation().setLocation(
                                Math.min(this.text_manager.getCaretLocation().getX(), this.text_manager.getLine(this.text_manager.getCaretLocation().getY() - 1).length()),
                                this.text_manager.getCaretLocation().getY() - 1
                        );
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (!shift_down) {
                        if (this.text_manager.isSelect())
                            this.text_manager.resetSelection();
                    } else {
                        if (this.text_manager.isNotSelect()) {
                            this.text_manager.defineSelectionPointToCaretLocation();
                        }
                    }
                    if (this.text_manager.getCaretLocation().getY() != this.text_manager.size() - 1) {
                        this.text_manager.getCaretLocation().setLocation(
                                Math.min(this.text_manager.getCaretLocation().getX(), this.text_manager.getLine(this.text_manager.getCaretLocation().getY() + 1).length()),
                                this.text_manager.getCaretLocation().getY() + 1
                        );
                    }
                    break;
                case KeyEvent.VK_CONTROL:
                    this.ctrl_down = true;
                    break;
                case KeyEvent.VK_DELETE:
                    if (this.text_manager.isNotSelect()) {
                        if (this.text_manager.getCaretLocation().getX() == this.text_manager.getCaretLine().length()) {
                            if (this.text_manager.getCaretLocation().getY() != this.text_manager.size() - 1) {
                                this.text_manager.getCaretLocation().setLocation(0, this.text_manager.getCaretLocation().getY() + 1);
                                int size_before = this.text_manager.getLine(this.text_manager.getCaretLocation().getY() - 1).length();
                                this.text_manager.insertInLine(this.text_manager.getCaretLocation().getY() - 1,
                                        this.text_manager.getLine(this.text_manager.getCaretLocation().getY() - 1).length(), this.text_manager.getCaretLine());
                                this.text_manager.getCaretLocation().setLocation(size_before, this.text_manager.getCaretLocation().getY() - 1);
                                this.text_manager.deleteLine(this.text_manager.getCaretLocation().getY() + 1);
                            }
                        } else
                            this.text_manager.deleteCharacterAtCaret();
                    } else
                        this.text_manager.deleteSelect();
                    break;
                case KeyEvent.VK_LEFT:
                    if (this.text_manager.getCaretLocation().getX() != 0) {
                        if (shift_down && this.text_manager.isNotSelect())
                            this.text_manager.defineSelectionPointToCaretLocation();
                        else if (!shift_down && this.text_manager.isSelect())
                            this.text_manager.resetSelection();
                        this.text_manager.moveCaretLeft();
                    } else if (!shift_down)
                        if (this.text_manager.isSelect())
                            this.text_manager.resetSelection();
                    break;
                case KeyEvent.VK_RIGHT:
                    if (this.text_manager.getCaretLocation().getX() != text_manager.getCaretLine().length()) {
                        if (shift_down && this.text_manager.isNotSelect())
                            this.text_manager.defineSelectionPointToCaretLocation();
                        else if (!shift_down && this.text_manager.isSelect())
                            this.text_manager.resetSelection();
                        this.text_manager.moveCaretRight();
                    } else if (!shift_down)
                        if (this.text_manager.isSelect())
                            this.text_manager.resetSelection();
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if (this.text_manager.isNotSelect()) {
                        if (this.text_manager.getCaretLocation().getX() == 0) {
                            if (this.text_manager.getCaretLocation().getY() != 0) {
                                int size_before = this.text_manager.getLine(this.text_manager.getCaretLocation().getY() - 1).length();
                                this.text_manager.insertInLine(this.text_manager.getCaretLocation().getY() - 1,
                                        this.text_manager.getLine(this.text_manager.getCaretLocation().getY() - 1).length(), this.text_manager.getCaretLine());
                                this.text_manager.getCaretLocation().setLocation(size_before, this.text_manager.getCaretLocation().getY() - 1);
                                this.text_manager.deleteLine(this.text_manager.getCaretLocation().getY() + 1);
                            }
                        } else {
                            this.text_manager.deleteCharacterInLine(this.text_manager.getCaretLocation().getY(), this.text_manager.getCaretLocation().getX() - 1);
                            this.text_manager.getCaretLocation().setX(this.text_manager.getCaretLocation().getX() - 1);
                        }
                    } else
                        this.text_manager.deleteSelect();

                    break;
                default:
                    if (this.text_manager.isSelect())
                        this.text_manager.deleteSelect();
                    this.text_manager.insertLineAtCharacter(e.getKeyChar() + "");
                    this.text_manager.moveCaretRight();
                    break;
            }
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    this.text_manager.getSelectionPoint().setLocation(0, 0);
                    this.text_manager.setCaret_location(new Location(this.text_manager.getLine(this.text_manager.size() - 1).length(), this.text_manager.size() - 1));
                    break;
                case KeyEvent.VK_C:
                    if (this.text_manager.isSelect()) {
                        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
                        StringSelection selection = new StringSelection(this.text_manager.selectedToString());
                        system.setContents(selection, selection);
                    }
                    break;
                case KeyEvent.VK_X:
                    if (this.text_manager.isSelect()) {
                        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
                        StringSelection selection = new StringSelection(this.text_manager.selectedToString());
                        system.setContents(selection, selection);
                        this.text_manager.deleteSelect();
                    }
                    break;
                case KeyEvent.VK_V:
                    try {
                        Clipboard system_x = Toolkit.getDefaultToolkit().getSystemClipboard();
                        String at_add = (String) system_x.getData(DataFlavor.stringFlavor);
                        this.text_manager.insertLineAtCharacter(at_add);
                        if (this.text_manager.isSelect())
                            this.text_manager.deleteSelect();
                        this.text_manager.setCaret_location(new Location(this.text_manager.getCaretLocation().getX() + at_add.length(), this.text_manager.getCaretLocation().getY()));
                    } catch (UnsupportedFlavorException | IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
        }
        this.text_manager.updateTimeCaret();
        this.update();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_CONTROL:
                this.ctrl_down = false;
                break;
            case KeyEvent.VK_SHIFT:
                this.shift_down = false;
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Location location = getCharacterIndexForWidth(e.getX(), e.getY());
        if (this.text_manager.isSelect()) {
            if (!((Math.max(this.text_manager.getSelectionPoint().getX(), this.text_manager.getCaretLocation().getX())) > location.getX() && Math.min(this.text_manager.getSelectionPoint().getX(), this.text_manager.getCaretLocation().getX()) < location.getX())) {
                this.text_manager.resetSelection();
                this.text_manager.getCaretLocation().setLocation(location);
            } else
                this.moving_text = true;
        } else
            this.text_manager.getCaretLocation().setLocation(location);
        this.text_manager.updateTimeCaret();
    }

    protected boolean pivot = false;

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!moving_text && this.text_manager.isSelect()) {
            Location location = getCharacterIndexForWidth(e.getX(), e.getY());
            this.text_manager.getCaretLocation().setLocation(location);
            if (this.text_manager.isSelect()) {
                if ((Math.max(this.text_manager.getSelectionPoint().getX(), location.getX())) > this.text_manager.getCaretLocation().getX() && Math.min(this.text_manager.getSelectionPoint().getX(), location.getX()) < this.text_manager.getCaretLocation().getX())
                    if (!pivot) {
                        pivot = true;
                    } else {
                        pivot = false;
                        this.text_manager.resetSelection();
                    }
            }
        } else {
            Manager.manager_last_Manager.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            moving_text = false;
            this.text_manager.resetSelection();
            Location location = getCharacterIndexForWidth(e.getX(), e.getY());
            this.text_manager.getCaretLocation().setLocation(location);
        }
        this.text_manager.updateTimeCaret();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Manager.manager_last_Manager.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Manager.manager_last_Manager.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!moving_text) {
            pivot = false;
            if (this.text_manager.isNotSelect())
                this.text_manager.defineSelectionPointToCaretLocation();
            Location location = getCharacterIndexForWidth(e.getX(), e.getY());
            this.text_manager.getCaretLocation().setLocation(location);
            this.text_manager.updateTimeCaret();
        } else
            Manager.manager_last_Manager.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void setSelected(boolean selected) {
        if (!selected && this.text_manager.getString().replaceAll("\n", "").replaceAll(" ", "").equals("")) {
            this.text_manager.insertInLine(0, 0, "empty");
            this.update();
        }
        pivot = false;
        ctrl_down = false;
        this.text_manager.resetSelection();
        moving_text = false;
        shift_down = false;
        super.setSelected(selected);
    }

    public String getBeforeCaret() {
        StringBuilder stringBuilder = new StringBuilder();
        this.text_manager.forEachLine((text, line_number) -> {
            if (line_number < this.text_manager.getCaretLineNumber())
                stringBuilder.append(text).append("\n");
            else if (line_number == this.text_manager.getCaretLineNumber())
                stringBuilder.append(text.substring(0, this.text_manager.getCaretLocation().getX()));
        });
        return stringBuilder.toString();
    }

    public String getAfterCaret() {
        StringBuilder stringBuilder = new StringBuilder();
        this.text_manager.forEachLine((text, line_number) -> {
            if (line_number > this.text_manager.getCaretLineNumber()) {
                stringBuilder.append(text);
                if (line_number != this.text_manager.size() - 1)
                    stringBuilder.append("\n");
            } else if (line_number == this.text_manager.getCaretLineNumber())
                stringBuilder.append(text.substring(this.text_manager.getCaretLocation().getX()));
        });
        return stringBuilder.toString();
    }
}
