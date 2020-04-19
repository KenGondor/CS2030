package cs2030.simulator;

/**
 * A spearate class to encapsulate a done event.
 *
 * @author LeeEnHao_A0204679W
 * */
public class DoneEvent extends Event {
    
    /**
     * Constructor for a done event.
     *
     * @param customer The customer.
     * @param time The event time.
     * @param server The server associated with the event.
     * */
    public DoneEvent(Customer customer, double time, Server server, Statistics stats) {
        super(customer, State.DONE, time, server);
        stats.served();
    }

    @Override
    public boolean isServerEvent() {
        return false;
    }

}
