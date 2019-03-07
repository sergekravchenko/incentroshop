/*
	handle Quick Order Functionality
 */

var QuickOrder = {};
// var stopTyping = 0;

QuickOrder.init = function() {
	QuickOrder.onAddMoreRows();
	QuickOrder.onRemoveRow();
	QuickOrder.quickOrderAutoSuggestion();
	QuickOrder.addToCartValidation();
	QuickOrder.submitFormByPressingEnter();
}

/**
 * This method is used to add more row in quick order form
 */

QuickOrder.onAddMoreRows = function() {
	$(document).on('click','.add-more-rows a', function() {
		// create clone of already exist of rows
		var newrows = $('.blank-quick-order-rows').clone(true);
		$(newrows).find('input').attr('value', '');
		// Blank all the new rows input field
		$('.quick-order-rows-container').append($(newrows).html());
		$('.quick-order-rows-container').attr('data-row-count', $('.quick-order-rows-container .list-item-row').length);
		// Update all the fields name
		updateFieldsName();
	});
}

/**
 * This method is used to remove row in quick order form
 */

QuickOrder.onRemoveRow = function() {
	$(document).on('click', '.quick-order-rows-container .remove-row', function() {
		$(this).closest('.list-item').parent().remove();
		updateFieldsName();
	});
}

/**
 * Update name and id of all the field after add/remove of rows
 */
function updateFieldsName() {
	var rowcount = 1;
	$('.quick-order-rows-container .list-item-row').each(function() {
		var self = $(this);
		// Updates id and name of field
		self.find('.search-productId').attr('name', 'SKU_' + rowcount);
		self.find('.color-variation').attr({
			'name' : 'colorvariation_' + rowcount,
			'id' : 'colorvariation_' + rowcount
		});
		self.find('.size-variation').attr({
			'name' : 'sizevariation_' + rowcount,
			'id' : 'sizevariation_' + rowcount
		});
		self.find('.quick-order-quantity').attr('name','Quantity_' + rowcount);
		self.find("[id^='availability-SKU_']").attr('id','availability-SKU_' + rowcount);
		rowcount++;
	});
}

/**
 * quick Order Auto complete: Auto suggest
 */
QuickOrder.quickOrderAutoSuggestion = function() {
	// Quick order suggest search start on press key in ProductId field
	$(document).on('keyup', 'input.search-productId',function(e) {
		$(this)
			.closest('list-item-row')
			.find('.form-control.color-variation')
			.html("<option>"+ $(this).closest('.list-item-row').find('.form-control.color-variation').attr('default-value')	+ "</option>").attr("disabled","");
		$(this)
			.closest('.list-item-row')
			.find('.form-control.size-variation')
			.html("<option>"+ $(this).closest('.list-item-row').find('.form-control.size-variation').attr('default-value')+ "</option>").attr("disabled","");
		if ($(this).val().length > 3) {
			// Select suggested search result items by Keyboard keys
			if (e.keyCode == 40 || e.keyCode == 38) {
				var suggestionList = $(this).siblings(
						'ul.search-suggest-results').children(
						'li');
				// Move focus on suggested search result list by press down arrow key
				if (e.keyCode == 40) {
					var noSuggestionSeleted = true;
					// If last list item selected then nothing happen on press down arrow key
					if (suggestionList.last().hasClass('active-suggestion')) {
						return false;
					}
					if (suggestionList.length > 0 && noSuggestionSeleted == true) {
						$.each(suggestionList, function(idx, value) {
							if ($(this).hasClass('active-suggestion') && (idx < suggestionList.length - 1)) {
								$(this).removeClass('active-suggestion');
								// Select nextlist item from auto suggested search list
								$(this).next().addClass('active-suggestion');
								noSuggestionSeleted = false;
								return false;
							}
						});
						// Select first auto suggested list item
						if (noSuggestionSeleted == true) {
							suggestionList.first().addClass('active-suggestion');
						}
					}
				}
				// Move focus on suggested search result list by press up arrow key
				else if (e.keyCode == 38) {
					// If First list item selected then nothing happen on press up arrow key
					if (suggestionList.first().hasClass('active-suggestion')) {
						return false;
					}
					if (suggestionList.length > 0) {
						$.each(suggestionList, function(idx, value) {
							if ($(this).hasClass('active-suggestion')) {
								$(this).removeClass('active-suggestion');
								// Select previous list item from auto suggested search list
								$(this).prev().addClass('active-suggestion');
								return false;
							}
						});
					}
				}

			} else {

				autoSuggestQuickOrder($(this));
			}
		} else {
			// Hide Quick order suggestion block when no letter exist in search field
			$('.quickorder-search-container').removeClass('populated-suggestion');
			$(this).siblings('ul.search-suggest-results').empty().hide();
		}
	});
	
	// Quick order suggest search item select by enter or tab key
	$(document).on('keydown','input.search-productId', function(e) {
		var currentField = $(this);
		var suggestionList = $(this).siblings('ul.search-suggest-results').children('li');
		
		// Move selected suggested list item into search field by tab or enter key
		if (e.keyCode == 9 || e.keyCode == 13) {
			e.preventDefault();
			if ($(suggestionList).length > 0) {
				$(suggestionList).each(function() {
					if ($(this).hasClass('active-suggestion')) {
						currentField.val($(this).find('button').attr('data-search-result'));
						$(this).find('button').click();
						currentField.siblings('ul.search-suggest-results').empty().hide();
						return false;
					}
				});
			}
		}
	});
}

