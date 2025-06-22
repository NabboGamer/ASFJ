package it.unibas.softwarefirewall.firewallapi;

public interface ISimulationStatusListener {
    void onSimulationStarted();
    void onSimulationFinished();
}
