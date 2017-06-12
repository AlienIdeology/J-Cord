package org.alienideology.jcord.event.message.guild;

import com.sun.istack.internal.NotNull;
import org.alienideology.jcord.Identity;
import org.alienideology.jcord.event.message.MessageDeleteEvent;
import org.alienideology.jcord.object.Guild;
import org.alienideology.jcord.object.channel.MessageChannel;
import org.alienideology.jcord.object.channel.TextChannel;

import java.time.OffsetDateTime;

/**
 * @author AlienIdeology
 */
public class GuildMessageDeleteEvent extends MessageDeleteEvent implements IGuildMessageEvent {

    public GuildMessageDeleteEvent(Identity identity, int sequence, MessageChannel channel, String id, OffsetDateTime deleteTimStamp) {
        super(identity, sequence, channel, id, deleteTimStamp);
    }

    @Override
    @NotNull
    public Guild getGuild() {
        return super.getGuild();
    }

    @Override
    @NotNull
    public TextChannel getTextChannel() {
        return super.getTextChannel();
    }

}
