package Alekxsski.Utils.PlayerBased;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public enum SoundsEnum {

    VERIFY(Key.key("ui.toast.in"), Sound.Source.UI),
    EXPIRE(Key.key("block.lava.extinguish"), Sound.Source.BLOCK),
    SUCCESS(Key.key("entity.player.levelup"), Sound.Source.PLAYER),
    FAIL(Key.key("block.note_block.bass"), Sound.Source.BLOCK),
    CRITICAL(Key.key("entity.dragon_fireball.explode"), Sound.Source.HOSTILE),
    DISCONNECT(Key.key("block.beacon.deactivate"), Sound.Source.BLOCK),
    DENY(Key.key("entity.villager.no"), Sound.Source.NEUTRAL);

    @Getter
    private final Sound.Source source;
    @Getter
    private final Key key;

    SoundsEnum(Key key, Sound.Source source) {
        this.key = key;
        this.source = source;
    }

}
