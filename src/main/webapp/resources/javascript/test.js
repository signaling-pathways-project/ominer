function scrollToAnchor(aid) {
	var aTag = $("a[name='" + aid + "']");
	$('html,body').animate({
		scrollTop : aTag.offset().top
	}, 'slow');
}

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
}

$(document).ready(function() {
	scrollToAnchor(getURLParameter('SubSection'));
});
