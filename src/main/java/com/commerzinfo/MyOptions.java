package com.commerzinfo;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FileOptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class MyOptions {

    @Option(name = "-c", usage = "path to configFile", handler = FileOptionHandler.class)
    private File configFile = new File(new File("."), "config.properties");

    @Argument(required = true)
    private List<String> arguments = new ArrayList<>();

    List<String> getArguments() {
        return arguments;
    }

    File getConfigFile() {
        return configFile;
    }
}
