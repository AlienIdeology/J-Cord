package org.alienideology.jcord.event.client.call.user;

import org.alienideology.jcord.handle.client.IClient;
import org.alienideology.jcord.handle.client.call.ICall;
import org.alienideology.jcord.handle.client.call.ICallUser;

/**
 * @author AlienIdeology
 */
public class CallUserStartWaitingEvent extends CallUserEvent {

    public CallUserStartWaitingEvent(IClient client, int sequence, ICall call, ICallUser callUser) {
        super(client, sequence, call, callUser);
    }

}
