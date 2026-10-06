package Alekxsski.ProxyManager.StartingServices;

import Alekxsski.ProxyManager.VelcordEventListener.VelcordListener;
import Alekxsski.Velcord;
import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;


public class EventListenerManager {

    private final ProxyServer server;
    private final VelcordListener velcordListener;

    @Inject
    public EventListenerManager(ProxyServer server, VelcordListener velcordListener){

        this.server = server;
        this.velcordListener = velcordListener;

    }

    public void registerListener(Velcord plugin){

        server.getEventManager().register(plugin,velcordListener);

    }

}

