package gui;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import javax.swing.*;


public class RobotCoordinatesWindow extends JInternalFrame implements Observer, StatefulWindow {
    private final JLabel coordinatesLabel = new JLabel("Координаты робота: ");

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        model.addObserver(this);
        setLayout(new BorderLayout());
        add(coordinatesLabel, BorderLayout.CENTER);
        setSize(250, 100);
        setLocation(500, 10);
        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        RobotModel model = (RobotModel) o;
        String text = String.format("X: %.2f, Y: %.2f", model.getRobotX(), model.getRobotY());
        coordinatesLabel.setText(text);
    }

    @Override
    public String getWindowId() {
        return "RobotCoordinatesWindow";
    }

    @Override
    public void saveState(Properties props) {
        props.setProperty(getWindowId() + ".x", String.valueOf(getX()));
        props.setProperty(getWindowId() + ".y", String.valueOf(getY()));
        props.setProperty(getWindowId() + ".width", String.valueOf(getWidth()));
        props.setProperty(getWindowId() + ".height", String.valueOf(getHeight()));
    }

    @Override
    public void loadState(Properties props) {
        int x = Integer.parseInt(props.getProperty(getWindowId() + ".x", "500"));
        int y = Integer.parseInt(props.getProperty(getWindowId() + ".y", "10"));
        int width = Integer.parseInt(props.getProperty(getWindowId() + ".width", "250"));
        int height = Integer.parseInt(props.getProperty(getWindowId() + ".height", "100"));
        setBounds(x, y, width, height);
    }
}
