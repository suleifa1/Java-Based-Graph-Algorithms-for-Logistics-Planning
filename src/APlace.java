import java.awt.geom.Point2D;

/**
 * Abstract class representing a place with a geometric location.
 * This class can be extended to represent any entity that has a location defined by x and y coordinates.
 */
public abstract class APlace {
    Point2D.Double point;

    /**
     * Retrieves the geometric point representing the location of the place.
     *
     * @return The current point of this place.
     */
    public Point2D.Double getPoint() {
        return point;
    }

    /**
     * Sets the geometric point representing the location of the place.
     *
     * @param point The geometric point to set as the new location of this place.
     */
    public void setPoint(Point2D.Double point) {
        this.point = point;
    }
}
