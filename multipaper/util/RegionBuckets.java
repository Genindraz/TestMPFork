package io.multipaper.util;

import net.minecraft.world.entity.Entity;

import java.util.*;

public final class RegionBuckets {
    private RegionBuckets() {}


    public static Map<Long, List<Entity>> byChunk(List<Entity> entities) {
        Map<Long, List<Entity>> buckets = new HashMap<>();

        for (Entity e : entities) {
            int cx = e.getBlockX() >> 4; // chunkX
            int cz = e.getBlockZ() >> 4; // chunkZ
            long key = pack(cx, cz);

            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(e);
        }
        return buckets;
    }


    public static long pack(int cx, int cz) {
        return ((long) cx << 32) ^ (cz & 0xffffffffL);
    }


    public static int unpackX(long key) {
        return (int) (key >> 32);
    }


    public static int unpackZ(long key) {
        return (int) key;
    }
}
