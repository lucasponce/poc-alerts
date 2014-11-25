package org.poc.api;

/**
 * An alert representation thrown from rules engine.
 */
public class Alert {

    String id;

    String msg;

    public Alert() {
    }

    public Alert(String id, String msg) {
        if (id==null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        this.id = id;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alert alert = (Alert) o;

        if (id != null ? !id.equals(alert.id) : alert.id != null) return false;
        if (msg != null ? !msg.equals(alert.msg) : alert.msg != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (msg != null ? msg.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id='" + id + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
