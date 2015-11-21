;
/**
 * Methods for showing the status of a connect-addon (loading, time'd-out etc)
 */
(function(define){
    "use strict";

    define("ac/history/main", ["connect-host"], function (_AP) {

        var lastAdded,
            anchorPrefix = "!",
            Uri = _AP._uriHelper;

        function stripPrefix (text) {
            if(text === undefined || text === null){
                return "";
            }
            return text.toString().replace(new RegExp("^" + anchorPrefix), "");
        }

        function addPrefix (text) {
            if(text === undefined || text === null){
                throw "You must supply text to prefix";
            }

            return anchorPrefix + stripPrefix(text);
        }

        function changeState (anchor, replace) {
            var currentUrlObj = new Uri.init(window.location.href),
            newUrlObj = new Uri.init(window.location.href);

            newUrlObj.anchor(addPrefix(anchor));

            // If the url has changed.
            if(newUrlObj.anchor() !== currentUrlObj.anchor()){
                lastAdded = newUrlObj.anchor();
                // If it was replaceState or pushState?
                if(replace){
                    window.location.replace("#" + newUrlObj.anchor());
                } else {
                    window.location.assign("#" + newUrlObj.anchor());
                }
                return newUrlObj.anchor();
            }
        }

        function pushState (url) {
            changeState(url);
        }

        function replaceState (url) {
            changeState(url, true);
        }

        function go (delta) {
            history.go(delta);
        }

        function hashChange (event, historyMessage) {
            var newUrlObj = new Uri.init(event.newURL);
            var oldUrlObj = new Uri.init(event.oldURL);
            if( ( newUrlObj.anchor() !== oldUrlObj.anchor() ) && // if the url has changed
                ( lastAdded !== newUrlObj.anchor() ) //  and it's not the page we just pushed.
             ){
                historyMessage(sanitizeHashChangeEvent(event));
            }
            lastAdded = null;
        }

        function sanitizeHashChangeEvent (e) {
            return {
                newURL: stripPrefix(new Uri.init(e.newURL).anchor()),
                oldURL: stripPrefix(new Uri.init(e.oldURL).anchor())
            };
        }

        function getState () {
            var hostWindowUrl = new Uri.init(window.location.href),
            anchor = stripPrefix(hostWindowUrl.anchor());
            return anchor;
        }

        return {
            pushState: pushState,
            replaceState: replaceState,
            go: go,
            hashChange: hashChange,
            getState: getState
        };

    });

})(define);
(function(define, AJS, $){
    "use strict";
    define('ac/history', ['ac/history/main', 'connect-host'], function(cHistory, _AP){

        _AP.extend(function(){
            return {
                init: function (state, xdm) {
                    if(state.uiParams.isGeneral){
                        // register for url hash changes to invoking history.popstate callbacks.
                        $(window).on("hashchange", function(e){
                            cHistory.hashChange(e.originalEvent, xdm.historyMessage);
                        });
                    }
                },
                internals: {
                    historyPushState: function (url) {
                        if(this.uiParams.isGeneral){
                            return cHistory.pushState(url);
                        } else {
                            AJS.log("History is only available to page modules");
                        }
                    },
                    historyReplaceState: function (url) {
                        if(this.uiParams.isGeneral){
                            return cHistory.replaceState(url);
                        } else {
                            AJS.log("History is only available to page modules");
                        }
                    },
                    historyGo: function (delta) {
                        if(this.uiParams.isGeneral){
                            return cHistory.go(delta);
                        } else {
                            AJS.log("History is only available to page modules");
                        }
                    }
                },
                stubs: ["historyMessage"]
            };
        });

    });
})(define, AJS, AJS.$);
