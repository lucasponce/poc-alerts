package org.poc.model.data;

/**
 * An internal incoming event.
 * It represents some internal state inside AlertsService.
 */
public class State extends Event {

    String state;

    public State(String id, Long time, String state) {
        super(id, time);
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("State{");
        sb.append("state='").append(state).append('\'');
        sb.append(", id='").append(getId()).append('\'');
        sb.append(", time=").append(getTime());
        sb.append('}');
        return sb.toString();
    }
}
