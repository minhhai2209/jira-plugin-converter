AP.define("confluence", ["_dollar", "_rpc"],
    function ($, rpc) {
    "use strict";

    return rpc.extend(function (remote) {
        return {
            /**
            * A Confluence specific Javascript module which provides functions to interact with the macro editor.
            * @exports confluence
            */
            apis: {
                /**
                * Save a macro with data that can be accessed when viewing the confluence page.
                * @param {Object} macroParameters - data to be saved with the macro.
                * @param {String} macroBody - the macro body to be saved with the macro. If omitted, the existing body will remain untouched.
                * @noDemo
                * @example
                * AP.require('confluence', function(confluence){
                *   confluence.saveMacro({foo: 'bar'});
                * });
                *
                * AP.require('confluence', function(confluence){
                *   confluence.saveMacro({foo: 'bar'}, "a new macro body");
                * });
                */
                saveMacro: function (macroParameters, macroBody) {
                    remote.saveMacro(macroParameters, macroBody);
                },
                /**
                * Get the data saved in the saveMacro method.
                * @param {Function} callback - callback to be passed the macro data.
                * @noDemo
                * @example
                * AP.require('confluence', function(confluence){
                *   confluence.getMacroData(function(data){
                *       alert(data);
                *   });
                * });
                */
                getMacroData: function (callback) {
                    remote.getMacroData(callback);
                },

                /**
                * Get the body saved in the saveMacro method.
                * @param {Function} callback - callback to be passed the macro data.
                * @noDemo
                * @example
                * AP.require('confluence', function(confluence){
                *   confluence.getMacroBody(function(body){
                *       alert(body);
                *   });
                * });
                */
                getMacroBody: function (callback) {
                  remote.getMacroBody(callback);
                },

                /**
                * Closes the macro editor, if it is open.
                * This call does not save any modified parameters to the macro, and saveMacro should be called first if necessary.
                * @noDemo
                * @example
                * AP.require('confluence', function(confluence){
                *   confluence.closeMacroEditor();
                * });
                */
                closeMacroEditor: function () {
                    remote.closeMacroEditor();
                }
            },
            stubs: ["saveMacro", "getMacroData", "getMacroBody", "closeMacroEditor"]
        };
    });

});
