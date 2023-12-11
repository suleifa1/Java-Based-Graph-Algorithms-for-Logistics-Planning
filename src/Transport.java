import java.util.Random;

/**
 * Represents a mode of transport with properties such as name, speed, distance to cover,
 * time to repair, and capacity. This class encapsulates the characteristics of a transport
 * vehicle and provides methods for managing its coal capacity and determining its operational
 * status. Speed and distance are determined within given ranges, using both uniform and
 * Gaussian distributions for variety.
 *
 * The class also includes getter and setter methods for all properties, allowing for controlled
 * access and modification of the transport's attributes.
 */
public class Transport {
    String name;
    double timeToRepair;
    double speed;
    double distance;
    int maxCapacity;
    double representation;
    int currentCoal;
    int pos;
    double time;


    /**
     * Constructs Transport with specified attributes and randomizes certain properties
     * within provided ranges.
     *
     * @param name           The name of the transport.
     * @param speedMin       The minimum speed the transport can have.
     * @param speedMax       The maximum speed the transport can have.
     * @param distanceMin    The minimum distance the transport covers.
     * @param distanceMax    The maximum distance the transport covers.
     * @param timeToRepair   The time required to repair the transport.
     * @param maxCapacity    The maximum coal capacity of the transport.
     * @param representation A value representing the transport in computations.
     */
    public Transport(String name, double speedMin, double speedMax,
                     double distanceMin, double distanceMax,
                     double timeToRepair, int maxCapacity,
                     double representation) {
        Random r = new Random();
        this.name = name;
        this.timeToRepair = timeToRepair;
        this.maxCapacity = maxCapacity;
        this.representation = representation;
        if (speedMin != speedMax) {
            this.speed = r.nextDouble(speedMin,speedMax + 1);
        } else {
            this.speed = speedMin;
        }
        this.distance = r.nextGaussian((distanceMin + distanceMax) / 2,(distanceMax - distanceMin) / 4);
        currentCoal = 0;

    }

    /**
     * Adds coal to the transport's current load, ensuring the capacity is not exceeded.
     *
     * @param coal The amount of coal to add.
     */
    public void addCoal(int coal) {
        currentCoal += coal;
        if (currentCoal > maxCapacity) {
            currentCoal = maxCapacity;
        }
    }

    /**
     * Checks if the transport is full.
     *
     * @return true if the transport is at maximum capacity, false otherwise.
     */
    public boolean isFull() {
        if (currentCoal < maxCapacity) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Calculates the amount of coal needed to fill the transport to maximum capacity.
     *
     * @return The amount of coal needed to reach max capacity.
     */
    public int coalToFull() {
        return maxCapacity - currentCoal;
    }


    // Getter and setter methods for the transport's properties
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTimeToRepair() {
        return timeToRepair;
    }

    public void setTimeToRepair(double timeToRepair) {
        this.timeToRepair = timeToRepair;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public double getRepresentation() {
        return representation;
    }

    public void setRepresentation(double representation) {
        this.representation = representation;
    }
}
