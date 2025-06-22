package it.unibas.softwarefirewall.clientsimulator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulatedClientTask implements Runnable {

    private final String clientName;
    private final int maxPackets;
    private final AtomicInteger packetsSent = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler;
    private final CountDownLatch latch;

    public SimulatedClientTask(String clientName, int maxPackets,
                               ScheduledExecutorService scheduler, CountDownLatch latch) {
        this.clientName = clientName;
        this.maxPackets = maxPackets;
        this.scheduler = scheduler;
        this.latch = latch;
    }

    @Override
    public void run() {
        if (packetsSent.incrementAndGet() > maxPackets) {
            latch.countDown(); // Notify that it has finished
            return;
        }
        log.info("🚚 " + clientName + " send packet #" + packetsSent.get());
    }
}
