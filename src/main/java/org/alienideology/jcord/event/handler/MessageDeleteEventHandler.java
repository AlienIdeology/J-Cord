package org.alienideology.jcord.event.handler;

import org.alienideology.jcord.event.message.dm.PrivateMessageDeleteEvent;
import org.alienideology.jcord.event.message.guild.GuildMessageDeleteEvent;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.channel.MessageChannel;
import org.alienideology.jcord.util.log.LogLevel;
import org.json.JSONObject;

import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * @author AlienIdeology
 */
public class MessageDeleteEventHandler extends EventHandler {

    public MessageDeleteEventHandler(IdentityImpl identity) {
        super(identity);
    }

    @Override
    public void dispatchEvent(JSONObject json, int sequence) {
        String msg_id = json.getString("id");
        String channel_id = json.getString("channel_id");

        MessageChannel channel = (MessageChannel) identity.getMessageChannel(channel_id);
        if (channel != null) {

            if (channel.isPrivate()) {
                dispatchEvent(new PrivateMessageDeleteEvent(identity, sequence, channel, msg_id, OffsetDateTime.from(Instant.now())));
            } else {
                dispatchEvent(new GuildMessageDeleteEvent(identity, sequence, channel, msg_id, OffsetDateTime.from(Instant.now())));
            }

        } else {
            logger.log(LogLevel.FETAL, "[UNKNOWN CHANNEL][MESSAGE_DELETE_EVENT]");
        }
    }
}
