package io.multipaper.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TickPools {

    private static final int CORES = Math.max(2, Runtime.getRuntime().availableProcessors());
    private static final int N_THREADS = Math.min(CORES, 6); // cap for safety while testing

    private static final ThreadFactory FACTORY = new ThreadFactory() {
        private final AtomicInteger idx = new AtomicInteger(1);
        @Override public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "mp-entity-tick-" + idx.getAndIncrement());
            t.setDaemon(true);
            t.setUncaughtExceptionHandler((th, ex) -> ex.printStackTrace());
            return t;
        }
    };

    public static final ExecutorService ENTITY_POOL =
            new ThreadPoolExecutor(
                    N_THREADS, N_THREADS,
                    30L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    FACTORY,
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );


    public static volatile boolean ENABLE_PARALLEL_DISTANT_MOBS = true;

    private TickPools() {}
}
