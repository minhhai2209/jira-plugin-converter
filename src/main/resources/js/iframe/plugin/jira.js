AP.define("jira", ["_dollar", "_rpc"], function ($, rpc) {
    "use strict";
    var workflowListener,
        validationListener,
        dashboardItemEditListener,
        issueCreateListener;

    /**
    * @class WorkflowConfiguration
    */
    var WorkflowConfiguration = {
        /**
        * Validate a workflow configuration before saving
        * @noDemo
        * @memberOf WorkflowConfiguration
        * @param {Function} listener called on validation. Return false to indicate that validation has not passed and the workflow cannot be saved.
        */
        onSaveValidation: function (listener) {
            validationListener = listener;
        },
        /**
        * Attach a callback function to run when a workflow is saved
        * @noDemo
        * @memberOf WorkflowConfiguration
        * @param {Function} listener called on save.
        */
        onSave: function (listener) {
            workflowListener = listener;
        },
        /**
        * Save a workflow configuration if valid.
        * @noDemo
        * @memberOf WorkflowConfiguration
        * @returns {WorkflowConfigurationTriggerResponse} An object Containing `{valid, value}` properties.valid (the result of the validation listener) and value (result of onSave listener) properties.
        */
        trigger: function () {
            var valid = true;
            if($.isFunction(validationListener)){
                valid = validationListener.call();
            }
            /**
            * An object returned when the {@link WorkflowConfiguration} trigger method is invoked.
            * @name WorkflowConfigurationTriggerResponse
            * @class
            * @property {Boolean} valid The result of the validation listener {@link WorkflowConfiguration.onSaveValidation}
            * @property {*} value The result of the {@link WorkflowConfiguration.onSave}
            */
            return {
                valid: valid,
                value: valid ? "" + workflowListener.call() :  undefined
            };
        }
    };
    /**
     * @class DashboardItem
     */
    var DashboardItem = {
        /**
         * Attach a callback function to run when user clicks 'edit' in the dashboard item's menu
         * @noDemo
         * @memberOf DashboardItem
         * @param {Function} listener called on dashboard item edit.
         */
        onDashboardItemEdit: function (listener){
            dashboardItemEditListener = listener;
        },
        triggerEdit: function (){
            if($.isFunction(dashboardItemEditListener)){
                dashboardItemEditListener.call();
            }
        }
    };

    var apis = rpc.extend(function (remote) {
        return {

            /**
            * Allows custom validation and save callback functions for jira workflow configurations and dashboard items.
            * @see {WorkflowConfiguration, DashboardItem}
            * @exports jira
            */
            apis: {
                /**
                * get a workflow configuration object
                *
                * @param {WorkflowConfiguration} callback - the callback that handles the response
                */
                getWorkflowConfiguration: function (callback) {
                    remote.getWorkflowConfiguration(callback);
                },

                /**
                 * Set title of a dashboard item
                 * @param title - the title to set
                 */
                setDashboardItemTitle: function(title) {
                    remote.setDashboardItemTitle(title);
                },

                /**
                 * Returns whether the current user is permitted to edit the dashboard item
                 *
                 * @param {Function} callback - the callback that handles the response
                 */
                isDashboardItemEditable : function(callback) {
                    remote.isDashboardItemEditable(callback);
                },

                /**
                * Refresh an issue page without reloading the browser.
                * This is helpful when your add-on updates information about an issue in the background.
                * @noDemo
                * @example
                * AP.require('jira', function(jira){
                *   jira.refreshIssuePage();
                * });
                */
                refreshIssuePage: function () {
                    remote.triggerJiraEvent('refreshIssuePage');
                },

                /**
                * Open the quick create issue dialog. The dialog fields may be pre-filled with supplied data. A callback will be invoked when the dialog is closed and will include an array of issues created. 
                * 
                * Note: This is unavailable on general admin and project admin pages.
                * 
                * @param {Function} callback - invoked when dialog is closed, takes a single parameter - array of issues created
                * @param {Object} fields - contains data to pre-fill the dialog with
                * @param {ProjectId} fields.pid - Project to pre-fill the dialog with
                * @param {IssueType} fields.issueType - Issue type to pre-fill the dialog with
                * @noDemo
                * @example
                * AP.require('jira', function(jira){
                *   jira.openCreateIssueDialog(function(issues){
                *       alert(issues[0]['fields']['summary']);
                *   },{
                *       pid: 10000,
                *       issueType: 1
                *   });
                * });
                */
                openCreateIssueDialog: function (callback, fields) {
                    issueCreateListener = callback || null;
                    remote.openCreateIssueDialog(fields);
                }
            },

            internals: {

                setWorkflowConfigurationMessage: function () {
                    return WorkflowConfiguration.trigger();
                },
                triggerDashboardItemEdit: function () {
                    return DashboardItem.triggerEdit();
                },
                triggerIssueCreateSubmit: function (issues) {
                    if($.isFunction(issueCreateListener)){
                        issueCreateListener.call({}, issues);
                    }
                }
            },
            stubs: ["triggerJiraEvent", "openCreateIssueDialog"]

        };

    });

    return $.extend(apis, {
        WorkflowConfiguration: WorkflowConfiguration,
        DashboardItem: DashboardItem
    });

});
