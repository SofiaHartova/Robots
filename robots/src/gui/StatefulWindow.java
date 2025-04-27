package gui;

import java.util.Properties;

public interface StatefulWindow {
    String getWindowId(); // уникальный идентификатор окна
    void saveState(Properties props);
    void loadState(Properties props);
}
