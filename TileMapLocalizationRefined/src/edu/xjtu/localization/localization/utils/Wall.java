package edu.xjtu.localization.localization.utils;

public class Wall {
	private Point2D sPoint;
	private Point2D ePoint;
	private static Point2D tmpPoint1 = new Point2D();
	private static Point2D tmpPoint2 = new Point2D();
	private static Wall tmpWall = new Wall(tmpPoint1, tmpPoint2);

	public Wall(Point2D startPoint, Point2D endPoint) {
		super();
		this.sPoint = startPoint;
		this.ePoint = endPoint;
	}

	public Point2D getsPoint() {
		return sPoint;
	}

	public void setsPoint(Point2D sPoint) {
		this.sPoint = sPoint;
	}

	public Point2D getePoint() {
		return ePoint;
	}

	public void setePoint(Point2D ePoint) {
		this.ePoint = ePoint;
	}

	public Wall(double x1, double y1, double x2, double y2) {
		this(new Point2D(x1, y1), new Point2D(x2, y2));
	}

	double cross(Point2D p1, Point2D p2, Point2D p0) {
		return ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y));
	}

	public boolean isIntersected(double x1, double y1, double x2, double y2) {
		tmpPoint1.x = x1;
		tmpPoint1.y = y1;
		tmpPoint2.x = x2;
		tmpPoint2.y = y2;
		return isIntersected(tmpPoint1, tmpPoint2);
	}

	public boolean isIntersected(Point2D p1, Point2D p2) {
		tmpWall.setsPoint(p1);
		tmpWall.setePoint(p2);
		return isIntersected(tmpWall);
	}

	public boolean isIntersected(Wall w) {
		return ((Math.max(this.sPoint.x, this.ePoint.x) >= Math.min(w.sPoint.x,
				w.ePoint.x))
				&& (Math.max(w.sPoint.x, w.ePoint.x) >= Math.min(this.sPoint.x,
						this.ePoint.x))
				&& (Math.max(this.sPoint.y, this.ePoint.y) >= Math.min(
						w.sPoint.y, w.ePoint.y))
				&& (Math.max(w.sPoint.y, w.ePoint.y) >= Math.min(this.sPoint.y,
						this.ePoint.y))
				&& (cross(w.sPoint, this.ePoint, this.sPoint)
						* cross(this.ePoint, w.ePoint, this.sPoint) >= 0) && (cross(
				this.sPoint, w.ePoint, w.sPoint)
				* cross(w.ePoint, this.ePoint, w.sPoint) >= 0));
	}
}
