;
(function(define, $){
    "use strict";
    define("ac/dialog/button", [], function() {

        function button(options){
            this.$el = $('<button />')
                .text(options.text)
                .addClass('aui-button aui-button-' + options.type)
                .addClass(options.additionalClasses);

            this.isEnabled = function(){
                return !(this.$el.attr('aria-disabled') === "true");
            };

            this.setEnabled = function(enabled){
                //cannot disable a noDisable button
                if(options.noDisable === true){
                    return false;
                }
                this.$el.attr('aria-disabled', !enabled);
                return true;
            };

            this.setEnabled(true);

            this.click = function(listener){
                if (listener) {
                    this.$el.unbind("ra.dialog.click");
                    this.$el.bind("ra.dialog.click", listener);
                } else {
                    this.dispatch(true);
                }
            };

            this.dispatch = function (result) {
                var name = result ? "done" : "fail";
                options.actions && options.actions[name] && options.actions[name]();
            };

            this.setText = function(text){
                if(text){
                    this.$el.text(text);
                }
            };

        }

        return {
            submit: function(actions){
                return new button({
                    type: 'primary',
                    text: 'Submit',
                    additionalClasses: 'ap-dialog-submit',
                    actions: actions
                });
            },
            cancel: function(actions){
                return new button({
                    type: 'link',
                    text: 'Cancel',
                    noDisable: true,
                    additionalClasses: 'ap-dialog-cancel',
                    actions: actions
                });
            }
        };

    });
})(define, AJS.$);

