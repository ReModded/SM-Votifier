package pl.ibcgames.smvotifier;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import pl.ibcgames.smvotifier.command.RewardCommand;
import pl.ibcgames.smvotifier.command.TestCommand;
import pl.ibcgames.smvotifier.command.VoteCommand;
import pl.ibcgames.smvotifier.config.Config;
import pl.ibcgames.smvotifier.config.ConfigLoader;

@Plugin(
        id = "smvotifier",
        name = "SMVotifier",
        description = "Plugin dodajacy mozliwosc odbierania nagrod za glosowanie",
        authors = {"PanSzelescik", "kisi69", "kaziu687", "DEv0on"},
        version = "1.1.0"
)
public class SMVotifier {

    @Inject
    public Logger logger;
    @Inject
    PluginContainer pluginContainer;
    public Config config;

    private static SMVotifier instance;

    public SMVotifier() {
        instance = this;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        ConfigLoader<Config> configLoader = new ConfigLoader<>(Config.class);
        try {
            config = configLoader.loadConfig("SM-Votifier.conf");
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Wystapil blad podczas ladowania configu!");
            e.printStackTrace();
        }
        String token = config.identifier;
        if (token == null || token.equalsIgnoreCase("tutaj_wpisz_identyfikator")) {
            logger.warn("Brak identyfikatora serwera w konfiguracji SM-Votifier");
            logger.warn("Wiecej informacji znajdziesz pod adresem:");
            logger.warn("https://serwery-minecraft.pl/konfiguracja-pluginu");
        }

        new VoteCommand(Text.EMPTY, "", new String[] {"sm-glosuj"}).registerCommand(this.pluginContainer);
        new TestCommand(Text.EMPTY, "smvotifier.admin", new String[] {"sm-test"}).registerCommand(this.pluginContainer);
        new RewardCommand(Text.EMPTY, "smvotifier.nagroda", new String[] {"sm-nagroda"}).registerCommand(this.pluginContainer);

    }

    public static SMVotifier getInstance() {
        return instance;
    }
}
