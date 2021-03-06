package org.alienideology.jcord.event.handler;

import org.alienideology.jcord.event.Event;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.ObjectBuilder;
import org.alienideology.jcord.util.log.Logger;
import org.json.JSONObject;

/**
 * EventHandler - Handle general events and dispatch them to more specific ones.
 * @author AlienIdeology
 */
public abstract class EventHandler {

    protected final Logger logger;

    protected final IdentityImpl identity;
    protected final ObjectBuilder builder;

    /**
     * Constructor
     * @param identity The identity events fired will belongs to.
     */
    public EventHandler (IdentityImpl identity) {
        this.identity = identity;
        this.logger = identity.getEventManager().getLogger();
        this.builder = new ObjectBuilder(this.identity);
    }

    public IdentityImpl getIdentity() {
        return identity;
    }

    /**
     * Dispatch events in every DispatcherAdaptor
     *
     * @param event The event to get fired
     */
    public void dispatchEvent(Event event) {
        identity.getEventManager().dispatchEvent(event);
    }

    /**
     * Process and dispatch events base on the provided json.
     *
     * @param json The json of an Discord Gateway event.
     * @param sequence The Gateway sequence.
     */
    public abstract void dispatchEvent(JSONObject json, int sequence);

}
