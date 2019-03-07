/**
 * functionality to check whether URL fragment information is present (e.g. after a redirect after login)
 * and if so try to select an according element of that class and click it
 **/
$(function() {
	if (location.hash.slice(1)) {
		var anchor = location.hash.slice(1);
		if(anchor.includes('#')) {
			anchor = anchor.split('#')[0];
		}
		var element = $('.' + $.escapeSelector(anchor))[0];
		if (element) {
			element.click();
		}
	}
});