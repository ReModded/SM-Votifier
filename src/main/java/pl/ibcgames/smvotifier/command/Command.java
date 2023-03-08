package pl.ibcgames.smvotifier.command;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor {
    public Text description;
    public String permission;
    public String[] alias;

    public List<CommandElement> args = new ArrayList<>();
    public List<Command> subCommands = new ArrayList<>();

    public Command(Text description, String permission, String[] alias) {
        this.description = description;
        this.permission = permission;
        this.alias = alias;
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        return CommandResult.empty();
    }

    public void registerCommand(PluginContainer plugin) {
        Sponge.getCommandManager().register(plugin, getCommandSpec(plugin), alias);
    }

    private CommandSpec getCommandSpec(PluginContainer plugin) {
        CommandSpec.Builder cmdSpec = CommandSpec.builder()
                .description(description)
                .permission(permission);

        if (!args.isEmpty())
            args.forEach(cmdSpec::arguments);

        cmdSpec.executor(this);

        subCommands.forEach(subCommand -> cmdSpec.child(subCommand.getCommandSpec(plugin), subCommand.alias));

        return cmdSpec.build();
    }
}
