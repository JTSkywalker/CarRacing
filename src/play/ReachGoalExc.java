/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package play;

/**
 *
 * @author VAIO
 */
public class ReachGoalExc extends Exception {

	final Car winner;

	ReachGoalExc(Car winner) {
		this.winner = winner;
	}

	public Car getWinner() {
		return winner;
	}

	public String getWinnerName() {
		return winner.name;
	}
}
