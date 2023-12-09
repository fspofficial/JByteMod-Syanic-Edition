package de.xbrowniecodez.jbytemod.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    private static Properties cachedProperties;

    public static Properties readPropertiesFile() {
        if (cachedProperties == null) {
            try (InputStream stream = Utils.class.getResourceAsStream("/resources/jbytemod.properties")) {
                Properties prop = new Properties();
                prop.load(stream);
                cachedProperties = prop;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        return cachedProperties;
    }
}
