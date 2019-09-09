/*
 * JavaScript for manipulating distributor source code cookies.
 *
 * Originally written by Spencer Yeh, GeneEd.com
 *
 * Cleaned up, documented and fixed by Chris Palmer, GeneEd.com, 29-30 August 2001
 *
 * Tested on:
 *    Windows 2000
 *       Navigator 4.08
 *       Communicator 6
 *       IE 5.5
 *    Mac OS 9.0
 *       Communicator 4.7
 *       IE 5
 */


/*
 * cookieSet()
 *
 * Sets or gets cookie values. Arguments are cookie name (string),
 * cookie value (unescaped string), expiry (Date object), and path
 * (string). Expiry and path are optional.
 *
 * Returns nothing.
 */

function cookieSet(name, value, expire, path) {
    document.cookie = name + "=" + escape(value) + 
	((expire == null) ? "" : ("; expires=" + expire.toGMTString())) +
	((path == null) ? "" : ("; path=" + path));
}


/*
 * cookieGet()
 *
 * Returns value of named cookie (possibly null). Argument is cookie
 * name (string).
 */

function cookieGet(name) {
    var search = name + '=';
    var begin  = document.cookie.indexOf(search);

    if (begin != -1) {
	begin += search.length;
	end = document.cookie.indexOf(";", begin);
	if (end == -1)
	    end = document.cookie.length;
	return unescape(document.cookie.substring(begin, end));
    }

    return null;
}


/*
 * cookieDelete()
 *
 * Sets expiry date of named cookie in specified path to a time in the
 * past and sets the value of the cookie to something
 * meaningless. Arguments are name and path (both strings).
 *
 * Returns nothing.
 *
 */

function cookieDelete(name, path) {
    var expires = new Date();

    expires.setTime(expires.getTime() - 1*(1000*60*60*24));
    document.cookie = name + '=DUMMY; expires=' + expires.toGMTString() +
	((path == null) ? "" : ("; path=" + path));
}


/*
 * cookieTest()
 *
 * Sets a fake cookie and checks if the object property document.cookie got set.
 *
 * Returns a boolean value.
 */

function cookieTest() {
    document.cookie = "COOKIE_TEST=1; expires=; path=/";

    if (document.cookie == "")
	return false;
    else
	return true;
}


/*
 * queryString()
 *
 * Given a key name (first argument) in the query string of the
 * current location, returns the (unescaped) value it is set to
 * (possibly null).
 *
 * The optional second argument is the query string or URL to search
 * against. It can be a string or a Location object. If no second
 * argument is given, this.location is used.
 *
 * This script does recognize the '+' as a space.
 */

function queryString(key, src) {
    if (src == null)
	src = this.location;

    // Get just query string out of URL; prepend with '&' to
    // facilitate search
    var qs = '&' + stripQuery(src);
  
    var begin = qs.indexOf('&' + key + '=');

    if(begin == -1)
	return null;

    // move past the '&' -sy
    begin++;

    var value = '', ch = '';

    for(var i = begin + key.length; i < qs.length; i++) {
	ch = qs.charAt(i);
	if(ch == '&' || ch == ';')
	    break;
	if(ch == '+')
	    value += ' ';
	else if(ch != '=')
	    value += ch;
    }

    return unescape(value);
}


/*
 * stripQuery()
 *
 * Given the source address (optional), returns the query string
 * portion of the address. If the source address is not given,
 * returns the query string portion for current window/frame.
 *
 * Accepts src as a URL in string format or a Location object.
 */

function stripQuery(src) {
    if(src == null)
	src = this.location;

    if(typeof src == 'string') {
	var string   = '';
	var qbegin = src.indexOf('?');

	string = (qbegin != -1)
	    ? src.substr(qbegin + 1, src.length)
	    : null;

	return string;
    } else if(typeof src == 'object')
	return src.search.substr(1, location.search.length);

    return null;
}


/*
 * encodeSourceCode()
 *
 * Encrypts the source code based on a seemingly arbitrary `every
 * time' pad. Argument is source code to be encoded (string); if
 * omitted, uses the value of the sc key from the current location's
 * query string.
 *
 * Returns encoded source code as string.
 *
 * I did not write this--CRP
 */

function encodeSourceCode(sourceCodeInput) {
    var offset = new Array(13,3,24,38,18,23,18,2,37,27,20,5,1,12,31,23,2,9,20,14,38,20,2,13,29,4,38,12,27,24,35,16,27,22,14,29,15,2,10,34,15,29,18,28,32,14,9,15,18,29,14,32,14,25,14,25,38,17,15,8);
    //var offset = new Array(13,3,24,39,18,23,18,2,39,27,20,5,1,12,31,23,2,9,20,14,38,20,2,13,29,4,38,12,27,24,35,39,27,22,14,29,15,2,10,34,15,29,18,28,32,14,9,15,18,29,14,32,14,25,14,25,38,17,15,8);

    var sourceCode = sourceCodeInput ? sourceCodeInput : queryString('sc');
    var s = '';

    if (sourceCode == null)
	return null;
    else {
	for (var i = 0; i < sourceCode.length; i++) {
	    s += sourceCode.charCodeAt(i) - offset[i];
	}
    }

    return s;
}


/*
 * decodeSourceCode()
 *
 * Reverse of encodeSourceCode().
 *
 */

function decodeSourceCode(keyInput) {
    var offset = new Array(13,3,24,38,18,23,18,2,37,27,20,5,1,12,31,23,2,9,20,14,38,20,2,13,29,4,38,12,27,24,35,16,27,22,14,29,15,2,10,34,15,29,18,28,32,14,9,15,18,29,14,32,14,25,14,25,38,17,15,8);

    var key = keyInput ? keyInput : queryString('c');
    var s = '';

    if (key == null)
	return null;
    else
	for (var i = 0; i < (key.length)/2; i++)
	    s += String.fromCharCode(Number(key.substr(i * 2, 2)) + Number(offset[i]));

    return s;
}

/*
 * SourceCode()
 *
 * Alias to decodeSourceCode().
 */

function SourceCode(keyInput) {
    return(decodeSourceCode(keyInput));
}


/*
 * sourceCodeCookieSet()
 *
 * Creates a cookie with name C and value of c key in location's query
 * string. Returns that value.
 *
 */

function sourceCodeCookieSet() {
    var cValue = queryString('c');

    if (cValue) {
	var expires = new Date();

	expires.setTime(expires.getTime() + 14*(1000*60*60*24));
	cookieSet('C', cValue, expires, '/');
    }
 
    return cValue;
}

sourceCodeCookieSet();

// EOF
