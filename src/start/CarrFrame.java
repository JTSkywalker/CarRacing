/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import play.ReachGoalExc;

/**
 *
 * @author VAIO
 */
public abstract class CarrFrame {
    private static final float stdWidth = 1200;
    private static final float stdHeight = 750;

    protected float Dwidth;
    protected float Dheight;
    protected int wid;//width of track
    protected int hei;//height of track
    protected float scale;
    private boolean fullscreen;
    protected int laps;

    public void run() throws LWJGLException, ReachGoalExc {
        wid = getWidth();
        hei = getHeight();
        if (wid*stdHeight > hei*stdWidth){
            Dwidth = stdWidth;
            Dheight = stdWidth*hei/wid;
            scale = Dwidth/wid;
        } else {
            Dwidth = stdHeight*wid/hei;
            Dheight = stdHeight;
            scale = Dheight/hei;
        }

        setupDisplay();
        Keyboard.create();
        Mouse.create();
        setupOGL();

		try {
			while (!Display.isCloseRequested() && laps > 0) {
				GL11.glClearColor(1f, 1f, 1f, 1f);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				render();

				Display.update();
				Display.sync(30);
			}
		} finally {
			Display.destroy();
		}
    }

    protected void setupDisplay() throws LWJGLException {
        if(!fullscreen)
            Display.setDisplayMode(new DisplayMode((int) Dwidth,
                                                   (int) Dheight));
        else {
            Display.setDisplayMode(Display.getDesktopDisplayMode());
            Display.setFullscreen(true);
        }
        Display.create();
//        Dwidth = Display.getWidth();
//        Dheight = Display.getHeight();
    }

    protected void setupOGL() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Dwidth, 0, Dheight, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    /**
     * draw a virgin sheet of paper
     * @param width
     * @param height
     */
    protected void drawSheet(){//maybe add some fancy stuff
        int width = getWidth();
        int height = getHeight();
        GL11.glLineWidth(2f);
        GL11.glColor3f(0.8f, 0.8f, 0.8f);
        GL11.glBegin(GL11.GL_LINES);
        for(float x=0; x<Dwidth; x+= Dwidth/width) {
            GL11.glVertex2f(x, 0);
            GL11.glVertex2f(x, Dheight);
        }
        for(float y=0; y<Dheight; y+= Dheight/height) {
            GL11.glVertex2f(0, y);
            GL11.glVertex2f(Dwidth, y);
        }
        GL11.glEnd();
    }

    protected abstract void render() throws ReachGoalExc;
    protected abstract int getWidth();
    protected abstract int getHeight();
}
