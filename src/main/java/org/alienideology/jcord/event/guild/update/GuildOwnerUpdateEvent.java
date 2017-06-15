package org.alienideology.jcord.event.guild.update;

import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.event.guild.GuildUpdateEvent;
import org.alienideology.jcord.internal.object.guild.Guild;
import org.alienideology.jcord.internal.object.guild.Member;

/**
 * @author AlienIdeology
 */
public class GuildOwnerUpdateEvent extends GuildUpdateEvent {

    public GuildOwnerUpdateEvent(IdentityImpl identity, Guild guild, int sequence, Guild oldGuild) {
        super(identity, guild, sequence, oldGuild);
    }

    public Member getNewOwner() {
        return guild.getOwner();
    }

    public Member getOldOwner() {
        return oldGuild.getOwner();
    }

}
