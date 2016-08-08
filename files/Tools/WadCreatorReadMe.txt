 Commandos - WAD Creator
-------------------------

This program repacks (or creates) extracted "Commandos Behind Enemy Lines" *.WAD archives.

Usage: java WadCreator <InputFolder>

All types of bitmaps are supported now!
You can add 1, 4, 8 and even 24-bit bmp's.
Source image and mask don't need to have the same color depth.
A default color palette (similiar to the MS Paint palette) for all 24-bit bmp's will be created = big loss of quality.
It's highly recommended to create an optimized palette for each true color bitmap!

To add IMAGE01.RLE to the archive, you have to store
 - IMAGE01.BMP
 - IMAGE01.MASK.BMP
in the <InputFolder>. No *.RLE extensions allowed!

MASK colors:
Black(RGB:0,0,0) = transparent,
White(RGB:255,255,255) = opaque,
anything else = semi-transparent

Don't forget to backup the original files before you replace them.

Please contact me, if the program reports any error message
or when the game crashes with the new WAD archive.

Java Runtime Environment (JRE) required -> www.java.com
No warranty, all rights reserved.
© 2010 ferdinand.graf.zeppelin@gmail.com aka herbert3000
Commandos Behind Enemy Lines is a trademark of Eidos Interactive Ltd


v1.5/101215