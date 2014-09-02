package edu.xjtu.localization.localization.particlefilter;

import edu.xjtu.localization.localization.utils.Point2D;

public class Particle {
	private Point2D point;
	private double weight = 1;

	public Point2D getPoint() {
		return point;
	}

	public void setPoint(Point2D point) {
		this.point = point;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	Particle(double x, double y) {
		this.point = new Point2D(x, y);
	}

	Particle(Point2D point, double weight) {
		this.point = point;
		this.weight = weight;
	}
}