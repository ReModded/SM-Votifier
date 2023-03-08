package pl.ibcgames.smvotifier.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import pl.ibcgames.smvotifier.SMVotifier;
import pl.ibcgames.smvotifier.util.RequestUtils;

import javax.annotation.Nonnull;
import java.util.Date;

import static org.spongepowered.api.command.CommandResult.empty;
import static pl.ibcgames.smvotifier.util.TextUtils.message;

public class VoteCommand extends Command {

    public VoteCommand(Text description, String permission, String[] alias) {
        super(description, permission, alias);
    }

    JsonArray messages;
    String url;
    Date lastUpdate = new Date();

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
        if (!(src instanceof Player))
            return empty();
        String token = SMVotifier.getInstance().config.identifier;
        Sponge.getScheduler().createAsyncExecutor(SMVotifier.getInstance()).execute(() -> {
            try {
                if (token == null || token.equalsIgnoreCase("tutaj_wpisz_identyfikator")) {
                    src.sendMessage(message("&cBrak identyfikatora serwera w konfiguracji SM-Votifier"));
                    src.sendMessage(message("&cWiecej informacji znajdziesz pod adresem:"));
                    src.sendMessage(message("&ahttps://serwery-minecraft.pl/konfiguracja-pluginu"));
                    return;
                }

                long diff = new Date().getTime() - lastUpdate.getTime();
                long diffMinutes = diff / (60 * 1000);
                lastUpdate = new Date();

                if (url == null || diffMinutes >= 60) {
                    src.sendMessage(message("&aTrwa pobieranie danych..."));
                    JsonObject res = RequestUtils.sendRequest("https://serwery-minecraft.pl/api/server-by-key/" + token + "/get-vote");

                    url = res.get("vote_url").getAsString();
                    messages = (JsonArray) res.get("text");
                }

                messages.forEach((message) -> src.sendMessage(message(message.getAsString())));
                src.sendMessage(message(url));
            } catch (Exception e) {
                e.printStackTrace();
                src.sendMessage(message("&cNie udalo sie pobrac danych, sprobuj pozniej"));
            }
        });

        return empty();
    }
}
