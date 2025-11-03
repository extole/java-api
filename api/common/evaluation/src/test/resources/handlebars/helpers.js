Handlebars.registerHelper('helperMissing', function (options) { 
    var context = options.context.model();  
    if(typeof context.getVariableContext==='function'){  
           return context.getVariableContext().get(options.helperName, 'default');  
    } 
    return null; 
});
