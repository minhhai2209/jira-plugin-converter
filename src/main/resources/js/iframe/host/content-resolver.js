(function($, UiParams, context){
    "use strict";

    function getContentUrl(pluginKey, moduleKey){
        return AJS.contextPath() + "/plugins/servlet/" + encodeURIComponent(pluginKey) + "/page/" + encodeURIComponent(moduleKey);
    }

    var contentResolver = {
        resolveByUrl: function(url) {
            var promise = jQuery.Deferred(function(defer){
                defer.resolve(url);
            }).promise();

            return promise;
        },
        resolveByParameters: function(params) {
            return $.ajax(getContentUrl(params.addonKey, params.moduleKey), {
                dataType: "html",
                data: {
                    "ui-params": UiParams.encode(params.uiParams),
                    "plugin-key": params.addonKey,
                    "product-context": JSON.stringify(params.productContext),
                    "key": params.moduleKey,
                    "width": params.width || "100%",
                    "height": params.height || "100%",
                    "classifier": params.classifier || "raw"
                }
            });
        } 
    };

    context._AP.contentResolver = contentResolver;


}(AJS.$, _AP.uiParams, this));