package pl.ibcgames.smvotifier.command;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import pl.ibcgames.smvotifier.SMVotifier;

import javax.annotation.Nonnull;

import java.util.Arrays;

import static org.spongepowered.api.command.CommandResult.empty;
import static pl.ibcgames.smvotifier.util.TextUtils.message;

public class TestCommand extends Command {

    public TestCommand(Text description, String permission, String[] alias) {
        super(description, permission, alias);
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
        String token = SMVotifier.getInstance().config.identifier;
        boolean require_permission = SMVotifier.getInstance().config.require_permissions;

        if (token == null || token.equalsIgnoreCase("tutaj_wpisz_identyfikator")) {
            src.sendMessage(message("&cBrak identyfikatora serwera w konfiguracji SM-Votifier"));
            src.sendMessage(message("&cWiecej informacji znajdziesz pod adresem:"));
            src.sendMessage(message("&ahttps://serwery-minecraft.pl/konfiguracja-pluginu"));
            return empty();
        }
        if (!(src instanceof Player)) {
            src.sendMessage(message("&cTa komenda moze zostac wykonana jedynie przez gracza"));
            return empty();
        }

        if (!src.hasPermission("smvotifier.admin")) {
            src.sendMessage(message("&cTa komenda jest dostepna tylko dla operatorow serwera"));
            return empty();
        }

        if (require_permission && !src.hasPermission("smvotifier.nagroda")) {
            src.sendMessage(message("&cPotrzebujesz uprawnienia &asmvotifier.nagroda"));
            return empty();
        }

        src.sendMessage(message("&aTa komenda pozwala na przetestowanie nagrody"));
        src.sendMessage(message("&aAby sprawdzic polaczenie pluginu z lista serwerow"));
        src.sendMessage(message("&apo prostu odbierz nagrode za pomoca &c/sm-nagroda"));
        runCommands((Player) src);

        return empty();
    }

    public void runCommands(Player src) {
        Arrays.stream(SMVotifier.getInstance().config.commands).forEach(command -> {
            command = command.replace("{GRACZ}", src.getName());
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
        });
    }
}
