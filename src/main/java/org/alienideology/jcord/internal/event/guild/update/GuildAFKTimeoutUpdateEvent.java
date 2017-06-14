package org.alienideology.jcord.internal.event.guild.update;

import org.alienideology.jcord.internal.Identity;
import org.alienideology.jcord.internal.event.guild.GuildUpdateEvent;
import org.alienideology.jcord.internal.object.Guild;

/**
 * @author AlienIdeology
 */
public class GuildAFKTimeoutUpdateEvent extends GuildUpdateEvent {

    public GuildAFKTimeoutUpdateEvent(Identity identity, Guild newGuild, int sequence, Guild oldGuild) {
        super(identity, newGuild, sequence, oldGuild);
    }

    public Guild.AFK_Timeout getNewAFKTimeout() {
        return guild.getAfkTimeout();
    }

    public Guild.AFK_Timeout getOldAFKTimeout() {
        return oldGuild.getAfkTimeout();
    }

}