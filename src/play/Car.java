/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package play;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author VAIO
 */
class Car {
    private byte[] color = new byte[3];
    String name;
    boolean alive = true;
    private int lapsleft;
    int lastfin = 0;
    private final ArrayList<Integer> traceX;
    private final ArrayList<Integer> traceY;

    Car(byte[] col, String nam, int laps) {
        color = col;
        name = nam;
        lapsleft = laps;
        traceX = new ArrayList();
        traceY = new ArrayList();
    }

    void drive(int x, int y) throws ReachGoalExc {
//            if(Math.abs(x - lazy[0]) <= 1 && Math.abs(y - lazy[1]) <= 1) {
        traceX.add(x);
        traceY.add(y);
        if (lapsleft == 0)
            throw new ReachGoalExc(this);
    }

    void die() {
        alive = false;
    }

    int[] getLazy() throws StartExc {
        int[] lazy = new int[2];
        try {
            int lastI = traceY.size() - 1;
            lazy[0] = 2*traceX.get(lastI) - traceX.get(lastI - 1);
            lazy[1] = 2*traceY.get(lastI) - traceY.get(lastI - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            try {
                int lastI = traceY.size() - 1;
                lazy[0] = traceX.get(lastI);
                lazy[1] = traceY.get(lastI);
            } catch (ArrayIndexOutOfBoundsException ex) {
                throw new StartExc();
            }
        }
        return lazy;
    }

    int[] getLast() throws StartExc {
        try {
            int lastI = traceX.size() - 1;
            int[] last = {traceX.get(lastI), traceY.get(lastI)};
            return last;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new StartExc();
        }
    }

	int getLapsLeft() {
		return lapsleft;
	}

    void draw (float scale) {
        GL11.glLineWidth(4f);
        byte[] c = {(byte) (color[0]/8 +112),
                    (byte) (color[1]/8 +112),
                    (byte) (color[2]/8 +112)};
        GL11.glColor3b(c[0], c[1], c[2]);
        int tsize = traceX.size();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int j = 0; j < lastfin; j++) {
            GL11.glVertex2f(traceX.get(j) * scale, traceY.get(j) * scale);
        }
        GL11.glColor3b(color[0],
                       color[1],
                       color[2]);
        for (int j = lastfin; j < tsize; j++) {
            GL11.glVertex2f(traceX.get(j) * scale, traceY.get(j) * scale);
        }
        GL11.glEnd();

        GL11.glColor3b(c[0], c[1], c[2]);
        for (int j = 0; j < lastfin; j++) {
            drawDot(traceX.get(j), traceY.get(j), scale, true);
        }
        GL11.glColor3b(color[0],
                       color[1],
                       color[2]);
        for (int j = lastfin; j < tsize; j++) {
            drawDot(traceX.get(j), traceY.get(j), scale, false);
        }
    }

    void drawPos(float scale) {
        GL11.glColor3b(color[0],
                       color[1],
                       color[2]);
        int lastI = traceX.size() - 1;
        if (lastI >= 0) {
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2d((traceX.get(lastI) - 0.3) * scale,
                            (traceY.get(lastI) - 0.3) * scale);
            GL11.glVertex2d((traceX.get(lastI) + 0.3) * scale,
                            (traceY.get(lastI) - 0.3) * scale);
            GL11.glVertex2d((traceX.get(lastI) + 0.3) * scale,
                            (traceY.get(lastI) + 0.3) * scale);
            GL11.glVertex2d((traceX.get(lastI) - 0.3) * scale,
                            (traceY.get(lastI) + 0.3) * scale);
            GL11.glEnd();
        }
    }

    void drawDot(int x, int y, float scale, boolean old) {
        if (!traceX.isEmpty()) {
            GL11.glBegin(GL11.GL_QUADS);
              GL11.glVertex2d((x - 0.2) * scale,
                      (y - 0.2) * scale);
              GL11.glVertex2d((x + 0.2) * scale,
                      (y - 0.2) * scale);
              GL11.glVertex2d((x + 0.2) * scale,
                      (y + 0.2) * scale);
              GL11.glVertex2d((x - 0.2) * scale,
                      (y + 0.2) * scale);
            GL11.glEnd();
        }
    }

    void drawCrosses(int[][] poss, float scale) {
        GL11.glColor3b(color[0],
        color[1],
        color[2]);
        GL11.glLineWidth(2f);
        for(int[] move : poss) {
            GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2d((move[0] - 0.25) * scale,
                                (move[1] + 0.25) * scale);
                GL11.glVertex2d((move[0] + 0.25) * scale,
                                (move[1] - 0.25) * scale);
                GL11.glVertex2d((move[0]) * scale,
                                (move[1]) * scale);
                GL11.glVertex2d((move[0] + 0.25) * scale,
                                (move[1] + 0.25) * scale);
                GL11.glVertex2d((move[0] - 0.25) * scale,
                                (move[1] - 0.25) * scale);
            GL11.glEnd();
        }
    }

    void drawDead(float scale) throws StartExc {
        GL11.glColor3b(color[0],
                       color[1],
                       color[2]);
        try {
            int[] p = getLast();
            GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2d((p[0] - 0.6) * scale,
                                (p[1] - 0.2) * scale);
                GL11.glVertex2d((p[0] + 0.6) * scale,
                                (p[1] - 0.2) * scale);
                GL11.glVertex2d((p[0] + 0.6) * scale,
                                (p[1] + 0.2) * scale);
                GL11.glVertex2d((p[0] - 0.6) * scale,
                                (p[1] + 0.2) * scale);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2d((p[0] - 0.2) * scale,
                                (p[1] - 0.9) * scale);
                GL11.glVertex2d((p[0] + 0.2) * scale,
                                (p[1] - 0.9) * scale);
                GL11.glVertex2d((p[0] + 0.2) * scale,
                                (p[1] + 0.6) * scale);
                GL11.glVertex2d((p[0] - 0.2) * scale,
                                (p[1] + 0.6) * scale);
            GL11.glEnd();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new StartExc();
        }
    }

    void incL() {
        lapsleft++;
        System.out.println(lapsleft);
    }

    void decL() {
        lapsleft--;
        System.out.println(lapsleft);
    }
}