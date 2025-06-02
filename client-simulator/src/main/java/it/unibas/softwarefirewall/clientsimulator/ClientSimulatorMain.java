package it.unibas.softwarefirewall.clientsimulator;

public class ClientSimulatorMain {
    
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(new ClientSimulatorMain().getGreeting());
    }
}
