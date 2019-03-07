/*
   Jquery Libraray to handle Order Functionality
 */ 

var OrderTemplate = {} ;
var OrderActions = {} ; // To handle order which are in pending, rejection and approval state

OrderTemplate.init = function()
{
	OrderTemplate.AddProductToOrderTemplate();
    OrderTemplate.RemoveOrderTemplate();
    OrderTemplate.OpenAddProductToQuickForm();
    OrderTemplate.MoveProductsToNewWishList();
    OrderTemplate.DeleteProductFromOrderTemplate();
    OrderTemplate.GetQuickView();
    OrderTemplate.emailWishList();
    OrderTemplate.onSubmitEmailWishListForm();
}

OrderActions.init = function(){
    OrderActions.onOrderApprove();
    OrderActions.onOrderReject();
}

/**
This method is used to check the validations on EditQuote page while saving and submitting the quote .
*/

OrderTemplate.MoveProductsToNewWishList = function() {
	
	var $doc = $(document.body);
	
	$doc.on("change",'[name="MoveWishlistItemForm_TargetWishlistID"]', function() {
		
		var selectedID = $(this).attr('id');
		var bootstrapValidator = $('#MoveWishListItemForm').data('bootstrapValidator');
		if(selectedID=='NewWishlistRadioButton') {
			$('#NewOrderTemplateName').val("");
			$('#NewOrderTemplateName').prop("disabled", false);
			bootstrapValidator.enableFieldValidators('MoveWishlistItemForm_NewWishlistName', true);
		}
		else {
			bootstrapValidator.enableFieldValidators('MoveWishlistItemForm_NewWishlistName', false);
			$('#NewOrderTemplateName').attr("disabled","");
		}
		
	});
	
	$doc.on("change",'[name="AddToWishlistForm_TargetWishlistID"]', function() {
		var selectedID = $(this).attr('id');
		if(selectedID=='NewWishlistRadioButton') {
			$('#NewOrderTemplateName').val("");
			$('#NewOrderTemplateName').prop("disabled", false);
		}
		else {
			$('#NewOrderTemplateName').attr("disabled","");
		}
		
	});
	
}


/**
 * This method opens the dialog to add products to quote and add products to order template
 */
OrderTemplate.OpenAddProductToQuickForm = function() {
	
    var $doc = $(document.body);
	$doc.on("click",'.add-to-quick-popup', function(e) { 
        e.preventDefault();
        
        var form = $(this).closest('form') ;
        var targetaction =  $(this).attr('data-wishlist-action');
        var param = $(form).serialize()  + "&isAjax=true";
        
        Dialog = new dialog();
        
        Dialog.options({
            formData: param
        });

        Dialog.create(targetaction, function() {});
        Dialog.open();      
  });
}


/**
This method add products to the selected order template and display the confirmation dialog .
*/

OrderTemplate.AddProductToOrderTemplate = function() {
	 
	var $doc = $(document.body);
	$doc.on("click",'.add-to-wishlist-confirmation-popup', function(e) {
		 e.preventDefault();
		 var form = $(this).closest('form');
		 var targetaction = $(form).attr('action');
		 var param = $(this).closest('form').serialize();
		 $.ajax({
	          url: targetaction,
	          type: 'post',
	          data:param ,
	          datatype: 'text/html',
	          success: function(data){
	             $('#quickmodalview').html(data);
	             $('#quickmodal').modal({show:true});
	          }           
	      });
		 
	});

	
}

/**
This method opens create order template dialog page  .
*/

OrderTemplate.GetQuickView = function() {
	
	$('.getquickmodal').on('click', function(e){
	    e.preventDefault();
	    var targetaction =  $(this).attr('data-url');
	    var qucikViewTitle =  $(this).attr('data-quick-title');
        $.ajax({
            url: targetaction,
            type: 'get',
            datatype: 'text/html',
            success: function(data){
                $('#quickmodalview').html(data);
                $('#quickmodalview').find('form').bootstrapValidator();
                $('#modalLabel').html(qucikViewTitle);
                $('#quickmodal').modal({show:true});
            }
        });
    });
}

