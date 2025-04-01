package net.ianfc;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.gui.WaitForUserDialog;

import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.menu.MenuConstants;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.concurrent.ExecutionException;

/**
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>Border Exclusion>Border Exclude Labels", menu = {
        @Menu(label = MenuConstants.PLUGINS_LABEL, weight = MenuConstants.PLUGINS_WEIGHT, mnemonic = MenuConstants.PLUGINS_MNEMONIC),
        @Menu(label = "Border Exclusion", weight = 0.5, mnemonic = 's'),
        @Menu(label = "Border Exclude Labels", weight = 0.5, mnemonic = 'e')
})
public class ExcludeLabels implements Command {
    @Parameter
    ImagePlus imp;

    @Parameter
    LogService log;

    @Parameter(label = "Keep Overlaps")
    Boolean keep_overlaps;

    @Parameter(label = "Interpolation", min="0", description = "Accelerates freehand and oval ROIs by interpolating curves. Set to 0 for no interpolation.")
    Integer interpolation=5;

    @Override
    public void run() {
        try {
            if (imp.isRGB() || imp.isComposite()){
                IJ.error("You can't use this command as a composite/RGB image");
                return;
            }
            if (imp.getRoi() == null) {
                WaitForUserDialog wait_dialog = new WaitForUserDialog("Draw an ROI");
                wait_dialog.setVisible(true);
            }
            Roi big_roi = imp.getRoi();
            if (big_roi != null) {
                BorderExclusion.remove_external(imp, big_roi, log, keep_overlaps, interpolation);
            }
            IJ.wait(500);
            imp.updateImage();
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    public static void main(final String[] args) throws ExecutionException, InterruptedException {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        String image_path = "src/resources/medium_labels.png";
        ImagePlus imp = IJ.openImage(image_path);
        imp.show();
        for (int i = 0; i < 50; i++) {
            ImagePlus imp2 = imp.duplicate();
            imp2.show();
            imp2.setRoi(new OvalRoi(276,711,1431,1281));
            ij.command().run(ExcludeLabels.class, true, "keep_overlaps", "false", "interpolation", i).get();
            imp2.close();
        }
    }
}

