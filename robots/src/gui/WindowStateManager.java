package gui;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class WindowStateManager {

    private static final String CONFIG_FILE =
        System.getProperty("user.home") + File.separator + ".robots_window_state.properties";

    public static void saveStates(Map<String, StatefulWindow> windows) {
        Properties props = new Properties();

        for (StatefulWindow window : windows.values()) {
            window.saveState(props);
        }

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Robots Window States");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadStates(Map<String, StatefulWindow> windows) {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return;

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (StatefulWindow window : windows.values()) {
            window.loadState(props);
        }
    }
}
