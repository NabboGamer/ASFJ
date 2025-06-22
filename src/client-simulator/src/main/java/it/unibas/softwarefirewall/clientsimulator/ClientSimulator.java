package it.unibas.softwarefirewall.clientsimulator;

import it.unibas.softwarefirewall.firewallapi.ISimulationStatusListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientSimulator {

    private final List<ISimulationStatusListener> listeners = new ArrayList<>();
    private volatile boolean running = false;
    private Integer clientsCount;
    private Integer maxPackets;
    private Long intervalMs;
    
    public ClientSimulator(){
        Properties props = new Properties();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("client-simulator.properties")) {
            props.load(in);
            String clientsCountProperty = props.getProperty("clientsimulator.clientsCount");
            this.clientsCount = clientsCountProperty != null ? Integer.valueOf(clientsCountProperty) : 10;
            
            String maxPacketsProperty = props.getProperty("clientsimulator.maxPackets");
            this.maxPackets = maxPacketsProperty != null ? Integer.valueOf(maxPacketsProperty) : 2;
            
            String intervalMsProperty = props.getProperty("clientsimulator.intervalMs");
            this.intervalMs = intervalMsProperty != null ? Long.valueOf(intervalMsProperty) : 1000L;
            
        } catch (IOException | NumberFormatException e) {
            log.error("Could not load ClientSimulator configuration: {}", e);
            this.clientsCount = 10;
            this.maxPackets = 2;
            this.intervalMs = 1000L;
        }
    }

    public void addSimulationStatusListener(ISimulationStatusListener listener) {
        listeners.add(listener);
    }

    public boolean isRunning() {
        return this.running;
    }

    public void startSimulation() {
        running = true;
        listeners.forEach(ISimulationStatusListener::onSimulationStarted);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(clientsCount);
        CountDownLatch latch = new CountDownLatch(clientsCount);

        for (int i = 0; i < clientsCount; i++) {
            scheduler.scheduleAtFixedRate(
                new SimulatedClientTask("Client-" + i, maxPackets, scheduler, latch),
                0,
                intervalMs,
                TimeUnit.MILLISECONDS
            );
        }

        // When all clients have finished:
        new Thread(() -> {
            try {
                latch.await(); // Wait until all clients are finished
                scheduler.shutdown(); // Stop the executor
                running = false; // Simulation status updated
                listeners.forEach(ISimulationStatusListener::onSimulationFinished); // End notification
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}

