package cs2030.simulator;

/**
 * A separate class to encapsulate a serve event.
 *
 * @author LeeEnHao_A0204679W
 * */
public class ServeEvent extends Event {

    /**
     * Constructor for a serve event.
     *
     * @param customer The event customer
     * @param time The event time.
     * @param server The event server.
     * @param stats The event statistics tracker.
     * */
    public ServeEvent(Customer customer, double time, Server server, Statistics stats) {
        super(customer, State.SERVED, time, server);
        double waitTime = customer.checkWaitTime(time);
        stats.increaseTime(waitTime);
    }

    @Override
    public boolean isServerEvent() {
        return false;
    }

}
