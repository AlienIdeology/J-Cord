package org.alienideology.jcord.event.handler;

import org.alienideology.jcord.Identity;
import org.alienideology.jcord.IdentityType;
import org.alienideology.jcord.event.gateway.ReadyEvent;
import org.alienideology.jcord.internal.gateway.GatewayAdaptor;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.ObjectBuilder;
import org.alienideology.jcord.internal.object.client.Client;
import org.alienideology.jcord.internal.object.client.Profile;
import org.alienideology.jcord.internal.object.client.setting.ClientSetting;
import org.alienideology.jcord.internal.object.user.User;
import org.alienideology.jcord.internal.rest.HttpPath;
import org.alienideology.jcord.internal.rest.Requester;
import org.alienideology.jcord.util.log.LogLevel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author AlienIdeology
 */
public class ReadyEventHandler extends EventHandler {

    private final GatewayAdaptor gateway;

    public ReadyEventHandler(IdentityImpl identity, GatewayAdaptor gateway) {
        super(identity);
        this.gateway = gateway;
    }

    @Override
    public void dispatchEvent(JSONObject json, int sequence) {
        String session_id = json.getString("session_id");

        try {
            /* Create Guilds */
            // We do not use GuildCreateEvent to initialize guilds
            // We use this to get the guilds' ID, then post http request to get guild information
            JSONArray guilds = json.getJSONArray("guilds");
            for (int i = 0; i < guilds.length(); i++) {
                JSONObject guild = guilds.getJSONObject(i);
                if (guild.has("unavailable") && guild.getBoolean("unavailable")) {
                    guild = new Requester(identity, HttpPath.Guild.GET_GUILD).request(guild.getString("id")).getAsJSONObject();
                }
                builder.buildGuild(guild); // Guild added to identity automatically
            }
            identity.LOG.log(LogLevel.DEBUG, "[READY] Guilds: " + guilds.length());

            /* Create PrivateChannels */
            // This will not work FOR BOTS since Discord does not send private channels on ready event
            int pmCounts = 0;
            JSONArray pms = json.getJSONArray("private_channels");
            for (int i = 0; i < pms.length(); i++) {
                JSONObject pm = pms.getJSONObject(i);

                if (pm.getJSONArray("recipients").length() == 1) {
                    pmCounts++;
                    builder.buildPrivateChannel(pm);
                }
            }
            identity.LOG.log(LogLevel.DEBUG, "[READY] Private Channels: " + pmCounts);

            /* Create Self User */
            final User self = builder.buildUser(json.getJSONObject("user"));
            identity.setSelf(self);
            identity.LOG.log(LogLevel.DEBUG, "[READY] Self");

            if (identity.getType().equals(IdentityType.CLIENT)) {

                Client client = identity.getClient();
                ObjectBuilder cb = new ObjectBuilder(client);

                /* Create Client Setting */
                ClientSetting setting = cb.buildClientSetting(json.getJSONObject("user_settings"));
                client.setSetting(setting);

                /* Create Profile */
                Profile profile = cb.buildProfile(json.getJSONObject("user"), self);
                client.setProfile(profile);

                /* Create Group */
                for (int i = 0; i < pms.length(); i++) {
                    JSONObject dm = pms.getJSONObject(i);

                    if (dm.getJSONArray("recipients").length() > 1) {
                        cb.buildGroup(dm);// Added to client automatically
                    }
                }
                identity.LOG.log(LogLevel.DEBUG, "[READY] Client - Groups: " + (pms.length() - pmCounts));

                /* Create Relationships */
                JSONArray relations = json.getJSONArray("relationships");
                for (int i = 0; i < relations.length(); i++) {
                    JSONObject rs = relations.getJSONObject(i);
                    cb.buildRelationship(rs); // Added to client automatically
                }
                identity.LOG.log(LogLevel.DEBUG, "[READY] Client - Relationships: " + relations.length());

                /* Create Notes */
                JSONObject notes = json.getJSONObject("notes");
                for (String key : notes.keySet()) {
                    cb.buildNote(key, notes.getString(key)); // Added to client automatically
                }
                identity.LOG.log(LogLevel.DEBUG, "[READY] Client - Notes: " + notes.length());

                /* Create Guild and TextChannel Settings */
                JSONArray settings = json.getJSONArray("user_guild_settings");
                for (int i = 0; i < settings.length(); i++) {
                    JSONObject gs = settings.getJSONObject(i);
                    cb.buildGuildSetting(gs); // Added to client automatically
                }
                identity.LOG.log(LogLevel.DEBUG, "[READY] Client - Guild (& TextChannel) Settings: " + settings.length());
            }

            dispatchEvent(new ReadyEvent(identity, gateway, sequence, session_id));

            identity.CONNECTION = Identity.Connection.READY;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
