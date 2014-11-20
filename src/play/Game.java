/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package play;

import java.util.ArrayList;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import start.CarrFrame;

/**
 *
 * @author VAIO
 */
public class Game extends CarrFrame {
    static byte[][] colors = {{(byte) 127,(byte)-128,(byte)-128},
                              {(byte)-128,(byte) 127,(byte)-128},
                              {(byte)-128,(byte)-128,(byte) 127},
                              {(byte) 127,(byte) 127,(byte)-128},
                              {(byte) 127,(byte)-128,(byte) 127},
                              {(byte)-128,(byte) 127,(byte) 127},
                              {(byte) 127,(byte)  87,(byte)-128}};
    static String[] names = {"Red","Green","Blue","Yellow",
                             "Violet","Turquis","Orange"};

    FinTrack track;
    private Car[] cars;
    int num;
    private byte nex = 0;
    int[][] poss;

    private int turns;

    public Game(int humans, int ai, FinTrack trackC, int lapsC) {//AI not supported
        cars = new Car[humans];
        for (byte i=0; i<humans; i++) {
            cars[i] = new Car(colors[i],names[i],lapsC);
        }
        track = trackC;
        laps = lapsC;
        num = humans;
    }

    public void play() throws LWJGLException, ReachGoalExc {
        wid = getWidth();
        hei = getHeight();
        try {
            //do something
            poss = getPoss();
        } catch (DoomedToDieExc ex) {
            System.out.println(cars[nex].name + " is doomed to die.");
            cars[nex].die();
            next();
        }
        run();
        //show winner in popup
    }

