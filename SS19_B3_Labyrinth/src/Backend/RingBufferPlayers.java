package Backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import Backend.Figure.Figure;

public class RingBufferPlayers  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Attribute der Klasse RingBufferPlayers.
	 */
	private int readPointer;
	private ArrayList<Figure> figures;
	private Figure activePlayer;
	

	/**
	 * Konstuktor der Klasse RingBufferPlayers.
	 */

	public RingBufferPlayers() {
		this.readPointer = 0;
		this.figures = new ArrayList<Figure>();

	}

	/**
	 * Methode um einen Spieler dem Spiel hinzuzuf�gen gibt zur�ck ob die Figur
	 * erfolgreich hinzugef�gt worden ist.
	 * 
	 * @param Figure figure
	 * @return boolean result
	 */

	public boolean addFigure(Figure figure) {

		boolean result = false;
		if (figures.size() <= 4) {
			result = true;
		}

		for (Figure i : figures) {
			if (i.getColor().equals(figure.getColor())) {
				result = false;
			}
		}

		if (result == true) {
			figures.add(figure);
			if (this.activePlayer == null) {
				this.activePlayer = figure;
			}

		}
		return result;
	}

	/**
	 * Methode um den aktuellen Spieler zu ermitteln.
	 * 
	 * @return Figure
	 */

	public Figure getActivePlayer() {

		return this.activePlayer;
	}

	/**
	 * Methode um den n�chsten Spieler zu ermitteln der an der Reihe ist.
	 * 
	 * @return Figure
	 */

	public Figure nextPlayer() {

		if (this.readPointer < figures.size() - 1) {
			this.readPointer++;
		} else {
			this.readPointer = 0;
		}

		Figure nextFigure = figures.get(this.readPointer);
		this.activePlayer = nextFigure;
		return nextFigure;
	}

	/**
	 * Methode um die Reihenfolge der Spieler zu mischen.
	 */

	public void shufflePlayers() {

		Collections.shuffle(this.figures);

	}

}
