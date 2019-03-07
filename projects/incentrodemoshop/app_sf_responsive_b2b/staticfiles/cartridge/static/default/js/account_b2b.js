/**
 * AccountB2B holds B2B specific js logic for B2B My Account pages
 */
var AccountB2B = {};

/**
 * init AccountB2B specific js
 */
AccountB2B.init = function(){
    AccountB2B.cookieNavigation();
    AccountB2B.onToggleSelectedItem();
    AccountB2B.initDataTables();
}

/**
 * cookie based state for B2B account navigation
 */
AccountB2B.cookieNavigation = function(){
    $(document).ready(function(){
        const COOKIENAME = "activeAccountPanels";
        var $AccountNavPanels = $("#AccountNav .collapse");
        
        if (typeof(Cookies.getJSON(COOKIENAME)) == "undefined") {
            Cookies.set(COOKIENAME, 'collapsePurchase');
        }
        $AccountNavPanels.removeClass('in');
        
        var expandedItems = Cookies.getJSON(COOKIENAME);
        if (expandedItems instanceof Array) {
            $.each(expandedItems, function(index, value) {
                $("#" + value).addClass("in");
            });
        } else {
            $("#" + expandedItems).addClass("in");
        }
        
        $AccountNavPanels.on('shown.bs.collapse', function(e) {
            if ($(this).is(e.target)) {
                var expandedItems = Cookies.getJSON(COOKIENAME);
                if (!(expandedItems instanceof Array)) {
                    var expandedItem = expandedItems;
                    expandedItems = new Array();
                    expandedItems.push(expandedItem);
                }
                expandedItems.push(this.id);
                Cookies.set(COOKIENAME, expandedItems);
            }
        });
        
        $AccountNavPanels.on('hidden.bs.collapse', function (e) {
            if ($(this).is(e.target)) {
                var expandedItems = Cookies.getJSON(COOKIENAME);
                if (expandedItems instanceof Array) {
                    expandedItems.splice(expandedItems.indexOf(this.id),1);
                } else {
                    expandedItems = '';
                }
                Cookies.set(COOKIENAME, expandedItems);
            }
        });
    });
};

/**
 * Toggle checkbox selections in list
 */
AccountB2B.onToggleSelectedItem = function(){
    $(document).on('click', '[data-toggle-select]', function(event){
       event.preventDefault();
       var action = $(this).attr('data-toggle-select');
       var formSelectCheckboxes = $(this).closest('form').find('[name="SelectedObjectUUID"]');
       
       $(this).closest('form').find('[data-toggle-select]').hide();
       
       if(action == 'all') {
           formSelectCheckboxes.prop('checked', true);
           $(this).closest('form').find('[data-toggle-select="clear"]').show();
       }
       
       if(action == 'clear') {
           formSelectCheckboxes.prop('checked', false);
           $(this).closest('form').find('[data-toggle-select="all"]').show();
       }
       
    });
}

/**
 * Initialize jquery DataTable plugin
 */
AccountB2B.initDataTables = function(){
    $('[data-plugin-datatable]').each(function() {
        var options = $.extend(true, {
            "bLengthChange" : true,
            "bFilter": true, 
            "bInfo": true
        }, $.parseJSON($(this).attr('data-plugin-datatable')) );
        $(this).DataTable(options);
    });
}



/**
 * init AccountB2B specific js
 */
$(function(){
    AccountB2B.init();
});

