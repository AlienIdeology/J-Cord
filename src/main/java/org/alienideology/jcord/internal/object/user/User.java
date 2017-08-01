package org.alienideology.jcord.internal.object.user;

import org.alienideology.jcord.handle.channel.IPrivateChannel;
import org.alienideology.jcord.handle.user.IPresence;
import org.alienideology.jcord.handle.user.IUser;
import org.alienideology.jcord.handle.user.OnlineStatus;
import org.alienideology.jcord.internal.gateway.HttpPath;
import org.alienideology.jcord.internal.gateway.Requester;
import org.alienideology.jcord.internal.object.DiscordObject;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.ObjectBuilder;
import org.alienideology.jcord.internal.object.channel.PrivateChannel;
import org.json.JSONObject;

import java.util.Objects;

/**
 * @author AlienIdeology
 */
// TODO: Connection Object
public class User extends DiscordObject implements IUser {

    protected final String id;

    private Presence presence;

    protected String name;
    protected String discriminator;
    private String avatar;
    private String email;

    private final boolean isBot;
    private final boolean isWebHook;
    private boolean isVerified;
    private boolean MFAEnabled;

    public User (IdentityImpl identity, String id, String name, String discriminator, String avatar, String email,
                 boolean isBot, boolean isWebHook, boolean isVerified, boolean MFAEnabled) {
        super(identity);
        this.id = id;
        this.name = name;
        this.discriminator = discriminator;
        this.avatar = avatar;
        this.email = email;
        this.isBot = isBot;
        this.isWebHook = isWebHook;
        this.isVerified = isVerified;
        this.MFAEnabled = MFAEnabled;
        this.presence = new Presence(identity, this, null, OnlineStatus.OFFLINE, null);
    }

    @Override
    public PrivateChannel getPrivateChannel() {
        PrivateChannel dm = (PrivateChannel) identity.getPrivateChannelByUserId(id);

        // Private Channel has not exist
        if (dm == null) {
            JSONObject json = new Requester(identity, HttpPath.User.CREATE_DM).request()
                    .updateRequestWithBody(body -> body.header("Content-Type", "app/json")
                            .body(new JSONObject().put("recipient_id", id))).getAsJSONObject();

            dm = new ObjectBuilder(identity).buildPrivateChannel(json);
            identity.addPrivateChannel(dm);
        }
        return dm;
    }

    @Override
    public void closePrivateChannel() {
        IPrivateChannel channel = identity.getPrivateChannelByUserId(id);
        if (channel == null) return;
        new Requester(identity, HttpPath.Channel.DELETE_CHANNEL).request(channel.getId())
                .performRequest();
        identity.removePrivateChannel(id);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDiscriminator() {
        return discriminator;
    }

    @Override
    public String getAvatarHash() {
        return avatar;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public IPresence getPresence() {
        return presence;
    }

    @Override
    public boolean isBot() {
        return isBot;
    }

    @Override
    public boolean isWebHook() {
        return isWebHook;
    }

    @Override
    public boolean isVerified() {
        return isVerified;
    }

    @Override
    public boolean isMFAEnabled() {
        return MFAEnabled;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof User) && Objects.equals(this.id, ((User) obj).getId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "key='" + id + '\'' +
                ", name='" + name + '\'' +
                ", discriminator='" + discriminator + '\'' +
                '}';
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }
}