;
(function(define, require, AJS, $){
    "use strict";
    define("ac/dialog", ["connect-host", "ac/dialog/button"], function(connect, dialogButton) {

        var $global = $(window);
        var idSeq = 0;
        var $nexus;
        var dialog;
        var dialogId;

        var buttons = {
            submit: dialogButton.submit({
                done: closeDialog
            }),
            cancel: dialogButton.cancel({
                done: closeDialog
            })
        };

        var keyPressListener = function(e){
            if(e.keyCode === 27 && dialog && dialog.hide){
                dialog.hide();
                $(document).unbind("keydown", keyPressListener);
            }
        };

        function createDialogElement(options, $nexus, chromeless){
            var $el,
            extraClasses = ['ap-aui-dialog2'];

            if(chromeless){
                extraClasses.push('ap-aui-dialog2-chromeless');
            }

            $el = $(aui.dialog.dialog2({
                id: options.id,
                titleText: options.header,
                titleId: options.titleId,
                size: options.size,
                extraClasses: extraClasses,
                removeOnHide: true,
                footerActionContent: true,
                modal: true
            }));

            if(chromeless){
                $el.find('header, footer').remove();
            } else {
                buttons.submit.setText(options.submitText);
                buttons.cancel.setText(options.cancelText);
                //soy templates don't support sending objects, so make the template and bind them.
                $el.find('.aui-dialog2-footer-actions').empty().append(buttons.submit.$el, buttons.cancel.$el);
            }

            $el.find('.aui-dialog2-content').append($nexus);
            $nexus.data('ra.dialog.buttons', buttons);

            function handler(button) {
                // ignore clicks on disabled links
                if(button.isEnabled()){
                    button.$el.trigger("ra.dialog.click", button.dispatch);
                }
            }

            $.each(buttons, function(i, button) {
                button.$el.click(function(){
                    handler(button);
                });
            });

            return $el;
        }

        function displayDialogContent($container, options){
            $container.append('<div id="embedded-' + options.ns + '" class="ap-dialog-container ap-content" />');
        }


        function parseDimension(value, viewport) {
            if (typeof value === "string") {
                var percent = value.indexOf("%") === value.length - 1;
                value = parseInt(value, 10);
                if (percent) value = value / 100 * viewport;
            }
            return value;
        }

        function closeDialog() {
            if ($nexus) {
                // Signal the XdmRpc for the dialog's iframe to clean up
                $nexus.trigger("ra.iframe.destroy")
                .removeData("ra.dialog.buttons")
                .unbind();
                // Clear the nexus handle to allow subsequent dialogs to open
                $nexus = null;
            }
            dialog.hide();
        }

        return {
            id: dialogId,
            getButton: function(name){
                var buttons = $nexus ? $nexus.data('ra.dialog.buttons') : null;
                return (name) && (buttons) ? buttons[name] : buttons;
            },

            /**
            * Constructs a new AUI dialog. The dialog has a single content panel containing a single iframe.
            * The iframe's content is either created by loading [options.src] as the iframe url. Or fetching the content from the server by add-on key + module key.
            *
            * @param {Object} options Options to configure the behaviour and appearance of the dialog.
            * @param {String} [options.header="Remotable Plugins Dialog Title"]  Dialog header.
            * @param {String} [options.headerClass="ap-dialog-header"] CSS class to apply to dialog header.
            * @param {String|Number} [options.width="50%"] width of the dialog, expressed as either absolute pixels (eg 800) or percent (eg 50%)
            * @param {String|Number} [options.height="50%"] height of the dialog, expressed as either absolute pixels (eg 600) or percent (eg 50%)
            * @param {String} [options.id] ID attribute to assign to the dialog. Default to "ap-dialog-n" where n is an autoincrementing id.
            */
            create: function(options, showLoadingIndicator) {

                var defaultOptions = {
                        // These options really _should_ be provided by the caller, or else the dialog is pretty pointless
                        width: "50%",
                        height: "50%"
                    },
                    dialogId = options.id || "ap-dialog-" + (idSeq += 1),
                    mergedOptions = $.extend({id: dialogId}, defaultOptions, options, {dlg: 1}),
                    dialogElement;

                // patch for an old workaround where people would make 100% height / width dialogs.
                if(mergedOptions.width === "100%" && mergedOptions.height === "100%"){
                    mergedOptions.size = "maximum";
                }

                mergedOptions.w = parseDimension(mergedOptions.width, $global.width());
                mergedOptions.h = parseDimension(mergedOptions.height, $global.height());

                $nexus = $("<div />").addClass("ap-servlet-placeholder ap-container").attr('id', 'ap-' + options.ns)
                .bind("ra.dialog.close", closeDialog);

                if(options.chrome){
                    dialogElement = createDialogElement(mergedOptions, $nexus);

                } else {
                    dialogElement = createDialogElement(mergedOptions, $nexus, true);
                }

                if(options.size){
                    mergedOptions.w = "100%";
                    mergedOptions.h = "100%";
                } else {
                    AJS.layer(dialogElement).changeSize(mergedOptions.w, mergedOptions.h);
                    dialogElement.removeClass('aui-dialog2-medium'); // this class has a min-height so must be removed.
                }

                dialog = AJS.dialog2(dialogElement);
                dialog.on("hide", closeDialog);
                // ESC key closes the dialog
                $(document).on("keydown", keyPressListener);

                $.each(buttons, function(name, button) {
                    button.click(function () {
                        button.dispatch(true);
                    });
                });

                displayDialogContent($nexus, mergedOptions);

                if(showLoadingIndicator !== false){
                    $nexus.append(connect._statusHelper.createStatusMessages());
                }

                //difference between a webitem and opening from js.
                if(options.src){
                    _AP.create(mergedOptions);
                }

                // give the dialog iframe focus so it can capture keypress events, etc.
                // the 'iframe' selector needs to be specified, otherwise Firefox won't focus the iframe
                dialogElement.on('ra.iframe.create', 'iframe', function () {
                    this.focus();
                });

                dialog.show();

            },
            close: closeDialog
        };

    });
})(define, require, AJS, AJS.$);

AJS.toInit(function ($) {

    (function(require, AJS){
        if(typeof window._AP !== "undefined"){
            //_AP.dialog global fallback.
            require(['ac/dialog'], function(dialog){
                _AP.Dialog = dialog;
            });
        }
    })(require, AJS);
});

(function(define, $){
    "use strict";
    define("ac/dialog/dialog-factory", ["ac/dialog"], function(dialog) {

        //might rename this, it opens a dialog by first working out the url (used for javascript opening a dialog).
        /**
        * opens a dialog by sending the add-on and module keys back to the server for signing.
        * Used by dialog-pages, confluence macros and opening from javascript.
        * @param {Object} options for passing to AP.create
        * @param {Object} dialog options (width, height, etc)
        * @param {String} productContextJson pass context back to the server
        */
        return function(options, dialogOptions, productContext) {
            var promise,
            container,
            uiParams = $.extend({isDialog: 1}, options.uiParams);

            dialog.create({
                id: options.id,
                ns: options.moduleKey || options.key,
                chrome: dialogOptions.chrome || options.chrome,
                header: dialogOptions.header,
                width: dialogOptions.width,
                height: dialogOptions.height,
                size: dialogOptions.size,
                submitText: dialogOptions.submitText,
                cancelText: dialogOptions.cancelText
            }, false);

            container = $('.ap-dialog-container');
            if(options.url){
                throw new Error('Cannot retrieve dialog content by URL');
            }

            promise = window._AP.contentResolver.resolveByParameters({
                addonKey: options.key,
                moduleKey: options.moduleKey,
                productContext: productContext,
                uiParams: uiParams
            });

            promise
                .done(function(data) {
                    var dialogHtml = $(data);
                    dialogHtml.addClass('ap-dialog-container');
                    container.replaceWith(dialogHtml);
                })
                .fail(function(xhr, status, ex) {
                    var title = $("<p class='title' />").text("Unable to load add-on content. Please try again later.");
                    var msg = status + (ex ? ": " + ex.toString() : "");
                    container.html("<div class='aui-message error ap-aui-message'></div>");
                    container.find(".error").text(msg);
                    container.find(".error").prepend(title);
                    AJS.log(msg);
                });

            return dialog;
        };
    });
})(define, AJS.$);

