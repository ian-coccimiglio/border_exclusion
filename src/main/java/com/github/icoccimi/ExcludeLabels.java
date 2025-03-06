package com.github.icoccimi;
import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.menu.MenuConstants;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;

/**
 *
 */
@Plugin(type = Command.class, label = "Exclude Labels", menu = {
        @Menu(label = MenuConstants.PLUGINS_LABEL, weight = Double.POSITIVE_INFINITY, mnemonic = MenuConstants.PLUGINS_MNEMONIC),
        @Menu(label = "Border Exclusion", weight = Double.POSITIVE_INFINITY, mnemonic = 's'),
        @Menu(label = "Exclude Labels", weight = Double.POSITIVE_INFINITY, mnemonic = 'e')
})
public class ExcludeLabels implements Command {
    @Parameter
    ImagePlus imp;

    @Parameter(label = "Keep Overlaps")
    Boolean keep_overlaps;

    @Override
    public void run() {
        if (imp.getRoi() == null) {
            WaitForUserDialog wait_dialog = new WaitForUserDialog("Draw an ROI");
            wait_dialog.setVisible(true);
        }
        Roi big_roi = imp.getRoi();
        if (big_roi != null) {
            BorderExclusion.remove_external(imp, big_roi, keep_overlaps);
        }
        imp.setRoi(big_roi);
        IJ.wait(500);
        imp.updateImage();
    }

    public static void main(final String[] args) throws Exception {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ImagePlus imp = IJ.openImage("https://imagej.net/images/blobs.gif");
        imp.show();
        IJ.setAutoThreshold(imp, "Default no-reset");
        Prefs.blackBackground = true;
        IJ.run(imp, "Convert to Mask", "");
        IJ.run(imp, "Analyze Particles...", "show=[Count Masks]");
        ImagePlus imp2 = IJ.getImage();
        imp2.setRoi(58,58,135,137);
        ij.command().run(ExcludeLabels.class, true);
    }
}
