import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import net.imagej.ImageJ;
import org.junit.Test;
import org.scijava.log.LogService;

import static net.ianfc.BorderExclusion.remove_external;
import static org.junit.Assert.assertEquals;

public class TestRemove {
    private final ImageJ ij = new ImageJ();
    LogService log;

    @Test
    public void remove() {
        ij.ui().showUI();
        ImagePlus imp = IJ.openImage("https://imagej.net/images/blobs.gif");
        imp.show();
        IJ.setAutoThreshold(imp, "Default no-reset");
        Prefs.blackBackground = true;
        IJ.run(imp, "Convert to Mask", "");
        IJ.run(imp, "Analyze Particles...", "show=[Count Masks]");
        ImagePlus imp2 = IJ.getImage();
        imp2.setRoi(58,58,135,137);
        int num_remain = remove_external(imp2, imp2.getRoi(), log, true, 5);
        assertEquals(18, num_remain);
    }
}
