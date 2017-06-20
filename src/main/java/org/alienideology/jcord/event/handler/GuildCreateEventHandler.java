package org.alienideology.jcord.event.handler;

import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.event.guild.GuildCreateEvent;
import org.alienideology.jcord.internal.object.guild.Guild;
import org.json.JSONObject;

/**
 * @author AlienIdeology
 */
public class GuildCreateEventHandler extends EventHandler {

    public GuildCreateEventHandler(IdentityImpl identity) {
        super(identity);
    }

    @Override
    public void dispatchEvent(JSONObject json, int sequence) {

        // Ignore initial guild create event
        if (identity.getGuild(json.getString("id")) != null) {
        } else {
            Guild guild = builder.buildGuild(json);
            GuildCreateEvent created = new GuildCreateEvent(identity, guild, sequence);
            fireEvent(created);
        }

    }

}