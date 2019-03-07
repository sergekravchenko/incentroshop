/**
 * CostCenter holds CostCenter specific js logic
 */
var CostCenter = {};

/**
 * init CostCenter specific js
 */
CostCenter.init = function(){
    CostCenter.onAddBuyerToCostCenter();
    CostCenter.onAddApproverToCostCenter();
}

/**
 * Modal dialog to add a buyer to the cost center 
 */
CostCenter.onAddBuyerToCostCenter = function(){
    $(document).on('click', '.open-general-costcenter-modal', function(event){
       event.preventDefault();
       var button = $(this);
       var url = $(this).attr('data-url');
       var modal = $('#general-costcenter-modal');
       $(modal).modal('hide');
       
       $.ajax({
           url : url,
           success : function(data){
               $(modal).find('.modal-body').html(data);
               $(modal).find('.modal-title').html(button.attr('data-quick-title'));
               $(modal).modal('show');
           }
       });
    });
}

/**
 * Modal dialog to add a approver to the cost center 
 */
CostCenter.onAddApproverToCostCenter = function(){
    $(document).on('click', '.open-general-costcenter-modal', function(event){
       event.preventDefault();
       var button = $(this);
       var url = $(this).attr('data-url');
       var modal = $('#general-costcenter-modal');
       $(modal).modal('hide');
       
       $.ajax({
           url : url,
           success : function(data){
               $(modal).find('.modal-body').html(data);
               $(modal).find('.modal-title').html(button.attr('data-quick-title'));
               $(modal).modal('show');
           }
       });
    });
}


/*
 * init CostCenter specific js
 */
$(function(){
   
    CostCenter.init();

   
});


/**
 * Modal dialog to create a new cost center 
 */
$(document).on('click', '.open-costcenter-modal', function(event){
   event.preventDefault();
   var button = $(this);
   var url;
   if ($(this).attr('href')) {
      url = $(this).attr('href');
   }
   if ($(this).attr('data-url')) {
      url = $(this).attr('data-url');
   }
   var modal = $('#general-costcenter-modal');
   $(modal).modal('hide');
   
   $.ajax({
       url : url,
       success : function(data){
           $(modal).find('.modal-body').html(data);
           $(modal).find('.modal-title').html(button.attr('data-quick-title'));
           $(modal).modal('show');
           
           var form = $(modal).find('form');
           form.bootstrapValidator();
           
       }
   });
});
