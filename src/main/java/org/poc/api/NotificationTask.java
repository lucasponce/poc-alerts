package org.poc.api;

/**
 * Interface that defines a plugable notification task to be invoked by
 */
public interface NotificationTask extends Runnable {

    String getId();

    void setMessage(String msg);

}
