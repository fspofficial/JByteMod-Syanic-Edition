package me.grax.jbytemod.utils.gui;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import com.github.weisj.darklaf.theme.SolarizedLightTheme;
import me.grax.jbytemod.JByteMod;
import me.grax.jbytemod.res.Option;

public class LookUtils {

    public static void setLAF() {
        if(JByteMod.ops.get("use_dark_theme").getBoolean())
            LafManager.setTheme(new OneDarkTheme());
        else
            LafManager.setTheme(new IntelliJTheme());
        try {
            JByteMod.LOGGER.log("Setting Look and Feel");
            LafManager.install();
        } catch (Throwable t) {
            JByteMod.LOGGER.err("Failed to set Look and Feel");
        }
    }
}
