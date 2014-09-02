package edu.xjtu.localization.localization.utils;

public class Rectangle {
	public Point2D bottomLeft;
	public Point2D topRight;

	public Rectangle(Point2D bottomLeft, Point2D topRight) {
		super();
		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
	}

	public boolean isInRect(Point2D p) {
		if ((bottomLeft.x - p.x) * (topRight.x - p.x) < 0
				&& (bottomLeft.y - p.y) * (topRight.y - p.y) < 0) {
			return true;
		}
		return false;
	}
}
