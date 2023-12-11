import java.awt.geom.Point2D;

/**
 * The Buyer class represents a buyer's location using x and y coordinates.
 * It extends the APlace class, inheriting its geometric location functionality.
 */
public class Buyer extends APlace{

    /**
     * Constructs a Buyer instance with specified x and y coordinates for its location.
     *
     * @param x The x-coordinate of the buyer's location.
     * @param y The y-coordinate of the buyer's location.
     */
    public Buyer(double x, double y) {
        this.point = new Point2D.Double(x, y);
    }

    /**
     * Retrieves the point representing the buyer's location.
     * Overrides the getPoint method from APlace.
     *
     * @return The geometric point representing the buyer's location.
     */
    @Override
    public Point2D.Double getPoint() {
        return point;
    }

    /**
     * Sets the point representing the buyer's location.
     * Overrides the setPoint method from APlace.
     *
     * @param point The new point representing the buyer's location.
     */
    @Override
    public void setPoint(Point2D.Double point) {
        this.point = point;
    }

    /**
     * Returns a string representation of the Buyer including its location.
     *
     * @return A string that contains the class name and the point representing the buyer's location.
     */
    @Override
    public String toString() {
        return "Buyer{" +
                "point=" + point +
                '}';
    }
}
