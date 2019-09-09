Note:  flash_detector.swf must be at the same depth as the courses 
since when AOL does a GetURL, the path is relative to /share/flash_detector/,
but when IE or Netscape do a GetURL from Flash, the path is relative to
the originating HTML file, or ,e.g. /c/p_microarrays/.
So as a trick, ../../c/p_microarrays will always end up in the right place.
AOL, arrrgggh.
-sy 11/15/00
