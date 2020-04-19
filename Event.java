package cs2030.simulator;

import java.util.Optional;

/**
 * A base class implementation of events.
 *
 * @author LeeEnHao_A0204679W
 * */
abstract class Event implements Comparable<Event> {
    final double time;
    final State state;
    final Optional<Customer> customer;
    final Optional<Server> server;

    /**
     * Constructor for an event.
     *
     * @param customer The event customer.
     * @param state The event state.
     * @param eventTime The event time.
     * @param server The event server.
     * */
    protected Event(Customer customer, State state, double eventTime, Server server) {
        // Can be done using factory methods but nvm.
        this.time = eventTime;
        this.customer = Optional.ofNullable(customer);
        this.state = state;
        this.server = Optional.ofNullable(server);
    }

    abstract boolean isServerEvent();

    /**
     * Allows events to be compared to one another by order of time.
     *
     * @param other The other event being compared to.
     * */
    @Override
    public int compareTo(Event other) {
        if (this.time != other.time) {
            return Double.compare(this.time, other.time);
        } else if (this.isServerEvent() && other.isServerEvent()) {
            // Compare server id if both are same timings.
            return Integer.compare(this.server.get().id, this.server.get().id);
        } else if (this.isServerEvent() && !other.isServerEvent()) {
            // Server event should come before customer event?
            return -1;
        } else if (!this.isServerEvent() && other.isServerEvent()) {
            return 1;
        } else {
            // Customer events with same timing, compare the customers.
            return this.customer.get().compareTo(other.customer.get());
        }
    }

    @Override
    public String toString() {
        if (this.state == State.LEFT) {
            return String.format("%.3f %s leaves", time, customer.get().toString());
        } else if (this.state == State.ARRIVED) {
            return String.format("%.3f %s arrives", time, customer.get().toString());
        } else if (this.state == State.DONE) {
            return this.server.get().isSelfCheckOut() 
                ? String.format("%.3f %s done serving by self-check %d",
                    time, customer.get().toString(), server.get().id)
                : String.format("%.3f %s done serving by server %d", 
                    time, customer.get().toString(), server.get().id);
        } else if (this.state == State.WAITING) {
            return this.server.get().isSelfCheckOut() 
                ? String.format("%.3f %s waits to be served by self-check %d", 
                    time, customer.get().toString(), server.get().id)
                : String.format("%.3f %s waits to be served by server %d", 
                    time, customer.get().toString(), server.get().id);
        } else if (this.state == State.SERVED) {
            return this.server.get().isSelfCheckOut() 
                ? String.format("%.3f %s served by self-check %d", 
                    time, customer.get().toString(), server.get().id)
                : String.format("%.3f %s served by server %d", 
                    time, customer.get().toString(), server.get().id);
        } else if (this.state == State.SERVER_REST) {
            return String.format("----------%.3f server %d shutdown------", time, server.get().id);
        } else {
            return String.format("----------%.3f server %d online--------", time, server.get().id);
        }
    }
}
