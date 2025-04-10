package net.ianfc;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.Wand;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import org.scijava.log.LogService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class BorderExclusion {
    public static void remove_external(RoiManager rm, Roi big_roi, boolean partial_overlaps) {
        Roi[] rois = rm.getRoisAsArray();
        int index = 0;
        List<Integer> idx_to_remove_list = new ArrayList<>();
        for (Roi small_roi : rois) {
            if (!check_overlapping(big_roi, small_roi, partial_overlaps)) {
                idx_to_remove_list.add(index);
            }
            index++;
        }
        int[] idx_to_remove = idx_to_remove_list.stream().mapToInt(i -> i).toArray();
        rm.setSelectedIndexes(idx_to_remove);
        rm.runCommand("delete");
    }

    public static int remove_external(ImagePlus imp, Roi big_roi, LogService log, boolean partial_overlaps, int interpolation_amount) {
        if (interpolation_amount > 0) {
            if (big_roi.getType() == 1 | big_roi.getType() == 3) {
                imp.setRoi(big_roi);
                IJ.run(imp, "Interpolate", "interval=" + interpolation_amount + " smooth adjust");
                big_roi = imp.getRoi();
            }
            else {
                interpolation_amount = 0 ;
                if (log != null) {
                    log.info("ROI not freehand or oval, skipping interpolation");
                }
            }
        }
        imp.hide();
        ImageProcessor ip = imp.getProcessor();
        Wand wand = new Wand(ip);
        int width = ip.getWidth();
        int height = ip.getHeight();

        int[] pixel_width = new int[width];
        int[] pixel_height = new int[height];

        IntStream.range(0, width - 1).forEach(val -> pixel_width[val] = val);
        IntStream.range(0, height - 1).forEach(val -> pixel_height[val] = val);
        ip.setColor(0);
        List<Roi> rois = new ArrayList<>();
        List<Double> vals = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        for (int y : pixel_height) {
            for (int x : pixel_width) {
                double val = ip.getValue(x, y);
                if (val > 0) {
                    wand.autoOutline(x, y, val, val, Wand.FOUR_CONNECTED);
                    if (wand.npoints > 0) {
                        Roi small_roi = new PolygonRoi(wand.xpoints, wand.ypoints, wand.npoints, Roi.FREEROI);
                        if (check_overlapping(big_roi, small_roi, partial_overlaps)) {
                            vals.add(val);
                            rois.add(small_roi);
                        }
                        ip.fill(small_roi);
                    }
                }
            }
        }

        for (int i =0; i < rois.size(); i++) {
            Roi small_roi = rois.get(i);
            double val = vals.get(i);
            ip.setColor(val);
            ip.fill(small_roi);
        }
        long endTime = System.currentTimeMillis();
        if (log != null) {
            log.info("Roi exclusion took " + (endTime - startTime) + " milliseconds, interpolation was " + interpolation_amount + ", detected " + rois.size() + " labels");
        }

        imp.show();
        imp.updateAndDraw();
        return rois.size();
    }

    public static boolean check_overlapping(Roi big_roi, Roi small_roi, boolean partial_overlaps) {
        ShapeRoi s1 = new ShapeRoi(big_roi);
        ShapeRoi s2 = new ShapeRoi(small_roi);
        s1.and(s2);
        if (s1.getBounds().width == 0 && s1.getBounds().height == 0) {
            return false;
        }

        if (!partial_overlaps) {
            return Arrays.equals(small_roi.getContainedPoints(), s1.getContainedPoints());
        }
        return true;
    }
}