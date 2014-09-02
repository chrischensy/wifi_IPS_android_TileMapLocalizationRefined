package edu.xjtu.localization.localization.utils;

public class Point2D {
	public double x;
	public double y;

	public Point2D() {
		this(0, 0);
	}

	public Point2D(Point2D point2d) {
		this(point2d.getX(), point2d.getY());
	}

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[x=" + x + ", y=" + y + "]";
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		Point2D point = (Point2D) o;
		if (point.getX() == this.x && point.getY() == this.y)
			return true;
		else
			return false;
	}

	public boolean IsInArea(double currentX, double currentY, double Radius) {
		double dst = 0;
		dst = Math.pow(currentX - this.x, 2) + Math.pow(currentY - this.y, 2);
		dst = Math.pow(dst, 0.5);
		if (dst <= Radius)
			return true;

		return false;
	}
}
