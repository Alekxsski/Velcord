package Alekxsski.Utils.DiscordAuth.Utils;

import com.google.inject.Singleton;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class DiscordAuthHashMap {

    @Getter
    private final HashMap <UUID, DiscordAuthData> discordAuthDataHashMap;

    DiscordAuthHashMap(){

        this.discordAuthDataHashMap = new HashMap<>();

    }

}
