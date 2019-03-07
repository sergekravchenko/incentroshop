//mobile.js - include functions needed to modify layout or interactions for mobile / small screen devices.

var Mobile = {};
Mobile.init = function() {
	// Register your mobile functions here
	Mobile.modifyDomStructure();
	Mobile.accountNavigation();
	Mobile.bootstrapSelect();
}

Mobile.modifyDomStructure = function() {
	if (Modernizr.mq('(max-width: 767px)')) {
		//collapse all filters by default
		$('.filter-panel').addClass('collapse');
		$('.filter-group h3').addClass('collapsed');
		$('.filter-group h3').find('span').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-right');
		$(".filter-list").removeClass('in');
	} else {
        $('.filter-panel').removeClass('collapse');
	}
}


Mobile.accountNavigation = function() {
	if (Modernizr.mq('(max-width: 991px)')) {
	    if ($('.account-nav-box select').length == 0) {
    		$('.account-navigation').each(function() {
    		    var select=$(document.createElement('select')).insertBefore($(this).hide());
    		    $('>li a, >li p', this).each(function() {
    		       
    		            		        
    		        if ($(this).hasClass('account-nav-heading')) {
    		            option=$(document.createElement('optgroup')).appendTo(select).attr('label',$(this).text());
    		        } else {
    		            if($(this).closest('li').hasClass('active')){
    		                option=$(document.createElement('option')).attr('selected', 'selected').appendTo(select).val(this.href).html($(this).html());
    		            }else{
    		                option=$(document.createElement('option')).appendTo(select).val(this.href).html($(this).html());
    		            }  
    		            
    		            $("select small").parent().hide();
    		        }
    		    });
    		    select.addClass('form-control');
    		    select.change(function(){
    		        window.location.href = this.value;
    		    })
    		});
	    }
	} else {
		$(".account-nav-box").find("select").remove();
		$('.account-nav-box .account-navigation').show();
	}
}


Mobile.bootstrapSelect = function() {
	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) ) {
		$('.selectpicker').selectpicker('mobile');
	}
}

//initialize all mobile functions
$(document).ready(function() {
	Mobile.init();
	Mobile.windowWidth = $(window).width();
});

// register the functionality that needs to be re-evaluated when the 
// screen size changes (e.g. switch from portrait to landscape)
$(window).on('resize', function(e){
	if ($(window).width() != Mobile.windowWidth) {
		Mobile.windowWidth = $(window).width();

		Mobile.accountNavigation();
		Mobile.modifyDomStructure();
	}
});

