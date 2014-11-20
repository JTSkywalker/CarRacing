/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import java.util.ArrayList;
import java.util.Arrays;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author VAIO
 */
public class ConTrack implements start.Track{
    private int width;
    private int height;
    private ArrayList<int[]> start;
    private ArrayList<ArrayList<Integer>> wallX;
    private ArrayList<ArrayList<Integer>> wallY;
    
    public ConTrack(int Width, int Height){
        width  = Width;
        height = Height;
        start = new ArrayList<>();
        wallX = new ArrayList<>();
        wallX.add(new ArrayList<Integer>());
        wallY = new ArrayList<>();
        wallY.add(new ArrayList<Integer>());
    }
    
    
    public ConTrack(play.FinTrack track) {
        width  = track.getWidth();
        height = track.getHeight();
        start  = new ArrayList(Arrays.asList(track.getStart()));
        wallX   = track.getConWallX();
        wallY   = track.getConWallY();
    }
    
    
    @Override
    public int getWidth() {
        return width;
    }
    @Override
    public int getHeight() {
        return height;
    }

    public int[][] getStart() {
        int[][] startArr = new int[start.size()][2];
        int size = start.size();
        for(int i=0; i<size; i++) {
            startArr[i] = start.get(i);
        }
        return startArr;
    }
    
    public int[][] getWallX() {
        int size = wallX.size();
        int[][] wallXarr = new int[size][];
        for(int i=0; i<size; i++) {
            int slsize = wallX.get(i).size();
            wallXarr[i] = new int[slsize];
            for(int j=0; j<slsize; j++) {
                wallXarr[i][j] = wallX.get(i).get(j);
            }
        }
        return wallXarr;
    }
    
    public int[][] getWallY() {
        int size = wallY.size();
        int[][] wallYarr = new int[size][];
        for(int i=0; i<size; i++) {
            int slsize = wallY.get(i).size();
            wallYarr[i] = new int[slsize];
            for(int j=0; j<slsize; j++) {
                wallYarr[i][j] = wallY.get(i).get(j);
            }
        }
        return wallYarr;
    }
    /**
     * draws the Track
     */
    @Override
    public void draw(float scale) {
        //draw start
        int ssize = start.size();
        if (ssize > 1) {
            GL11.glColor3f(0, 1, 0);
            GL11.glLineWidth(4f);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int i= ssize-1; i >= 0; i--) {
                GL11.glVertex2f(start.get(i)[0] * scale,
                                start.get(i)[1] * scale);
            }
            for(int i=0; i < ssize-1; i++) {
//                v[0]*x + v[1]*y = 0
//                x^2 + y^2 = 1/4
                final int lx1 = start.get(i)[0];
                final int lx2 = start.get(i + 1)[0];
                final int ly1 = start.get(i)[1];
                final int ly2 = start.get(i + 1)[1];
                float[] v = {lx2 - lx1, ly2 - ly1};
                float x = v[1]*v[1] / (4*(v[0]*v[0]+v[1]*v[1]));
                float y = (float) Math.sqrt(0.25 - x);
                x = (float) Math.sqrt(x);
                if(lx1 < lx2) {
                    if(ly1 < ly2) {
                        x = -x;
                    }
                } else {
                    if(ly1 < ly2) {
                        x = -x;
                        y = -y;
                    } else {
                        y = -y;
                    }
                }
                x += (lx1 + lx2) / 2f;
                y += (ly1 + ly2) / 2f;
                GL11.glVertex2f(x * scale, y * scale);
                GL11.glVertex2f(lx2 * scale, ly2 * scale);
            }
            GL11.glEnd();
        }
        
