package com.github.icoccimi;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "Plugins>Border Exclusion>IJ2 Command")
public class IJ2Command extends ContextCommand {
    @Parameter
    private LogService log;

    @Override
    public void run() {
        log.info("Running IJ2 command");
    }
}