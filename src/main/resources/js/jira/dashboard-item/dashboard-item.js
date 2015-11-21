(function($, define){
    "use strict";

    require(['connect-host'], function(_AP) {

        _AP.extend(function () {
            return {
                init: function (state, xdm) {
                    // register handle for the edit button in jira (if needed)
                    $(xdm.iframe).on('dashboardItemEdit', function(){
                        xdm.triggerDashboardItemEdit();
                    });
                },
                internals: {
                    setDashboardItemTitle: function(title) {
                        // TODO this should be replaced with a valid gadget API
                        var dashboardItemTitle = $($(this.iframe).parents('.gadget-container').find('h3.dashboard-item-title')[0]);
                        dashboardItemTitle.text(title);
                    },
                    isDashboardItemEditable: function(callback) {
                        var configureOption = $(this.iframe).parents('.gadget-container').find('li.configure');
                        callback(configureOption.length != 0);
                    }
                },
                stubs: ["triggerDashboardItemEdit"]
            };
        });
    });

    define('atlassian-connect/connect-dashboard-item', function() {
        return function(){
            return {
                render: function(){
                },
                renderEdit: function($element){
                    $element.find('iframe').trigger('dashboardItemEdit');
                }
            };
        };
    });


    $(document).ready(function() {

        $("body").on("resized",".ap-container", function(e, dimensions) {
            var resizedParents = $(e.target).parents(".gadget-inline");
            if (resizedParents.length > 0) {
                var inlineGadgetContainer = resizedParents.first();
                var gadgetId = inlineGadgetContainer.attr("id");
                var gadget = new AG.InlineGadgetAPI(inlineGadgetContainer);
                var layout = AG.DashboardManager.getLayout();
                var result = layout.getGadgets().filter(function (g) {
                    return "gadget-".concat(g.getId()) == gadgetId;
                });
                result[0].resize();
                layout.refresh();
            }
        });

    });


})(AJS.$, define);