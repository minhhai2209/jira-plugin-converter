;
(function(define, AJS){
    "use strict";
    define("ac/cookie/main", [], function () {

        function prefixCookie (addonKey, name){
            if (!addonKey || addonKey.length === 0) {
                throw new Error('addon key must be defined on cookies');
            }

            if (!name || name.length === 0) {
                throw new Error('Name must be defined');
            }
            return addonKey + '$$' + name;
        }

        return {
            saveCookie: function(addonKey, name, value, expires){
                AJS.Cookie.save(prefixCookie(addonKey, name), value, expires);
            },
            readCookie: function(addonKey, name, callback){
                var value = AJS.Cookie.read(prefixCookie(addonKey, name));
                if (typeof callback === "function") {
                    callback(value);
                }
            },
            eraseCookie: function(addonKey, name){
                 AJS.Cookie.erase(prefixCookie(addonKey, name));
            }
        };
    });
})(define, AJS);
(function(define){
    "use strict";
    define('ac/cookie', ['ac/cookie/main', 'connect-host'], function(cookie, _AP){
        _AP.extend(function () {
            return {
                internals: {
                    saveCookie: function(name, value, expires){
                        cookie.saveCookie(this.addonKey, name, value, expires);
                    },
                    readCookie: function(name, callback){
                        cookie.readCookie(this.addonKey, name, callback);
                    },
                    eraseCookie: function(name){
                        cookie.eraseCookie(this.addonKey, name);
                    }
                }
            };
        });
    });
})(define);
