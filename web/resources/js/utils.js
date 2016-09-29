function isNotBlank(obj) {
    if (obj !== null && obj !== undefined && obj !== "") {
        return true;
    }
    return false;
}

function validateForm(formId, submitButton) {

    var form = document.getElementById(formId);
    if (form === null || !isNotBlank(form.outerHTML) || form.outerHTML.indexOf("<form") !== 0) {
        return;
    }

    var formElements = form.elements;
    if (formElements === null || formElements.length === 0) {
        return;
    }

    var i = 0;
    for (i; i < formElements.length; i++) {
        var element = formElements[i];
        if (element.outerHTML.indexOf("<input") === 0 && element.id.indexOf(formId) === 0 && element.value === "") {
            PF(submitButton).disable();
            return;
        }
    }
    PF(submitButton).enable();
}

function resetForm(formId, submitButton) {
    var form = document.getElementById(formId);
    if (form === null || !isNotBlank(form.outerHTML) || form.outerHTML.indexOf("<form") !== 0) {
        return;
    }
    
    var formElements = form.elements;
    if (formElements === null || formElements.length === 0) {
        return;
    }
    
    var i = 0;
    for (i; i < formElements.length; i++) {
        var element = formElements[i];
        
        if (element.outerHTML.indexOf("<input") === 0 && element.id.indexOf(formId) === 0 && element.value !== "") {
            element.value = ""; 
        }
        
        var fullElementId = element.id; 
        
        if(!isNotBlank(fullElementId) || fullElementId.indexOf(':') === -1){
            continue; 
        }
        
        if(fullElementId.indexOf("_input") !== -1){
            fullElementId = fullElementId.slice(0, fullElementId.indexOf("_input")); 
        }
        
        var elementId = fullElementId.slice(fullElementId.indexOf(':') + 1); 
        var messageElement = document.getElementById(formId + ":msg" + elementId.charAt(0).toUpperCase() + elementId.slice(1));
        
        if(messageElement === null){
            continue; 
        }
        messageElement.innerHTML = ""; 
    }
    PF(submitButton).disable();
}

jQuery(window).load(function () {
     jQuery(document).delegate("#gamesList td", "click", function (event) {
        var columnNumber = jQuery(this).index();
        var rowID = jQuery(this).parent().children()[0].innerHTML;//get index of clicked row
        jQuery("#tableCellValue").val(rowID + ":" + columnNumber); //set value in the inputtext
        jQuery("#tableCellValue").change(); //this will trigger the ajax listener
    });
});