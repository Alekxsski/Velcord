package Alekxsski.ProxyManager.JdaHook;

import Alekxsski.ProxyManager.JdaHook.JdaListener.JdaListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.Utils.Config;
import Alekxsski.ProxyManager.JdaHook.Commands.DiscordVerify;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;


@Singleton
//Class that initializes jda
public class JdaHook {

    private JDA jda;

    private final Config config;

    private final DiscordVerify discordVerify;

    private final JdaListener jdaListener;


    @Inject
    public JdaHook(Config config, DiscordVerify discordVerify, JdaListener jdaListener){

        this.discordVerify = discordVerify;

        this.config = config;

        this.jdaListener = jdaListener;

    }

    public void setUp() {

        jda = JDABuilder.createDefault(config.getBotToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MESSAGES)
                .disableCache(CacheFlag.ACTIVITY,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ONLINE_STATUS,
                        CacheFlag.VOICE_STATE
                )
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(discordVerify, jdaListener).build();

    }

    public void stop(){

        jda.shutdown();

    }


}




