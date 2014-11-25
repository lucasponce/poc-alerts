package org.poc.api;

/**
 * An intermediate state representation managed by rules engine.
 */
public class State {

    String id;

    String state;

    public State() {
    }

    public State(String id, String state) {
        if (id==null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.id = id;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        State state1 = (State) o;

        if (id != null ? !id.equals(state1.id) : state1.id != null) return false;
        if (state != null ? !state.equals(state1.state) : state1.state != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "State{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
