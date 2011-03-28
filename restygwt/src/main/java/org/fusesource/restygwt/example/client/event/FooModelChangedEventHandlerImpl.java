package org.fusesource.restygwt.example.client.event;

import java.util.ArrayList;
import java.util.List;

public class FooModelChangedEventHandlerImpl implements ModelChangedEventHandler {

    /**
     * for testing purposes, we keep all events catched
     */
    private List<ModelChangeEvent> catched = new ArrayList<ModelChangeEvent>();

    @Override
    public void onEvent(ModelChangeEvent event) {
        catched.add(event);
    }

    /**
     * e.g. in a test, access all events catched
     *
     * @return
     */
    public List<ModelChangeEvent> getAllCatchedEvents() {
        return catched;
    }
}
