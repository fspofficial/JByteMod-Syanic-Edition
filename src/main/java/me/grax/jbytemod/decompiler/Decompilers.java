package me.grax.jbytemod.decompiler;

import de.xbrowniecodez.jbytemod.utils.Utils;
import lombok.Getter;

@Getter
public enum Decompilers {
    CFR("CFR", Utils.readPropertiesFile().getProperty("cfr")), PROCYON("Procyon", Utils.readPropertiesFile().getProperty("procyon")), FERNFLOWER("Fernflower", ""), KOFFEE("Koffee", "1.0");
    private final String version;
    private final String name;

    Decompilers(String name, String version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String toString() {
        return name + " " + version;
    }
}
