package cs2030.simulator;

import java.util.Optional;

/**
 * Provides a method to handle events, whilst keeping track of statistics associated with the event.
 *
 * @author LeeEnHao_A0204679W
 * */
interface EventHandler<E extends Event> {

    /**
     * Handles the given event and updates the statistics of the event.
     * The returned event may or may not exist depending on the event handled.
     *
     * @param e The event to be handled.
     * @param stats The event statistics tracker.
     * */
    public Optional<E> handle(E e, Statistics stats);

}
