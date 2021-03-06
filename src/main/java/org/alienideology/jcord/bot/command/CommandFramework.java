package org.alienideology.jcord.bot.command;

import org.alienideology.jcord.event.DispatcherAdaptor;
import org.alienideology.jcord.event.message.MessageCreateEvent;
import org.alienideology.jcord.event.message.dm.PrivateMessageCreateEvent;
import org.alienideology.jcord.event.message.guild.GuildMessageCreateEvent;
import org.alienideology.jcord.handle.channel.IChannel;
import org.alienideology.jcord.handle.guild.IGuild;
import org.alienideology.jcord.handle.guild.IMember;
import org.alienideology.jcord.handle.message.IEmbed;
import org.alienideology.jcord.handle.message.IMessage;
import org.alienideology.jcord.handle.permission.Permission;
import org.alienideology.jcord.internal.object.IdentityImpl;
import org.alienideology.jcord.internal.object.channel.Channel;
import org.alienideology.jcord.internal.object.channel.MessageChannel;
import org.alienideology.jcord.internal.object.channel.PrivateChannel;
import org.alienideology.jcord.internal.object.channel.TextChannel;
import org.alienideology.jcord.internal.object.guild.Guild;
import org.alienideology.jcord.internal.object.guild.Member;
import org.alienideology.jcord.internal.object.message.Message;
import org.alienideology.jcord.internal.object.user.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CommandFramework - The core command framework of J-Cord.
 * @author AlienIdeology
 */
public class CommandFramework {

    private List<CommandResponder> responders;

    private List<String> prefixes;
    private HashMap<Command, ResponderContainer> annotations;
    private DispatcherAdaptor dispatcher;

    /**
     * Default Constructor
     */
    public CommandFramework() {
        this.responders = new ArrayList<>();
        this.prefixes = new ArrayList<>();
        this.annotations = new HashMap<>();
        this.dispatcher = new DispatcherAdaptor() {

            @Override
            public void onGuildMessageCreate(GuildMessageCreateEvent event) {
                if(filterSource(event)) return;
                for (Command command : annotations.keySet()) {
                    if (command.privateOnly()) return;
                    handleCommand(command, new CommandParser(event));
                }
            }

            @Override
            public void onPrivateMessageCreate(PrivateMessageCreateEvent event) {
                if(filterSource(event)) return;
                for (Command command : annotations.keySet()) {
                    if (command.guildOnly()) return;
                    handleCommand(command, new CommandParser(event));
                }
            }

            private boolean filterSource(MessageCreateEvent event) {
                if (event.getUser().isBot() || event.getUser().isWebHook() || event.getMessage().isFromSelf())
                    return true;

                if (prefixes == null || prefixes.size() == 0) {
                    IllegalStateException exception = new IllegalStateException("Prefix need to be set before listening to messages!");
                    exception.printStackTrace();
                    throw exception;
                }

                for (String prefix : prefixes) {
                    if (event.getMessage().getContent().startsWith(prefix))
                        return false;
                }
                return true;
            }

        };
    }

    /**
     * Set the prefix of this CommandFramework.
     * If no prefix is set, then the framework will not respond to any commands.
     * @param prefixes The varargs of prefixes. Can be more than 1.
     * @return The CommandFramework for chaining.
     */
    public CommandFramework setPrefixes(String... prefixes) {
        this.prefixes.addAll(Arrays.asList(prefixes));
        return this;
    }

