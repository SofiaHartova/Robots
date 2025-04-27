package gui;

import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.swing.*;

public class GameWindow extends JInternalFrame implements StatefulWindow {

    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        add(m_visualizer);
        pack();
    }

    @Override
    public String getWindowId() {
        return "GameWindow";
    }

    @Override
    public void saveState(Properties props) {
        props.setProperty(getWindowId() + ".x", String.valueOf(getX()));
        props.setProperty(getWindowId() + ".y", String.valueOf(getY()));
        props.setProperty(getWindowId() + ".width", String.valueOf(getWidth()));
        props.setProperty(getWindowId() + ".height", String.valueOf(getHeight()));
        props.setProperty(getWindowId() + ".isIcon", String.valueOf(isIcon()));
        props.setProperty(getWindowId() + ".isMaximum", String.valueOf(isMaximum()));
        
        // Например, потом тут можно добавить сохранение состояния визуализатора (позиция робота)
    }

    @Override
    public void loadState(Properties props) {
        int x = Integer.parseInt(props.getProperty(getWindowId() + ".x", "50"));
        int y = Integer.parseInt(props.getProperty(getWindowId() + ".y", "50"));
        int width = Integer.parseInt(props.getProperty(getWindowId() + ".width", "400"));
        int height = Integer.parseInt(props.getProperty(getWindowId() + ".height", "400"));
        boolean isIcon = Boolean.parseBoolean(props.getProperty(getWindowId() + ".isIcon", "false"));
        boolean isMaximum = Boolean.parseBoolean(props.getProperty(getWindowId() + ".isMaximum", "false"));

        setBounds(x, y, width, height);
        try {
            setIcon(isIcon);
            setMaximum(isMaximum);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }
}
