## Border Exclusion

Have you ever tried to remove labels or ROIs from a dedicated region, but struggled to specify exactly which region?

Then this plugin is for you!
* Works on label images
* Works on binary masks
* Works on collections of ROIs
* Fast on large images if interpolation

Parameters:
Keep Overlaps: If true, this will keep any labels/rois overlapping with the boundary. Otherwise, all overlaps are excluded.
Interpolation: For oval/freehand ROIs, interpolating the selection can speed up the functionality by converting your ROI into a polygon shape prior to removal.

### Using Border Exclusion on Label Images

https://github.com/user-attachments/assets/5b8fc173-e1d9-4ab4-9a23-09a96e390f9f

## Interpolation acceleration

By increasing the interpolation interval, an OvalRoi or Freehand ROI is no longer subpixel-accurate, but the speed increase is substantial for resolving label images. 

The test below was used on a binary image of 2480 by 2512 pixels, using an oval ROI of size (w 1431,h 1281).
<p align="center">
    <img width="600" src="assets/medium_labels_ovalroi.png">
</p>

<p align="center">
  <img width="600" src="assets/Algorithm_Acceleration.png" alt="">
</p>

## Scripting with Border Exclusion


## Installation
For now, simply place the border-exclusion-0.2.0-SNAPSHOT.jar file into your Fiji.app/jars directory.

## Contributions
Any contributions are very welcome! Please file any issues or send a pull-request if you have suggestions.

I hope you enjoy this plugin!