    /**
     * Register objects that extends CommandResponder
     * @param commands The varargs of command objects
     * @return The CommandFramework for chaining.
     */
    public CommandFramework registerCommandResponders(CommandResponder... commands) {
        responders.addAll(Arrays.asList(commands));
        for (CommandResponder responder : commands) {
            for (Method method : responder.getClass().getMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    annotations.put(method.getAnnotation(Command.class), new ResponderContainer(responder, method));
                }
            }
        }
        return this;
    }

    /**
     * @return A list of prefixes
     */
    public List<String> getPrefixes() {
        return prefixes;
    }

    /**
     * @return The DispatcherAdaptor of this CommandFramework
     */
    public DispatcherAdaptor getDispatcher() {
        return dispatcher;
    }

    /**
     * @return The command responders registered to this command framework.
     */
    public List<CommandResponder> getResponders() {
        return responders;
    }

    /**
     * @return All aliases found in all CommandResponders.
     */
    private List<String> getAliases() {
        return annotations.keySet().stream().flatMap(c -> Arrays.stream(c.aliases())).collect(Collectors.toList());
    }

    @SuppressWarnings("InstantiatingObjectToGetClassObject")
    private void handleCommand(Command command, CommandParser parser) {
        MessageCreateEvent event = parser.event;

        /* Check Permissions */
        if (command.permissions().length != 0) { // Specified permissions
            Collection<Permission> permissions = Arrays.asList(command.permissions());
            if (event.fromType(IChannel.Type.GUILD_TEXT)) { // From Text Channels
                if (!event.getTextChannel().hasAllPermission(event.getMember(), permissions)) {
                    return;
                }
            }
        }

        /* Perform Actions */
        for (String alias : command.aliases()) {
            if (alias.equals(parser.alias)) {
                Method method = annotations.get(command).method;

                IdentityImpl identity = (IdentityImpl) event.getIdentity();
                IMessage message = event.getMessage();
                IGuild guild = event.getGuild();
                IMember member = event.getMember();

                Object[] params = new Object[method.getParameterTypes().length];

                HashMap<Type, Object> types = new HashMap<>();
                types.put(String.class, message.getContent());
                types.put(String[].class, parser.args);
                types.put(int.class, event.getSequence());
                types.put(Integer.TYPE, event.getSequence());
                // DiscordObjects
                types.put(IdentityImpl.class, identity);
                types.put(User.class, event.getUser());
                types.put(Message.class, message);
                types.put(Guild.class, guild);
                types.put(Member.class, member);
                // Channels
                types.put(Channel.class, event.getChannel());
                types.put(MessageChannel.class, event.getChannel());
                types.put(TextChannel.class, event.getTextChannel());
                types.put(PrivateChannel.class, event.getPrivateChannel());

                types.put(MessageCreateEvent.class, event);
                if (event instanceof GuildMessageCreateEvent) {
                    types.put(GuildMessageCreateEvent.class, event);
                } else {
                    types.put(PrivateMessageCreateEvent.class, event);
                }

                /* Initialize Parameters */
                for (int i = 0; i < params.length; i++) {
                    Type type = method.getParameterTypes()[i];
                    params[i] = types.get(type);
                }

                try {
                    Object invoked = method.invoke(annotations.get(command).responder, params);


                    /* Reply with return values */
                    if (invoked instanceof String) {
                        event.getChannel().sendMessage((String) invoked);
                    } else if (invoked instanceof IMessage) {
                        event.getChannel().sendMessage((IMessage) invoked);
                    } else if (invoked instanceof IEmbed) {
                        event.getChannel().sendMessage((IEmbed) invoked);
                    }
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * @return A list of {@link Command} annotations registered by CommandResponders.
     */
    public List<Command> getAnnotations() {
        return new ArrayList<>(annotations.keySet());
    }

    private class ResponderContainer {
        public CommandResponder responder;
        public Method method;

        public ResponderContainer(CommandResponder responder, Method method) {
            this.responder = responder;
            this.method = method;
        }

    }

    private class CommandParser {

        String alias;
        String[] args;
        MessageCreateEvent event;

        CommandParser(MessageCreateEvent event) {
            String content = event.getMessage().getContent();

            /* Find Prefix */
            for (String prefix : prefixes) {
                if (content.startsWith(prefix)) {
                    content = content.replaceFirst(prefix, "");
                    break;
                }
            }

            /* Find Aliases */
            List<String> aliases = getAliases();
            aliases.sort((o1, o2) -> Integer.compare(o2.length(), o1.length())); // Put longer aliases at the front

            for (int i = 0; i < aliases.size(); i++) {
                String alias = aliases.get(i);
                /* Find Match */
                if (content.startsWith(alias)) {
                    content = content.replaceFirst(alias, "");
                    this.alias = alias;
                    break;
                }
                /* No Match */
                if (i == aliases.size() - 1) {
                    content = content.replaceFirst(content.split("\\s+")[0], "");
                }
            }

            String[] split = content.split("\\s+");

            this.alias = alias != null ? // Initialized because has found match
                    alias : split.length == 0 ? // Avoid ArrayIndexOutOfBound
                            null : split[0];

            this.args = split.length <= 1 ? new String[]{} : Arrays.copyOfRange(split, 1, split.length);
            this.event = event;
        }
    }

}
