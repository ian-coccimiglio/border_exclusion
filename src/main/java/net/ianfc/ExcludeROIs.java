package net.ianfc;

import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.menu.MenuConstants;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;
import ij.plugin.frame.RoiManager;

/**
 *
 */
@Plugin(type = Command.class, label = "Border Exclude ROIs", menu = {
        @Menu(label = MenuConstants.PLUGINS_LABEL, weight = MenuConstants.PLUGINS_WEIGHT, mnemonic = MenuConstants.PLUGINS_MNEMONIC),
        @Menu(label = "Border Exclusion", weight = 0.5, mnemonic = 's'),
        @Menu(label = "Border Exclude ROIs", weight = 0.5, mnemonic = 'e')
})
public class ExcludeROIs implements Command {
    @Parameter
    RoiManager rm;

    @Parameter
    ImagePlus imp;

    @Parameter
    LogService log;

    @Parameter(label = "Keep Overlaps")
    Boolean keep_overlaps=false;

    @Override
    public void run() {
        if (imp.getRoi() == null) {
            WaitForUserDialog wait_dialog = new WaitForUserDialog("Draw an ROI");
            wait_dialog.setVisible(true);
        }
        Roi big_roi = imp.getRoi();
        if (big_roi != null) {
            BorderExclusion.remove_external(rm, big_roi, keep_overlaps);
        }
        else {
            log.info("No ROI set, skipping exclusion");
        }
        imp.setRoi(big_roi);
    }

    public static void main(final String[] args) {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ImagePlus imp = IJ.openImage("https://imagej.net/images/blobs.gif");
        imp.show();
        IJ.setAutoThreshold(imp, "Default no-reset");
        Prefs.blackBackground = true;
        IJ.run(imp, "Convert to Mask", "");
        IJ.run(imp, "Analyze Particles...", "add exclude");
        imp.setRoi(58,58,135,137);
        ij.command().run(ExcludeROIs.class, true, "keep_overlaps", false);
    }
}
