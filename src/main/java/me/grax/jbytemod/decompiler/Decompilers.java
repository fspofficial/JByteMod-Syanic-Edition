package me.grax.jbytemod.decompiler;

public enum Decompilers {
    CFR("CFR", "0.152"), PROCYON("Procyon", "0.6.0"), FERNFLOWER("Fernflower", ""), KRAKATAU("Krakatau", "502");
    private String version;
    private String name;

    Decompilers(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + version;
    }
}
