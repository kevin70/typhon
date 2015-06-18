package org.skfiy.typhon.spi;

public interface IPlayerEvent<T> extends Event<T> {

    /**
     *
     * @return
     */
    String getEventName();

    /**
     *
     * @return
     */
    boolean isDeletable();
}
