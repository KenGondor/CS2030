package cs2030.simulator;

/**
 * A class that implements a simple customer with basic customer information,
 * and it is supported by a set of actions that a customer can implement.
 *
 * @author LeeEnHao_A02046979W
 * */
public class Customer implements Comparable<Customer> {
    private static int count = 0;
    private final int id;
    private final double arrivalTime;
    private final boolean isGreedy;


    /**
     * Creates a customer at the specified time of arrival. 
     *
     * @param arrivalTime Arriving time of the customer.
     * */
    public Customer(double arrivalTime, boolean isGreedy) {
        count++;
        this.id = count;
        this.arrivalTime = arrivalTime;
        this.isGreedy = isGreedy;
    }

    /**
     * Returns the time of arrival for the customer.
     *
     * @return Time of arrival.
     * */
    public double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Returns how long the customer has waited with respect to a given time.
     *
     * @return Wait time of the customer.
     * */
    public double checkWaitTime(double serveTime) {
        return serveTime - arrivalTime;
    }

    /**
     * Checks if the customer is a greedy customer.
     * 
     * @return true if customer is greedy.
     */
    public boolean isGreedy() {
        return isGreedy;
    }

    /**
     * Enables comparing of customers to other customer by their id.
     * */
    @Override 
    public int compareTo(Customer other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof Customer) {
            return this.id == ((Customer)other).id;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return isGreedy
            ? String.format("%d(greedy)", id)
            : String.format("%d", id);
    }
}
