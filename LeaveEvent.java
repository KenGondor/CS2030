package cs2030.simulator;

/**
 * A separate class to encapsulate a leave event.
 *
 * @author LeeEnHao_A0204679W
 * */
public class LeaveEvent extends Event {

    /**
     * Constructor for a leave event.
     * Arrival event has no associated server.
     *
     * @param customer The customer.
     * @param time The time of the event.
     * @param stats Statistics to track the event.
     * */
    public LeaveEvent(Customer customer, double time, Statistics stats) {
        super(customer, State.LEFT, time, null);
        stats.notServed();
    }

    @Override
    public boolean isServerEvent() {
        return false;
    }
}
