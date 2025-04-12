package gui;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.Properties;
import javax.swing.*;

public class WindowStateManager {

    private static final String CONFIG_FILE =
            System.getProperty("user.home") + File.separator + ".robots_window_state.properties";

    public static void saveWindowStates(JInternalFrame[] frames) {
        Properties properties = new Properties();

        for (JInternalFrame frame : frames) {
            String prefix = frame.getClass().getSimpleName();

            properties.setProperty(prefix + ".x", String.valueOf(frame.getX()));
            properties.setProperty(prefix + ".y", String.valueOf(frame.getY()));
            properties.setProperty(prefix + ".width", String.valueOf(frame.getWidth()));
            properties.setProperty(prefix + ".height", String.valueOf(frame.getHeight()));
            properties.setProperty(prefix + ".isIcon", String.valueOf(frame.isIcon()));
            properties.setProperty(prefix + ".isMaximum", String.valueOf(frame.isMaximum()));
        }

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Robots Window States");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadWindowStates(JInternalFrame[] frames) {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return;

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (JInternalFrame frame : frames) {
            String prefix = frame.getClass().getSimpleName();

            int x = Integer.parseInt(properties.getProperty(prefix + ".x", "10"));
            int y = Integer.parseInt(properties.getProperty(prefix + ".y", "10"));
            int width = Integer.parseInt(properties.getProperty(prefix + ".width", "400"));
            int height = Integer.parseInt(properties.getProperty(prefix + ".height", "400"));
            boolean isIcon = Boolean.parseBoolean(properties.getProperty(prefix + ".isIcon", "false"));
            boolean isMaximum = Boolean.parseBoolean(properties.getProperty(prefix + ".isMaximum", "false"));

            frame.setLocation(x, y);
            frame.setSize(width, height);

            try {
                frame.setIcon(isIcon);
                frame.setMaximum(isMaximum);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }
}
