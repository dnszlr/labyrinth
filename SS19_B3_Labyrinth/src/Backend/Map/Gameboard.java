package Backend.Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import Backend.Color;
import Backend.Direction;
import Backend.PositionsCard;
import Backend.Treasure;
import Backend.Figure.Figure;

public class Gameboard implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Attribute der Klasse Gameboard.
	 */
	private MazeCard freeCard;
	private MazeCard[][] map;

	/**
	 * Konstruktor der Klasse Gameboard.
	 */
	public Gameboard() {

		this.map = new MazeCard[7][7];
		this.map[Color.RED.getPos()[0]][Color.RED.getPos()[1]] = new CurveCard(Color.RED, null);
		this.map[Color.YELLOW.getPos()[0]][Color.YELLOW.getPos()[1]] = new CurveCard(Color.YELLOW, null);
		this.map[Color.BLUE.getPos()[0]][Color.BLUE.getPos()[1]] = new CurveCard(Color.BLUE, null);
		this.map[Color.GREEN.getPos()[0]][Color.GREEN.getPos()[1]] = new CurveCard(Color.GREEN, null);
		this.map[2][0] = new CrotchCard(Treasure.book);
		this.map[4][0] = new CrotchCard(Treasure.moneybag);
		this.map[0][2] = new CrotchCard(Treasure.map);
		this.map[2][2] = new CrotchCard(Treasure.crown);
		this.map[4][2] = new CrotchCard(Treasure.key);
		this.map[6][2] = new CrotchCard(Treasure.skull);
		this.map[0][4] = new CrotchCard(Treasure.ring);
		this.map[2][4] = new CrotchCard(Treasure.chest);
		this.map[4][4] = new CrotchCard(Treasure.emerald);
		this.map[6][4] = new CrotchCard(Treasure.sword);
		this.map[2][6] = new CrotchCard(Treasure.menorah);
		this.map[4][6] = new CrotchCard(Treasure.helmet);

		List<MazeCard> freeCards = generateFreeCards();
		Collections.shuffle(freeCards);

		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map[i].length; j++) {
				if (this.map[i][j] == null) {
					this.map[i][j] = freeCards.get(0);
					freeCards.remove(0);
				}
			}
		}

		setAllNeighbours();

		this.freeCard = freeCards.get(0);
	}

	/**
	 * BufferedReader laden des Gameboards
	 * 
	 * @param csv
	 * @throws IOException
	 */
	public Gameboard(BufferedReader reader) throws IOException {
		String line = "Start";
		String[] fields;
		MazeCard mazeCard = null;
		ArrayList<MazeCard> cards = new ArrayList<MazeCard>();
		this.map = new MazeCard[7][7];
		while (!line.equals("End")) {

			line = reader.readLine();
			fields = line.split(";");

			if (fields[0].equals("EvenCard")) {
				mazeCard = new EvenCard();
				while (!Arrays.equals(mazeCard.getWall(), new int[] { Integer.parseInt(fields[1]),
						Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]) })) {
					mazeCard.rotateLeft();
				}

				cards.add(mazeCard);

			} else if (fields[0].equals("CurveCard")) {
				if (fields[5].equals("null") && fields[6].equals("null")) {
					mazeCard = new CurveCard(null, null);
				} else if (fields[6].equals("null")) {
					mazeCard = new CurveCard(Color.valueOf(fields[5]), null);
				} else if (fields[5].equals("null")) {
					mazeCard = new CurveCard(null, Treasure.valueOf(fields[6]));
				} else {
					mazeCard = new CurveCard(Color.valueOf(fields[5]), Treasure.valueOf(fields[6]));
				}
				while (!Arrays.equals(mazeCard.getWall(), new int[] { Integer.parseInt(fields[1]),
						Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]) })) {
					mazeCard.rotateLeft();
				}

				cards.add(mazeCard);

			} else if (fields[0].equals("CrotchCard")) {
				if (fields[6].equals("null")) {
					mazeCard = new CrotchCard(null);
				} else {
					mazeCard = new CrotchCard(Treasure.valueOf(fields[6]));
				}
				while (!Arrays.equals(mazeCard.getWall(), new int[] { Integer.parseInt(fields[1]),
						Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]) })) {
					mazeCard.rotateLeft();
				}

				cards.add(mazeCard);

			}

		}

		this.freeCard = cards.get(0);
		cards.remove(0);

		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map.length; j++) {
				this.map[i][j] = cards.get(0);
				cards.remove(0);
			}
		}

		setAllNeighbours();
	}

	/**
	 * writeToStream Methode f�r CSV
	 * 
	 * @param pw
	 */
	public void writeToStream(PrintWriter pw) {

		pw.println(this.freeCard.toString());
		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map[i].length; j++) {
				pw.println(this.map[i][j].toString());

			}
		}

		pw.println("End");
		pw.flush();

	}

	/**
	 * Methode um ein neue Labyrinthkarte zu generieren.
	 * 
	 * @param List<Treasure> treasures
	 * @return MazeCard
	 */
	private MazeCard generateNewMaze(List<Treasure> treasures) {
		int randomTreasure;
		if (treasures.size() > 1) {
			randomTreasure = (int) (Math.random() * (treasures.size() - 1) + 1);
		} else {
			randomTreasure = 0;
		}
		MazeCard generatedMaze;
		if (treasures.size() >= 7) {
			generatedMaze = new CrotchCard(treasures.get(randomTreasure));
		} else if (treasures.size() > 0 && treasures.size() < 7) {
			generatedMaze = new CurveCard(null, treasures.get(randomTreasure));
		} else {
			generatedMaze = new CurveCard(null, null);
		}

		return generatedMaze;

	}

	/**
	 * Erstellt eine Liste mit Karten die frei auf dem Feld verteilbar sind.
	 * 
	 * @return List<MazeCard>
	 */

	private List<MazeCard> generateFreeCards() {

		List<MazeCard> freeCards = new ArrayList<MazeCard>();
		List<Treasure> treasures = new ArrayList<Treasure>(Arrays.asList(Treasure.owl, Treasure.ghost,
				Treasure.sorceress, Treasure.rat, Treasure.dragon, Treasure.spider, Treasure.bat, Treasure.gnome,
				Treasure.moth, Treasure.lizard, Treasure.spook, Treasure.scarab));
		for (int a = 0; a <= 33; a++) {
			if (a <= 21) {
				freeCards.add(generateNewMaze(treasures));
				if (a <= 11) {
					treasures.remove(freeCards.get(a).getTreasure());
				}
			} else {
				freeCards.add(new EvenCard());
			}
		}

		return freeCards;

	}

	/**
	 * Methode um f�r jede Karte die Nachbarkarten zu ermitteln
	 */
	private void setAllNeighbours() {

		int low = 0;
		int high = this.map.length - 1;

		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map[i].length; j++) {

				if (j != low) {
					this.map[i][j].setNeighboring(this.map[i][j - 1], Direction.north);
				}
				if (j == low) {
					this.map[i][j].setNeighboring(null, Direction.north);
				}
				if (j != high) {
					this.map[i][j].setNeighboring(this.map[i][j + 1], Direction.south);
				}
				if (j == high) {
					this.map[i][j].setNeighboring(null, Direction.south);
				}
				if (i != low) {
					this.map[i][j].setNeighboring(this.map[i - 1][j], Direction.west);
				}
				if (i == low) {
					this.map[i][j].setNeighboring(null, Direction.west);
				}
				if (i != high) {
					this.map[i][j].setNeighboring(this.map[i + 1][j], Direction.east);
				}
				if (i == high) {
					this.map[i][j].setNeighboring(null, Direction.east);
				}

			}

		}

	}

	/**
	 * Methode um die Figuren auf dem Spielfeld zu platzieren.
	 * 
	 * @param Figure[] figures
	 */
	public void placeFigures(Figure[] figures) {

		for (int i = 0; i < figures.length; i++) {

			this.getMapCard(figures[i].getPos()[0], figures[i].getPos()[1]).addFigure(figures[i]);

		}

	}

	// Doppelte Spieler von FreeCard nehmen! danach sollte es gehen

	/**
	 * Methode um die Labyrinthkarten zu verschieben. gibt neue freecard nach jedem
	 * schieben bekommt jeder neue nachbarn
	 * 
	 * @param PositionCard move
	 * @param MazeCard     card
	 * @return MazeCard
	 */
	public MazeCard moveGears(PositionsCard move, MazeCard card) {

		MazeCard[] safer = new MazeCard[7];
		switch (move) {
		case A2:
			if (this.map[1][6].getFigures().size() > 0) {
				for (Figure i : this.map[1][6].getFigures()) {
					i.setPos(new int[] { 1, 0 });
				}
				card.addFigures(this.map[1][6].getFigures());
			}

			this.freeCard = getMapCard(1, 6);
			for (int i = 0; i < this.map[1].length - 1; i++) {
				for (Figure j : this.map[1][i].getFigures()) {
					j.setPos(new int[] { 1, i + 1 });
				}
				safer[i + 1] = this.map[1][i];
			}
			safer[0] = card;
			this.map[1] = safer;

			break;

		case A4:
			if (this.map[3][6].getFigures().size() > 0) {
				for (Figure i : this.map[3][6].getFigures()) {
					i.setPos(new int[] { 3, 0 });
				}
				card.addFigures(this.map[3][6].getFigures());
			}

			this.freeCard = getMapCard(3, 6);
			for (int i = 0; i < this.map[3].length - 1; i++) {
				for (Figure j : this.map[3][i].getFigures()) {
					j.setPos(new int[] { 3, i + 1 });
				}
				safer[i + 1] = this.map[3][i];
			}
			safer[0] = card;
			this.map[3] = safer;

			break;

		case A6:

			if (this.map[5][6].getFigures().size() > 0) {
				for (Figure i : this.map[5][6].getFigures()) {
					i.setPos(new int[] { 5, 0 });
				}
				card.addFigures(this.map[5][6].getFigures());
			}

			this.freeCard = getMapCard(5, 6);
			for (int i = 0; i < this.map[5].length - 1; i++) {
				for (Figure j : this.map[5][i].getFigures()) {
					j.setPos(new int[] { 5, i + 1 });
				}
				safer[i + 1] = this.map[5][i];
			}
			safer[0] = card;
			this.map[5] = safer;

			break;

		case G2:
			if (this.map[1][0].getFigures().size() > 0) {
				for (Figure i : this.map[1][0].getFigures()) {
					i.setPos(new int[] { 1, 6 });
				}
				card.addFigures(this.map[1][0].getFigures());
			}

			this.freeCard = getMapCard(1, 0);
			for (int i = this.map[1].length - 1; i > 0; i--) {
				for (Figure j : this.map[1][i].getFigures()) {
					j.setPos(new int[] { 1, i - 1 });
				}
				safer[i - 1] = this.map[1][i];
			}
			safer[6] = card;
			this.map[1] = safer;
			break;

		case G4:
			if (this.map[3][0].getFigures().size() > 0) {
				for (Figure i : this.map[3][0].getFigures()) {
					i.setPos(new int[] { 3, 6 });
				}
				card.addFigures(this.map[3][0].getFigures());
			}

			this.freeCard = getMapCard(3, 0);
			for (int i = this.map[3].length - 1; i > 0; i--) {
				for (Figure j : this.map[3][i].getFigures()) {
					j.setPos(new int[] { 3, i - 1 });
				}
				safer[i - 1] = this.map[3][i];
			}
			safer[6] = card;
			this.map[3] = safer;

			break;
		case G6:
			if (this.map[5][0].getFigures().size() > 0) {
				for (Figure i : this.map[5][0].getFigures()) {
					i.setPos(new int[] { 5, 6 });
				}
				card.addFigures(this.map[5][0].getFigures());
			}

			this.freeCard = getMapCard(5, 0);
			for (int i = this.map[5].length - 1; i > 0; i--) {
				for (Figure j : this.map[5][i].getFigures()) {
					j.setPos(new int[] { 5, i - 1 });
				}
				safer[i - 1] = this.map[5][i];
			}
			safer[6] = card;
			this.map[5] = safer;

			break;

		case B1:
			if (this.map[6][1].getFigures().size() > 0) {
				for (Figure i : this.map[6][1].getFigures()) {
					i.setPos(new int[] { 0, 1 });
				}
				card.addFigures(this.map[6][1].getFigures());
			}
			this.freeCard = getMapCard(6, 1);
			for (int i = 0; i < this.map.length - 1; i++) {
				for (Figure j : this.map[i][1].getFigures()) {
					j.setPos(new int[] { i + 1, 1 });
				}
				safer[i + 1] = this.map[i][1];
			}
			safer[0] = card;
			for (int i = 0; i < this.map.length; i++) {
				this.map[i][1] = safer[i];
			}

			break;

		case D1:
			if (this.map[6][3].getFigures().size() > 0) {
				for (Figure i : this.map[6][3].getFigures()) {
					i.setPos(new int[] { 0, 3 });
				}

				card.addFigures(this.map[6][3].getFigures());
			}
			this.freeCard = getMapCard(6, 3);
			for (int i = 0; i < this.map.length - 1; i++) {
				for (Figure j : this.map[i][3].getFigures()) {
					j.setPos(new int[] { i + 1, 3 });
				}
				safer[i + 1] = this.map[i][3];
			}
			safer[0] = card;
			for (int i = 0; i < this.map.length; i++) {
				this.map[i][3] = safer[i];
			}

			break;
		case F1:
			if (this.map[6][5].getFigures().size() > 0) {
				for (Figure i : this.map[6][5].getFigures()) {
					i.setPos(new int[] { 0, 5 });
				}
				card.addFigures(this.map[6][5].getFigures());
			}
			this.freeCard = getMapCard(6, 5);
			for (int i = 0; i < this.map.length - 1; i++) {
				for (Figure j : this.map[i][5].getFigures()) {
					j.setPos(new int[] { i + 1, 5 });
				}
				safer[i + 1] = this.map[i][5];
			}
			safer[0] = card;
			for (int i = 0; i < this.map.length; i++) {
				this.map[i][5] = safer[i];
			}

			break;

		case B7:
			if (this.map[0][1].getFigures().size() > 0) {
				for (Figure i : this.map[0][1].getFigures()) {
					i.setPos(new int[] { 6, 1 });
				}
				card.addFigures(this.map[0][1].getFigures());

			}
			this.freeCard = getMapCard(0, 1);
			for (int i = this.map.length - 1; i > 0; i--) {
				for (Figure j : this.map[i][1].getFigures()) {
					j.setPos(new int[] { i - 1, 1 });
				}
				safer[i - 1] = this.map[i][1];
			}
			safer[6] = card;
			for (int i = 0; i < this.map.length; i++) {
				this.map[i][1] = safer[i];
			}

			break;

		case D7:
			if (this.map[0][3].getFigures().size() > 0) {
				for (Figure i : this.map[0][3].getFigures()) {
					i.setPos(new int[] { 6, 3 });
				}
				card.addFigures(this.map[0][3].getFigures());
			}
			this.freeCard = getMapCard(0, 3);
			for (int i = this.map.length - 1; i > 0; i--) {
				for (Figure j : this.map[i][3].getFigures()) {
					j.setPos(new int[] { i - 1, 3 });
				}
				safer[i - 1] = this.map[i][3];
			}
			safer[6] = card;
			for (int i = 0; i < this.map.length; i++) {
				this.map[i][3] = safer[i];
			}

			break;
		case F7:
			if (this.map[0][5].getFigures().size() > 0) {
				for (Figure i : this.map[0][5].getFigures()) {
					i.setPos(new int[] { 6, 5 });
				}
				card.addFigures(this.map[0][5].getFigures());
			}
			this.freeCard = getMapCard(0, 5);
			for (int i = this.map.length - 1; i > 0; i--) {
				for (Figure j : this.map[i][5].getFigures()) {
					j.setPos(new int[] { i - 1, 5 });
				}
				safer[i - 1] = this.map[i][5];
			}
			safer[6] = card;
			for (int i = 0; i < this.map.length; i++) {
				this.map[i][5] = safer[i];
			}

			break;
		}

		this.freeCard.setNeighboring(null, Direction.north);
		this.freeCard.setNeighboring(null, Direction.east);
		this.freeCard.setNeighboring(null, Direction.south);
		this.freeCard.setNeighboring(null, Direction.west);
		this.freeCard.removeFigures();
		setAllNeighbours();
		return this.freeCard;

	}

	/**
	 * Getter um die aktuell freie Karte zu erhalten.
	 * 
	 * @return MazeCard
	 */
	public MazeCard getFreeCard() {

		return this.freeCard;
	}

	/**
	 * Getter um eine bestimmte Koordinate auf dem Spielfeld zu erfragen.
	 * 
	 * @param int x
	 * @param int y
	 * @return MazeCard
	 */
	public MazeCard getMapCard(int x, int y) {

		MazeCard maze = this.map[x][y];

		return maze;
	}

	/**
	 * Methode um zu erfragen ob ein Zug m�glich w�re.
	 * 
	 * @param int[]  currentPos
	 * @param int[]  oldPos
	 * @param Figure figure
	 * @return boolean result
	 */

	public boolean moveFigure(int[] currentPos, int[] oldPos, Figure figure) {

		boolean result = false;
		int[][] visited = new int[7][7];
		if (moveFigureWithArray(currentPos, oldPos, visited, figure)) {
			result = true;
		}

		return result;

	}

	/**
	 * Hilfsmethode f�r moveFigure.
	 * 
	 * @param int[]   currentPos
	 * @param int[]   oldPos
	 * @param int[][] visited
	 * @param Figure  figure
	 * @return boolean
	 */
	private boolean moveFigureWithArray(int[] currentPos, int[] oldPos, int[][] visited, Figure figure) {

		if (oldPos[0] == currentPos[0] && oldPos[1] == currentPos[1]) {
			return true;
		}
		if (visited[oldPos[0]][oldPos[1]] == 1) {
			return false;
		}

		visited[oldPos[0]][oldPos[1]] = 1;
		MazeCard old = getMapCard(oldPos[0], oldPos[1]);

		if (old.getNeighboring(Direction.north) != null && old.getWall()[0] == 0
				&& old.getNeighboring(Direction.north).getWall()[2] == 0) {

			if (moveFigureWithArray(currentPos, new int[] { oldPos[0], oldPos[1] - 1 }, visited, figure)) {
				return true;

			}

		}

		if (old.getNeighboring(Direction.east) != null && old.getWall()[1] == 0
				&& old.getNeighboring(Direction.east).getWall()[3] == 0) {

			if (moveFigureWithArray(currentPos, new int[] { oldPos[0] + 1, oldPos[1] }, visited, figure)) {
				return true;

			}

		}

		if (old.getNeighboring(Direction.south) != null && old.getWall()[2] == 0
				&& old.getNeighboring(Direction.south).getWall()[0] == 0) {

			if (moveFigureWithArray(currentPos, new int[] { oldPos[0], oldPos[1] + 1 }, visited, figure)) {
				return true;

			}
		}

		if (old.getNeighboring(Direction.west) != null && old.getWall()[3] == 0
				&& old.getNeighboring(Direction.west).getWall()[1] == 0) {

			if (moveFigureWithArray(currentPos, new int[] { oldPos[0] - 1, oldPos[1] }, visited, figure)) {
				return true;

			}

		}

		return false;
	}

}
