package com.github.gru;

/**
 * User: mdehaan
 * Date: 7/30/13
 */
public class GruVersion implements Comparable<GruVersion> {
    public static final GruVersion GRU_VERSION = new GruVersion("0.0.6");

    private String version;

    public GruVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(GruVersion o) {
        String[] vals1 = this.version.split("\\.");
        String[] vals2 = o.version.split("\\.");
        int i=0;
        while(i<vals1.length&&i<vals2.length&&vals1[i].equals(vals2[i])) {
            i++;
        }

        if (i<vals1.length&&i<vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return diff<0?-1:diff==0?0:1;
        }

        return vals1.length<vals2.length?-1:vals1.length==vals2.length?0:1;
    }

    @Override
    public String toString() {
        return version;
    }
}
