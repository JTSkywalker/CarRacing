package editor;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import play.FinTrack;
import play.ReachGoalExc;
import start.CarrFrame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author VAIO
 */
public class TrackEditor extends CarrFrame {
    protected ConTrack track;
    protected int selectedSlot;//-1: draw start, else: number = number of line to draw

    public TrackEditor(int height, int width) {
        track = new ConTrack(height, width);
    }

    public TrackEditor(FinTrack track) {
        this.track = new ConTrack(track);
    }

    public FinTrack edit() throws LWJGLException {
        selectedSlot = 0;
		try {
			run();
		} catch (ReachGoalExc ex) {
			Logger.getLogger(TrackEditor.class.getName()).log(Level.SEVERE, null, ex);
		}
        // not sure whether there is something missing...don't think so
        FinTrack trackF = new FinTrack(track);//exception?
        return trackF;
    }

    /**
     * render: draw track,
     */
    @Override
    protected void render() {
        drawSheet();
        track.draw(scale);
        try {
            track.drawSlot(selectedSlot, scale);
        } catch (ArrayIndexOutOfBoundsException ex) {}
        if(Mouse.isButtonDown(0)) {
            int x = Math.round(Mouse.getX()/scale);
            int y = Math.round(Mouse.getY()/scale);
            track.addEdge(x, y, selectedSlot);
        }

        while(Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case (Keyboard.KEY_BACK):
                        try {
                            track.delEdge(selectedSlot);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            //do nothing
                        }
                        break;
                    case (Keyboard.KEY_1):
                        selectedSlot = 0;
                        break;
                    case (Keyboard.KEY_2):
                        selectedSlot = 1;
                        break;
                    case (Keyboard.KEY_3):
                        selectedSlot = 2;
                        break;
                    case (Keyboard.KEY_4):
                        selectedSlot = 3;
                        break;
                    case (Keyboard.KEY_5):
                        selectedSlot = 4;
                        break;
                    case (Keyboard.KEY_6):
                        selectedSlot = 5;
                        break;
                    case (Keyboard.KEY_7):
                        selectedSlot = 6;
                        break;
                    case (Keyboard.KEY_8):
                        selectedSlot = 7;
                        break;
                    case (Keyboard.KEY_9):
                        selectedSlot = 8;
                        break;
                    case (Keyboard.KEY_0):
                        selectedSlot = 9;
                        break;
                    case (Keyboard.KEY_LEFT):
                        if (selectedSlot > 0) {
                            selectedSlot--;
                        }
                        break;
                    case (Keyboard.KEY_RIGHT):
                        // I should definately change this. There should always be only one free slot
                        selectedSlot++;
                        break;
                    case (Keyboard.KEY_LSHIFT):
                        track.invert(selectedSlot);
                        break;
                    case (Keyboard.KEY_RSHIFT):
                        track.invert(selectedSlot);
                        break;
                    case (Keyboard.KEY_S):
                        selectedSlot = -1;
                        break;
                }
            }
        }
    }

    @Override
    protected int getWidth() {
        return track.getWidth();
    }

    @Override
    protected int getHeight() {
        return track.getHeight();
    }
}