;
(function(define, $){
    "use strict";

    /**
     * A modified Header for the AUI Dialog when its size is set to 'maximum' (fullscreen).
     *
     * The header's design is based on the File Viewer (https://extranet.atlassian.com/display/ADG/File+viewer+1.0).
     */
    define("ac/dialog/header-controls", [], function() {

        // Note: This can use Soy if it eventually moves to AUI. dT
        var markup =
            '<div class="header-title-container" class="aui-item expanded">' +
                '<div>' +
                    '<span class="header-title"></span>' +
                '</div>' +
            '</div>' +
            '<div class="header-control-panel" class="aui-item"></div>';

        return {

            // This method is not intended for the public API - it's only in a separate JS file to split some code out
            // of main.js.
            create: function(options) {

                var $header = $(markup);

                // Using .text() here escapes any HTML in the header string.
                $header.find('.header-title').text(options.header || '');

                return {
                    $el: $header
                };
            }
        };

    });
})(define, AJS.$);
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

            this.isHidden = function(){                
                return (this.$el.css('display') === 'none');
            };

            this.setHidden = function(hide){
                //cannot disable a noHide button
                if(options.noHide === true){
                    return false;
                }
                if (hide) {
                    this.$el.hide();
                } else {
                    this.$el.show();
                }
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
            button: button,
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
                    noHide: true,
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
    define("ac/dialog", [
        "connect-host",
        "ac/dialog/button",
        "ac/dialog/header-controls"], function(
        connect,
        dialogButton,
        headerControls) {

        var $global = $(window);
        var idSeq = 0;

        // Stack of dialogs. The classic scenario is opening a full-screen editor dialog from a
        // smaller modal dialog. Stacking overlapping dialogs is not encouraged, design-wise.
        var dialogs = [];

        // References for the currently-active dialog, cached in this scope to avoid having to pass them
        // around from function to function.
        var $nexus;
        var dialog;
        var buttons;

        function createButtons() {
            return {
                submit: dialogButton.submit({
                    done: closeDialog
                }),
                cancel: dialogButton.cancel({
                    done: closeDialog
                })
            };
        }

        var keyPressListener = function(e){
            if(e.keyCode === 27 && dialog && dialog.hide){
                dialog.hide();
                $(document).unbind("keydown", keyPressListener);
            }
        };

        function createDialogElement(options){
            var $el,
            extraClasses = ['ap-aui-dialog2'];

            // Fullscreen dialogs always have chrome.
            var chromeless = options.size !== 'fullscreen' && !options.chrome;
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

                // TODO - once the API for pluggable buttons is supported we'll delegate to it here. dT
                // The buttonContainer will probably end up being the 'controlBar.$el' element.
                var $buttonContainer;

                if (options.size === 'fullscreen') {
                    // Replace the default AUI dialog header with the markup required for a File-Viewer-like L&F.

                    // The dialog itself needs an extra class so that the top and margin-top styles can be overridden.
                    $el.addClass('ap-header-controls');

                    var hc = headerControls.create(options);
                    var $container = $el.find('header');
                    $container.addClass('aui-group').empty().append(hc.$el);
                    $buttonContainer = $container.find('.header-control-panel');

                    buttons.submit.$el.addClass('aui-icon aui-icon-small aui-iconfont-success');
                    buttons.cancel.$el.addClass('aui-icon aui-icon-small aui-iconfont-close-dialog');
                }
                else {
                    //soy templates don't support sending objects, so make the template and bind them.
                    $buttonContainer = $el.find('.aui-dialog2-footer-actions');
                    $buttonContainer.empty();
                }

                $buttonContainer.append(buttons.submit.$el, buttons.cancel.$el);
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
            if (!dialog || dialogs.length === 0) {
                throw Error("Can't close dialog: no dialogs are currently open");
            }

            // Stop this callback being re-invoked from the hide binding when dialog.hide() is called below.
            if (dialog.isClosing) {
                return;
            }
            dialog.isClosing = true;

            // Unbind and unassign singletons.
            if ($nexus) {
                // Signal the XdmRpc for the dialog's iframe to clean up
                $nexus.trigger("ra.iframe.destroy")
                .removeData("ra.dialog.buttons")
                .unbind();
                // Clear the nexus handle to allow subsequent dialogs to open
                $nexus = null;
            }
            buttons = null;

            dialog.hide();

            var closedDialog = dialogs.pop();
            if (dialog !== closedDialog) {
                throw Error('The dialog being closed must be the last dialog to be opened.')
            }

            // The new class-level dialog var will be the next dialog in the stack.
            dialog = dialogs[dialogs.length - 1];
            if (dialog) {
                // Re-assign singletons.
                $nexus = dialog.$el.find('.ap-servlet-placeholder');
                buttons = $nexus.data('ra.dialog.buttons');
            }
        }

        return {
            _getActiveDialog: function () {
                return dialog;
            },
            isCloseOnEscape: function () {
                return $nexus && $nexus.data('ra.dialog.closeOnEscape');
            },
            getButton: function (name) {
                var buttons = $nexus && $nexus.data('ra.dialog.buttons') || {};
                return name ? buttons[name] : buttons;
            },
            createButton: function(name, options) {
                var button = new dialogButton.button({
                    type: 'secondary',
                    text: name,
                    additionalClasses: 'ap-dialog-custom-button'
                });

                dialog.$el.find('.aui-dialog2-footer-actions').prepend(button.$el);

                buttons[name] = button;

                button.$el.click(function() {
                    if (button.isEnabled()) {
                        button.$el.trigger("ra.dialog.click", button.dispatch);
                    }
                });

                return button;
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

                // We don't support multiple copies of the same dialog being open at the same time.
                var nexusId = 'ap-' + options.ns;
                // This is a workaround because just using $('#' + nexusId) doesn't work in unit tests. :/
                dialogs.forEach(function (dialog) {
                    if (dialog.$el.find('#' + nexusId).length > 0) {
                        throw new Error("Can't create dialog. A dialog is already open with namespace: " + options.ns);
                    }
                });

                var defaultOptions = {
                        // These options really _should_ be provided by the caller, or else the dialog is pretty pointless
                        width: "50%",
                        height: "50%",

                        // default values
                        closeOnEscape: true
                    },
                    dialogId = options.id || "ap-dialog-" + (idSeq += 1),
                    mergedOptions = $.extend({id: dialogId}, defaultOptions, options, {dlg: 1}),
                    $dialogEl;

                // patch for an old workaround where people would make 100% height / width dialogs.
                if(mergedOptions.width === "100%" && mergedOptions.height === "100%"){
                    mergedOptions.size = "maximum";
                }

                if (mergedOptions.size === 'maximum' &&
                    typeof mergedOptions.chrome === 'undefined') {
                    // ACJS-129 This default will be set to true in a future release and then, depending on design intention,
                    // it might be hard-coded to be true (with no opt-out by setting chrome:false in the dialog options)
                    mergedOptions.chrome = false;
                }

                // Assign the singleton $nexus and buttons vars.
                $nexus = $("<div />")
                                .addClass("ap-servlet-placeholder ap-container")
                                .attr('id', nexusId)
                                .bind("ra.dialog.close", closeDialog);

                buttons = createButtons();

                $dialogEl = createDialogElement(mergedOptions);
                $dialogEl.find('.aui-dialog2-content').append($nexus);

                // Set the mergedOptions.w and h properties in case they're needed in the _AP.create call below.
                // See iframe/host/create.js for how w and h are used.
                if (options.size) {
                    mergedOptions.w = "100%";
                    mergedOptions.h = "100%";
                } else {
                    mergedOptions.w = parseDimension(mergedOptions.width, $global.width());
                    mergedOptions.h = parseDimension(mergedOptions.height, $global.height());

                    AJS.layer($dialogEl).changeSize(mergedOptions.w, mergedOptions.h);
                    $dialogEl.removeClass('aui-dialog2-medium'); // this class has a min-height so must be removed.
                }

                dialog = AJS.dialog2($dialogEl);
                dialogs.push(dialog);
                dialog.on("hide", closeDialog);

                // store it here so the client side handler can also check this value
                $nexus.data('ra.dialog.closeOnEscape', mergedOptions.closeOnEscape);

                if(mergedOptions.closeOnEscape) {
                    // ESC key closes the dialog
                    $(document).on("keydown", function (event) {
                        keyPressListener(event);
                    });
                }

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
                $dialogEl.on('ra.iframe.create', 'iframe', function () {
                    this.focus();
                });

                dialog.show();

                return dialog;
            },
            close: closeDialog
        };

    });
})(define, require, AJS, AJS.$);

