package com.commerzinfo;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FileOptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyOptions {

    @Option(name = "-r", usage = "enable folder recursion")
    private boolean recursive;

    @Option(name = "-c", usage = "path to configFile", handler = FileOptionHandler.class)
    private File configFile = new File(new File("."), "config.properties");

    @Argument
    private List<String> arguments = new ArrayList<String>();

    public boolean isRecursive() {
        return recursive;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public File getConfigFile() {
        return configFile;
    }
}
