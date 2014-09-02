package edu.xjtu.localization.localization.utils;

import java.util.ArrayList;
import java.util.List;

public class MapRestrictor {
	private static List<Wall> walls = new ArrayList<Wall>();
	private static List<Rectangle> rects = new ArrayList<Rectangle>();
	private static MapRestrictor instance = new MapRestrictor();

	private MapRestrictor() {
		super();
		setupRestriction();
	}

	public static MapRestrictor getInstance() {
		return instance;
	}

	private void setupRestriction() {
		// //mainFrame
		// addRect(new Point2D(0,0), new Point2D(15.2,0), new Point2D(0,32), new
		// Point2D(15.2,32));
		// //bottom rooms
		// addRect(new Point2D(3.2,0), new Point2D(15.2,0), new
		// Point2D(3.2,2.8), new Point2D(15.2,2.8));
		// //middle working spaces
		// addRect(new Point2D(3.2,6.5), new Point2D(15.2,6.5), new
		// Point2D(3.2,7.2), new Point2D(15.2,7.2));
		// addRect(new Point2D(3.2,9.6), new Point2D(15.2,9.6), new
		// Point2D(3.2,11.0), new Point2D(15.2,11.0));
		// addRect(new Point2D(3.2,12.5), new Point2D(15.2,12.5), new
		// Point2D(3.2,14.3), new Point2D(15.2,14.3));
		// addRect(new Point2D(3.2,17.6), new Point2D(15.2,17.6), new
		// Point2D(3.2,19), new Point2D(15.2,19));
		// addRect(new Point2D(3.2,20.8), new Point2D(15.2,20.8), new
		// Point2D(3.2,22.1), new Point2D(15.2,22.1));
		// //top rooms
		// addRect(new Point2D(6.88,22.4), new Point2D(15.2,22.4), new
		// Point2D(6.88,28), new Point2D(15.2,28));
		// addRect(new Point2D(0,28), new Point2D(15.2,28), new Point2D(0,32),
		// new Point2D(15.2,32));

		// double[] rectLocation ={
		// 3.2, 0, 15.2, 2.8,
		// 3.2, 4.4, 4.8, 7.2,
		// 4.8, 5.12, 13.2, 6.4,
		// 13.2, 4.4, 15.2, 7.2,
		// 3.2, 8.1, 4.8, 11.04,
		// 4.8, 9.04, 13.2, 10.24,
		// 13.2, 8.1, 15.2, 11.04,
		// 3.2, 12.16, 4.8, 15.04,
		// 4.8, 13.12, 13.2, 14.24,
		// 13.2, 12.16, 15.2, 15.04,
		// 3.2, 16, 4.8, 17.6,
		// 4.8, 17.04, 13.2, 17.6,
		// 13.2, 16, 15.2, 17.6,
		// 3.2, 20.8, 15.2, 22.24,
		// 7.2, 22.24, 15.2, 28,
		// 0, 28, 15.2, 32
		// };
		double[] rectLocation = { 3.2, 0, 15.2, 2.8,
				// 3.2, 4.4, 4.8, 7.2,
				3.2, 5.12, 15.2, 6.4,
				// 13.2, 4.4, 15.2, 7.2,
				// 3.2, 8.1, 4.8, 11.04,
				3.2, 9.04, 15.2, 10.24,
				// 13.2, 8.1, 15.2, 11.04,
				// 3.2, 12.16, 4.8, 15.04,
				3.2, 13.12, 15.2, 14.24,
				// 13.2, 12.16, 15.2, 15.04,
				// 3.2, 16, 4.8, 17.6,
				3.2, 17.04, 15.2, 17.6,
				// 13.2, 16, 15.2, 17.6,
				3.2, 20.8, 15.2, 22.24, 7.2, 22.24, 15.2, 28, 0, 28, 15.2, 32 };
		for (int i = 0; i < rectLocation.length / 4; i++) {
			Point2D bottomLeft = new Point2D(rectLocation[i * 4],
					rectLocation[i * 4 + 1]);
			Point2D bottomRight = new Point2D(rectLocation[i * 4 + 2],
					rectLocation[i * 4 + 1]);
			Point2D topLeft = new Point2D(rectLocation[i * 4],
					rectLocation[i * 4 + 3]);
			Point2D topRight = new Point2D(rectLocation[i * 4 + 2],
					rectLocation[i * 4 + 3]);
			addRect(bottomLeft, bottomRight, topLeft, topRight);
		}

		// initial MainFrame

		Point2D bl = new Point2D(0, 0);
		Point2D br = new Point2D(15.2, 0);
		Point2D tl = new Point2D(0, 32);
		Point2D tr = new Point2D(15.2, 32);
		walls.add(new Wall(bl, br));
		walls.add(new Wall(bl, tl));
		walls.add(new Wall(tl, tr));
		walls.add(new Wall(br, tr));
	}

	private void addRect(Point2D bottomLeft, Point2D bottomRight,
			Point2D topLeft, Point2D topRight) {
		rects.add(new Rectangle(bottomLeft, topRight));
		walls.add(new Wall(bottomLeft, bottomRight));
		walls.add(new Wall(bottomLeft, topLeft));
		walls.add(new Wall(topRight, topLeft));
		walls.add(new Wall(topRight, bottomRight));
	}

	/**
	 * judge this movement if it can happen at real world
	 * 
	 * @param s
	 *            start position of this movement
	 * @param e
	 *            end position of this movement
	 * @return if this movement does not violate any wall, return true.
	 *         Otherwise, return false;
	 */
	public static boolean judgeMovement(Point2D s, Point2D e) {
		for (Wall w : walls) {
			if (w.isIntersected(s, e)) {
				return false;
			}
		}
		return true;
	}

	public static boolean inAnyRect(Point2D p) {
		for (Rectangle r : rects) {
			if (r.isInRect(p)) {
				return true;
			}
		}
		return false;
	}
}
