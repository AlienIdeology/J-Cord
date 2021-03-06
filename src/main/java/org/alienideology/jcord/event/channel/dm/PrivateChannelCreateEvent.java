package org.alienideology.jcord.event.channel.dm;

import org.alienideology.jcord.event.channel.ChannelCreateEvent;
import org.alienideology.jcord.handle.channel.IPrivateChannel;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.channel.Channel;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlienIdeology
 */
public class PrivateChannelCreateEvent extends ChannelCreateEvent implements IPrivateChannelEvent {

    public PrivateChannelCreateEvent(IdentityImpl identity, int sequence, Channel channel) {
        super(identity, sequence, channel);
    }

    @Override
    @NotNull
    public IPrivateChannel getPrivateChannel() {
        return (IPrivateChannel) channel;
    }

}
