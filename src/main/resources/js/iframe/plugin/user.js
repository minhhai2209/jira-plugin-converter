AP.define("user", ["_dollar", "_rpc"], function ($, rpc) {
    "use strict";

    var apis = rpc.extend(function (remote) {
        return {
            /**
             * A Javascript module which provides functions to interact with the user currently in session.
             * @exports User
             */
            apis: {
                /**
                 * Retrieves the current user object containing the user's id and full name
                 * @param {Function} callback - the callback that handles the response
                 * @noDemo
                 * @example
                 * AP.getUser(function(user){
                 *   console.log("user id", user.id);
                 *   console.log("user key", user.key);
                 *   console.log("user name", user.fullName);
                 * });
                 */
                getUser: function (callback) {
                    remote.getUser(callback);
                },
                /**
                 * Retrieve the current user's timezone. If there is no logged in user, the server timezone is returned.
                 * @param {Function} callback - the callback that handles the response
                 * @noDemo
                 * @example
                 * AP.getTimeZone(function(timezone){
                 *   alert(timezone);
                 * });
                 */
                getTimeZone: function (callback) {
                    remote.getTimeZone(callback);
                }
            }
        };
    });

    // backwards compatibility.
    AP.getUser = apis.getUser;
    AP.getTimeZone = apis.getTimeZone;

    return apis;
});
