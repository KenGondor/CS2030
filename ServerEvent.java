package cs2030.simulator;

/**
 * An extension class to encapsulate server events.
 * 
 * @author LeeEnHao_A0204679W
 * */
public class ServerEvent extends Event {

    private ServerEvent(double time, State state, Server server) {
        super(null, state, time, server);
    }

    /**
     * Creates a Server resting event.
     * 
     * @param server The resting server
     * @param time The event time
     */
    public static ServerEvent rest(Server server, double time) {
        assert server != null;
        return new ServerEvent(time, State.SERVER_REST, server);
    }

    /**
     * Creates a server back to work event.
     * 
     * @param server The server finished resting
     * @param time The event time
     */
    public static ServerEvent back(Server server, double time) {
        assert server != null;
        return new ServerEvent(time, State.SERVER_BACK, server);
    }

    /**
     * Always return true for a server event. Enables quick identification
     * event type.
     * 
     * @return True.
     */
    @Override
    public boolean isServerEvent() {
        return true;
    }

}