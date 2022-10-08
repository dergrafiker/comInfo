package com.commerzinfo;

import com.google.common.collect.Lists;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FileOptionHandler;

import java.io.File;
import java.util.List;

class MyOptions {

    @Option(name = "-c", usage = "path to configFile", handler = FileOptionHandler.class)
    private File configFile = new File(new File("."), "config.properties");

    @Argument(required = true)
    private List<String> arguments = Lists.newArrayList();

    List<String> getArguments() {
        return arguments;
    }

    File getConfigFile() {
        return configFile;
    }
}
