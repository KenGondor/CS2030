package cs2030.simulator;

/**
 * A separate class to encapsulate a wait event.
 *
 * @author LeeEnHao_A0204679W
 * */
public class WaitEvent extends Event {
    
    /**
     * Constructor for a wait event.
     *
     * @param customer The event customer.
     * @param time The event time.
     * @param server The event server.
     * @param stats The event statistics tracker.
     * */
    public WaitEvent(Customer customer, double time, Server server, Statistics stats) {
        super(customer, State.WAITING, time, server);
    }

    @Override
    public boolean isServerEvent() {
        return false;
    }

}
