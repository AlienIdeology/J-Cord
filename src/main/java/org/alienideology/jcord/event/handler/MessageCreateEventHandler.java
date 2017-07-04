package org.alienideology.jcord.event.handler;

import org.alienideology.jcord.event.message.dm.PrivateMessageCreateEvent;
import org.alienideology.jcord.event.message.guild.GuildMessageCreateEvent;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.channel.MessageChannel;
import org.alienideology.jcord.internal.object.message.Message;
import org.json.JSONObject;

/**
 * @author AlienIdeology
 */
public class MessageCreateEventHandler extends EventHandler {

    public MessageCreateEventHandler(IdentityImpl identity) {
        super(identity);
    }

    @Override
    public void dispatchEvent(JSONObject json, int sequence) {
        System.out.println(json.toString(4));
        Message message = builder.buildMessage(json);
        try {
            MessageChannel channel = (MessageChannel) message.getChannel();
            channel.setLatestMessage(message);
            if (channel.isPrivate()) {
                dispatchEvent(new PrivateMessageCreateEvent(identity, sequence, channel, message));
            } else {
                dispatchEvent(new GuildMessageCreateEvent(identity, sequence, channel, message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
