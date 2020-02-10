package de.ellpeck.wywa;

import java.util.ArrayDeque;
import java.util.Queue;

public class WorkerThread extends Thread {

    private final Queue<ChunkData> queue = new ArrayDeque<>();

    @Override
    public void run() {
        while (true) {
            if (this.queue.isEmpty())
                continue;

            ChunkData next;
            synchronized (this.queue) {
                next = this.queue.remove();
            }
            try {
                next.tickEverything();
            } catch (Exception e) {
                WYWA.LOGGER.error("There was an error in the worker thread", e);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void enqueue(ChunkData data) {
        synchronized (this.queue) {
            this.queue.add(data);
        }
    }
}
