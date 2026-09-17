package com.yamakotaro.hanabifestival;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** ワンド/コマンドでプレイヤーごとに一時保持する2点選択（永続化はしない）。 */
public class SelectionManager {

    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();

    public void setPos1(UUID uuid, Location location) {
        pos1.put(uuid, location.clone());
    }

    public void setPos2(UUID uuid, Location location) {
        pos2.put(uuid, location.clone());
    }

    public Location getPos1(UUID uuid) {
        return pos1.get(uuid);
    }

    public Location getPos2(UUID uuid) {
        return pos2.get(uuid);
    }
}
