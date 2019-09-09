/*
 * aspen.js: interface between ActionScript and the SCORM LMS
 * API. ActionScript calls these functions to talk to the
 * LMS. Specifically for use with Aspen and AstraZeneca.
 *
 * Chris Palmer, GeneEd chris.palmer@geneed.com
 *
 */

/* slideArray contains the status ("incomplete" or "completed") for
 * each slide in the course. See initSlides(), below.
 */
var slideArray = new Array();

/* Has LMSFinish() been called? */
var finished = 0;       /* is set to > 0 when scormStop() is called by Flash */

/* Bookmark updated by setBookMark() */
var bookmark = "1";

/* session_time updated by setSessionTime() */
var session_time = "0000:00:00";

/* used to calculate session time later */
var startMilliseconds = 0;

/* turn debugging on and off with one easy switch */
var DEBUG = 0;		/* 1 is on, 0 is off */
var DEBUG2 = 0;
var DEBUG3 = 0;


/*
 * The HTML wrapper must invoke these handlers onload and onunload to
 * make sure that the LMS is properly initialized/finished. In this
 * case, Flash should call scormStart() and
 * scormStop()--onLoadHandler() is a noop and onUnloadHandler() is
 * only there in case the user hits the close button.
 *
 */

function onLoadHandler() {
	/* Do nothing; JavaScript in index.html will call scormStart() */
}

function onUnloadHandler() {
	/* Unfortunately, there is no way we can do anything else
         * here. The window closes before the any methods or
         * properties of the movie can be accessed. We just have to do
         * the best we can, and set known wrong values. */

	//ars: previously this was never called because there was no
	//reference to it in the html page

	if (DEBUG3) alert("onUnloadHandler(); finished: " + finished);

	if (finished == 0) {
		if (DEBUG3) alert("onUnloadHandler()");

		//if ((api = getAPI(window)) == null)
		//	alert("Could not find LMS.");
		//if (DEBUG3) alert(api);

		scormLmsSetValue("cmi.core.session_time", session_time);

		if ((! bookmark) || isNaN(bookmark))
			bookmark = "1";
		else
			bookmark = bookmark.toString();
		scormLmsSetValue("cmi.core.lesson_location", bookmark);
	}
}


/* Get lesson_location and tell Flash what it is. */
function getBookmark() {
	if (DEBUG) alert("getBookmark()--called!!");
	var bm = scormLmsGetValue("cmi.core.lesson_location");
	if (bm)
		bm = bm.toString();
	else
		bm = 0;
	if (DEBUG) alert("bm is " + bm);
	return "bookmark=" + bm;
}


/*
 * scormStart() and scormStop() are generally accessible functions
 * (i.e. accessible both to Flash and other JavaScript functions) that
 * handle any work that needs to be done to start and stop
 * communication with the LMS. Put all such functionality here, so
 * that it can be managed in once place.  */

function scormStart(totalSlides) {
	if (DEBUG) alert("scormStart(): entering; totalSlides: " + totalSlides);
	var currentStatus;
	var lessonLocation;

	/* Initialize LMS. Do this first so that later code in this function
	 * can make use of the LMS. */
	scormLmsInitialize();

	/* set up slide list */
	initSlides(totalSlides);

	/* Check the user's status on the lesson. Unless it's completed,
	 * explicitly set it to incomplete. */
	currentStatus = scormLmsGetValue("cmi.core.lesson_status");
	if (currentStatus != "completed")	
		scormLmsSetValue("cmi.core.lesson_status", "incomplete");

	/* store start time */
	var startDate = new Date();
	startMilliseconds = startDate.valueOf();

	if (DEBUG) alert("scormStart(): leaving");
}