        //draw wall
        GL11.glColor3f(0, 0, 1);
        GL11.glLineWidth(4f);
        for(int i=0; i < wallX.size(); i++) {
            GL11.glBegin(GL11.GL_LINE_STRIP);
            ArrayList<Integer> lineX;
            lineX = wallX.get(i);
            ArrayList<Integer> lineY;
            lineY = wallY.get(i);
            for(int j=0; j < lineX.size(); j++) {
                GL11.glVertex2f(lineX.get(j)*scale, lineY.get(j)*scale);
            }
            GL11.glEnd();
        }
    }
    
    void drawSlot(int slot, float scale) throws ArrayIndexOutOfBoundsException {
        while (wallX.size()-1 < slot) {
            wallX.add(new ArrayList<Integer>());
            wallY.add(new ArrayList<Integer>());
        }
        if (slot > -1) {
            GL11.glColor3f(1, 0, 0);
            GL11.glLineWidth(4f);
            
            ArrayList<Integer> slotX = wallX.get(slot);
            ArrayList<Integer> slotY = wallY.get(slot);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int j = 0; j < slotX.size(); j++) {
                GL11.glVertex2f(slotX.get(j) * scale, slotY.get(j) * scale);
            }
            GL11.glEnd();
            
            int lastI = slotX.size() - 1;
            if (lastI >= 0) {
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2d((slotX.get(lastI) - 0.5) * scale, (slotY.get(lastI) - 0.5) * scale);
                GL11.glVertex2d((slotX.get(lastI) + 0.5) * scale, (slotY.get(lastI) - 0.5) * scale);
                GL11.glVertex2d((slotX.get(lastI) + 0.5) * scale, (slotY.get(lastI) + 0.5) * scale);
                GL11.glVertex2d((slotX.get(lastI) - 0.5) * scale, (slotY.get(lastI) + 0.5) * scale);
                GL11.glEnd();
            }
        } else {
            GL11.glColor3f(0, 1, 0);
            GL11.glLineWidth(4f);
            int lastI = start.size() - 1;
            if (lastI >= 0) {
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2d((start.get(lastI)[0] - 0.5) * scale, (start.get(lastI)[1] - 0.5) * scale);
                GL11.glVertex2d((start.get(lastI)[0] + 0.5) * scale, (start.get(lastI)[1] - 0.5) * scale);
                GL11.glVertex2d((start.get(lastI)[0] + 0.5) * scale, (start.get(lastI)[1] + 0.5) * scale);
                GL11.glVertex2d((start.get(lastI)[0] - 0.5) * scale, (start.get(lastI)[1] + 0.5) * scale);
                GL11.glEnd();
            }    
        }
    }

    void addEdge(int x, int y, int slot) {
        int lastI = wallX.size() - 1;
        while (lastI < slot) {
            wallX.add(new ArrayList<Integer>());
            wallY.add(new ArrayList<Integer>());
        }
        if(slot > -1) {
            boolean dub;
            int lastIslot = wallX.get(slot).size() - 1;
            try {
                dub = wallX.get(slot).get(lastIslot) == x
                   && wallY.get(slot).get(lastIslot) == y;
            } catch (IndexOutOfBoundsException ex) {
                dub = false;
            }
            if (!dub) {
                wallX.get(slot).add(x);
                wallY.get(slot).add(y);
            }
        } 
        else {
            if(slot == -1) {
                boolean dub;
                int lastIstart = start.size() - 1;
                try {
                    dub = start.get(lastIstart)[0] == x
                       && start.get(lastIstart)[1] == y;
                } catch (IndexOutOfBoundsException ex) {
                    dub = false;
                }
                if (!dub) {
                    int[] edge = {x, y};
                    start.add(edge);
                }
            }
        }
    }
    
    void delEdge(int slot) throws ArrayIndexOutOfBoundsException {
        while (wallX.size()-1 < slot) {
            wallX.add(new ArrayList<Integer>());
            wallY.add(new ArrayList<Integer>());
        }
        if (slot > -1) {
            ArrayList<Integer> lineX = wallX.get(slot);
            ArrayList<Integer> lineY = wallY.get(slot);
            
            lineX.remove(lineX.size() - 1);
            lineY.remove(lineY.size() - 1);
        } else {
            if (slot == -1){
                start.remove(start.size() - 1);
            }
        }
    }

    void invert(int slot) {
        while (wallX.size()-1 < slot) {
            wallX.add(new ArrayList<Integer>());
            wallY.add(new ArrayList<Integer>());
        }
        if (slot >= 0) {
            int wallSize = wallX.get(slot).size();
            ArrayList<Integer> oldX = wallX.get(slot);
            ArrayList<Integer> oldY = wallY.get(slot);
            ArrayList<Integer> newX = new ArrayList<>(wallSize);
            ArrayList<Integer> newY = new ArrayList<>(wallSize);
            try {
                for(int i=0; i<wallSize; i++){
                    newX.add(oldX.get(wallSize - i - 1));
                    newY.add(oldY.get(wallSize - i - 1));
                }
                wallX.set(slot, newX);
                wallY.set(slot, newY);
            } catch (IndexOutOfBoundsException ex) {}
        } else if (slot == -1) {
            int sSize = start.size();
            ArrayList<int[]> neu = new ArrayList<>(sSize);
            try {
                for(int i=0; i<sSize; i++){
                    int[] arr = {start.get(sSize - i - 1)[0],
                                 start.get(sSize - i - 1)[1]};
                    neu.add(arr);
                }
                start = neu;
            } catch (IndexOutOfBoundsException ex) {}
        }
    }
}