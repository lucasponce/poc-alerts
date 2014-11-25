package org.poc.api;

/**
 * An input event.
 */
public class Event {

    private String id;

    private Double value;

    private long timeStamp;

    public Event() {
    }

    public Event(String id, Double value, long timeStamp) {
        if (id==null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.id = id;
        this.value = value;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (timeStamp != event.timeStamp) return false;
        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (value != null ? !value.equals(event.value) : event.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
