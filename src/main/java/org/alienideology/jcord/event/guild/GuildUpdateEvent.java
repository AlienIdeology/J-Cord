package org.alienideology.jcord.event.guild;

import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.guild.Guild;

/**
 * @author AlienIdeology
 */
public class GuildUpdateEvent extends GuildEvent {

    protected final Guild oldGuild;

    public GuildUpdateEvent(IdentityImpl identity, int sequence, Guild newGuild, Guild oldGuild) {
        super(identity, sequence, newGuild);
        this.oldGuild = oldGuild;
    }

    /**
     * @return The new, updated guild.
     */
    @Override
    public Guild getGuild() {
        return super.getGuild();
    }

    public Guild getOldGuild() {
        return oldGuild;
    }
}
