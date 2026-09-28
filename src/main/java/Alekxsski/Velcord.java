package Alekxsski;

import Alekxsski.ProxyManager.BootStrapper;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import Alekxsski.Database.DatabaseManager;
import Alekxsski.ProxyManager.StartingServices.CommandManagerVelcord;
import Alekxsski.ProxyManager.JdaHook.JdaHook;
import Alekxsski.ProxyManager.StartingServices.TaskManager;
import org.slf4j.Logger;

@Plugin(id = "velcord", name = "velcord", version = "1.0-alpha", dependencies =
@Dependency(id = "luckperms")
)
public class Velcord {

    private final BootStrapper bootStrapper;


    @Inject
    public Velcord(BootStrapper bootStrapper){

        this.bootStrapper = bootStrapper;

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws InterruptedException {

        bootStrapper.start(this);

    }


    @Subscribe
    public void proxyShutdownEvent(ProxyShutdownEvent event){

        bootStrapper.shutDown();

    }

}
