package cs2030.simulator;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

/**
 * Class to simulate the discrete event.
 *
 * @author LeeEnHao_A0204679W
 * */
public class EventSimulator {
    /**A priority queue which logs all events.*/
    private final Queue<Event> log;
    /**Event statistics tracker.*/
    private final Statistics stats;
    /**Logic implementation for events.*/
    private final Servlet controller;
    private final List<Double> arrivals;
    /**Total number of customers. */
    private final int totalArrivals;
    private final RandomGenerator rng;
    private final double gP;


    private EventSimulator(int seed, int num, int nSelf, int qMax, int total, 
        double lambda, double mu, double rho, double restProb, double greedP) {
        totalArrivals = total;
        this.log = new PriorityQueue<>();
        stats = new Statistics();
        rng = new RandomGenerator(seed, lambda, mu, rho);
        controller = new Servlet(num, nSelf, qMax, rng, restProb);
        arrivals = new ArrayList<>();
        double timestamp = 0.000;
        gP = greedP;
        for (int i = 0; i < totalArrivals; i++) {
            arrivals.add(timestamp);
            timestamp += rng.genInterArrivalTime();
        }
    }

    /**
     * Initialises the event simulator with specified starting conditions. 
     *
     * @param seed Seed for random generator.
     * @param num Number of servers for this simulation.
     * @param qMax Queue size for each server.
     * @param total The total number of customer arrivals.
     * @param lambda Arrival rate.
     * @param mu Service rate.
     * @param rho Resting rate.
     * @param restProb Probability of resting.
     * */
    public static EventSimulator launch(int seed, int num, int nSelf, int qMax, int total, 
        double lambda, double mu, double rho, double restProb, double greed) {
        return new EventSimulator(seed, num, nSelf, qMax, total, 
            lambda, mu, rho, restProb, greed);
    }

    /**
     * Runs the event simulator.
     * */
    public void run() {

        for (Double d : arrivals) {
            double greed = rng.genCustomerType();

            Customer guy = greed < gP 
                ? new Customer(d, true)
                : new Customer(d, false);

            log.add(new ArrivalEvent(guy, d, stats));
        }

        while (log.size() > 0) {
            Event e = log.poll();

            //Debugging purpose
            // System.out.println(e);

            if (!e.isServerEvent()) {
                System.out.println(e);
            }

            controller.handle(e, stats).ifPresent(event -> log.add(event));


        }
    }
    
    /**
     * Prints the statistics of the statistics after the event.
     */
    public void printStats() {
        System.out.println(stats);
    }

}
