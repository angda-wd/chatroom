package com.angda.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class CommUtilTest {

    @Test
    public void loadProperties() {
        Properties pro=CommUtil.loadProperties("db.properties");
        Assert.assertNotNull(pro);
    }
}