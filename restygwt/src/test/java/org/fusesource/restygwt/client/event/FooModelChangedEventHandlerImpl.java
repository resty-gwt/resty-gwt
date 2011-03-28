package org.fusesource.restygwt.client.event;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.restygwt.example.client.event.ModelChangeEvent;
import org.fusesource.restygwt.example.client.event.ModelChangedEventHandler;

public class FooModelChangedEventHandlerImpl implements ModelChangedEventHandler {

    /**
     * for testing purposes, we keep all events catched
     */
    private List<ModelChangeEvent> catched = new ArrayList<ModelChangeEvent>();

    @Override
    public void onModelChange(ModelChangeEvent event) {
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
