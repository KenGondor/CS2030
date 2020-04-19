package cs2030.simulator;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public class Servlet implements EventHandler<Event> {
    /**Number of available servers. */
    private final Server[] serverList;
    private final int selfIndex;

    /**The rng for server rest. */
    private final RandomGenerator rng;

    /**Benchmark for server restting probability. */
    private double probability;

    /**Tracking queue for self-checkouts. Cutomers will be adidtionally enqueued here. */
    private Queue<Customer> pq;

    /**
     * Constructor for a Servlet object.
     *
     * @param servers Number of servers available.
     * */
    public Servlet(int servers, int checkers, int qMax, 
        RandomGenerator randomGenerator, double restProb) {

        serverList = new Server[servers + checkers];

        for (int i = 0; i < servers; i++) {

            serverList[i] = new Server(qMax, false);

        }

        // The first self-checkout maintains the combined queue.
        // Other checkout do not maintain queues.

        for (int i = 0; i < checkers; i++) {

            if (i == 0) {

                serverList[servers + i] = new Server(qMax, true);

            } else {

                serverList[servers + i] = new Server(0, true);

            }

        }

        selfIndex = servers;

        rng = randomGenerator;

        probability = restProb;

        pq = new PriorityQueue<>();

    }


    /**
     * Handles the given event and updates the statistics of the event.
     * The returned event may or may not exist depending on the event handled.
     *
     * @param e The event to be handled.
     * @param stats The event statistics tracker.
     * */
    @Override
    public Optional<Event> handle(Event e, Statistics stats) {
        switch (e.state) {

            case ARRIVED:
                return handleArrived(e, stats);

            case LEFT:
                return handleLeft(e, stats);

            case DONE:
                return handleDone(e, stats);

            case WAITING:
                return handleWaiting(e, stats);

            case SERVED:
                return handleServed(e, stats);
                
            case SERVER_REST:
                return handleServerRest(e, stats);

            case SERVER_BACK:
                return handleServerBack(e, stats);

            default:
                return Optional.empty();
        }
    }


    /**
     * A wait event also has no following event schduled after this.
     * Original implementation was serve event prediction based on 
     * server completion time to schdule the next serve event.
     * Enqueues the customer at the appropriate place.
     */
    private Optional<Event> handleWaiting(Event e, Statistics stats) {

        Server s = e.server.get();

        Customer c = e.customer.get();
        // Case 1: self-checkout server
        // Case 2: normal server

        if (s.isSelfCheckOut()) {

            pq.add(c);

            s.enqueue(c);

        } else {

            s.enqueue(c);

        }

        return Optional.empty();

    }


    /**
     * A leave event has no follwoing event.
     */
    private Optional<Event> handleLeft(Event e, Statistics stats) {
        return Optional.empty();
    }


    /**
     * A done event. Immediately resets the server, calling reset(), which does 
     * mutate the state of the queue the server has. 
     * Upon completion of service, the server may or may not rest.
     * If it does not rest, it will log a new event where the serve serves 
     * a first-in-line customer if there is a queue. Else it will be idle.
     * The dequeueing of customer is not done here.
     * */
    private Optional<Event> handleDone(Event e, Statistics stats) {
        
        Server s = e.server.get();

        s.reset();

        // Case 1: Server is self-checkout
        // Case 2: Normal server

        if (s.isSelfCheckOut()) { 
            // Case 1 : either checkout queue empty or not
            if (pq.size() > 0) {

                Customer c = pq.peek();// Do not dequeue here!!!

                return Optional.of(new ServeEvent(c, e.time, s, stats));

            } else {

                // Sits idle if no customer.
                return Optional.empty();

            }

        } else {
            // Case 2 : Either normal server rests or continue
            double restrate = rng.genRandomRest();

            if (restrate < probability) {

                return Optional.of(ServerEvent.rest(s, e.time));

            } else {

                if (!s.isQueueEmpty()) {
                    
                    Customer c = s.peek();// Do not dequeue here!!!
                    return Optional.of(new ServeEvent(c, e.time, s, stats));

                } else {
                    // Else the server sits idle
                    // No next following event
                    return Optional.empty();
                }
            }
        }
    }


    /**
     * Schedules a done event based on server's completion time. 
     * Makes the server serve the customer. Dequeues customer accordingly when
     * necessary.
     * */
    private Optional<Event> handleServed(Event e, Statistics stats) {
    
        Server s = e.server.get();      
    
        Customer c = e.customer.get();
    
        double completionTime = e.time + rng.genServiceTime();

        // Either of three cases: 
        // Case 1: customer is in checkout queue 
        // Case 2: customer is first-in-line in normal server queue
        // Case 3: customer just arrived(not queueing)
        
        if (s.isSelfCheckOut()) {
            // Case 1 : remove the customer from the combined queue & checkout private queue

            if (pq.size() > 0) {

                Customer guy = pq.poll();

                assert guy.equals(c);

            }

            remove(c);

            s.serve(c, e.time, completionTime);

            return Optional.of(new DoneEvent(c, completionTime, s, stats));

        } else if (s.check(c)) {

            // Case 2
            s.dequeueAndServe(e.time, completionTime);

            return Optional.of(new DoneEvent(c, completionTime, s, stats));

        } else {

            // Case 3
            s.serve(c, e.time, completionTime);

            return Optional.of(new DoneEvent(c, completionTime, s, stats));

        }

    }


    /**
     * Handles the arrival event. 
     * */
    private Optional<Event> handleArrived(Event e, Statistics stats) {

        Customer c = e.customer.get();

        if (!c.isGreedy()) {

            // Does a linear scan and if possible, inserts the customer accordingly
            // so that it is either served or waiting to be served.
            // If none of the two happens, the customer leaves.
            Optional<Pair<Integer, State>> didCustomerLeave = linearInsert(c);

            Optional<Event> ret = didCustomerLeave.map(pair -> {
                return pair.second == State.SERVED
                    ? new ServeEvent(c, e.time, serverList[pair.first], stats)
                    : new WaitEvent(c, e.time, serverList[pair.first], stats);
            });
           
            return ret.or(() -> Optional.of(new LeaveEvent(c, e.time, stats)));

        } else {
            Optional<Pair<Integer, State>> didCusLeave = biasedInsert(c);

            Optional<Event> ret = didCusLeave.map(pair -> {
                return pair.second == State.SERVED
                    ? new ServeEvent(c, e.time, serverList[pair.first], stats)
                    : new WaitEvent(c, e.time, serverList[pair.first], stats);
            });

            return ret.or(() -> Optional.of(new LeaveEvent(c, e.time, stats)));

        }

    }


    /**
     * Following event will be a server back event at a random time.
     */
    private Optional<Event> handleServerRest(Event e, Statistics stats) {

        Server s = e.server.get();

        s.rest();

        double time = e.time;

        double restEnd = time + rng.genRestPeriod();

        return Optional.of(ServerEvent.back(s, restEnd));

    }


    /**
     * Server stops resting. If queue is empty does nothing. 
     * Else the server will proceed to serve the first-in-line customer.
     */
    private Optional<Event> handleServerBack(Event e, Statistics stats) {
    
        Server s = e.server.get();
    
        assert s.isIdle();
    
        s.stopResting();
    
        if (!s.isQueueEmpty()) {
    
            // The server that is currently not serving will serve an enqueued
            // customer.
            // Following event to be logged is a serve event.
            Customer c = s.peek();

            return Optional.of(new ServeEvent(c, e.time, s, stats));

        } else {

            // Else the server sits idle.
            // No next following event.
            return Optional.empty();

        }

    }


    /**
     * Does a linear scan of all available servers to check the availability
     * of the servers. Returns true if no server is available to serve or enqueue
     * a customer.
     * */
    private boolean isFullHouse() {
        boolean boo = true;
        for (int i = 0; i < serverList.length; i++) {
            if (serverList[i].isIdle() || serverList[i].hasWaitingSpace()) {
                boo = false;
            }
        }
        return boo;
    }

    /**
     * Runs a linear scan twice, first to see if any server can serve the customer,
     * second to see if the customer can join a any server's wait queue.
     * Primary value returned would be the server identity and the state of the customer.
     * */
    private Optional<Pair<Integer, State>> linearInsert(Customer customer) {
        for (int i = 0; i < serverList.length; i++) {

            Server curr = serverList[i];

            if (curr.canServe(customer) && !curr.isResting()) {

                return Optional.of(new Pair<Integer, State>(i, State.SERVED));

            }

        } 

        for (int i = 0; i < serverList.length; i++) {
            // The other checkouts do not hold queues. No point in checking for queueing space
            Server curr = serverList[i];

            if (curr.hasWaitingSpace()) {

                return Optional.of(new Pair<Integer, State>(i, State.WAITING));

            }

        }

        return Optional.empty();

    }


    /**
     * Inserting a greedy customer into the queue if possible.
     * By smallest queue size.
     * 
     * @param customer customer to be inserted
     * @return server index and state of customer
     */
    private Optional<Pair<Integer, State>> biasedInsert(Customer customer) {
        for (int i = 0; i < serverList.length; i++) {

            Server curr = serverList[i];

            if (curr.canServe(customer) && !curr.isResting()) {

                return Optional.of(new Pair<Integer, State>(i, State.SERVED));

            }

        } 

        int index = -1;
        int minQ = Integer.MAX_VALUE;
        for (int i = 0; i < serverList.length; i++) {
            // The other checkouts do not hold queues. No point in checking for queueing space
            Server curr = serverList[i];

            if (curr.hasWaitingSpace()) {

                int qsize = curr.getQSize();

                if (qsize < minQ) {

                    minQ = qsize;

                    index = i;

                }

            }

        }

        return index == -1 
            ? Optional.empty()
            : Optional.of(new Pair<Integer, State>(index, State.WAITING));

    } 

    /**
     * Removes the customer from the focal queue of the first
     * checkout counter.(Ultility function)
     * 
     * @param c customer to be dequeued
     */
    private void remove(Customer c) {
        // The first check-out server holds the whole queue
        serverList[selfIndex].dequeue();
    }

    /**
     * Class to store more information.
     */
    private static final class Pair<T, U> {
        public final T first;
        public final U second;

        Pair(T t, U u) {
            first = t;
            second = u;
        }
    }
}
