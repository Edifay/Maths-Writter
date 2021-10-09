package maths.writter.element.special;

import dependences.Location;

import java.util.ArrayList;
import java.util.Arrays;

public class TextManager {

    private static int timerVisible = 750;
    protected boolean isPair = false;

    protected ArrayList<String> text;

    protected Location caret_location;
    protected Location selection_point = new Location(-1, -1);

    protected long last_time_change_caret;

    public TextManager(String text) {
        this.text = new ArrayList<>();
        this.caret_location = new Location(0, 0);
        String[] lines = text.split("\n");
        this.text.addAll(Arrays.asList(lines));
        updateTimeCaret();
    }

    protected int size() {
        return this.text.size();
    }

    public synchronized void forEachLine(LineRunner lineRunner) {
        for (int i = 0; i < this.text.size(); i++)
            lineRunner.run(this.text.get(i), i);
    }

    public synchronized void forEachLineSelected(LineSelectedRunner lineSelectedRunner) {

        int min_y = Math.min(this.caret_location.getY(), this.selection_point.getY());
        int max_y = Math.max(this.caret_location.getY(), this.selection_point.getY());

        int min_x = Math.min(this.caret_location.getX(), this.selection_point.getX());
        int max_x = Math.max(this.caret_location.getX(), this.selection_point.getX());

        for (int i = min_y; i < max_y + 1; i++) {

            String actual_text = this.text.get(i);

            if (min_y == i) {
                if (this.caret_location.getY() == min_y && this.selection_point.getY() != min_y)
                    min_x = this.caret_location.getX();
                else if (this.selection_point.getY() == min_y && this.caret_location.getY() != min_y)
                    min_x = this.selection_point.getX();
            } else
                min_x = 0;

            if (max_y == i) {
                if (this.caret_location.getY() == max_y && this.selection_point.getY() != max_y)
                    max_x = this.caret_location.getX();
                else if (this.selection_point.getY() == max_y && this.caret_location.getY() != max_y)
                    max_x = this.selection_point.getX();
            } else
                max_x = actual_text.length();


            lineSelectedRunner.run(actual_text, i, i - min_y, min_x, max_x, min_y, max_y);
        }
    }

    public synchronized void onCaretLocation(CaretRunner runner) {
        runner.run(this.caret_location.getX(), this.caret_location.getY(), this.text.get(this.caret_location.getY()));
    }

    public synchronized void insertInLine(int line, int offset, String at_insert) {
        this.text.set(line, new StringBuilder(this.text.get(line)).insert(offset, at_insert).toString());
    }

    public synchronized void insertLine(int offset, String line) {
        this.text.add(offset, line);
    }

    public synchronized void insertLineAtCharacter(String at_insert) {
        this.insertInLine(this.caret_location.getY(), this.caret_location.getX(), at_insert);
    }

    public synchronized void deleteInLine(int line, int offset, int length) {
        this.text.set(line, new StringBuilder(this.text.get(line)).delete(offset, offset + length).toString());
    }

    public synchronized void deleteInLineWithOutReplace(int line, int offset, int length) {
        this.text.set(line, new StringBuilder(this.text.get(line)).delete(offset, offset + length).toString());
    }

    public synchronized void deleteCharacterInLine(int line, int index) {
        deleteInLine(line, index, 1);
    }

    public synchronized void deleteCharacterAtCaret() {
        deleteCharacterInLine(this.caret_location.getY(), this.caret_location.getX());
    }

    public synchronized void deleteSelect() {
        final int max_y = Math.max(this.caret_location.getY(), this.selection_point.getY());
        final int min_y = Math.min(this.caret_location.getY(), this.selection_point.getY());

        final int min_x = Math.min(this.caret_location.getX(), this.selection_point.getX());
        if (this.caret_location.getY() != this.selection_point.getY()) {
            this.forEachLineSelected((string, line_number, line_number_selected, caret_x_min, caret_x_max, min_y_caret, max_y_caret) -> {
                if (line_number == min_y)
                    this.deleteInLineWithOutReplace(line_number, caret_x_min, caret_x_max - caret_x_min);
                else if (line_number == max_y)
                    this.insertInLine(min_y_caret, this.getLine(min_y_caret).length(), string.substring(caret_x_max));
            });
            if (max_y >= min_y + 1)
                this.text.subList(min_y + 1, max_y + 1).clear();
        } else
            this.deleteInLine(this.caret_location.getY(), min_x,
                    Math.abs(this.caret_location.getX() - this.selection_point.getX()));
        this.caret_location.setLocation(min_x, min_y);
        this.resetSelection();
    }

