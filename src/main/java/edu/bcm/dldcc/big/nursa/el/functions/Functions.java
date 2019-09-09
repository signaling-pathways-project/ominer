package edu.bcm.dldcc.big.nursa.el.functions;

import java.util.Iterator;
import java.util.List;

import edu.bcm.dldcc.big.nursa.model.common.DataResource;

/* custom EL functions
 * it's quite conceivable that this may go into DLDCC Common at some point
 * 3/12/14 CMW
 */

public final class Functions {

	private Functions() {
	}

	/**
	 * given a source and substring, returns HTML markup that highlights the
	 * substring(s) matching the regex in the source.
	 * 
	 * this also returns results in uppercase to fit the use case for which this
	 * was built
	 * 
	 * consider modifying this to allow for specifying the HTML markup for
	 * highlighting, as well as additional formatting options
	 * 
	 * @author cmw
	 * 
	 * @param theSource
	 *            the source string
	 * @param theRegex
	 *            the regex matching the substring(s) to highlight
	 * @return formatted HTML highlighting theSubstring in theSource
	 */
	public static String highlightByRegex(String theSource, String theRegex) {
		// this HTML, particularly the color, is NURSA-specific
		return theSource.replaceAll("(" + theRegex + ")",
				"<strong><span style = 'color:#E74C3C'>$1</span></strong>")
				.toUpperCase();
	}

	/**
	 * highlights a character in the supplied text
	 * 
	 * consider modifying this to allow for specifying the HTML markup for
	 * highlighting, as well as additional formatting options
	 * 
	 * @author cmw
	 * 
	 * @param theSource
	 *            the source string
	 * @param thePos
	 *            the position at which to apply the highlight
	 * @return formatted HTML highlighting the specified character in theSource
	 */
	public static String highlightByIndex(String theSource, int thePos) {
		// this HTML, particularly the color, is NURSA-specific
		// make sure we have a long enough string
		if (theSource.length() > thePos) {

			return theSource.substring(0, thePos)
					+ "<strong><span style = 'color:#E74C3C'>"
					+ theSource.charAt(thePos) + "</span></strong>"
					+ theSource.substring(thePos + 1, theSource.length());

		}
		// if not, return the original string
		else {
			return theSource;
		}
	}

	/**
	 * truncates a strong to the nearest word given a maximum number of characters
	 * 
	 * @author cw
	 * 
	 * @param theSource the source string
	 * @param maxChars the maximum number of characters in the return value
	 * @return a string containing no more than maxChars truncated to the nearest word
	 * 
	 */

	public static String truncateToWord(String theSource, int maxChars) {
		// init return value here
		String returnVal = "Not Available";
		if(theSource == null)
			return returnVal;
		
		// Do we really have a string?
		if (theSource.toString().length() < 1) {
			return returnVal;
		}

		// is the string shorter than maxChar? If so, return the string
		if (theSource.toString().length() <= maxChars) {
			return theSource;
		}

		// the truncated string before parsing for word breaks
		// substring already subtracts 1 from the second arg, so we're at the
		// right length
		String toTruncate = theSource.substring(0, maxChars);

		// subtract 1 so that we're at the end of the string
		int i = maxChars - 1;

		// find the first non word character backwards from the end and don't go
		// out of bounds
		while (String.valueOf(toTruncate.charAt(i)).matches("\\w") && i > 0) {

			i--;

		}

		// now decrement i (since we skipped doing so on our last match), go to
		// the first period or letter, and that is our substring
		// there is a smarter way to do this, but I've been looking at it for a
		// while
		// and this seems to cover punctuation and other non letters
		i--;
		while (String.valueOf(toTruncate.charAt(i)).matches("^\\w")
				&& String.valueOf(toTruncate.charAt(i)) != "." && i > 0) {

			i--;

		}

		// if i < 0, we have a very long word. This is unlikely, but let's deal
		// with it here
		if (i == 0) {
			returnVal = "Available on detail page";
		}

		// else if the last char is a period - lucky us - so return that here
		else if (toTruncate.charAt(i) == '.') {

			// substring subtracts 1 from second argument, so we want the full
			// value
			// there is a smarter way to do this, but I've been looking at this
			// all day
			returnVal = toTruncate.substring(0, i + 1);

		}

		// otherwise, slice it off last char, add an ellipsis, and return
		else {

			returnVal = toTruncate.substring(0, i).concat("...");

		}

		return returnVal;

	}

}