(function(require, $){
    "use strict";
    require(["connect-host", "ac/dialog/dialog-factory", "ac/dialog"], function (connect, dialogFactory, dialogMain) {

        connect.extend(function () {
            return {
                stubs: ["dialogMessage"],
                init: function(state, xdm){
                    // fallback for old connect p2 plugin.
                    if(state.dlg === "1"){
                        xdm.uiParams.isDialog = true;
                    }

                    if(xdm.uiParams.isDialog){
                        var buttons = dialogMain.getButton();
                        if(buttons){
                            $.each(buttons, function(name, button) {
                                button.click(function (e, callback) {
                                    if(xdm.isActive() && xdm.buttonListenerBound){
                                        xdm.dialogMessage(name, callback);
                                    } else {
                                        callback(true);
                                    }
                                });
                            });
                        }
                    }
                },
                internals: {
                    dialogListenerBound: function(){
                        this.buttonListenerBound = true;
                    },
                    setDialogButtonEnabled: function (name, enabled) {
                        dialogMain.getButton(name).setEnabled(enabled);
                    },
                    isDialogButtonEnabled: function (name, callback) {
                        var button =  dialogMain.getButton(name);
                        callback(button ? button.isEnabled() : void 0);
                    },
                    createDialog: function (dialogOptions) {
                        var xdmOptions = {
                            key: this.addonKey
                        };

                        //open by key or url. This can be simplified when opening via url is removed.
                        if(dialogOptions.key) {
                            xdmOptions.moduleKey = dialogOptions.key;
                        } else if(dialogOptions.url) {
                            throw new Error('Cannot open dialog by URL, please use module key');
                        }

                        if($(".aui-dialog2 :visible").length !== 0) {
                            throw new Error('Cannot open dialog when a layer is already visible');
                        }

                        dialogFactory(xdmOptions, dialogOptions, this.productContext);

                    },
                    closeDialog: function() {
                        this.events.emit('ra.iframe.destroy');
                        dialogMain.close();
                    }
                }
            };
        });

    });
})(require, AJS.$);

/**
 * Binds all elements with the class "ap-dialog" to open dialogs.
 * TODO: document options
 */
AJS.toInit(function ($) {

    (function(require, AJS){
        "use strict";
        require(["ac/dialog", "ac/dialog/dialog-factory", "connect-host"], function(dialog, dialogFactory, connect) {

            var action = "click",
                selector = ".ap-dialog",
                callback = function(href, options){

                    var webItemOptions = connect.webItemHelper.getOptionsForWebItem(options.bindTo),
                    moduleKey = connect.webItemHelper.getWebItemModuleKey(options.bindTo),
                    addonKey = connect.webItemHelper.getWebItemPluginKey(options.bindTo);

                    $.extend(options, webItemOptions);

                    if (!options.ns) {
                        options.ns = moduleKey;
                    }
                    if(!options.container){
                        options.container = options.ns;
                    }

                    // webitem target options can sometimes be sent as strings.
                    if(typeof options.chrome === "string"){
                        options.chrome = (options.chrome.toLowerCase() === "false") ? false : true;
                    }

                    //default chrome to be true for backwards compatibility with webitems
                    if(options.chrome === undefined){
                      options.chrome = true;
                    }

                    dialogFactory({
                        key: addonKey,
                        moduleKey: moduleKey
                    }, options,
                    options.productContext);
                };

            connect.webItemHelper.eventHandler(action, selector, callback);
        });
    })(require, AJS);
});

