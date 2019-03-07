var DevStorefrontPages = {};

DevStorefrontPages.init = function() {
    DevStorefrontPages.initTestEmails();
}

DevStorefrontPages.initTestEmails = function() {
    $(document).on('click', 'button[data-sendmail-type]', function(ev){
        //alert('Hallo');
    });
}

$(document).ready(function(){
    DevStorefrontPages.init();
});