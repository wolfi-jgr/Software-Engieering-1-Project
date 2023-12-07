package client.positioning;

import java.util.Objects;

public class Coordinate {

	private int x, y;

	public Coordinate() {
		this.x = -1;
		this.y = -1;
	}

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof Coordinate))
			return false;

		Coordinate pos = (Coordinate) obj;
		return pos.getX() == getX() && pos.getY() == getY();
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "Coordinate [x=" + x + ", y=" + y + "]";
	}

	public boolean isNotDefined() {
		return x == -1 && y == -1;
	}

	public boolean isOutOfBounds(int height, int width) {
		return x < 0 || y < 0 || x > width || y > height;
	}

}
