/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package play;

import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author VAIO
 * Just a simple track no super awesome extra features...yet...
 */


public class FinTrack implements start.Track, Serializable {
    private int width;
    private int height;
    private int[][] start;
    private int[][] wallX;
    private int[][] wallY; 
    
    public FinTrack(editor.ConTrack track) {
        width  = track.getWidth();
        height = track.getHeight();
        start  = track.getStart();
        wallX   = track.getWallX();
        wallY   = track.getWallY();
    }
    
    //the following 2 constructors may be dispensable
    public FinTrack(int Width, int Height){
        width  = Width;
        height = Height;
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
        return start;
    }
    
    public int[][] getWallX() {
        return wallX;
    }
    
    public int[][] getWallY() {
        return wallY;
    }
    
    public ArrayList<ArrayList<Integer>> getConWallX() {
        int size = wallX.length;
        ArrayList<ArrayList<Integer>> conWall = new ArrayList<>(size);
        for(int i=0;i<size;i++) {
            conWall.add(new ArrayList());
            int[] arr = wallX[i];
            for(int a : arr){
                conWall.get(i).add(a);
            }
        }
        return conWall;
    }
    
    public ArrayList<ArrayList<Integer>> getConWallY() {
        int size = wallY.length;
        ArrayList<ArrayList<Integer>> conWall = new ArrayList<>(size);
        for(int i=0;i<size;i++) {
            conWall.add(new ArrayList());
            int[] arr = wallY[i];
            for(int a : arr){
                conWall.get(i).add(a);
            }
        }
        return conWall;
    }
    

    /**
     * draws the Track
     */
    @Override
    public void draw(float scale) {
        //draw start
        int ssize = start.length;
        if(ssize > 1) {
            GL11.glColor3f(0.5f, 0.5f, 0.5f);
            GL11.glLineWidth(4f);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int i= ssize-1; i >= 0; i--) {
                GL11.glVertex2f(start[i][0] * scale,
                                start[i][1] * scale);
            }
            for(int i=0; i < ssize-1; i++) {
//                v[0]*x + v[1]*y = 0
//                x^2 + y^2 = 1/4
                final int lx1 = start[i][0];
                final int lx2 = start[i + 1][0];
                final int ly1 = start[i][1];
                final int ly2 = start[i + 1][1];
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
        GL11.glColor3f(0.3f, 0.3f, 0.3f);
        GL11.glLineWidth(4f);
        for(int i=0; i < wallX.length; i++) {
            GL11.glBegin(GL11.GL_LINE_STRIP);
            int[] lineX = wallX[i];
            int[] lineY = wallY[i];
            for(int j=0; j < lineX.length; j++) {
                GL11.glVertex2f(lineX[j]*scale, lineY[j]*scale);
            }
            GL11.glEnd();
        }
    }
    
    public boolean isStart(int x, int y) {
        for(int i=0;i<start.length;i++) {
            if(start[i][0] == x && start[i][1] == y)
                return true;
        }
        return false;
    }
}
