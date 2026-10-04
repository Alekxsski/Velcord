package Alekxsski.Utils.DiscordAuth.Utils;

import com.google.inject.Singleton;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DiscordAuthHashMap {

    @Getter
    private final ConcurrentHashMap<UUID, DiscordAuthData> discordAuthDataHashMap;

    @Getter
    private final ConcurrentHashMap<String, UUID> discordAuthCodeHashMap;

    DiscordAuthHashMap(){

        this.discordAuthDataHashMap = new ConcurrentHashMap<>();
        this.discordAuthCodeHashMap = new ConcurrentHashMap<>();

    }

}
