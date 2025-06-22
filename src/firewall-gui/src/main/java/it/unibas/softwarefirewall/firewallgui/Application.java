package it.unibas.softwarefirewall.firewallgui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.jthemedetecor.OsThemeDetector;
import it.unibas.softwarefirewall.clientsimulator.ClientSimulatorModule;
import it.unibas.softwarefirewall.firewallcore.FirewallCoreModule;
import it.unibas.softwarefirewall.firewallgui.view.MainPanel;
import it.unibas.softwarefirewall.firewallgui.view.MainView;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    
    private static Application singleton = new Application();
   
    public static Application getInstance() {
        return singleton;
    }
    
    public static void main(String[] args) {
        try {
            final OsThemeDetector detector = OsThemeDetector.getDetector();
            final boolean isDarkThemeUsed = detector.isDark();
            if (isDarkThemeUsed) {
                FlatDarculaLaf.setup();
            } else {
                FlatIntelliJLaf.setup();
            }
            Font jetbrainsMonoFont = new Font("JetBrains Mono", Font.PLAIN, 14);
            UIManager.put("defaultFont", jetbrainsMonoFont);
        } catch (Exception ex) {
            log.error("Unable to set the custom look and feel");
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Application.getInstance().init();
            }
        });
    }
    
    private Injector injector;
    
    private Application() {
        Properties props = new Properties();
        Stage stage;
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("firewall-gui.properties")) {
            props.load(in);
            String stageProperty = props.getProperty("firewallgui.application.guice.stage");
            if (stageProperty != null && stageProperty.equals("tool")){
                stage = Stage.TOOL;
            } else if(stageProperty != null && stageProperty.equals("development")){
                stage = Stage.DEVELOPMENT;
            } else if(stageProperty != null && stageProperty.equals("production")) {
                stage = Stage.PRODUCTION;
            } else {
                throw new IOException("Invalid value for the property 'firewallgui.application.guice.stage'. Please use one of the following: tool, development, or production.");
            }
        } catch (IOException | NumberFormatException ex) {
            log.error("Could not load Guice stage configuration:", ex);
            stage = Stage.DEVELOPMENT;
        }
        
        this.injector = Guice.createInjector(stage, new FirewallGUIModule(), new FirewallCoreModule(), new ClientSimulatorModule());
    }

    private void init() {
        MainView mainView = injector.getInstance(MainView.class);
        MainPanel mainPanel = injector.getInstance(MainPanel.class);
        mainPanel.init();
        mainView.init();
    }
}
