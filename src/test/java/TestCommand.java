import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import net.ianfc.ExcludeLabels;
import net.ianfc.ExcludeROIs;
import net.imagej.ImageJ;
import org.junit.Test;

public class TestCommand {
    private final ImageJ ij = new ImageJ();

    @Test
    public void exclude_labels() throws Exception {
        ij.ui().showUI();
        ImagePlus imp = IJ.openImage("https://imagej.net/images/blobs.gif");
        imp.show();
        IJ.setAutoThreshold(imp, "Default no-reset");
        Prefs.blackBackground = true;
        IJ.run(imp, "Convert to Mask", "");
        IJ.run(imp, "Analyze Particles...", "show=[Count Masks]");
        ImagePlus imp2 = IJ.getImage();
        imp2.setRoi(58,58,135,137);
        ij.command().run(ExcludeLabels.class, true, "keep_overlaps", "true", "interpolation", 5).get();
    }

    @Test
    public void exclude_ROIs() throws Exception {
        ij.ui().showUI();
        ImagePlus imp = IJ.openImage("https://imagej.net/images/blobs.gif");
        imp.show();
        IJ.setAutoThreshold(imp, "Default no-reset");
        Prefs.blackBackground = true;
        IJ.run(imp, "Convert to Mask", "");
        IJ.run(imp, "Analyze Particles...", "add exclude");
        imp.setRoi(58,58,135,137);
        ij.command().run(ExcludeROIs.class, true, "keep_overlaps", "true").get();
        imp.close();
    }
}