/**
  This method opens remove order template confirmation dialog page .
 */

OrderTemplate.RemoveOrderTemplate = function() {
	
    $('.remove-order-template').on('click', function(e) {
        e.preventDefault();
        var actionUrl = $(this).attr('href');
        var qucikViewTitle = $(this).attr('data-quick-title');
        $.ajax({
            url: actionUrl,
            type: 'get',
            datatype: 'text/html',
            success: function(data) {
                $('#quickmodalview').html(data);
                $('#modalLabel').html(qucikViewTitle);
                $('#quickmodal').modal({
                    show: true
                });
            }
        });

    });
	    
}

/**
 * Approve order by Approver
 */
OrderActions.onOrderApprove = function(){
    $('.approve-pending-order').on('click', function(e){ 
        e.preventDefault();
        if (typeof SYNCHRONIZER_TOKEN_VALUE !== "undefined" && SYNCHRONIZER_TOKEN_VALUE != "") {
            $(this).attr("href", $(this).attr("href")+"&"+SYNCHRONIZER_TOKEN_NAME+"="+SYNCHRONIZER_TOKEN_VALUE);
        }
        var actionUrl =  $(this).attr('href');
        $.ajax({
            url: actionUrl,
            type: 'get',
            datatype: 'text/html',
            success: function(data){
               $('#orderAproveForm').html(data);
               $('#orderAproveModal').modal({show:true});
            }           
        });           
      
  });
}
/**
 * Remove product from order template
 */
OrderTemplate.DeleteProductFromOrderTemplate = function(){
    $('a.delete-ordertemplateproduct-popup').on('click', function(){
       $('#removetemplatemodal a.btn-primary').attr('href', $(this).data('href')); 
    });
}



/**
 * Reject order by Approver
 */
OrderActions.onOrderReject = function(){
    $('.reject-pending-order').on('click', function(e){ 
        e.preventDefault();
        var actionUrl =  $(this).attr('href');
        $.ajax({
            url: actionUrl,
            type: 'get',
            datatype: 'text/html',
            success: function(data){
               $('#orderRejectForm').html(data);
               $('#orderRejectModal').modal({show:true});
            }           
        });           
    });
}
/**
 * Confirm Rejection of order by Approver
 */
$(document).on("submit", ".rejectOrderConfirmation", function(e){
    e.preventDefault();
    var actionUrl =  $(this).attr('action');
    var param = $(this).serialize()+'&submitNew=submitNew';
    $.ajax({
        url: actionUrl,
        type: 'post',
        data: param,
        datatype: 'text/html',
        success: function(data){
           $('#orderRejectForm').html(data);
        }           
    });           
});

/**
 * Send your order template to your friend by email
 */
OrderTemplate.emailWishList = function(){
    $('.wishlist-email-to-friend').on('click', function(e){ 
        e.preventDefault();
        var actionUrl =  $(this).attr('href');
        $.ajax({
            url: actionUrl,
            type: 'get',
            datatype: 'text/html',
            success: function(data){
               $('#emailToFriend .modal-content').html(data);
               $('#emailToFriend').modal({show:true});
               $('#emailToFriend').find('form').bootstrapValidator();
            }           
        });           
    });
}

/**
 * Submit email order template to friend by Ajax and display thank you message in Popup
 */

OrderTemplate.onSubmitEmailWishListForm = function(){
    $(document).on('success.form.bv','.email-wishlist-form', function(event) {
        event.preventDefault();
        var param = $(this).serialize();
        $.ajax({
            url: $(this).attr('action'),
            type: 'post',
            data: param,
            success: function(data) 
            {
                $('#emailToFriend .modal-content').html(data);
            }
        });  
    });
}
$(document).ready(function(){
	OrderTemplate.init();
	OrderActions.init();
});