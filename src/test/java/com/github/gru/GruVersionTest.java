package com.github.gru;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: mdehaan
 * Date: 7/30/13
 */
public class GruVersionTest {
    @Test
    public void testHotfixCompare() {
        GruVersion oldVersion = new GruVersion("0.0.1");
        GruVersion newVersion = new GruVersion("0.0.2");
        Assert.assertTrue("Old version should be less than new version", oldVersion.compareTo(newVersion) < 0);
        Assert.assertTrue("New version should be greater than old version", newVersion.compareTo(oldVersion) > 0);
    }

    @Test
    public void testMinorCompare() {
        GruVersion oldVersion = new GruVersion("0.1.0");
        GruVersion newVersion = new GruVersion("0.2.0");
        Assert.assertTrue("Old version should be less than new version", oldVersion.compareTo(newVersion) < 0);
        Assert.assertTrue("New version should be greater than old version", newVersion.compareTo(oldVersion) > 0);
    }

    @Test
    public void testMajorCompare() {
        GruVersion oldVersion = new GruVersion("1.0.0");
        GruVersion newVersion = new GruVersion("2.0.0");
        Assert.assertTrue("Old version should be less than new version", oldVersion.compareTo(newVersion) < 0);
        Assert.assertTrue("New version should be greater than old version", newVersion.compareTo(oldVersion) > 0);
    }

    @Test
    public void testMixedNumbers() {
        GruVersion oldVersion = new GruVersion("1.9.9");
        GruVersion newVersion = new GruVersion("2.0.0");
        Assert.assertTrue("Old version should be less than new version", oldVersion.compareTo(newVersion) < 0);
        Assert.assertTrue("New version should be greater than old version", newVersion.compareTo(oldVersion) > 0);
    }

    @Test
    public void testMixedNumbers2() {
        GruVersion oldVersion = new GruVersion("1.0.20");
        GruVersion newVersion = new GruVersion("1.0.21");
        Assert.assertTrue("Old version should be less than new version", oldVersion.compareTo(newVersion) < 0);
        Assert.assertTrue("New version should be greater than old version", newVersion.compareTo(oldVersion) > 0);
    }


}
