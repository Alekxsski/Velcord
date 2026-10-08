package Alekxsski.Utils.PlayerBased;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.Map;

//Class provides two methods to send or play sounds to player using kyori api
public class KyoriAdevntureApi {

    private static final Map<String,SoundsHelper> SoundsMap = new HashMap<>();

    @Inject
    public KyoriAdevntureApi(){

        setUp();

    }

    private void setUp(){

        SoundsMap.put("verify",new SoundsHelper(Key.key("ui.toast.in"), Sound.Source.UI));

        SoundsMap.put("expire",new SoundsHelper(Key.key("button.click_off"), Sound.Source.BLOCK));

        SoundsMap.put("success",new SoundsHelper(Key.key("toast.challenge_complete"), Sound.Source.UI));

        SoundsMap.put("fail",new SoundsHelper(Key.key("block.note_block.bass"), Sound.Source.BLOCK));

        SoundsMap.put("critical",new SoundsHelper(Key.key("entity.ghast.scream"), Sound.Source.HOSTILE));

        SoundsMap.put("disconnect",new SoundsHelper(Key.key("block.beacon.deactivate"), Sound.Source.BLOCK));

        SoundsMap.put("deny",new SoundsHelper(Key.key("entity.villager.no"), Sound.Source.NEUTRAL));

    }

    public static Component MinimessagePlain(String message) {

        MiniMessage mm = MiniMessage.miniMessage();

        return mm.deserialize(message);}


    public static void PlaySoundVerication(Player player, String state){

        SoundsHelper soundsHelper = SoundsMap.get(state);

        player.playSound(Sound.sound(soundsHelper.key(), soundsHelper.source(),1,1), Sound.Emitter.self());

    }

}
