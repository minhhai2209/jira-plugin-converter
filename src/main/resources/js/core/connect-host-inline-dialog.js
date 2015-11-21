;
(function(define, $){
    "use strict";
    define("ac/inline-dialog", ["connect-host"], function (connect) {

        function getInlineDialog($content){
            return $content.closest('.contents').data('inlineDialog');
        }

        function showInlineDialog($content) {
            getInlineDialog($content).show();
        }

        function resizeInlineDialog($content, width, height) {
            $content.closest('.contents').css({width: width, height: height});
            refreshInlineDialog($content);
        }

        function refreshInlineDialog($content) {
            getInlineDialog($content).refresh();
        }

        function hideInlineDialog($content){
            getInlineDialog($content).hide();
        }

        connect.extend(function () {
            return {
                init: function(state, xdm){
                    if(xdm.uiParams.isInlineDialog){
                        $(xdm.iframe).closest(".ap-container").on("resized", function(e, dimensions){
                            resizeInlineDialog($(xdm.iframe), dimensions.width, dimensions.height);
                        });
                    }
                },
                internals: {
                    hideInlineDialog: function(){
                        hideInlineDialog($(this.iframe));
                    }
                }
            };
        });

    });

})(define, AJS.$);
(function(define, AJS, $){
    "use strict";
    define("ac/inline-dialog/simple", ["connect-host"], function(connect) {

        return function (contentUrl, options) {
            var $inlineDialog;

            // Find the web-item that was clicked, we'll be needing its ID.
            if (!options.bindTo || !options.bindTo.jquery) {
                return;
            }

            var webItem = options.bindTo.hasClass("ap-inline-dialog") ? options.bindTo : options.bindTo.closest(".ap-inline-dialog");
            var itemId = webItem.attr("id");
            if (!itemId) {
                return;
            }

            var displayInlineDialog = function(content, trigger, showInlineDialog) {

                trigger = $(trigger); // sometimes it's not jQuery. Lets make it jQuery.
                content.data('inlineDialog', $inlineDialog);
                var pluginKey = connect.webItemHelper.getWebItemPluginKey(trigger),
                    moduleKey = connect.webItemHelper.getWebItemModuleKey(trigger),
                    promise = window._AP.contentResolver.resolveByParameters({
                        addonKey: pluginKey,
                        moduleKey: moduleKey,
                        isInlineDialog: true,
                        productContext: options.productContext,
                        uiParams: {
                            isInlineDialog: true
                        }
                    });

                promise.done(function(data) {
                    content.empty().append(data);
                    // if target options contain width and height. set it.
                    if(options.width || options.height){
                        content.css({width: options.width, height: options.height});
                    }
                })
                .fail(function(xhr, status, ex) {
                    var title = $("<p class='title' />").text("Unable to load add-on content. Please try again later.");
                    content.html("<div class='aui-message error ap-aui-message'></div>");
                    content.find(".error").append(title);
                    var msg = status + (ex ? ": " + ex.toString() : "");
                    content.find(".error").text(msg);
                    AJS.log(msg);
                })
                .always(function(){
                    showInlineDialog();
                });

            };

            var dialogElementIdentifier = "ap-inline-dialog-content-" + itemId;

            $inlineDialog = $(document.getElementById("inline-dialog-" + dialogElementIdentifier));

            if($inlineDialog.length !== 0){
                $inlineDialog.remove();
            }

            //Create the AUI inline dialog with a unique ID.
            $inlineDialog = AJS.InlineDialog(
                options.bindTo,
                //assign unique id to inline Dialog
                dialogElementIdentifier,
                displayInlineDialog,
                options
            );

            return {
                id: $inlineDialog.attr('id'),
                show: function() {
                    $inlineDialog.show();
                },
                hide: function() {
                    $inlineDialog.hide();
                }
            };

        };

    });
})(define, AJS, AJS.$);
;
AJS.toInit(function ($) {
    (function(require, AJS){
        "use strict";
            require(["ac/inline-dialog/simple", "connect-host"], function(simpleInlineDialog, _AP) {

            var inlineDialogTrigger = '.ap-inline-dialog';
            var action = "click mouseover mouseout",
                callback = function(href, options, eventType){
                    var webItemOptions = _AP.webItemHelper.getOptionsForWebItem(options.bindTo);
                    $.extend(options, webItemOptions);
                    if(options.onHover !== "true" && eventType !== 'click'){
                        return;
                    }

                    // don't repeatedly open if already visible as dozens of mouse-over events are fired in quick succession
                    if (options.onHover === true && options.bindTo.hasClass('active')) {
                        return;
                    }
                    simpleInlineDialog(href, options).show();
                };
            _AP.webItemHelper.eventHandler(action, inlineDialogTrigger, callback);
        });
    })(require, AJS);
});

