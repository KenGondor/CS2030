package cs2030.simulator;

/**
 * A simple class to instantiate a an object to track the event
 * statistics.
 *
 * @author LeeEnHao_A0204679W
 * */
public class Statistics {

    private double totalWaitTime;
    private int served;
    private int notServed;

    /**
     * Creates the object for tracking the statistics.
     * */
    public Statistics() {
        totalWaitTime = 0.000;
        served = 0;
        notServed = 0;
    }

    /**
     * Increments the total waiting time by the given
     * time.
     *
     * @param time Wait time
     * */
    public void increaseTime(double time) {
        totalWaitTime += time;
    }

    /**
     * Increments the total customers served.
     * */
    public void served() {
        served++;
    }

    /**
     * Increments the toal customers not served.
     * */
    public void notServed() {
        notServed++;
    }

    @Override
    public String toString() {
        double average = totalWaitTime / served;
        return String.format("[%.3f %d %d]", served == 0 ? 0.000 : average, served, notServed);
    }
}
