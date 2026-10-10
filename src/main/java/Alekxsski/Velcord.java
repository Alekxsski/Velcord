package Alekxsski;

import Alekxsski.ProxyManager.BootStrapper;
import Alekxsski.ProxyManager.LibbyManager.LibbyManager;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Plugin(id = "velcord", name = "velcord", version = "1.0", dependencies = {@Dependency(id = "luckperms",optional = true)})
public class Velcord {

    private BootStrapper bootStrapper;
    private final Logger logger;
    private final LibbyManager libbyManager;
    private final Injector injector;


    @Inject
    public Velcord(LibbyManager libbyManager, Logger logger, Injector injector){

        this.logger = logger;
        this.libbyManager = libbyManager;
        this.injector = injector;

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        logger.info("Velcord started");

        libbyManager.loadLibby(this);

        bootStrapper = injector.getInstance(BootStrapper.class);

        bootStrapper.Hook(this);

    }

    @Subscribe
    public void proxyShutdownEvent(ProxyShutdownEvent event){

        bootStrapper.shutDown();

    }

}
