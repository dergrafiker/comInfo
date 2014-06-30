package com.commerzinfo;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LauncherTest {

    @Ignore
    @Test
    public void testMain() throws Exception {
        List<String> args = new ArrayList<String>();

        URL inputDirResource = LauncherTest.class.getClassLoader().getResource("testdata");
        Assert.assertNotNull(inputDirResource);
        File inputDir = new File(inputDirResource.toURI());
        args.add(inputDir.getAbsolutePath());

        Launcher.main(args.toArray(new String[]{""}));
    }
}