    public synchronized void appendLine(String text) {
        this.text.add(text);
    }

    public synchronized void setCaret_location(Location caret_location) {
        this.caret_location.setLocation(caret_location);
    }

    public synchronized void setSelection_point(Location selection_point) {
        this.selection_point.setLocation(selection_point);
    }

    public synchronized void resetSelection() {
        this.selection_point.setLocation(-1, -1);
    }

    public synchronized String getString() {
        StringBuilder atReturn = new StringBuilder();
        for (int i = 0; i < this.text.size(); i++)
            if (i != this.text.size() - 1)
                atReturn.append(this.text.get(i)).append("\n");
            else
                atReturn.append(this.text.get(i));
        return atReturn.toString();
    }

    public boolean isSelect() {
        return !isNotSelect();
    }

    public boolean isNotSelect() {
        return this.selection_point.getX() == -1 && this.selection_point.getY() == -1;
    }

    public synchronized String getLine(int index) {
        return this.text.get(index);
    }

    public Location getCaretLocation() {
        return caret_location;
    }

    public Location getSelectionPoint() {
        return selection_point;
    }

    public synchronized String getCaretLine() {
        return this.text.get(this.caret_location.getY());
    }

    public int getCaretLineNumber() {
        return this.caret_location.getY();
    }

    public synchronized void defineSelectionPointToCaretLocation() {
        this.setSelection_point(this.caret_location);
    }

    public synchronized void moveCaretLeft() {
        this.caret_location.setX(this.caret_location.getX() - 1);
    }

    public synchronized void moveCaretRight() {
        this.caret_location.setX(this.caret_location.getX() + 1);
    }

    public synchronized String selectedToString() {
        StringBuilder atReturn = new StringBuilder("");
        this.forEachLineSelected((text1, line_number, line_number_selected, caret_x_min, caret_x_max, min_y, max_y) -> {
            atReturn.append(text1, caret_x_min, caret_x_max);
            if (line_number != max_y)
                atReturn.append("\n");
        });
        return atReturn.toString();
    }

    public String getBeforeCaret() {
        StringBuilder stringBuilder = new StringBuilder();
        this.forEachLine((text, line_number) -> {
            if (line_number < this.getCaretLineNumber())
                stringBuilder.append(text).append("\n");
            else if (line_number == this.getCaretLineNumber())
                stringBuilder.append(text.substring(0, this.getCaretLocation().getX()));
        });
        return stringBuilder.toString();
    }

    public String getAfterCaret() {
        StringBuilder stringBuilder = new StringBuilder();
        this.forEachLine((text, line_number) -> {
            if (line_number > this.getCaretLineNumber()) {
                stringBuilder.append(text);
                if (line_number != this.size() - 1)
                    stringBuilder.append("\n");
            } else if (line_number == this.getCaretLineNumber())
                stringBuilder.append(text.substring(this.getCaretLocation().getX()));
        });
        return stringBuilder.toString();
    }

    public synchronized void insertLineAtCaret(String text) {
        this.insertLine(this.caret_location.getY() + 1, text);
    }

    public synchronized void deleteLine(int line) {
        this.text.remove(line);
    }

    public void updateTimeCaret() {
        this.last_time_change_caret = System.currentTimeMillis();
        if (!isCaretVisible())
            isPair = !isPair;
    }

    public boolean isCaretVisible() {
        long actual = System.currentTimeMillis();
        long difference = actual - this.last_time_change_caret;
        if (difference < 900)
            return isPair;
        long division_result = difference / timerVisible;

        return isPair ? division_result % 2 == 0 : division_result % 2 == 1;
    }

}