function scormStop() {
	if (DEBUG) alert("in js:scormStop()");
	/* comma-separated string of slide numbers completed */
	var completedSlides = "";

	/* Set cmi.core.session_time by subtracting the time now from
	 * the time stored in scormStart(). */
	var endDate = new Date();
	var endMilliseconds = endDate.valueOf();
	session_time = msToTimeString(endMilliseconds - startMilliseconds);
	scormLmsSetValue("cmi.core.session_time", session_time);

	/* Bookmark current pos. in course. (ActionScript should have
         * called setBookMark() before calling this function.) */
	if (DEBUG) alert("Bookmark is: " + bookmark);
	if ((! bookmark) || isNaN(bookmark))
		bookmark = "1";
	else
		bookmark = bookmark.toString();
	scormLmsSetValue("cmi.core.lesson_location", bookmark);

	/* store completed slides */
	for (var i = 1; i <= slideArray.length; i++)
		//if (DEBUG) alert(i + " = " + slideArray[i]);
		if (slideArray[i] == "completed")
			if (completedSlides == "")
				completedSlides = i.toString();
			else
				completedSlides += "," + i.toString();
	scormLmsSetValue("cmi.suspend_data", completedSlides);

	if (DEBUG) {
		var debugString = "";
		alert("slideArray.length is " + slideArray.length);
		for (var i = 1; i < slideArray.length; i++)
			debugString += i + " = " + slideArray[i] + "\n";
		alert(debugString);
	}

	scormLmsSetValue("cmi.core.lesson_status", verifyCourseComplete());
	scormLmsFinish();

	/* Prevent this function from being called again */
	finished = 1;
}


/*
 * Called by ActionScript at the first frame of a slide. If the slide
 * has not already been completed, its status is set to "incomplete"
 * in the global array slideArray. Argument is the slide number (int).
 */

/* -- redundant
function slideStart(curSlideOrder) {
	if (DEBUG) alert("slideStart() called with curSlideOrder = " +
		curSlideOrder);
	if (slideArray[curSlideOrder] != "completed")
		slideArray[curSlideOrder] = "incomplete";
}
*/


/* 
 * Called by ActionScript at the last frame of a slide. Sets the
 * status of that slide to "completed" in the global array
 * slideArray. Argument is the slide number (int).
 */

function slideEnd(curSlideOrder) {
//	if (DEBUG) alert("entering slideEnd() with curSlideOrder = " +
//		curSlideOrder);
	slideArray[curSlideOrder] = "completed";
}


/*
 * Before setting the cmi.core.lesson_status to "completed", we need
 * to verify that all slides have been viewed to the last frame (see
 * slideEnd()). This function takes no arguments and returns the
 * status of the course (either of the strings "incomplete" or
 * "completed", as the case may be) based on the status of each slide
 * in slideArray.
 */

function verifyCourseComplete() {
	for (var i = 1; i < slideArray.length; i++) {
		if (DEBUG)
			alert("verifyCourseComplete: Slide " + i + " is " +
				slideArray[i]);
		if (slideArray[i] != "completed")
			return("incomplete");
	}
	return("completed");
}


/*
 * Defines slideArray. Argument is totalSlides (int), the total number
 * of slides in the course. The first element of slideArray is set to
 * null so that the slide number and the index into slideArray are the
 * same. All slides are initialized "incomplete". This function should
 * be called at the beginning of the course, from scormStart()
 * (above).
 */

function initSlides(totalSlides) {
	var completedSlides, completedArray;

	// Make sure completedSlides was not null or undefined
	if ( (completedSlides = scormLmsGetValue("cmi.suspend_data")) )
		completedArray = completedSlides.split(",");

	slideArray[0] = null;
	for (var i = 1; i <= totalSlides; i++)
		slideArray[i] = "incomplete";

	/* inefficient, but works: set completed slides to "completed" */
	for (i in completedArray)
		slideArray[completedArray[i]] = "completed";

	/* if (DEBUG) {
		var debugString = "";
		alert("slideArray.length is " + slideArray.length);
		for (var i = 1; i < slideArray.length; i++)
			debugString += i + " = " + slideArray[i] + "\n";
		alert(debugString);
	} */
}

/*
 * Updates the global variable bookmark. Must be called before
 * scormStop(). Argument bm is integer or string of integer.
 */

function setBookMark(bm) {
	if (DEBUG) alert("entering setBookMark() with bm = " + bm.toString());
	bookmark = bm.toString();
}

/*
 * Updates the global variable session_time. Must be called before
 * scormStop(). Argument time is in format (HH)HH:MM:SS(.SS).
 */

function setSessionTime(time) {
	//alert("entering setSessionTime() with time = " +
	//	time.toString());
	session_time = time.toString();
}


/* SCORM UTILITY FUNCTIONS */

/*
 * Finds API object and returns a reference to it (success) or null
 * (failure). Takes a reference to a Window object as its one
 * argument.
 *
 */