AJS.toInit(function ($) {

    (function (require, AJS) {
        if (typeof window._AP !== "undefined") {
            //_AP.dialog global fallback.
            require(['ac/dialog'], function (dialog) {
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
            uiParams = $.extend({isDialog: 1}, options.uiParams, {customData: dialogOptions.customData});

            var chrome = dialogOptions.chrome;
            if (typeof chrome === 'undefined') {
                chrome = options.chrome;
            }

            var createdDialog = dialog.create({
                id: options.id,
                ns: options.moduleKey || options.key,
                chrome: chrome,
                header: dialogOptions.header,
                width: dialogOptions.width,
                height: dialogOptions.height,
                size: dialogOptions.size,
                submitText: dialogOptions.submitText,
                cancelText: dialogOptions.cancelText,
                closeOnEscape: dialogOptions.closeOnEscape
            }, false);

            container = createdDialog.$el.find('.ap-dialog-container');
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

        function initializeButtonCallback(name, button) {
            // Assumes that button events are only triggered on the currently-active dialog.
            var xdm = dialogMain._getActiveDialog().xdm;
            button.click(function (e, callback) {
                if (xdm.isActive() && xdm.buttonListenerBound) {
                    xdm.dialogMessage(name, callback);
                } else {
                    callback(true);
                }
            });
        }

        connect.extend(function () {
            return {
                stubs: ["dialogMessage"],
                init: function(state, xdm){
                    // We cache the xdm in the active dialog so that it's available for button bindings.
                    dialogMain._getActiveDialog().xdm = xdm;

                    // fallback for old connect p2 plugin.
                    if(state.dlg === "1"){
                        xdm.uiParams.isDialog = true;
                    }

                    if (xdm.uiParams.isDialog) {
                        $.each(dialogMain.getButton(), function(name, button) {
                            initializeButtonCallback(name, button);
                        });
                    }
                },
                internals: {
                    dialogListenerBound: function(){
                        this.buttonListenerBound = true;
                    },
                    setDialogButtonEnabled: function (name, enabled) {
                        dialogMain.getButton(name).setEnabled(enabled);
                    },
                    setDialogButtonHidden: function (name, hidden) {
                        dialogMain.getButton(name).setHidden(hidden);
                    },                    
                    isDialogButtonEnabled: function (name, callback) {
                        var button =  dialogMain.getButton(name);
                        callback(button ? button.isEnabled() : void 0);
                    },
                    isDialogButtonHidden: function (name, callback) {
                        var button =  dialogMain.getButton(name);
                        callback(button ? button.isHidden() : void 0);
                    },                    
                    createButton: function(name, options) {
                        var button = dialogMain.createButton(name, options);
                        initializeButtonCallback(name, button);
                    },
                    isCloseOnEscape: function (callback) {
                        callback(dialogMain.isCloseOnEscape());
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
                    // ACJS-129 Keep chrome as opt-in for 'maximum' dialogs.
                    if(options.chrome === undefined && options.size !== 'maximum'){
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

