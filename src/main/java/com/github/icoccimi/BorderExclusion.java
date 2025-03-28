package com.github.icoccimi;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.Wand;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class BorderExclusion {

    public static void remove_external(RoiManager rm, Roi big_roi, boolean partial_overlaps) {
        Roi[] rois = rm.getRoisAsArray();
        int index = 0;
        List<Integer> idx_to_remove_list = new ArrayList<Integer>();
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

    public static void remove_external(ImagePlus imp, Roi big_roi, boolean partial_overlaps) {
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
        List<Roi> rois = new ArrayList<Roi>();
        List<Integer> vals = new ArrayList<Integer>();
        for (int y : pixel_height) {
            for (int x : pixel_width) {
                int val = ip.getPixel(x, y);
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
            float val = vals.get(i);
            ip.setColor(val);
            ip.fill(small_roi);
        }

        imp.show();
        imp.updateAndDraw();
    }

    public static boolean check_overlapping(Roi big_roi, Roi small_roi, boolean partial_overlaps) {
        ShapeRoi s1 = new ShapeRoi(big_roi);
        ShapeRoi s2 = new ShapeRoi(small_roi);
        s1.and(s2);
        if (s1.getBounds().width == 0 && s1.getBounds().height == 0) {
            return false;
        }

        if (!partial_overlaps) {
            if (!Arrays.equals(small_roi.getContainedPoints(), s1.getContainedPoints())) {
                return false;
            }
        }
        return true;
    }
}