package org.alienideology.jcord.event.guild.voice;

import org.alienideology.jcord.handle.channel.IVoiceChannel;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.guild.Guild;
import org.alienideology.jcord.internal.object.guild.Member;

/**
 * @author AlienIdeology
 */
public class GuildMemberLeaveVoiceEvent extends GuildMemberVoiceEvent {

    private final IVoiceChannel channelLeft;

    public GuildMemberLeaveVoiceEvent(IdentityImpl identity, int sequence, Guild guild, Member member, IVoiceChannel channelLeft) {
        super(identity, sequence, guild, member);
        this.channelLeft = channelLeft;
    }

    public IVoiceChannel getChannelLeft() {
        return channelLeft;
    }

}
