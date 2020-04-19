package cs2030.simulator;

import java.util.Queue;
import java.util.LinkedList;

/**
 * A simple class for implementing a server object.
 * Server object keeps track of the customer it is 
 * serving.
 *
 * @author LeeEnHao_A0204679W
 * @see Customer
 * */
public class Server {
    private Customer customerBeingServed;
    private double nextServiceTime;
    private final int capacity;
    private final Queue<Customer> waitList;
    public final int id;
    private static int count = 0;
    private boolean isResting;
    private boolean isSelfCheckOut;

    /**
     * Creates an idle server.
     * */
    public Server(int initCapacity, boolean isSC) {
        count++;
        this.id = count;
        this.nextServiceTime = 0.000;
        this.customerBeingServed = null;
        this.capacity = initCapacity;
        this.waitList = new LinkedList<>();
        isResting = false;
        isSelfCheckOut = isSC;
    }

    /**
     * Checks if the current server can serve a given customer that
     * is not on the wait list. Can be served if the server is idle 
     * or the next service time is greater than the customer arrival time.
     *
     * @param customer Customer of inquiry.
     * @return Indicates whether the customer is servable by server.
     * */
    public boolean canServe(Customer customer) {
        if (this.isIdle()) {
            return true;
        } else {
            if (customer.getArrivalTime() >= this.nextServiceTime) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Adds a customer to the server wait lis.
     *
     * @param customer Waiting customer.*/
    public void enqueue(Customer customer) {
        assert !canServe(customer) && hasWaitingSpace();
        waitList.add(customer);
    }

    /**
     * Checks if the queue/waitlist for server is empty.
     *
     * @return True if the waitlist/queue is empty.*/
    public boolean isQueueEmpty() {
        return hasWaitingSpace() && waitList.size() == 0;
    }

    /**
     * Checks if the server is a self checkout server.
     * 
     * @return true if this a self checkout server.
     */
    public boolean isSelfCheckOut() {
        return isSelfCheckOut;
    }

    /**
     * Checks if anymore customers can be added to wait list.
     *
     * @return True if there is more space for waiting customers.
     * */
    public boolean hasWaitingSpace() {
        return waitList.size() < capacity;
    }

    /**
     * Gets the queue size.
     * 
     * @return size of queue
     */
    public int getQSize() {
        return waitList.size();
    }

    /**
     * Peeks the first-in-line customer of the queue.
     * 
     * @return customer at the front of queue, null if no customer
     */
    public Customer peek() {
        return this.waitList.peek();
    }

    /**
     * Checks if the customer is at the front of the queue.
     * 
     * @param c customer.
     * @return true if the customer is the first-in-line for this server.
     */
    public boolean check(Customer c) {
        if (waitList.size() <= 0) {
            return false;
        } else {
            return c.equals(waitList.peek());
        }
    }

    /**
     * Makes the server serve the customer that is waiting.
     * 
     * @param time Time the server serves the waiting customer.
     * @param completionTime Time of the completion.
     * @return The next in line customer that is currently being served.
     * */
    public Customer dequeueAndServe(double time, double completionTime) {
        assert waitList.size() > 0;
        Customer c = waitList.poll();
        this.serve(c, time, completionTime);
        return c;
    }

    /**
     * Dequeue the first-in-line customer but does not make this server serve the customer.
     * 
     * @return the customer being dequeued
     */
    public Customer dequeue() {
        assert waitList.size() > 0;
        return waitList.poll();
    }

    /**
     * Returns the next time the server is able to serve.
     *
     * @return time
     * */
    public double getNextServiceTime() {
        return nextServiceTime;
    }

    /**
     * Makes the server serve the customer.
     *
     * @param customer Customer to be served.
     * @param time Time which server serve customer.
     * */
    public void serve(Customer customer, double time, double completionTime) {
        nextServiceTime = time + completionTime;
        customerBeingServed = customer;
    }

    /**
     * Resets the server, wiping the customer and reseting the next service time to
     * 0.000. The server is now idle. Queue may still have customers.
     */
    public void reset() {
        this.customerBeingServed = null;
        this.nextServiceTime = 0.000;
    }

    /**
     * Checks if the server is serving any customer now.
     */
    public boolean isIdle() {
        return customerBeingServed == null;
    }

    /**
     * Changes the server state to resting.
     */
    public void rest() {
        isResting = true;
    }

    /**
     * Changes the server state to back to work.
     */
    public void stopResting() {
        isResting = false;
    }

    /**
     * Checks if the server is resting.
     *
     * @return True if server is resting
     */
    public boolean isResting() {
        return isResting;
    }

    @Override
    public String toString() {
        if (this.isIdle()) {
            return "Server is idle as there are no customers to serve";
        } else {
            return String.format("Customer served; next service @ %.3f", nextServiceTime); 
        }
    }

}
