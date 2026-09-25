package Alekxsski.Utils.DiscordAuth.Utils;

import lombok.Getter;

import java.time.Instant;


public class DiscordAuthData {

    @Getter
    private final String auth_url;

    @Getter
    private final long time;

    @Getter
    private final String code;

    public DiscordAuthData(String auth_url,String code){

        this.auth_url = auth_url;
        this.time  = Instant.now().getEpochSecond() + 300L;
        this.code = code;


    }

}