function getAPI(win) {
	var api        = null;    /* API object reference */
	apiAttempts    = 0;       /* attempts to find the API */
	apiAttemptsMax = 7;       /* This number is arbitrary */

	if (DEBUG) alert("getAPI(): entering");

	while ( (win.API == null) &&
	        (win.parent != null) &&
	        (win.parent != win) )
	{
		if (++apiAttempts > apiAttemptsMax)
			return null;

		//if (DEBUG) alert("getAPI(): searching for API: " + apiAttempts);

		win = win.parent;
	}

	api = win.API;

	//if (DEBUG) alert("getAPI(): leaving");

	return api;
}


/*
 * Calls api.LMSInitialize().
 *
 */

function scormLmsInitialize() {
//	if (DEBUG) alert("scormLmsInitialize(): entering");

	var result;
	
	if ((api = getAPI(window)) == null)
		return die("Could not find LMS.");

	if (DEBUG) alert("scormLmsInitialize(): called getAPI()");

	result = api.LMSInitialize("");
	if (DEBUG) alert("result of api init was: " + result);
	if (result.toString() != "true")
		return die("Could not initialize LMS.");

//	if (DEBUG) alert("scormLmsInitialize(): called api.LMSInitialize()");

//	if (DEBUG) alert("scormLmsInitialize(): leaving");

	//return true;
}

/*
 * Calls api.LMSFinish().
 *
 */

function scormLmsFinish() {
	var result;

	if ((api = getAPI(window)) == null)
		return die("Could not find LMS.");

	result = api.LMSFinish("");
	if (result.toString() != "true")
		return die("Could not close LMS.");

	//return true;
}


/*
 * Arguments are the name of the variable to set and the value to set
 * it to (both are strings). Returns 0 on success, -1 on failure.
 */

function scormLmsSetValue(name, value) {
	var result;

	// Apparently, when ActionScript calls this function, the
	// arguments are sent as something other than strings. Cast
	// the strings to strings.
	name  = name.toString();
	value = value.toString();

	if (DEBUG) alert("Entering scormLmsSetValue().\nname = " + name +
		"\nvalue = " + value);

	if ((api = getAPI(window)) == null)
		return die("Could not find LMS.");

	result = api.LMSSetValue(name, value);
	if (DEBUG3) alert(result.toString());
	var errNum = api.LMSGetLastError();
	var errMsg = api.LMSGetErrorString(errNum);
	if (result.toString() != "true")
		return die("Could not set value " + name + " = " + value + "; result was " + result.toString() + "; Error: " + errMsg);

	// Force the transaction through; don't let API adaptor cache
	// values. This is potentially slower but safer. I don't know
	// how appropriate or helpful error detection is here. Maybe
	// later.
	api.LMSCommit("");

	//return true;
}


/*
 * Argument is the name of the SCORM data model element whose value
 * you want to retrieve from the LMS. Returns that value (possibly
 * empty string).
 */

function scormLmsGetValue(name) {
	var result;

	if (api == null)
		return die("Lost contact with LMS.");

	name = name.toString();
	result = api.LMSGetValue(name);

	return result;
}


/*
 * NOP. Maybe filled in someday? Right now we use LMSCommit() always
 * immediately after LMSSetValue (in scormLmsSetValue()).
 */

function scormLmsCommit() {
	//if (api == null)
	//	return die("Lost contact with LMS.");

	//return true;
}


/*
 * Called if a fatal error occurs, e.g. unable to find API
 * object. Argument is message to give user via alert(). Returns null.
 */

function die(msg) {
	//alert(msg);

	//return false;
}

/* 
 * Takes number of milliseconds as argument and returns a string in the
 * format HHHH:MM:SS.
 */
function msToTimeString(ms) {
	var hours = 0, minutes = 0, seconds = 0;
	var junk = new Array();

	seconds = ms / 1000;
	junk = seconds.toString().split('.');
	seconds = junk[0];
	if (seconds < 1) return "0000:00:00";

	minutes = seconds / 60;
	junk = minutes.toString().split('.');
	minutes = junk[0];
	if (minutes < 1) minutes = 0;
	seconds = seconds % 60;

	hours = minutes / 60;
	junk = hours.toString().split('.');
	hours = junk[0];
	if (hours < 1) hours = 0;
	minutes = minutes %60;

	if (seconds < 10) seconds = "0" + seconds;
	if (minutes < 10) minutes = "0" + minutes;
	if (hours < 10) hours = "000" + hours;
	else if (hours < 100) hours = "00" + hours;
	else if (hours < 1000) hours = "0" + hours;

	return hours + ":" + minutes + ":" + seconds;
}

/* EOF */
