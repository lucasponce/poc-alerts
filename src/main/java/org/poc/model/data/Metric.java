package org.poc.model.data;

/**
 * An external incoming event.
 */
public class Metric extends Event {

    Double value;

    public Metric(String id, Long time, Double value) {
        super(id, time);
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Metric{");
        sb.append("value=").append(value);
        sb.append(", id='").append(getId()).append('\'');
        sb.append(", time=").append(getTime());
        sb.append('}');
        return sb.toString();
    }
}
