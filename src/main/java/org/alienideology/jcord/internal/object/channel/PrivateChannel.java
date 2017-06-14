package org.alienideology.jcord.internal.object.channel;

import org.alienideology.jcord.internal.Identity;
import org.alienideology.jcord.internal.object.user.User;
import org.alienideology.jcord.internal.object.Message;

import java.util.Objects;

/**
 * PrivateChannel - A one-to-one channel between two users
 * @author AlienIdeology
 */
public class PrivateChannel extends MessageChannel {

    private final User recipient;

    public PrivateChannel(Identity identity, String id, User recipient, Message lastMessage) {
        super(identity, id, Type.PRIVATE, lastMessage);
        this.recipient = recipient;
    }

    public User getRecipient() {
        return recipient;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PrivateChannel) && Objects.equals(this.id, ((PrivateChannel) obj).getId());
    }

    @Override
    public String toString() {
        return "ID: "+id+"\tRecipient: "+ recipient.getName();
    }

}