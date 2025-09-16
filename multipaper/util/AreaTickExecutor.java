package io.multipaper.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class AreaTickExecutor {

    private AreaTickExecutor() {}

    public static void tickAreaParallel(ServerLevel level, GridMap area, int workers, double safeDistBlocks) {
        if (workers <= 1 || area == null || area.isEmpty() || !TickPools.ENABLE_PARALLEL_DISTANT_MOBS) return;

        final double safeDistSqr = safeDistBlocks * safeDistBlocks;
        final List<Entity> candidates = new ArrayList<>();

        for (Entity e : level.getAllEntities()) {
            if (isParallelSafe(e, level, safeDistSqr, area)) {
                candidates.add(e);
            }
        }
        if (candidates.isEmpty()) return;

        Map<Long, List<Entity>> buckets = RegionBuckets.byChunk(candidates);
        List<GridMap> tiles = GridSplitter.split(area.x0, area.z0, area.x1, area.z1, workers);

        CountDownLatch latch = new CountDownLatch(tiles.size());
        for (GridMap tile : tiles) {
            TickPools.ENTITY_POOL.submit(() -> {
                try {
                    for (Map.Entry<Long, List<Entity>> entry : buckets.entrySet()) {
                        long key = entry.getKey();
                        int cx = (int) (key >> 32);
                        int cz = (int) (key & 0xffffffffL);
                        if (!tile.containsChunk(cx, cz)) continue;

                        for (Entity ent : entry.getValue()) {
                            try {
                                level.guardEntityTick(level::tickNonPassenger, ent);
                            } catch (Throwable ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        try { latch.await(); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private static boolean isParallelSafe(Entity e, ServerLevel level, double safeDistSqr, GridMap area) {
        if (e.isRemoved()) return false;
        if (!(e instanceof LivingEntity)) return false;

        if (e instanceof ServerPlayer) return false;
        if (e instanceof Projectile) return false;
        if (e instanceof AbstractMinecart) return false;
        if (e instanceof ItemEntity) return false;

        for (ServerPlayer p : level.players()) {
            if (p.distanceToSqr(e) <= safeDistSqr) { return false; }
        }
        return true;
    }
}
