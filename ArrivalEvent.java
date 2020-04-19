package cs2030.simulator;

/**
 * A separate class to encapsulate an arrival event.
 *
 * @author LeeEnHao_A0204679W
 * */
public class ArrivalEvent extends Event {

    /**
     * Constructor for an arrival event.
     * Leave event has no associated server.
     *
     * @param customer The event customer.
     * @param time The event time.
     * @param stats The event statistics tracker.
     * */
    public ArrivalEvent(Customer customer, double time, Statistics stats) {
        super(customer, State.ARRIVED, time, null);
    }

    @Override
    public boolean isServerEvent() {
        return false;
    }

}
