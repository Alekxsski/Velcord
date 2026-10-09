package Alekxsski.Utils.PlayerBased;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

//Class provides two methods to send or play sounds to player using kyori api
public class KyoriAdevntureApi {

    @Inject
    public KyoriAdevntureApi(){

    }


    public static Component MinimessagePlain(String message) {

        MiniMessage mm = MiniMessage.miniMessage();

        return mm.deserialize(message);}


    public void PlaySoundVerication(Player player,SoundsEnum soundsEnum){

        Sound sound = Sound.sound(
                soundsEnum.getKey(),
                soundsEnum.getSource(),
                1.0f,
                1.0f
        );

        player.playSound(sound, Sound.Emitter.self());

    }

}
