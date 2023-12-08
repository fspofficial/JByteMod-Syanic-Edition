package me.grax.jbytemod.utils.gui;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import me.grax.jbytemod.JByteMod;

public class LookUtils {
    public static void setLAF() {
        try {
            JByteMod.LOGGER.log("Setting default Look and Feel");
            LafManager.setTheme(new OneDarkTheme());
            LafManager.install();

        } catch (Throwable t) {
            t.printStackTrace();
            JByteMod.LOGGER.err("Failed to set Look and Feel");
        }
    }
}
