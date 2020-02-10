package de.ellpeck.wywa;

import java.util.ArrayDeque;
import java.util.Queue;

public class WorkerThread extends Thread {

    private final Queue<AbstractChunkData> queue = new ArrayDeque<>();

    @Override
    public void run() {
        while (true) {
            if (this.queue.isEmpty())
                continue;

            AbstractChunkData next;
            synchronized (this.queue) {
                next = this.queue.remove();
            }
            try {
                next.tickEverything();
            } catch (Exception e) {
                WYWA.LOGGER.error("There was an error in the worker thread. If you get this a lot, consider disabling multi threading in the config", e);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void enqueue(AbstractChunkData data) {
        synchronized (this.queue) {
            this.queue.add(data);
        }
    }
}