    @Override
    protected void render() throws ReachGoalExc {
        drawSheet();
        track.draw(scale);
        for(Car c : cars){
            c.draw(scale);
            if(!c.alive) {
                try {
                    c.drawDead(scale);
                } catch (StartExc ex) {/*do nothing*/}
            }
        }
        cars[nex].drawPos(scale);

        try {
            cars[nex].drawCrosses(poss, scale);
            //<editor-fold defaultstate="collapsed" desc="Mouse Listener">
            while (Mouse.next()) {
                if (Mouse.getEventButtonState()) {
                    if (Mouse.getEventButton() == 0) {
                        int x = Math.round(Mouse.getX() / scale);
                        int y = Math.round(Mouse.getY() / scale);
                        tryDrive(x, y);
                    }
                }
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Keyboard Listener">
            while (Keyboard.next()) {
                try {
                    if (Keyboard.getEventKeyState()) {
                        int[] lazy = cars[nex].getLazy();
                        switch (Keyboard.getEventKey()) {
                            case (Keyboard.KEY_NUMPAD1):
                                tryDrive(lazy[0] - 1, lazy[1] - 1);
                                break;
                            case (Keyboard.KEY_NUMPAD2):
                                tryDrive(lazy[0], lazy[1] - 1);
                                break;
                            case (Keyboard.KEY_NUMPAD3):
                                tryDrive(lazy[0] + 1, lazy[1] - 1);
                                break;
                            case (Keyboard.KEY_NUMPAD4):
                                tryDrive(lazy[0] - 1, lazy[1]);
                                break;
                            case (Keyboard.KEY_NUMPAD5):
                                tryDrive(lazy[0], lazy[1]);
                                break;
                            case (Keyboard.KEY_NUMPAD6):
                                tryDrive(lazy[0] + 1, lazy[1]);
                                break;
                            case (Keyboard.KEY_NUMPAD7):
                                tryDrive(lazy[0] - 1, lazy[1] + 1);
                                break;
                            case (Keyboard.KEY_NUMPAD8):
                                tryDrive(lazy[0], lazy[1] + 1);
                                break;
                            case (Keyboard.KEY_NUMPAD9):
                                tryDrive(lazy[0] + 1, lazy[1] + 1);
                                break;
                            case (Keyboard.KEY_E):
                                cars[nex].lastfin = 3;
                        }
                    }
                } catch (StartExc startException) {
                    //do not react
                }
            }
            //</editor-fold>
        } catch (DoomedToDieExc ex) {
            System.out.println(cars[nex].name + " is doomed to die.");
            cars[nex].die();
            next();
        }
		laps = getLaps();
    }

    @Override
    protected int getWidth() {
        return track.getWidth();
    }

    @Override
    protected int getHeight() {
        return track.getHeight();
    }

    void tryDrive(int x, int y) throws DoomedToDieExc, ReachGoalExc {
		if(isPoss(x,y)) {
			int[] move = {x, y};
			try {
				checkSLine(move, cars[nex].getLast());
			} catch (StartExc ex) { }
			cars[nex].drive(x, y);
			next();
		}
		else
			System.out.println("Impossible move!");
    }

    void next() {
        nex = (byte) ((nex < num - 1)?(nex+1):0);
        if(!cars[nex].alive) {
            boolean soAlive = false;
            for(Car c : cars) {
                soAlive = c.alive || soAlive;
            }
            if(soAlive) {
                next();
            } else {
                System.out.println("Everyone is dead.");
            }
        } else {
            try {
                poss = getPoss();
            } catch (DoomedToDieExc ex) {
                cars[nex].die();
                boolean soAlive = false;
                for(Car c : cars) {
                    soAlive = c.alive || soAlive;
                }
                if(soAlive) {
                    next();
                } else {
                    System.out.println("Everyone is dead.");
                }
            }
        }
    }

    int[][] getPoss() throws DoomedToDieExc {
        try {
            int[] lazy = cars[nex].getLazy();
            int[] last;
            last = cars[nex].getLast();
            ArrayList<int[]> moves = new ArrayList();
            int[] move = new int[2];
            for (int i = -1; i <= 1; i++) {
                move[0] = lazy[0] + i;
                for (int j = -1; j <= 1; j++) {
                    move[1] = lazy[1] + j;
                    if (!isLethal(move, last)) {
                        moves.add(move.clone());
                    }
                }
            }
            if(moves.isEmpty())
                throw new DoomedToDieExc();
//                return new int[0][2];
            int[][] arr = new int[moves.size()][2];
            for(int i=0;i<arr.length;i++)
                arr[i] = moves.get(i);
            return arr;
        } catch (StartExc ex) {
            int[][] start = track.getStart();
            ArrayList<int[]> moves = new ArrayList();
            for(int[] move : start) {
                if (!isLethal(move, move)) {
                    moves.add(move);
                }
            }
            if(moves.isEmpty())
                throw new DoomedToDieExc();
//                return new int[0][2];
            int[][] arr = new int[moves.size()][2];
            for(int i=0;i<arr.length;i++)
                arr[i] = moves.get(i);
            return arr;
        }
    }

    boolean isPoss(int x, int y) {
        for(int i=0;i<poss.length;i++) {
            if(poss[i][0] == x && poss[i][1] == y)
                return true;
        }
        return false;
    }

    private boolean isLethal(int[] move, int[] last) {
        //<editor-fold defaultstate="collapsed" desc="variables">
        int[][] wallX = track.getWallX();
        int[][] wallY = track.getWallY();
        int[][] start = track.getStart();
        int[] stedg1 = start[0];
        int[] stedg2 = start[start.length - 1];
        final double mx1 = last[0];
        final double my1 = last[1];
        final double mx2 = move[0];
        final double my2 = move[1];
        double wx1;
        double wx2;
        double wy1;
        double wy2;

        double d;
        double e;
        double sx;
        double sy;
        //</editor-fold>

        if (mx2 < 0 || mx2 > wid
         || my2 < 0 || my2 > hei) {
            return true;
        }

        if (my1 == my2) {
            //<editor-fold defaultstate="collapsed" desc="if horizontal">

            //includes other car
            for(int i=0; i<cars.length; i++) {
                if (i != nex) {
                    try {
                        int[] pos = cars[i].getLast();
                        if (pos[1] == my1 && (
                                (pos[0] <= mx1 && pos[0] >= mx2)
                             || (pos[0] >= mx1 && pos[0] <= mx2) )) {
                            return true;
                        }
                    } catch (StartExc ex) {
                        //ignore
                    }
                }
            }

            //cuts start edge
            if (stedg1[1] == my1 && (
                   (stedg1[0] <= mx1 && stedg1[0] >= mx2)
                || (stedg1[0] >= mx1 && stedg1[0] <= mx2) )) {
                return true;
            } else if (stedg2[1] == my1 && (
                          (stedg2[0] <= mx1 && stedg2[0] >= mx2)
                       || (stedg2[0] >= mx1 && stedg2[0] <= mx2) )) {
                return true;
            }

            //cuts wall
            for (int sl = 0; sl < wallX.length; sl++) {
                for (int j = 1; j < wallX[sl].length; j++) {

                    wx1 = wallX[sl][j - 1];
                    wx2 = wallX[sl][j];
                    wy1 = wallY[sl][j - 1];
                    wy2 = wallY[sl][j];

                    if (wy1 == wy2) {
                        if ((wy1 == my1)
                          && ((wx1 <= mx1 && mx1 <= wx2)
                           || (wx1 <= mx2 && mx2 <= wx2)
                           || (mx1 <= wx1 && wx1 <= mx2)
                           || (mx1 <= wx2 && wx2 <= mx2)
                           || (wx1 >= mx1 && mx1 >= wx2)
                           || (wx1 >= mx2 && mx2 >= wx2)
                           || (mx1 >= wx1 && wx1 >= mx2)
                           || (mx1 >= wx2 && wx2 >= mx2)))
                            return true;
                    } else {
                        //intersection of lines && and in range
                        // 1 d| e
                        // 0 1|my
                        d = (wx2 - wx1) / (wy1 - wy2);
                        e = wx2 + d * wy2;
                        // 1 0|sx
                        // 0 1|sy
                        sx = e - d * my1;
                        sy = my1;
                        if  (((wx1 <= sx && sx <= wx2)
                             || (wx1 >= sx && sx >= wx2))
                          && ((wy1 <= sy && sy <= wy2)
                             || (wy1 >= sy && sy >= wy2))
                          && ((mx1 <= sx && sx <= mx2)
                             || (mx1 >= sx && sx >= mx2)))
                            return true;
                    }
                }
            }
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="else">
            // x + b*y = c
            final double b = (mx2 - mx1) / (my1 - my2);
            final double c = mx2 + b * my2;

            //includes other car
            for(int i=0; i<cars.length; i++) {
                try {
                    if (i != nex) {
                        int[] pos = cars[i].getLast();
                        if (pos[0] + b * pos[1] == c) {
                            if  (((mx1 <= pos[0] && pos[0] <= mx2)
                                 || (mx1 >= pos[0] && pos[0] >= mx2))
                              && ((my1 <= pos[1] && pos[1] <= my2)
                                 || (my1 >= pos[1] && pos[1] >= my2)))
                                return true;
                        }
                    }
                } catch (StartExc ex) {
                    //ignore;
                }
            }

            //cuts start edge
            if (stedg1[0] + b * stedg1[1] == c) {
                if  (((mx1 <= stedg1[0] && stedg1[0] <= mx2)
                     || (mx1 >= stedg1[0] && stedg1[0] >= mx2))
                  && ((my1 <= stedg1[1] && stedg1[1] <= my2)
                     || (my1 >= stedg1[1] && stedg1[1] >= my2)))
                    return true;
            } else if (stedg2[0] + b * stedg2[1] == c) {
                     if  (((mx1 <= stedg2[0] && stedg2[0] <= mx2)
                           || (mx1 >= stedg2[0] && stedg2[0] >= mx2))
                        && ((my1 <= stedg2[1] && stedg2[1] <= my2)
                           || (my1 >= stedg2[1] && stedg2[1] >= my2)))
                    return true;
            }

            //cuts wall
            for (int sl = 0; sl < wallX.length; sl++) {
                for (int j = 1; j < wallX[sl].length; j++) {

                    wx1 = wallX[sl][j - 1];
                    wx2 = wallX[sl][j];
                    wy1 = wallY[sl][j - 1];
                    wy2 = wallY[sl][j];

                    if (wy1 == wy2) {
                        //intersection of lines && and in range
                        // 1 b| c
                        // 0 1|wy

                        // 1 0|sx
                        // 0 1|sy
                        sx = c - b * wy1;
                        sy = wy1;
                        if  (((wx1 <= sx && sx <= wx2)
                             || (wx1 >= sx && sx >= wx2))
                          && ((mx1 <= sx && sx <= mx2)
                             || (mx1 >= sx && sx >= mx2))
                          && ((my1 <= sy && sy <= my2)
                             || (my1 >= sy && sy >= my2)))
                            return true;
                    } else {
                        //intersection of lines && and in range
                        // 1 b|c
                        // 1 d|e
                        d = (wx2 - wx1) / (wy1 - wy2);
                        e = wx2 + d * wy2;
                        // 1 b |c
                        // 0 d'|e'
                        d -= b;
                        e -= c;
                        // 1 b| c
                        // 0 1|sy
                        sy = e / d;
                        // 1 0|sx
                        // 0 1|sy
                        sx = c - b * sy;
                        if  (((wx1 <= sx && sx <= wx2)
                             || (wx1 >= sx && sx >= wx2))
                          && ((wy1 <= sy && sy <= wy2)
                             || (wy1 >= sy && sy >= wy2))
                          && ((mx1 <= sx && sx <= mx2)
                             || (mx1 >= sx && sx >= mx2))
                          && ((my1 <= sy && sy <= my2)
                             || (my1 >= sy && sy >= my2)))
                            return true;
                    }
                }
            }
            //</editor-fold>
        }
        return false;
    }

    private void checkSLine(int[] move, int[] last) {
        //<editor-fold defaultstate="collapsed" desc="variables">
        final int[][] sline = track.getStart();
        final double mx1 = last[0];
        final double my1 = last[1];
        final double mx2 = move[0];
        final double my2 = move[1];
        double lx1;
        double lx2;
        double ly1;
        double ly2;

        double d;
        double e;
        double sx;
        double sy;

        int chang = 0;
        //</editor-fold>

        if (my1 == my2) {
            //<editor-fold defaultstate="collapsed" desc="if horizontal">
            for (int j = 1; j < sline.length; j++) {

                lx1 = sline[j - 1][0];
                lx2 = sline[j][0];
                ly1 = sline[j - 1][1];
                ly2 = sline[j][1];

                if (ly1 != ly2) {
                    //intersection of lines && and in range
                    // 1 d| e
                    // 0 1|my
                    d = (lx2 - lx1) / (ly1 - ly2);
                    e = lx2 + d * ly2;
                    // 1 0|sx
                    // 0 1|sy
                    sx = e - d * my1;
                    sy = my1;
                    if(leq_(mx1, sx, mx2)) {
                        if(leq_(ly1, sy, ly2)) {
                            chang++;
                        } else if(geq_(ly1, sy, ly2)) {
                            chang--;
                        }
                    } else if(geq_(mx1, sx, mx2)) {
                        if(leq_(ly1, sy, ly2)) {
                            chang--;
                        } else if(geq_(ly1, sy, ly2)) {
                            chang++;
                        }
                    }
                }
            }
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="else">
            // x + b*y = c
            final double b = (mx2 - mx1) / (my1 - my2);
            final double c = mx2 + b * my2;

            for (int j = 1; j < sline.length; j++) {

                lx1 = sline[j - 1][0];
                lx2 = sline[j][0];
                ly1 = sline[j - 1][1];
                ly2 = sline[j][1];

                if (ly1 == ly2) {
                    //intersection of lines && and in range
                    // 1 b| c
                    // 0 1|wy

                    // 1 0|sx
                    // 0 1|sy
                    sx = c - b * ly1;
                    sy = ly1;

                    if(leq_(lx1, sx, lx2)) {
                        if(leq_(my1, sy, my2)) {
                            chang--;
                        } else if(geq_(my1, sy, my2)) {
                            chang++;
                        }
                    } else if(geq_(lx1, sx, lx2)) {
                        if(leq_(my1, sy, my2)) {
                            chang++;
                        } else if(geq_(my1, sy, my2)) {
                            chang--;
                        }
                    }
                } else {
                    //intersection of lines && and in range
                    // 1 b|c
                    // 1 d|e
                    double ld = (lx2 - lx1) / (ly2 - ly1);
                    double md = (mx2 - mx1) / (my2 - my1);
                    d = -ld;
                    e = lx2 + d * ly2;
                    // 1 b |c
                    // 0 d'|e'
                    d -= b;
                    e -= c;
                    // 1 b| c
                    // 0 1|sy
                    sy = e / d;
                    // 1 0|sx
                    // 0 1|sy
                    sx = c - b * sy;

                    boolean frosta = false;
                    boolean tosta = false;
                    for(int[] s : sline) {
                        if(s[0] == mx1 && s[1] == my1) {
                            frosta = true;
                        }
                        if(s[0] == mx2 && s[1] == my2) {
                            tosta = true;
                        }
                    }

//                    if (!frosta || !tosta) {
                        if (       ((lx1 < sx && sx <= lx2)
                                || (lx1 > sx && sx >= lx2))
                                && ((ly1 < sy && sy <= ly2)
                                || (ly1 > sy && sy >= ly2))
                                && ((mx1 <= sx && sx <= mx2)
                                || (mx1 >= sx && sx >= mx2))
                                && ((my1 <= sy && sy <= my2)
                                || (my1 >= sy && sy >= my2))) {
                            if (ly2 < ly1 ^ my2 < my1) {
                                if (ld < md) {
                                    chang--;
                                } else if (md < ld) {
                                    chang++;
                                }
                            } else {
                                if (ld < md) {
                                    chang++;
                                } else if (md < ld) {
                                    chang--;
                                }
                            }
                        }
//                    }
                }
            }
            //</editor-fold>
        }

        while (chang > 0) {
            cars[nex].incL();
            chang--;
        }
        while (chang < 0) {
            cars[nex].decL();
            chang++;
        }
    }

    static boolean leq_(double a, double b, double c) {
        return a < b && b <= c;
    }
    static boolean geq_(double a, double b, double c) {
        return a > b && b >= c;
    }

	private int getLaps() {
		int min = Integer.MAX_VALUE;
		for(Car c : cars) {
			if(c.alive)
				min = Math.min(min, c.getLapsLeft());
		}
		return min;
	}
}