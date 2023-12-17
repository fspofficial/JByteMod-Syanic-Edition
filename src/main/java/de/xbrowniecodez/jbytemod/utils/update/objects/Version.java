package de.xbrowniecodez.jbytemod.utils.update.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Version {
    private final int majorVersion;
    private final int minorVersion;

    public Version(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public Version(String version) {
        String[] split = version.split("\\.");
        this.majorVersion = Integer.parseInt(split[0]);
        this.minorVersion = Integer.parseInt(split[1]);
    }

    /**
     * Compares this version to another version.
     *
     * @param version The version to compare to.
     * @return true if the version is greater than the other version, false otherwise.
     */
    public boolean isNewer(Version version) {
        return (majorVersion > version.majorVersion)
                || (minorVersion > version.minorVersion);
    }

    public String toString() {
        return String.format("%d.%d", majorVersion, minorVersion);
    }
}
