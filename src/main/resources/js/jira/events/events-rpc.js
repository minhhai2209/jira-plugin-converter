(function($, require){
    "use strict";
    require(["ac/jira/events", "connect-host"], function(jiraEvents, _AP){
        _AP.extend(function () {
            return {
                internals: {
                    triggerJiraEvent: function () {
                        jiraEvents.refreshIssuePage();
                    }
                }
            };
        });
    });

})(AJS.$, require);
