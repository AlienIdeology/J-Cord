package org.alienideology.jcord.event.channel.guild.voice.update;

import org.alienideology.jcord.event.channel.guild.voice.VoiceChannelUpdateEvent;
import org.alienideology.jcord.handle.channel.IGuildChannel;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.channel.Channel;

/**
 * @author AlienIdeology
 */
public class VoiceChannelBitrateUpdateEvent extends VoiceChannelUpdateEvent {

    public VoiceChannelBitrateUpdateEvent(IdentityImpl identity, int sequence, Channel channel, IGuildChannel oldChannel) {
        super(identity, sequence, channel, oldChannel);
    }

    public int getNewBitrate() {
        return channel.getBitrate();
    }

    public int getOldBitrate() {
        return oldChannel.getBitrate();
    }

}