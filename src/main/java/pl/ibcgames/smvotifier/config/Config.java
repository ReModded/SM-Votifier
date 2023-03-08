package pl.ibcgames.smvotifier.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Config {
    @Setting(value = "identyfikator", comment = "Jak poprawnie skonfigurowac plugin przeczytasz pod adresem:\nhttps://serwery-minecraft.pl/konfiguracja-pluginu")
    public String identifier = "tutaj_wpisz_identyfikator";
    @Setting(value = "wymagaj_uprawnien", comment = "Jesli chcesz ograniczyc dostep do komendy /sm-nagroda\n  false - komenda bedzie dostepna dla kazdego\n  true - do jej uzycia wymagane bedzie uprawnienie smvotifier.nagroda")
    public boolean require_permissions = false;

    @Setting(value = "komendy", comment = "Lista komend, ktora zostanie wyslana na konsole po zweryfikowaniu glosu\n{GRACZ} zostanie zastapione nazwa gracza korzystajacego z komendy")
    public String[] commands = {
      "give {GRACZ} stone 10",
      "say {GRACZ} zaglosowal na serwery-minecraft.pl!",
      "say Uzyj komendy /sm-glosuj zeby zaglosowac na serwer"
    };
}
