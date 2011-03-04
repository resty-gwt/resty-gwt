package org.fusesource.restygwt.example.client.event;

import java.util.ArrayList;
import java.util.List;

public class FooModelChangedEventHandlerImpl implements FooModelChangedEventHandler {

    /**
     * for testing purposes, we keep all events catched
     */
    private List<FooModelChangedEvent> catched = new ArrayList<FooModelChangedEvent>();

    @Override
    public void onEvent(FooModelChangedEvent event) {
        catched.add(event);
    }

    /**
     * e.g. in a test, access all events catched
     *
     * @return
     */
    public List<FooModelChangedEvent> getAllCatchedEvents() {
        return catched;
    }
}
