package it.unibas.softwarefirewall.clientsimulator;

import com.google.inject.Inject;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
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
    private final IFirewallFacade firewall;
    private volatile boolean running = false;
    private Integer clientsCount;
    private Integer maxPackets;
    private Long intervalMs;
    
    @Inject
    public ClientSimulator(IFirewallFacade firewall){
        this.firewall = firewall;
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
        this.listeners.add(listener);
    }

    public boolean isRunning() {
        return this.running;
    }

    public void startSimulation() {
        this.running = true;
        this.listeners.forEach(ISimulationStatusListener::onSimulationStarted);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(this.clientsCount);
        CountDownLatch latch = new CountDownLatch(this.clientsCount);

        for (int i = 0; i < this.clientsCount; i++) {
            scheduler.scheduleAtFixedRate(
                new SimulatedClientTask("Client-" + i, this.maxPackets, scheduler, latch, this.firewall),
                0,
                this.intervalMs,
                TimeUnit.MILLISECONDS
            );
        }

        // When all clients have finished:
        new Thread(() -> {
            try {
                latch.await(); // Wait until all clients are finished
                scheduler.shutdown(); // Stop the executor
                this.running = false; // Simulation status updated
                this.listeners.forEach(ISimulationStatusListener::onSimulationFinished); // End notification
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}

