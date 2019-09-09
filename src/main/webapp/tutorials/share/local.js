var cookieDomain = document.domain;
if (! cookieDomain) cookieDomain = "localdomain";

var bookmark = 1;
var slideArray = [];
var suspendData = new Cookie(document, "suspendData", 2400);

suspendData.load();
if (! suspendData.completedSlides) suspendData.completedSlides = "";
initSlides(totalSlides);


function getBookmark() {
  return "bookmark=" + verifyBookmark(suspendData.lessonLocation);
}

function getCompletedSlides() {
  return suspendData.completedSlides;
}

function scormStop() {
  suspendData.lessonLocation = verifyBookmark(bookmark);
  suspendData.completedSlides = getCompletedString(slideArray);
  suspendData.store();
}

function slideEnd(curSlideOrder) {
  slideArray[curSlideOrder] = "completed";
}

function initSlides(totalSlides) {
  var completedArray = suspendData.completedSlides.split(",");

  slideArray[0] = null;    // placeholder
  for (var i = 1; i <= totalSlides; i++) slideArray[i] = "incomplete";
  for (i in completedArray) slideArray[completedArray[i]] = "completed";
}

function setBookMark(bm) {
  bookmark = bm.toString();
}

/**
 * Checks if its argument is NaN, null or < 1. If so, returns 1; else
 * returns the argument after casting it to a Number. 1 is also
 * returned if the cast fails.
 */
function verifyBookmark(bm) {
  bm = Number(bm);
  if (bm == null || isNaN(bm) || bm < 1) return 1;
  return bm;
}

/**
 * Turns an array like slideArray into a comma-separated string. The
 * array is expected to be an array of strings. Any item which ==
 * "completed" will have its index added to the string. Index 0 of the
 * list will be ignored.
 */
function getCompletedString(list) {
  var cs = "";

  for (var i = 1; i <= list.length; i++) {
    if (list[i] == "completed") {
      if (cs == "") cs = i + "";
      else cs += "," + i;
    }
  }

  return cs;
}
