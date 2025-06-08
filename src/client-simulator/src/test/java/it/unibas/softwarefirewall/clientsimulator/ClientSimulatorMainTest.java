package it.unibas.softwarefirewall.clientsimulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClientSimulatorMainTest {

    @Test
    public void appHasAGreeting() {
        ClientSimulatorMain classUnderTest = new ClientSimulatorMain();
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
    }
}
