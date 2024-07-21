package me.grax.jbytemod.utils.gui;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import com.github.weisj.darklaf.theme.OneDarkTheme;
import de.xbrowniecodez.jbytemod.Main;

public class LookUtils {

    public static void setTheme() {
        Main.getInstance().getLogger().log("Setting Theme");
        LafManager.install(Main.getInstance().getJByteMod().getOptions().get("use_dark_theme").getBoolean() ? new OneDarkTheme() : new IntelliJTheme());
    }

    public static void changeTheme() {
        if(Main.getInstance().getJByteMod().getOptions().get("use_dark_theme").getBoolean()) {
            LafManager.setTheme(new OneDarkTheme());
        } else {
            LafManager.setTheme(new IntelliJTheme());
        }
        LafManager.install();
        Main.getInstance().getJByteMod().getDecompilerPanel().setTheme();
    }
}
