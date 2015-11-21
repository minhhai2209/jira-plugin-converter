;
(function(define){
    "use strict";
    define("ac/env", ['connect-host'], function (_AP) {

        var connectModuleData; // data sent from the velocity template

        _AP.extend(function () {
            return {
                init: function (state) {
                    connectModuleData = state;
                },
                internals: {
                    getLocation: function () {
                        return window.location.href;
                    }
                }
            };
        });

    });
})(define);
