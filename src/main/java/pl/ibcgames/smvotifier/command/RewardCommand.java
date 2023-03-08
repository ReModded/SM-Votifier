package pl.ibcgames.smvotifier.command;

import com.google.gson.JsonObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import pl.ibcgames.smvotifier.SMVotifier;
import pl.ibcgames.smvotifier.util.RequestUtils;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.spongepowered.api.command.CommandResult.empty;
import static pl.ibcgames.smvotifier.util.TextUtils.message;

public class RewardCommand extends Command {

    Map<String, Date> timeouts = new HashMap<>();
    public RewardCommand(Text description, String permission, String[] alias) {
        super(description, permission, alias);
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
        String token = SMVotifier.getInstance().config.identifier;
        boolean require_permission = SMVotifier.getInstance().config.require_permissions;
        Sponge.getScheduler().createAsyncExecutor(SMVotifier.getInstance()).execute(() -> {
            if (token == null || token.equalsIgnoreCase("tutaj_wpisz_identyfikator")) {
                src.sendMessage(message("&cBrak identyfikatora serwera w konfiguracji SM-Votifier"));
                src.sendMessage(message("&cWiecej informacji znajdziesz pod adresem:"));
                src.sendMessage(message("&ahttps://serwery-minecraft.pl/konfiguracja-pluginu"));
                return;
            }

            if (!(src instanceof Player)) {
                src.sendMessage(message("&cTa komenda moze zostac wykonana jedynie przez gracza"));
                return;
            }

            if (require_permission && !src.hasPermission("smvotifier.nagroda")) {
                src.sendMessage(message("&cPotrzebujesz uprawnienia &asmvotifier.nagroda"));
                return;
            }

            if (timeouts.containsKey(src.getName())) {
                Date d = timeouts.get(src.getName());
                long diff = (long) Math.floor((new Date().getTime() / 1000) - (d.getTime() / 1000));

                if (diff < 60) {
                    long remaining = 60 - diff;
                    src.sendMessage(message("&cTa komenda moze byc uzyta za " + remaining +"s"));

                    return;
                }
            }

            src.sendMessage(message("&aSprawdzamy Twoj glos, prosze czekac..."));
            JsonObject res = RequestUtils.sendRequest("https://serwery-minecraft.pl/api/server-by-key/" + token + "/get-vote/" + src.getName());
            timeouts.put(src.getName(), new Date());
            runCommands(res, (Player) src);
        });

        return empty();
    }

    private void runCommands(JsonObject res, Player sender) {
        boolean canClaimReward = false;

        if (res.has("can_claim_reward")) {
            canClaimReward = Boolean.parseBoolean(
                    res.get("can_claim_reward").toString()
            );
        }

        if (res.has("error")) {
            sender.sendMessage(message(res.get("error").toString()));
        }

        if (!canClaimReward && !res.has("error")) {
            sender.sendMessage(message("&cNie udalo sie odebrac nagrody, sprobuj pozniej"));
            return;
        }

        if (canClaimReward) {
            Arrays.stream(SMVotifier.getInstance().config.commands).forEach(command -> {
                command = command.replace("{GRACZ}", sender.getName());

                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
            });
        }
    }
}
