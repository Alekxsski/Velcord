package Alekxsski.Utils.PlayerBased;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Minimessage {

    public static Component MinimessagePlain(String message) {

        MiniMessage mm = MiniMessage.miniMessage();

        return mm.deserialize(message);}

}
