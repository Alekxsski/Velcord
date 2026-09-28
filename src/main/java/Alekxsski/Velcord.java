package Alekxsski;

import Alekxsski.ProxyManager.BootStrapper;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ListenerBoundEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(id = "velcord", name = "velcord", version = "1.0-alpha", dependencies = {@Dependency(id = "luckperms",optional = true)})
public class Velcord {

    private final BootStrapper bootStrapper;
    private final Logger logger;


    @Inject
    public Velcord(BootStrapper bootStrapper, Logger logger){

        this.bootStrapper = bootStrapper;
        this.logger = logger;

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        logger.info("Velcord started");

    }

    @Subscribe
    public void onProxyFinalization(ListenerBoundEvent event) throws InterruptedException {

        bootStrapper.Hook(this);

    }


    @Subscribe
    public void proxyShutdownEvent(ProxyShutdownEvent event){

        bootStrapper.shutDown();

    }

}
