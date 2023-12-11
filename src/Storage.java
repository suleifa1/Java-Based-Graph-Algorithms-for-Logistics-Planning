import java.awt.geom.Point2D;

/**
 * The Storage class represents a storage facility for coal.
 * It extends APlace, allowing it to have a location represented by x and y coordinates.
 * In addition to location, it stores information about coal, generation and loading times, and when it will be full.
 */
public class Storage extends APlace{
    public int coal; // The amount of coal in storage.
    public double timeToGenerate; // The time it takes to generate coal.
    public double timeToLoad; // The time it takes to load coal onto transport.
    public double timeWhenWillBeFull; // The estimated time when the storage will be full.
    public int currentCoal; // The current amount of coal in storage.

    /**
     * Constructs a Storage instance with specified location coordinates and initial storage parameters.
     *
     * @param x              The x-coordinate of the storage's location.
     * @param y              The y-coordinate of the storage's location.
     * @param coal           The initial amount of coal in storage.
     * @param timeToGenerate The time it takes to generate a certain amount of coal.
     * @param timeToLoad     The time it takes to load coal onto a transport.
     */
    public Storage(double x, double y, int coal, double timeToGenerate, double timeToLoad) {
        this.point = new Point2D.Double(x, y);
        this.coal = coal;
        this.timeToGenerate = timeToGenerate;
        this.timeToLoad = timeToLoad;
        this.timeWhenWillBeFull = 0;
    }

    // Getter and setter methods with JavaDoc comments.

    /** Returns the amount of coal in storage. */
    public int getCoal() {
        return coal;
    }

    /** Sets the amount of coal in storage. */
    public void setCoal(int coal) {
        this.coal = coal;
    }

    /** Returns the time it takes to generate coal. */
    public double getTimeToGenerate() {
        return timeToGenerate;
    }

    /** Sets the time it takes to generate coal. */
    public void setTimeToGenerate(double timeToGenerate) {
        this.timeToGenerate = timeToGenerate;
    }

    /** Returns the time it takes to load coal onto transport. */
    public double getTimeToLoad() {
        return timeToLoad;
    }

    /** Sets the time it takes to load coal onto transport. */
    public void setTimeToLoad(double timeToLoad) {
        this.timeToLoad = timeToLoad;
    }

    /**
     * Returns a string representation of the Storage including its location, coal amount, and times to generate and load.
     *
     * @return A string that contains the location coordinates, coal amount, and generation and loading times.
     */
    @Override
    public String toString() {
        return point.x + " " + point.y +
                " " + coal +
                " " + timeToGenerate +
                " " + timeToLoad;
    }
}