(function($, require){
    "use strict";
    require(["ac/jira/issue", "connect-host"], function(jiraIssue, _AP){
        _AP.extend(function () {
            return {
                internals: {
                    openCreateIssueDialog: function (fields) {
                        jiraIssue.createIssueDialog(
                            this.triggerIssueCreateSubmit,
                            fields
                        );
                    }
                },
                stubs: ['triggerIssueCreateSubmit']
            };
        });
    });

})(AJS.$, require);
