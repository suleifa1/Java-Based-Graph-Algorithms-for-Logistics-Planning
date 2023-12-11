/**
 * The Order class represents a customer order in a system that processes orders based on coal delivery.
 * It includes details such as the time of order creation, customer index, coal quantity, and delivery deadline.
 */
public class Order {
    double timeCreation;
    int indexCustomer;
    int coal;
    double timeDeadline;

    /**
     * Constructs an Order with specified time of creation, customer index, coal quantity, and deadline.
     * The timeDeadline is calculated by adding the timeCreation to the provided deadline value.
     *
     * @param timeCreation  The timestamp of when the order was created.
     * @param indexCustomer The index or ID of the customer who placed the order.
     * @param coal          The quantity of coal ordered.
     * @param timeDeadline  The deadline for the order, relative to the time of creation.
     */
    public Order(double timeCreation, int indexCustomer, int coal, double timeDeadline) {
        this.timeCreation = timeCreation;
        this.indexCustomer = indexCustomer;
        this.coal = coal;
        this.timeDeadline = timeDeadline + timeCreation;
    }


    /** Returns the time of creation for this order. */
    public double getTimeCreation() {
        return timeCreation;
    }

    /** Sets the time of creation for this order. */
    public void setTimeCreation(double timeCreation) {
        this.timeCreation = timeCreation;
    }

    /** Returns the index or ID of the customer for this order. */
    public int getIndexCustomer() {
        return indexCustomer;
    }

    /** Sets the index or ID of the customer for this order. */
    public void setIndexCustomer(int indexCustomer) {
        this.indexCustomer = indexCustomer;
    }

    /** Returns the quantity of coal for this order. */
    public int getCoal() {
        return coal;
    }

    /** Sets the quantity of coal for this order. */
    public void setCoal(int coal) {
        this.coal = coal;
    }

    /** Returns the deadline for this order. */
    public double getTimeDeadline() {
        return timeDeadline;
    }

    /** Sets the deadline for this order. */
    public void setTimeDeadline(double timeDeadline) {
        this.timeDeadline = timeDeadline;
    }

    /**
     * Returns a string representation of the Order including its creation time, customer index, coal quantity,
     * and deadline.
     *
     * @return A string that contains the creation time, customer index, coal quantity, and deadline of the order.
     */
    @Override
    public String toString() {
        return timeCreation +
                " " + indexCustomer +
                " " + coal +
                " " + timeDeadline;
    }
}
