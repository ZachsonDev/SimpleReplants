package de.jeff_media.replant.acf.commands.lib.expiringmap.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory
implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String nameFormat;

    public NamedThreadFactory(String string) {
        this.nameFormat = string;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, String.format(this.nameFormat, this.threadNumber.getAndIncrement()));
        thread.setDaemon(true);
        return thread;
    }
}

