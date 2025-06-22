package it.unibas.softwarefirewall.clientsimulator;

import it.unibas.softwarefirewall.firewallapi.EProtocol;
import static it.unibas.softwarefirewall.firewallapi.EProtocol.*;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallcore.PseudoHeader;
import it.unibas.softwarefirewall.firewallcore.PseudoPacket;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

//This class is hand-created and not managed by Guice so I can't inject anything
@Slf4j
public class SimulatedClientTask implements Runnable {

    private final AtomicInteger packetsSent  = new AtomicInteger(0);
    private final Random random              = new Random();
    private final String[]  sourceIPs        = {"193.204.19.174", "193.204.19.178", "193.204.19.14", "192.168.0.140", "192.168.0.140"};
    private final String[]  destinationIPs   = {"192.168.0.140", "192.168.0.140", "192.168.0.140", "8.8.8.8", "9.9.9.9"};
    private final Integer[] sourcePorts      = {1000, 8080, 9090, 1024, 5000};
    private final Integer[] destinationPorts = {80, 80, 5432, 53, 53};
    private final EProtocol[] protocols      = {TCP, TCP, TCP, UDP,UDP};
    private final String clientName;
    private final Integer maxPackets;
    private final ScheduledExecutorService scheduler;
    private final CountDownLatch latch;
    private final IFirewallFacade firewall;

    public SimulatedClientTask(String clientName, Integer maxPackets,
                               ScheduledExecutorService scheduler, CountDownLatch latch,
                               IFirewallFacade firewall) {
        this.clientName = clientName;
        this.maxPackets = maxPackets;
        this.scheduler = scheduler;
        this.latch = latch;
        this.firewall = firewall;
    }

    @Override
    public void run() {
        if (packetsSent.incrementAndGet() > maxPackets) {
            latch.countDown(); // Notify that it has finished
            return;
        }
        IPacket randomPacket = this.generateRandomPacket(clientName);
        firewall.activeRuleSetProcessPacket(randomPacket);
//        log.debug("🚚 " + clientName + " send packet #" + this.packetsSent.get());
    }
    
    private IPacket generateRandomPacket(String clientName) {
        int randomIndex = random.nextInt(sourceIPs.length);
        IHeader randomHeader = new PseudoHeader(sourceIPs[randomIndex], destinationIPs[randomIndex], 
                                                sourcePorts[randomIndex], destinationPorts[randomIndex], 
                                                protocols[randomIndex]);
        IPacket randomPacket = new PseudoPacket(randomHeader, "Random packet #" + this.packetsSent.get() + " sent by " + clientName);
        return randomPacket;
    }
}