/**
 * Display Quick order suggest search list
 */
function autoSuggestQuickOrder(SearchField) {

	var searchTerm = $.trim($(SearchField).val());
	var ajaxurl = $(SearchField).attr('data-suggesturl') + "&SearchTerm=" + searchTerm;
	var listPlaceholder = $(SearchField).nextAll('ul');
	
	$.ajax({
		url : ajaxurl,
		type : 'post',
		datatype : 'text/html',
		success : function(data) {
			// Show Quick order suggestion list item block
			if ($(data).find('li').length > 0) {
				$(listPlaceholder).html($(data).filter('.suggest-results-list').html()).fadeIn(500);
				$('.quickorder-search-container').addClass('populated-suggestion');
			} else {
				// Hide Quick order suggestion block when suggestion result are zero
				$('.quickorder-search-container').removeClass('populated-suggestion');
				$(listPlaceholder).empty().fadeOut(500);
			}
		}
	});
}

/**
* Click on Quick order suggest search list item
*/
$(document).on("click", "ul.search-suggest-results li button", function() {
	var currentRow = $(this).closest('.list-item-row');
	currentRow.find('input.search-productId').val($(this).attr('data-search-result'));
	currentRow.find('input.quick-order-quantity ').val($(this).attr('min-order-qty'));
	var jsonproductid = $(this).attr('jsonproductid');
	$(this).closest('.list-item-row').find('#jsonproductid').val(jsonproductid);
	if ($(this).attr('ismaster') == 'true') {
		currentRow.find('.form-control.color-variation').html($(this).closest('li').find('.color-variation').html()).prop('disabled', false);
		currentRow.find('.form-control.size-variation').html($(this).closest('li').find('.size-variation').html()).prop('disabled', false);
	}
	/* remove error messages */
	currentRow.find('.form-group').removeClass('has-error');
	currentRow.find('.help-block').html('');
	currentRow.find('.search-suggest-results').empty().hide();
});

/**
 * Check whether product exists against the SKU
 */

QuickOrder.addToCartValidation = function() {

	$('.js-quickorder-productvalidation').on('click', function(e) {
		e.preventDefault();
		var $this = $(this);
		var $form = $(this).closest('form');
		var productids = $form.find('.search-productId');
		var productskus = "";

		$.each(productids, function(i, item) { // Loop through all variation attributes
			var sku = $(this).val();
			var erromessageplaceholderid = $(this).attr('name');
			sku = $.trim(sku);
			
			if (sku !== '') {
				productskus = productskus + "availability-" + erromessageplaceholderid + "," + sku + ";";
			}
		});

		var targeturl = $('#data-checkSKU-URL').attr('target-url') + "?SKU=" + productskus;
		productskus == $.trim(productskus);
		var submit = false;
		if (productskus !== '') {

			submit = true;
			$.ajax({
				url : targeturl,
				contentType : 'application/json; charset=utf-8',
				dataType : "json",
				type : 'get',
				success : function(data) {
					var productsstatus = data.ProductsStatus;
					$.each(productsstatus, function(i, productstatus) {
						var status = productstatus['Status'];
						var errorid = "#" + productstatus['ErrorPlaceHolderID'];
						if (status != 'true') {
							submit = false;
							$(errorid).html(status);
							$(errorid).closest('.form-group').addClass(
									'has-error');
						} else {
							$(errorid).html("");
							$(errorid).closest('.form-group')
									.removeClass('has-error');
						}
					});
					if (submit) {
						$this.off('click').click(); 	/* submit - also if bootstrap validation is on */
					}
				}
			});
		}
		else {
			$(".alert-formempty").removeClass("hide");
		}
		return false;
	});
}

/**
 * This method is used to submit the form by pressing enter on quantity field - submit the next submit button
 */

QuickOrder.submitFormByPressingEnter = function() {
	$('.quick-order-rows-container .quantity').on('keydown', function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
			
			$(this).closest(".quick-order-rows-container").find('button[type="submit"]').click();
		}
	});
}

/* init quick order functionality */
$(document).ready(function() {
	QuickOrder.init();
});