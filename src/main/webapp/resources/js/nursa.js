	/**
 *
 * global functions for NURSA site
 * 
 * 3/4/14 cmw
 * 
 */

/*set default for  blockUI */
$(function() {
		$.blockUI.defaults.css.border = "none";
		$.blockUI.defaults.css.left = "45%";
		$.blockUI.defaults.overlayCSS = {backgroundColor:"#ccc", opacity: .5};
		$.blockUI.defaults.message = $("#nursaBlockUI");
}
);

/* capture expired view exceptions from ajax requests and allow for page refresh */
/* attaching it PF's own pfAjaxComplete event so as to not have to manually attach 
 * it to each ajaxified component
 */
$(document).on('pfAjaxComplete', function(e, xhr, settings) {   

	//make sure there is an error message
	//TODO can parse out message and take different actions depending on the error
	if($(xhr.responseXML).find("partial-response error error-message").size()) {
		//next line commented out - the original intent is to actually display the error message
		//being generated, but it needs some tweaking to pull the message from the XML
		//alert(xhr.responseXML).find("partial-response error error-message");
		//call the PF dialog box saying the view has expired
		//this is declared in template.xhtml
		PF('expiredView').show();
	}


 });
