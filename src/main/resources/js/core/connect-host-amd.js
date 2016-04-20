(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        //Allow using this built library as an AMD module
        //in another project. That other project will only
        //see this AMD call, not the internal modules in
        //the closure below.
        define("connect-host", [], factory);
    } else {
        //Browser globals case. Just assign the
        //result to a property on the global.

        if(!window._AP){
            window._AP = {};
        }

        AJS.$.extend(_AP, factory());
    }
}(this, function () {
;
/**
 * Private namespace for host-side code.
 * @type {*|{}}
 * @private
 * @deprecated use AMD instead of global namespaces. The only thing that should be on _AP is _AP.define and _AP.require.
 */
if(!window._AP){
    window._AP = {};
}
;
/**
 * The iframe-side code exposes a jquery-like implementation via _dollar.
 * This runs on the product side to provide AJS.$ under a _dollar module to provide a consistent interface
 * to code that runs on host and iframe.
 */
define("_dollar", [],function () {
  return AJS.$;
});

define("host/_util", [],function () {
    "use strict";

    return {
        escapeSelector: function( s ){
            if(!s){
                throw new Error("No selector to escape");
            }
            return s.replace(/[!"#$%&'()*+,.\/:;<=>?@[\\\]^`{|}~]/g, "\\$&");
        }
    };
});
( (typeof _AP !== "undefined") ? define : AP.define)("_events", ["_dollar"], function ($) {

  "use strict";

  var w = window,
      log = (w.AJS && w.AJS.log) || (w.console && w.console.log) || (function() {});

  /**
   * A simple pub/sub event bus capable of running on either side of the XDM bridge with no external
   * JS lib dependencies.
   *
   * @param {String} key The key of the event source
   * @param {String} origin The origin of the event source
   * @constructor
   */
  function Events(key, origin) {
    this._key = key;
    this._origin = origin;
    this._events = {};
    this._any = [];
  }

  var proto = Events.prototype;

  /**
   * Subscribes a callback to an event name.
   *
   * @param {String} name The event name to subscribe the listener to
   * @param {Function} listener A listener callback to subscribe to the event name
   * @returns {Events} This Events instance
   */
  proto.on = function (name, listener) {
    if (name && listener) {
      this._listeners(name).push(listener);
    }
    return this;
  };

  /**
   * Subscribes a callback to an event name, removing the it once fired.
   *
   * @param {String} name The event name to subscribe the listener to
   * @param {Function}listener A listener callback to subscribe to the event name
   * @returns {Events} This Events instance
   */
  proto.once = function (name, listener) {
    var self = this;
    var interceptor = function () {
      self.off(name, interceptor);
      listener.apply(null, arguments);
    };
    this.on(name, interceptor);
    return this;
  };

  /**
   * Subscribes a callback to all events, regardless of name.
   *
   * @param {Function} listener A listener callback to subscribe for any event name
   * @returns {Events} This Events instance
   */
  proto.onAny = function (listener) {
    this._any.push(listener);
    return this;
  };

  /**
   * Unsubscribes a callback to an event name.
   *
   * @param {String} name The event name to unsubscribe the listener from
   * @param {Function} listener The listener callback to unsubscribe from the event name
   * @returns {Events} This Events instance
   */
  proto.off = function (name, listener) {
    var all = this._events[name];
    if (all) {
      var i = $.inArray(listener, all);
      if (i >= 0) {
        all.splice(i, 1);
      }
      if (all.length === 0) {
        delete this._events[name];
      }
    }
    return this;
  };

  /**
   * Unsubscribes all callbacks from an event name, or unsubscribes all event-name-specific listeners
   * if no name if given.
   *
   * @param {String} [name] The event name to unsubscribe all listeners from
   * @returns {Events} This Events instance
   */
  proto.offAll = function (name) {
    if (name) {
      delete this._events[name];
    } else {
      this._events = {};
    }
    return this;
  };

  /**
   * Unsubscribes a callback from the set of 'any' event listeners.
   *
   * @param {Function} listener A listener callback to unsubscribe from any event name
   * @returns {Events} This Events instance
   */
  proto.offAny = function (listener) {
    var any = this._any;
    var i = $.inArray(listener, any);
    if (i >= 0) {
      any.splice(i, 1);
    }
    return this;
  };

  /**
   * Emits an event on this bus, firing listeners by name as well as all 'any' listeners. Arguments following the
   * name parameter are captured and passed to listeners.  The last argument received by all listeners after the
   * unpacked arguments array will be the fired event object itself, which can be useful for reacting to event
   * metadata (e.g. the bus's namespace).
   *
   * @param {String} name The name of event to emit
   * @param {Array.<String>} args 0 or more additional data arguments to deliver with the event
   * @returns {Events} This Events instance
   */
  proto.emit = function (name) {
    return this._emitEvent(this._event.apply(this, arguments));
  };

  /**
   * Creates an opaque event object from an argument list containing at least a name, and optionally additional
   * event payload arguments.
   *
   * @param {String} name The name of event to emit
   * @param {Array.<String>} args 0 or more additional data arguments to deliver with the event
   * @returns {Object} A new event object
   * @private
   */
  proto._event = function (name) {
    return {
      name: name,
      args: [].slice.call(arguments, 1),
      attrs: {},
      source: {
        key: this._key,
        origin: this._origin
      }
    };
  };

  /**
   * Emits a previously-constructed event object to all listeners.
   *
   * @param {Object} event The event object to emit
   * @param {String} event.name The name of the event
   * @param {Object} event.source Metadata about the original source of the event, containing key and origin
   * @param {Array} event.args The args passed to emit, to be delivered to listeners
   * @returns {Events} This Events instance
   * @private
   */
  proto._emitEvent = function (event) {
    var args = event.args.concat(event);
    fire(this._listeners(event.name), args);
    fire(this._any, [event.name].concat(args));
    return this;
  };

  /**
   * Returns an array of listeners by event name, creating a new name array if none are found.
   *
   * @param {String} name The event name for which listeners should be returned
   * @returns {Array} An array of listeners; empty if none are registered
   * @private
   */
  proto._listeners = function (name) {
    return this._events[name] = this._events[name] || [];
  };

  // Internal helper for firing an event to an array of listeners
  function fire(listeners, args) {
    for (var i = 0; i < listeners.length; ++i) {
      try {
        listeners[i].apply(null, args);
      } catch (e) {
        log(e.stack || e.message || e);
      }
    }
  }

  return {
    Events: Events
  };

});

/*
 Copyright (c) 2008 Fred Palmer fred.palmer_at_gmail.com

 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following
 conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.

 Modified slightly to wrap in our define() and function($) logic.
 */

( (typeof _AP !== "undefined") ? define : AP.define)("_base64", ["_dollar"], function ($) {

    "use strict";


    function StringBuffer()
    {
        this.buffer = [];
    }

    StringBuffer.prototype.append = function append(string)
    {
        this.buffer.push(string);
        return this;
    };

    StringBuffer.prototype.toString = function toString()
    {
        return this.buffer.join("");
    };

    var Base64 =
    {
        codex : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

        encode : function (input)
        {
            var output = new StringBuffer();

            var enumerator = new Utf8EncodeEnumerator(input);
            while (enumerator.moveNext())
            {
                var chr1 = enumerator.current;

                enumerator.moveNext();
                var chr2 = enumerator.current;

                enumerator.moveNext();
                var chr3 = enumerator.current;

                var enc1 = chr1 >> 2;
                var enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                var enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                var enc4 = chr3 & 63;

                if (isNaN(chr2))
                {
                    enc3 = enc4 = 64;
                }
                else if (isNaN(chr3))
                {
                    enc4 = 64;
                }

                output.append(this.codex.charAt(enc1) + this.codex.charAt(enc2) + this.codex.charAt(enc3) + this.codex.charAt(enc4));
            }

            return output.toString();
        },

        decode : function (input)
        {
            var output = new StringBuffer();

            var enumerator = new Base64DecodeEnumerator(input);
            while (enumerator.moveNext())
            {
                var charCode = enumerator.current;

                if (charCode < 128)
                    output.append(String.fromCharCode(charCode));
                else if ((charCode > 191) && (charCode < 224))
                {
                    enumerator.moveNext();
                    var charCode2 = enumerator.current;

                    output.append(String.fromCharCode(((charCode & 31) << 6) | (charCode2 & 63)));
                }
                else
                {
                    enumerator.moveNext();
                    var charCode2 = enumerator.current;

                    enumerator.moveNext();
                    var charCode3 = enumerator.current;

                    output.append(String.fromCharCode(((charCode & 15) << 12) | ((charCode2 & 63) << 6) | (charCode3 & 63)));
                }
            }

            return output.toString();
        }
    }


    function Utf8EncodeEnumerator(input)
    {
        this._input = input;
        this._index = -1;
        this._buffer = [];
    }

    Utf8EncodeEnumerator.prototype =
    {
        current: Number.NaN,

        moveNext: function()
        {
            if (this._buffer.length > 0)
            {
                this.current = this._buffer.shift();
                return true;
            }
            else if (this._index >= (this._input.length - 1))
            {
                this.current = Number.NaN;
                return false;
            }
            else
            {
                var charCode = this._input.charCodeAt(++this._index);

                // "\r\n" -> "\n"
                //
                if ((charCode == 13) && (this._input.charCodeAt(this._index + 1) == 10))
                {
                    charCode = 10;
                    this._index += 2;
                }

                if (charCode < 128)
                {
                    this.current = charCode;
                }
                else if ((charCode > 127) && (charCode < 2048))
                {
                    this.current = (charCode >> 6) | 192;
                    this._buffer.push((charCode & 63) | 128);
                }
                else
                {
                    this.current = (charCode >> 12) | 224;
                    this._buffer.push(((charCode >> 6) & 63) | 128);
                    this._buffer.push((charCode & 63) | 128);
                }

                return true;
            }
        }
    }

    function Base64DecodeEnumerator(input)
    {
        this._input = input;
        this._index = -1;
        this._buffer = [];
    }

    Base64DecodeEnumerator.prototype =
    {
        current: 64,

        moveNext: function()
        {
            if (this._buffer.length > 0)
            {
                this.current = this._buffer.shift();
                return true;
            }
            else if (this._index >= (this._input.length - 1))
            {
                this.current = 64;
                return false;
            }
            else
            {
                var enc1 = Base64.codex.indexOf(this._input.charAt(++this._index));
                var enc2 = Base64.codex.indexOf(this._input.charAt(++this._index));
                var enc3 = Base64.codex.indexOf(this._input.charAt(++this._index));
                var enc4 = Base64.codex.indexOf(this._input.charAt(++this._index));

                var chr1 = (enc1 << 2) | (enc2 >> 4);
                var chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                var chr3 = ((enc3 & 3) << 6) | enc4;

                this.current = chr1;

                if (enc3 != 64 && chr2 != 0)
                    this._buffer.push(chr2);

                if (enc4 != 64 && chr3 != 0)
                    this._buffer.push(chr3);

                return true;
            }
        }
    };

    function encode(plainText) {
        return Base64.encode(plainText);
    }

    function decode(encodedText) {
        return Base64.decode(encodedText);
    }

    return {
        encode: encode,
        decode: decode
    };

});

( (typeof _AP !== "undefined") ? define : AP.define)("_jwt", ["_base64"], function(base64){
    "use strict";

    function parseJwtIssuer(jwt) {
        return parseJwtClaims(jwt)['iss'];
    }

    function parseJwtClaims(jwt) {

        if (null === jwt || '' === jwt) {
            throw('Invalid JWT: must be neither null nor empty-string.');
        }

        var firstPeriodIndex = jwt.indexOf('.');
        var secondPeriodIndex = jwt.indexOf('.', firstPeriodIndex + 1);

        if (firstPeriodIndex < 0 || secondPeriodIndex <= firstPeriodIndex) {
            throw('Invalid JWT: must contain 2 period (".") characters.');
        }

        var encodedClaims = jwt.substring(firstPeriodIndex + 1, secondPeriodIndex);

        if (null === encodedClaims || '' === encodedClaims) {
            throw('Invalid JWT: encoded claims must be neither null nor empty-string.');
        }

        var claimsString = base64.decode(encodedClaims);
        return JSON.parse(claimsString);
    }

    function isJwtExpired(jwtString, skew){
        if(skew === undefined){
            skew = 60; // give a minute of leeway to allow clock skew
        }
        var claims = parseJwtClaims(jwtString),
        expires = 0,
        now = Math.floor(Date.now() / 1000); // UTC timestamp now

        if(claims && claims.exp){
            expires = claims.exp;
        }

        if( (expires - now) < skew){
            return true;
        }

        return false;

    }

    return {
        parseJwtIssuer: parseJwtIssuer,
        parseJwtClaims: parseJwtClaims,
        isJwtExpired: isJwtExpired
    };
});
/*!
 * jsUri
 * https://github.com/derek-watson/jsUri
 *
 * Copyright 2013, Derek Watson
 * Released under the MIT license.
 *
 * Includes parseUri regular expressions
 * http://blog.stevenlevithan.com/archives/parseuri
 * Copyright 2007, Steven Levithan
 * Released under the MIT license.
 */

 /*globals define, module */
( (typeof _AP !== "undefined") ? define : AP.define)("_uri", [], function () {

  var re = {
    starts_with_slashes: /^\/+/,
    ends_with_slashes: /\/+$/,
    pluses: /\+/g,
    query_separator: /[&;]/,
    uri_parser: /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/
  };

  /**
   * Define forEach for older js environments
   * @see https://developer.mozilla.org/en-US/docs/JavaScript/Reference/Global_Objects/Array/forEach#Compatibility
   */
  if (!Array.prototype.forEach) {
    Array.prototype.forEach = function(fn, scope) {
      for (var i = 0, len = this.length; i < len; ++i) {
        fn.call(scope || this, this[i], i, this);
      }
    };
  }

  /**
   * unescape a query param value
   * @param  {string} s encoded value
   * @return {string}   decoded value
   */
  function decode(s) {
    if (s) {
      s = decodeURIComponent(s);
      s = s.replace(re.pluses, ' ');
    }
    return s;
  }

  /**
   * Breaks a uri string down into its individual parts
   * @param  {string} str uri
   * @return {object}     parts
   */
  function parseUri(str) {
    var parser = re.uri_parser;
    var parserKeys = ["source", "protocol", "authority", "userInfo", "user", "password", "host", "port", "relative", "path", "directory", "file", "query", "anchor"];
    var m = parser.exec(str || '');
    var parts = {};

    parserKeys.forEach(function(key, i) {
      parts[key] = m[i] || '';
    });

    return parts;
  }

  /**
   * Breaks a query string down into an array of key/value pairs
   * @param  {string} str query
   * @return {array}      array of arrays (key/value pairs)
   */
  function parseQuery(str) {
    var i, ps, p, n, k, v;
    var pairs = [];

    if (typeof(str) === 'undefined' || str === null || str === '') {
      return pairs;
    }

    if (str.indexOf('?') === 0) {
      str = str.substring(1);
    }

    ps = str.toString().split(re.query_separator);

    for (i = 0; i < ps.length; i++) {
      p = ps[i];
      n = p.indexOf('=');

      if (n !== 0) {
        k = decodeURIComponent(p.substring(0, n));
        v = decodeURIComponent(p.substring(n + 1).replace(/\+/g, " "));
        pairs.push(n === -1 ? [p, null] : [k, v]);
      }

    }
    return pairs;
  }

  /**
   * Creates a new Uri object
   * @constructor
   * @param {string} str
   */
  function Uri(str) {
    this.uriParts = parseUri(str);
    this.queryPairs = parseQuery(this.uriParts.query);
    this.hasAuthorityPrefixUserPref = null;
  }

  /**
   * Define getter/setter methods
   */
  ['protocol', 'userInfo', 'host', 'port', 'path', 'anchor'].forEach(function(key) {
    Uri.prototype[key] = function(val) {
      if (typeof val !== 'undefined') {
        this.uriParts[key] = val;
      }
      return this.uriParts[key];
    };
  });

  /**
   * if there is no protocol, the leading // can be enabled or disabled
   * @param  {Boolean}  val
   * @return {Boolean}
   */
  Uri.prototype.hasAuthorityPrefix = function(val) {
    if (typeof val !== 'undefined') {
      this.hasAuthorityPrefixUserPref = val;
    }

    if (this.hasAuthorityPrefixUserPref === null) {
      return (this.uriParts.source.indexOf('//') !== -1);
    } else {
      return this.hasAuthorityPrefixUserPref;
    }
  };

  /**
   * Serializes the internal state of the query pairs
   * @param  {string} [val]   set a new query string
   * @return {string}         query string
   */
  Uri.prototype.query = function(val) {
    var s = '', i, param;

    if (typeof val !== 'undefined') {
      this.queryPairs = parseQuery(val);
    }

    for (i = 0; i < this.queryPairs.length; i++) {
      param = this.queryPairs[i];
      if (s.length > 0) {
        s += '&';
      }
      if (param[1] === null) {
        s += param[0];
      } else {
        s += param[0];
        s += '=';
        if (param[1]) {
          s += encodeURIComponent(param[1]);
        }
      }
    }
    return s.length > 0 ? '?' + s : s;
  };

  /**
   * returns the first query param value found for the key
   * @param  {string} key query key
   * @return {string}     first value found for key
   */
  Uri.prototype.getQueryParamValue = function (key) {
    var param, i;
    for (i = 0; i < this.queryPairs.length; i++) {
      param = this.queryPairs[i];
      if (key === param[0]) {
        return param[1];
      }
    }
  };

  /**
   * returns an array of query param values for the key
   * @param  {string} key query key
   * @return {array}      array of values
   */
  Uri.prototype.getQueryParamValues = function (key) {
    var arr = [], i, param;
    for (i = 0; i < this.queryPairs.length; i++) {
      param = this.queryPairs[i];
      if (key === param[0]) {
        arr.push(param[1]);
      }
    }
    return arr;
  };

  /**
   * removes query parameters
   * @param  {string} key     remove values for key
   * @param  {val}    [val]   remove a specific value, otherwise removes all
   * @return {Uri}            returns self for fluent chaining
   */
  Uri.prototype.deleteQueryParam = function (key, val) {
    var arr = [], i, param, keyMatchesFilter, valMatchesFilter;

    for (i = 0; i < this.queryPairs.length; i++) {

      param = this.queryPairs[i];
      keyMatchesFilter = decode(param[0]) === decode(key);
      valMatchesFilter = param[1] === val;

      if ((arguments.length === 1 && !keyMatchesFilter) || (arguments.length === 2 && (!keyMatchesFilter || !valMatchesFilter))) {
        arr.push(param);
      }
    }

    this.queryPairs = arr;

    return this;
  };

  /**
   * adds a query parameter
   * @param  {string}  key        add values for key
   * @param  {string}  val        value to add
   * @param  {integer} [index]    specific index to add the value at
   * @return {Uri}                returns self for fluent chaining
   */
  Uri.prototype.addQueryParam = function (key, val, index) {
    if (arguments.length === 3 && index !== -1) {
      index = Math.min(index, this.queryPairs.length);
      this.queryPairs.splice(index, 0, [key, val]);
    } else if (arguments.length > 0) {
      this.queryPairs.push([key, val]);
    }
    return this;
  };

  /**
   * replaces query param values
   * @param  {string} key         key to replace value for
   * @param  {string} newVal      new value
   * @param  {string} [oldVal]    replace only one specific value (otherwise replaces all)
   * @return {Uri}                returns self for fluent chaining
   */
  Uri.prototype.replaceQueryParam = function (key, newVal, oldVal) {
    var index = -1, i, param;

    if (arguments.length === 3) {
      for (i = 0; i < this.queryPairs.length; i++) {
        param = this.queryPairs[i];
        if (decode(param[0]) === decode(key) && decodeURIComponent(param[1]) === decode(oldVal)) {
          index = i;
          break;
        }
      }
      this.deleteQueryParam(key, oldVal).addQueryParam(key, newVal, index);
    } else {
      for (i = 0; i < this.queryPairs.length; i++) {
        param = this.queryPairs[i];
        if (decode(param[0]) === decode(key)) {
          index = i;
          break;
        }
      }
      this.deleteQueryParam(key);
      this.addQueryParam(key, newVal, index);
    }
    return this;
  };

  /**
   * Define fluent setter methods (setProtocol, setHasAuthorityPrefix, etc)
   */
  ['protocol', 'hasAuthorityPrefix', 'userInfo', 'host', 'port', 'path', 'query', 'anchor'].forEach(function(key) {
    var method = 'set' + key.charAt(0).toUpperCase() + key.slice(1);
    Uri.prototype[method] = function(val) {
      this[key](val);
      return this;
    };
  });

  /**
   * Scheme name, colon and doubleslash, as required
   * @return {string} http:// or possibly just //
   */
  Uri.prototype.scheme = function() {
    var s = '';

    if (this.protocol()) {
      s += this.protocol();
      if (this.protocol().indexOf(':') !== this.protocol().length - 1) {
        s += ':';
      }
      s += '//';
    } else {
      if (this.hasAuthorityPrefix() && this.host()) {
        s += '//';
      }
    }

    return s;
  };

  /**
   * Same as Mozilla nsIURI.prePath
   * @return {string} scheme://user:password@host:port
   * @see  https://developer.mozilla.org/en/nsIURI
   */
  Uri.prototype.origin = function() {
    var s = this.scheme();

    if (s == 'file://') {
      return s + this.uriParts.authority;
    }

    if (this.userInfo() && this.host()) {
      s += this.userInfo();
      if (this.userInfo().indexOf('@') !== this.userInfo().length - 1) {
        s += '@';
      }
    }

    if (this.host()) {
      s += this.host();
      if (this.port()) {
        s += ':' + this.port();
      }
    }

    return s;
  };

  /**
   * Adds a trailing slash to the path
   */
  Uri.prototype.addTrailingSlash = function() {
    var path = this.path() || '';

    if (path.substr(-1) !== '/') {
      this.path(path + '/');
    }

    return this;
  };

  /**
   * Serializes the internal state of the Uri object
   * @return {string}
   */
  Uri.prototype.toString = function() {
    var path, s = this.origin();

    if (this.path()) {
      path = this.path();
      if (!(re.ends_with_slashes.test(s) || re.starts_with_slashes.test(path))) {
        s += '/';
      } else {
        if (s) {
          s.replace(re.ends_with_slashes, '/');
        }
        path = path.replace(re.starts_with_slashes, '/');
      }
      s += path;
    } else {
      if (this.host() && (this.query().toString() || this.anchor())) {
        s += '/';
      }
    }
    if (this.query().toString()) {
      if (this.query().toString().indexOf('?') !== 0) {
        s += '?';
      }
      s += this.query().toString();
    }

    if (this.anchor()) {
      if (this.anchor().indexOf('#') !== 0) {
        s += '#';
      }
      s += this.anchor();
    }

    return s;
  };

  /**
   * Clone a Uri object
   * @return {Uri} duplicate copy of the Uri
   */
  Uri.prototype.clone = function() {
    return new Uri(this.toString());
  };

  return {
    init: Uri
  };

});
( (typeof _AP !== "undefined") ? define : AP.define)("_ui-params", ["_dollar", "_base64", "_uri"], function($, base64, Uri) {

    /**
    * These are passed into the main host create statement and can override
    * any options inside the velocity template.
    * Additionally these are accessed by the js inside the client iframe to check if we are in a dialog.
    */

    return {
        /**
        * Encode options for transport
        */
        encode: function(options){
            if(options){
                return base64.encode(JSON.stringify(options));
            }
        },
        /**
        * return ui params from a Url
        **/
        fromUrl: function(url){
            var url = new Uri.init(url),
            params = url.getQueryParamValue('ui-params');
            return this.decode(params);
        },
        /**
        * returns ui params from window.name
        */
        fromWindowName: function(w, param){
            w = w || window;
            var decoded = this.decode(w.name);

            if(!param){
                return decoded;
            }
            return (decoded) ? decoded[param] : undefined;
        },
        /**
        * Decode a base64 encoded json string containing ui params
        */
        decode: function(params){
            var obj = {};
            if(params && params.length > 0){
                try {
                    obj = JSON.parse(base64.decode(params));
                } catch(e) {
                    if(console && console.log){
                        console.log("Cannot decode passed ui params", params);
                    }
                }
            }
            return obj;
        }
    };

});

var deps = ["_events", "_jwt", "_uri",  "_ui-params", "host/_util"];
if(this.AP){
  deps = ["_events", "_jwt", "_uri",  "_ui-params"];
}
( (typeof _AP !== "undefined") ? define : AP.define)("_xdm", deps, function (events, jwt, uri, uiParams, util) {

  "use strict";

  // Capture some common values and symbol aliases
  var count = 0;

  /**
   * Sets up cross-iframe remote procedure calls.
   * If this is called from a parent window, iframe is created and an RPC interface for communicating with it is set up.
   * If this is called from within the iframe, an RPC interface for communicating with the parent is set up.
   *
   * Calling a remote function is done with the signature:
   *     fn(data..., doneCallback, failCallback)
   * doneCallback is called after the remote function executed successfully.
   * failCallback is called after the remote function throws an exception.
   * doneCallback and failCallback are optional.
   *
   * @param {Object} $ jquery or jquery-like utility
   * @param {Object} config Configuration parameters
   * @param {String} config.remoteKey The remote peer's add-on key (host only)
   * @param {String} config.remote The src of remote iframe (host only)
   * @param {String} config.remoteOrigin The src of remote origin. Required in case when remote doesn't points directly to the add-on servlet. (host only)
   * @param {String} config.container The id of element to which the generated iframe is appended (host only)
   * @param {Object} config.props Additional attributes to add to iframe element (host only)
   * @param {String} config.channel Channel (host only); deprecated
   * @param {Object} bindings RPC method stubs and implementations
   * @param {Object} bindings.local Local function implementations - functions that exist in the current context.
   *    XdmRpc exposes these functions so that they can be invoked by code running in the other side of the iframe.
   * @param {Array} bindings.remote Names of functions which exist on the other side of the iframe.
   *    XdmRpc creates stubs to these functions that can be invoked from the current page.
   * @returns XdmRpc instance
   * @constructor
   */
  function XdmRpc($, config, bindings) {

    var self, id, target, remoteOrigin, channel, mixin,
        localKey, remoteKey, addonKey,
        w = window,
        loc = w.location.toString(),
        locals = bindings.local || {},
        remotes = bindings.remote || [],
        localOrigin = getBaseUrl(loc);

    // A hub through which all async callbacks for remote requests are parked until invoked from a response
    var nexus = function () {
      var callbacks = {};
      return {
        // Registers a callback of a given type by uid
        add: function (uid, done, fail) {
          callbacks[uid] = {
            done: done || null,
            fail: fail || null,
            async: !!done
          };
        },
        // Invokes callbacks for a response of a given type by uid if registered, then removes all handlers for the uid
        invoke: function (type, uid, arg) {
          var handled;
          if (callbacks[uid]) {
            if (callbacks[uid][type]) {
              // If the intended callback exists, invoke it and mark the response as handled
              callbacks[uid][type](arg);
              handled = true;
            } else {
              // Only mark other calls as handled if they weren't expecting a callback and didn't fail
              handled = !callbacks[uid].async && type !== "fail";
            }
            delete callbacks[uid];
          }
          return handled;
        }
      };
    }();

    // Use the config and enviroment to construct the core of the new XdmRpc instance.
    //
    // Note: The xdm_e|c|p variables that appear in an iframe URL are used to pass message to the XdmRpc bridge
    // when running inside an add-on iframe.  Their names are holdovers from easyXDM, which was used prior
    // to building this proprietary library (which was done both to greatly reduce the total amount of JS
    // needed to drive the postMessage-based RPC communication, and to allow us to extend its capabilities).
    //
    // AC-451 describes how we can reduce/improve these (and other) iframe url parameters, but until that is
    // addressed, here's a brief description of each:
    //
    //  - xdm_e contains the base url of the host app; it's presence indicates that the XdmRpc is running in
    //    an add-on iframe
    //  - xdm_c contains a unique channel name; this is a holdover from easyXDM that was used to distinguish
    //    postMessage events between multiple iframes with identical xdm_e values, though this may now be
    //    redundant with the current internal implementation of the XdmRpc and should be considered for removal
    if (!/xdm_e/.test(loc)) {
      // Host-side constructor branch

      // if there is already an iframe created. Destroy it. It's an old version.
      $("#" + util.escapeSelector(config.container)).find('iframe').trigger('ra.iframe.destroy');

      var iframe = createIframe(config);
      target = iframe.contentWindow;
      localKey = param(config.remote, "oauth_consumer_key") || param(config.remote, "jwt");
      remoteKey = config.remoteKey;
      addonKey = remoteKey;
      remoteOrigin = (config.remoteOrigin ? config.remoteOrigin : getBaseUrl(config.remote)).toLowerCase();
      channel = config.channel;
      // Define the host-side mixin
      mixin = {
        isHost: true,
        iframe: iframe,
        uiParams: config.uiParams,
        destroy: function () {
          window.clearTimeout(self.timeout); //clear the iframe load time.
          // Unbind postMessage handler when destroyed
          unbind();
          // Then remove the iframe, if it still exists
          if (self.iframe) {
            $(self.iframe).remove();
            delete self.iframe;
          }
        },
        isActive: function () {
          // Host-side instances are only active as long as the iframe they communicate with still exists in the DOM
          return $.contains(document.documentElement, self.iframe);
        }
      };
      $(iframe).on('ra.iframe.destroy', mixin.destroy);
    } else {
      // Add-on-side constructor branch
      target = w.parent;
      localKey = "local"; // Would be better to make this the add-on key, but it's not readily available at this time

      // identify the add-on by unique key: first try JWT issuer claim and fall back to OAuth1 consumer key
      var jwtParam = param(loc, "jwt");
      remoteKey = jwtParam ? jwt.parseJwtIssuer(jwtParam) : param(loc, "oauth_consumer_key");

      // if the authentication method is "none" then it is valid to have no jwt and no oauth in the url
      // but equally we don't trust this iframe as far as we can throw it, so assign it a random id
      // in order to prevent it from talking to any other iframe
      if (null === remoteKey) {
          remoteKey = Math.random(); // unpredictable and unsecured, like an oauth consumer key
      }

      addonKey = localKey;
      remoteOrigin = param(loc, "xdm_e").toLowerCase();
      channel = param(loc, "xdm_c");
      // Define the add-on-side mixin
      mixin = {
        isHost: false,
        isActive: function () {
          // Add-on-side instances are always active, as they must always have a parent window peer
          return true;
        }
      };
    }

    id = addonKey + "|" + (count += 1);

    // Create the actual XdmRpc instance, and apply the context-sensitive mixin
    self = $.extend({
      id: id,
      remoteOrigin: remoteOrigin,
      channel: channel,
      addonKey: addonKey
    }, mixin);

    // Sends a message of a specific type to the remote peer via a post-message event
    function send(sid, type, message) {
      try {
        target.postMessage(JSON.stringify({
          c: channel,
          i: sid,
          t: type,
          m: message
        }), remoteOrigin);
      } catch (ex) {
        log(errmsg(ex));
      }
    }

    // Sends a request with a specific remote method name, args, and optional callbacks
    function sendRequest(methodName, args, done, fail) {
      // Generate a random ID for this remote invocation
      var sid = Math.floor(Math.random() * 1000000000).toString(16);
      // Register any callbacks with the nexus so they can be invoked when a response is received
      nexus.add(sid, done, fail);
      // Send a request to the remote, where:
      //  - n is the name of the remote function
      //  - a is an array of the (hopefully) serializable, non-callback arguments to this method
      send(sid, "request", {n: methodName, a: args});
    }

    function sendDone(sid, message) {
      send(sid, "done", message);
    }

    function sendFail(sid, message) {
      send(sid, "fail", message);
    }

    // Handles an normalized, incoming post-message event
    function receive(e) {
      try {
        // Extract message payload from the event
        var payload = JSON.parse(e.data),
            pid = payload.i, pchannel = payload.c, ptype = payload.t, pmessage = payload.m;

        // if the iframe has potentially been reloaded. re-attach the source contentWindow object
        if (e.source !== target && e.origin.toLowerCase() === remoteOrigin && pchannel === channel) {
          target = e.source;
        }

        // If the payload doesn't match our expected event signature, assume its not part of the xdm-rpc protocol
        if (e.source !== target || e.origin.toLowerCase() !== remoteOrigin || pchannel !== channel){
          return;
        }

        if (ptype === "request") {
          // If the payload type is request, this is an incoming method invocation
          var name = pmessage.n, args = pmessage.a,
              local = locals[name], done, fail, async;
          if (local) {
            // The message name matches a locally defined RPC method, so inspect and invoke it according
            // Create responders for each response type
            done = function (message) { sendDone(pid, message); };
            fail = function (message) { sendFail(pid, message); };
            // The local method is considered async if it accepts more arguments than the message has sent;
            // the additional arguments are filled in with the above async responder callbacks;
            // TODO: consider specifying args somehow in the remote stubs so that non-callback args can be
            //       verified before sending a request to fail fast at the callsite
            async = (args ? args.length : 0) < local.length;
            var context = locals;
            if(self.isHost === true){
                context = self;
                if(context.analytics){
                  context.analytics.trackBridgeMethod(name);
                }
            } else {
              context.isHost = false;
            }
            try {
              if (async) {
                // If async, apply the method with the responders added to the args list
                local.apply(context, args.concat([done, fail]));
              } else {
                // Otherwise, immediately respond with the result
                done(local.apply(context, args));
              }
            } catch (ex) {
              // If the invocation threw an error, invoke the fail responder callback with it
              fail(errmsg(ex));
              logError(ex);
            }
          } else {
            // No such local rpc method name found
            debug("Unhandled request:", payload);
          }
        } else if (ptype === "done" || ptype === "fail") {
          // The payload is of a response type, so try to invoke the appropriate callback via the nexus registry
          if (!nexus.invoke(ptype, pid, pmessage)) {
            // The nexus didn't find an appropriate reponse callback to invoke
            debug("Unhandled response:", ptype, pid, pmessage);
          }
        }
      } catch (ex) {
        log(errmsg(ex));
      }
    }

    // Creates a bridging invocation function for a remote method
    function bridge(methodName) {
      // Add a method to this instance that will convert from 'rpc.method(args..., done?, fail?)'-style
      // invocations to a postMessage event via the 'send' function
      return function () {
        var args = [].slice.call(arguments), done, fail;
        // Pops the last arg off the args list if it's a function
        function popFn() {
          if ($.isFunction(args[args.length - 1])) {
            return args.pop();
          }
        }
        // Remove done/fail callbacks from the args list
        fail = popFn();
        done = popFn();
        if (!done) {
          // Set the done cb to the value of the fail cb if only one callback fn was given
          done = fail;
          fail = undefined;
        }
        sendRequest(methodName, args, done, fail);
      };
    }

    // For each remote method, generate a like-named interceptor on this instance that converts invocations to
    // post-message request events, tracking async callbacks as necessary.
    $.each(remotes, function (methodName, v) {
      // If remotes were specified as an array rather than a map, promote v to methodName
      if (typeof methodName === "number") methodName = v;
      self[methodName] = bridge(methodName);
    });

    // Create and attach a local event emitter for bridged pub/sub
    var bus = self.events = new events.Events(localKey, localOrigin);
    // Attach an any-listener to forward all locally-originating events to the remote peer
    bus.onAny(function () {
      // The actual event object is the last argument passed to any listener
      var event = arguments[arguments.length - 1];
      var trace = event.trace = event.trace || {};
      var traceKey = self.id + "|xdm";
      if ((self.isHost && !trace[traceKey] && event.source.channel !== self.id)
          || (!self.isHost && event.source.key === localKey)) {
        // Only forward an event once in this listener
        trace[traceKey] = true;
        // Clone the event and forward without tracing info, to avoid leaking host-side iframe topology to add-ons
        event = $.extend({}, event);
        delete event.trace;
        debug("Forwarding " + (self.isHost ? "host" : "addon") + " event:", event);
        sendRequest("_event", [event]);
      }
    });
    // Define our own reserved local to receive remote events
    locals._event = function (event) {
      // Reset/ignore any tracing info that may have come across the bridge
      delete event.trace;
      if (this.isHost) {
        // When the running on the host-side, forcibly reset the event's key and origin fields, to prevent spoofing by
        // untrusted add-ons; also include the host-side XdmRpc instance id to tag the event with this particular
        // instance of the host/add-on relationship
        event.source = {
          channel: this.id || id, // Note: the term channel here != the deprecated xdm channel param
          key: this.addonKey,
          origin: this.remoteOrigin || remoteOrigin
        };
      }
      debug("Receiving as " + (this.isHost ? "host" : "addon") + " event:", event);
      // Emit the event on the local bus
      bus._emitEvent(event);
    };

    // Handles incoming postMessages from this XdmRpc instance's remote peer
    function postMessageHandler(e) {
      if (self.isActive()) {
        // Normalize and forward the event message to the receiver logic
        receive(e.originalEvent ? e.originalEvent : e);
      } else {
        // If inactive (due to the iframe element having disappeared from the DOM), force cleanup of this callback
        unbind();
      }
    }

    // Starts listening for window messaging events
    function bind() {
      $(window).bind("message", postMessageHandler);
    }

    // Stops listening for window messaging events
    function unbind() {
      $(window).unbind("message", postMessageHandler);
    }

    // Crudely extracts a query param value from a url by name
    function param(url, name) {
      return new uri.init(url).getQueryParamValue(name);
    }

    // Determines a base url consisting of protocol+domain+port from a given url string
    function getBaseUrl(url) {
      return new uri.init(url).origin();
    }

    // Appends a map of query parameters to a base url
    function toUrl(base, params) {
      var url = new uri.init(base);
      $.each(params, function (k, v) {
        url.addQueryParam(k,v);
      });
      return url.toString();
    }

    // Creates an iframe element from a config option consisting of the following values:
    //  - container:  the parent element of the new iframe
    //  - remote:     the src url of the new iframe
    //  - props:      a map of additional HTML attributes for the new iframe
    //  - channel:    deprecated
    function createIframe(config) {
      if(!config.container){
        throw new Error("config.container must be defined");
      }
      var iframe = document.createElement("iframe"),
        id = "easyXDM_" + config.container + "_provider",
        windowName = "";

      if(config.uiParams){
        windowName = uiParams.encode(config.uiParams);
      }
      $.extend(iframe, {id: id, name: windowName, frameBorder: "0"}, config.props);
      //$.extend will not add the attribute rel.
      iframe.setAttribute('rel', 'nofollow');
      $("#" + util.escapeSelector(config.container)).append(iframe);
      $(iframe).trigger("ra.iframe.create");
      iframe.src = config.remote;
      return iframe;
    }

    function errmsg(ex) {
      return ex.message || ex.toString();
    }

    function debug() {
      if (XdmRpc.debug) log.apply(w, ["DEBUG:"].concat([].slice.call(arguments)));
    }

    function log() {
      var log = $.log || (w.AJS && w.AJS.log);
      if (log) log.apply(w, arguments);
    }

    function logError() {
      // $.error seems to do the same thing as $.log in client console
      var error = (w.AJS && w.AJS.error);
      if (error) error.apply(w, arguments);
    }

    // Immediately start listening for events
    bind();

    return self;
  }

//  XdmRpc.debug = true;

  return XdmRpc;

});

define("host/jwt-keepalive", ["_dollar", "_jwt"], function($, jwt){
    "use strict";

    function updateUrl (config){
        var promise = $.Deferred(function(defer){
            var contentPromise = window._AP.contentResolver.resolveByParameters({
                addonKey: config.addonKey,
                moduleKey: config.moduleKey,
                productContext: config.productContext,
                uiParams: config.uiParams,
                width: config.width,
                height: config.height,
                classifier: 'json'
            });

            contentPromise.done(function(data){
                var values = JSON.parse(data);
                defer.resolve(values.src);
            });
        });

        return promise;
    }

    return {
        updateUrl: updateUrl,
        isExpired: jwt.isJwtExpired
    };

});
define("_rpc", ["_dollar", "_xdm", "host/jwt-keepalive", "_uri"], function ($, XdmRpc, jwtKeepAlive, uri) {

    "use strict";

    var each = $.each,
        extend = $.extend,
        isFn = $.isFunction,
        rpcCollection = [],
        apis = {},
        stubs = [],
        internals = {},
        inits = [];

    return {

        extend: function (config) {
            if (isFn(config)) config = config();
            extend(apis, config.apis);
            extend(internals, config.internals);
            stubs = stubs.concat(config.stubs || []);

            var init = config.init;
            if (isFn(init)) inits.push(init);
            return config.apis;
        },

        // init connect host side
        // options = things that go to all init functions

        init: function (options, xdmConfig) {

            var remoteUrl = new uri.init(xdmConfig.remote),
            remoteJwt = remoteUrl.getQueryParamValue('jwt'),
            promise;

            options = options || {};
            // add stubs for each public api
            each(apis, function (method) { stubs.push(method); });

            // refresh JWT tokens as required.
            if(remoteJwt && jwtKeepAlive.isExpired(remoteJwt)){
                promise = jwtKeepAlive.updateUrl({
                    addonKey: xdmConfig.remoteKey,
                    moduleKey: options.ns,
                    productContext: options.productContext || {},
                    uiParams: xdmConfig.uiParams,
                    width: xdmConfig.props.width,
                    height: xdmConfig.props.height
                });
            }

            $.when(promise).always(function(src){
                // if the promise resolves to a new url. update it.
                if(src){
                    xdmConfig.remote = src;
                }
                // TODO: stop copying internals and fix references instead (fix for events going across add-ons when they shouldn't)
                var rpc = new XdmRpc($, xdmConfig, {remote: stubs, local: $.extend({}, internals)});

                rpcCollection[rpc.id] = rpc;
                each(inits, function (_, init) {
                    try { init(extend({}, options), rpc); }
                    catch (ex) { console.log(ex); }
                });
            });

        }

    };

});

define("resize", ["_dollar", "_rpc"], function ($, rpc) {
    "use strict";
    rpc.extend(function () {
        return {
            init: function (config, xdm) {
                xdm.resize = AJS.debounce(function resize ($, width, height) {
                    $(this.iframe).css({width: width, height: height});
                    var nexus = $(this.iframe).closest('.ap-container');
                    nexus.trigger('resized', {width: width, height: height});

                });
            },
            internals: {
                resize: function(width, height) {
                    if(!this.uiParams.isDialog){
                        this.resize($, width, height);
                    }
                },
                sizeToParent: AJS.debounce(function(hideFooter) {

                    var resizeHandler = function (iframe) {
                        var height;
                        if (hideFooter) {
                            $('.ac-content-page #footer').css({display: "none"});
                            $('.ac-content-page').css({overflow: "hidden !important"});
                            height = $(document).height() - $("#header > nav").outerHeight();
                        } else {
                            height = $(document).height() - $("#header > nav").outerHeight() - $("#footer").outerHeight() - 20;
                        }

                        $(iframe).css({width: "100%", height: height + "px"});
                    };
                    // sizeToParent is only available for general-pages
                    if (this.uiParams.isGeneral) {
                        // This adds border between the iframe and the page footer as the connect addon has scrolling content and can't do this
                        $(this.iframe).addClass("full-size-general-page");
                        $(window).on('resize', function(){
                            resizeHandler(this.iframe);
                        });
                        resizeHandler(this.iframe);
                    }
                    else {
                        // This is only here to support integration testing
                        // see com.atlassian.plugin.connect.test.pageobjects.RemotePage#isNotFullSize()
                        $(this.iframe).addClass("full-size-general-page-fail");
                    }
                })
            }
        };
    });

});

require("resize");
/**
 * Methods for showing the status of a connect-addon (loading, time'd-out etc)
 */

define("host/_status_helper", ["_dollar"], function ($) {
    "use strict";

    var statuses = {
        loading: {
            descriptionHtml: '<div class="small-spinner"></div>Loading add-on...'
        },
        "load-timeout": {
            descriptionHtml: '<div class="small-spinner"></div>Add-on is not responding. Wait or <a href="#" class="ap-btn-cancel">cancel</a>?'
        },

        "load-error": {
            descriptionHtml: 'Add-on failed to load.'
        }
    };

    function hideStatuses($home){
        // If there's a pending timer to show the loading status, kill it.
        if ($home.data('loadingStatusTimer')) {
            clearTimeout($home.data('loadingStatusTimer'));
            $home.removeData('loadingStatusTimer');
        }
        $home.find(".ap-status").addClass("hidden");
    }

    function showStatus($home, status){
        hideStatuses($home);
        $home.closest('.ap-container').removeClass('hidden');
        $home.find(".ap-stats").removeClass("hidden");
        $home.find('.ap-' + status).removeClass('hidden');
        /* setTimout fixes bug in AUI spinner positioning */
        setTimeout(function(){
            var spinner = $home.find('.small-spinner','.ap-' + status);
            if(spinner.length && spinner.spin){
                spinner.spin({zIndex: "1"});
            }
        }, 10);
    }

    //when an addon has loaded. Hide the status bar.
    function showLoadedStatus($home){
        hideStatuses($home);
    }

    function showLoadingStatus($home, delay){
        if (!delay) {
            showStatus($home, 'loading');
        } else {
            // Wait a second before showing loading status.
            var timer = setTimeout(showStatus.bind(null, $home, 'loading'), delay);
            $home.data('loadingStatusTimer', timer);
        }
    }

    function showloadTimeoutStatus($home){
        showStatus($home, 'load-timeout');
    }

    function showLoadErrorStatus($home){
        showStatus($home, 'load-error');
    }

    function createStatusMessages() {
        var i,
        stats = $('<div class="ap-stats" />');

        for(i in statuses){
            var status = $('<div class="ap-' + i + ' ap-status hidden" />');
            status.append('<small>' + statuses[i].descriptionHtml + '</small>');
            stats.append(status);
        }
        return stats;
    }

    return {
        createStatusMessages: createStatusMessages,
        showLoadingStatus: showLoadingStatus,
        showloadTimeoutStatus: showloadTimeoutStatus,
        showLoadErrorStatus: showLoadErrorStatus,
        showLoadedStatus: showLoadedStatus
    };

});

require(["_dollar", "_rpc", "host/_status_helper"], function ($, rpc, statusHelper) {
    "use strict";

    rpc.extend(function (config) {
        return {
            init: function (state, xdm) {
                var $home = $(xdm.iframe).closest(".ap-container");
                statusHelper.showLoadingStatus($home, 0);

                $home.find(".ap-load-timeout a.ap-btn-cancel").click(function () {
                    statusHelper.showLoadErrorStatus($home);
                    if(xdm.analytics && xdm.analytics.iframePerformance){
                        xdm.analytics.iframePerformance.cancel();
                    }
                });

                xdm.timeout = setTimeout(function(){
                    xdm.timeout = null;
                    statusHelper.showloadTimeoutStatus($home);
                    // if inactive, the iframe has been destroyed by the product.
                    if(xdm.isActive() && xdm.analytics && xdm.analytics.iframePerformance){
                        xdm.analytics.iframePerformance.timeout();
                    }
                }, 20000);
            },
            internals: {
                init: function() {
                    if(this.analytics && this.analytics.iframePerformance){
                        this.analytics.iframePerformance.end();
                    }
                    var $home = $(this.iframe).closest(".ap-container");
                    statusHelper.showLoadedStatus($home);

                    clearTimeout(this.timeout);
                    // Let the integration tests know the iframe has loaded.
                    $home.find(".ap-content").addClass("iframe-init");
                }
            }
        };

    });

});

/**
 * Utility methods for rendering connect addons in AUI components
 */

define("host/content", ["_dollar", "_uri"], function ($, uri) {
    "use strict";

    function getWebItemPluginKey(target){
        var cssClass = target.attr('class');
        var m = cssClass ? cssClass.match(/ap-plugin-key-([^\s]*)/) : null;
        return $.isArray(m) ? m[1] : false;
    }
    function getWebItemModuleKey(target){
        var cssClass = target.attr('class');
        var m = cssClass ? cssClass.match(/ap-module-key-([^\s]*)/) : null;
        return $.isArray(m) ? m[1] : false;
    }

    function getOptionsForWebItem(target){
        var moduleKey = getWebItemModuleKey(target),
            type = target.hasClass('ap-inline-dialog') ? 'inlineDialog' : 'dialog';
            return window._AP[type + 'Options'][moduleKey] || {};
    }

    function contextFromUrl (url) {
        var pairs = new uri.init(url).queryPairs;
        var obj = {};
        $.each(pairs, function (key, value) {
            obj[value[0]] = value[1];
        });
        return obj;
    }

    function eventHandler(action, selector, callback) {

        function domEventHandler(event) {
            event.preventDefault();
            var $el = $(event.target).closest(selector),
            href = $el.attr("href"),
            url = new uri.init(href),
            options = {
                bindTo: $el,
                header: $el.text(),
                width:  url.getQueryParamValue('width'),
                height: url.getQueryParamValue('height'),
                cp:     url.getQueryParamValue('cp'),
                key: getWebItemPluginKey($el),
                productContext: contextFromUrl(href)
            };
            callback(href, options, event.type);
        }

        $(window.document).on(action, selector, domEventHandler);

    }

    return {
        eventHandler: eventHandler,
        getOptionsForWebItem: getOptionsForWebItem,
        getWebItemPluginKey: getWebItemPluginKey,
        getWebItemModuleKey: getWebItemModuleKey
    };


});

/*global unescape, module, define, window, global*/

/*
 UriTemplate Copyright (c) 2012-2013 Franz Antesberger. All Rights Reserved.
 Available via the MIT license.
 */
(function (exportCallback) {
    "use strict";

    var UriTemplateError = (function () {

        function UriTemplateError (options) {
            this.options = options;
        }

        UriTemplateError.prototype.toString = function () {
            if (JSON && JSON.stringify) {
                return JSON.stringify(this.options);
            }
            else {
                return this.options;
            }
        };

        return UriTemplateError;
    }());

    var objectHelper = (function () {
        function isArray (value) {
            return Object.prototype.toString.apply(value) === '[object Array]';
        }

        function isString (value) {
            return Object.prototype.toString.apply(value) === '[object String]';
        }

        function isNumber (value) {
            return Object.prototype.toString.apply(value) === '[object Number]';
        }

        function isBoolean (value) {
            return Object.prototype.toString.apply(value) === '[object Boolean]';
        }

        function join (arr, separator) {
            var
                result = '',
                first = true,
                index;
            for (index = 0; index < arr.length; index += 1) {
                if (first) {
                    first = false;
                }
                else {
                    result += separator;
                }
                result += arr[index];
            }
            return result;
        }

        function map (arr, mapper) {
            var
                result = [],
                index = 0;
            for (; index < arr.length; index += 1) {
                result.push(mapper(arr[index]));
            }
            return result;
        }

        function filter (arr, predicate) {
            var
                result = [],
                index = 0;
            for (; index < arr.length; index += 1) {
                if (predicate(arr[index])) {
                    result.push(arr[index]);
                }
            }
            return result;
        }

        function deepFreezeUsingObjectFreeze (object) {
            if (typeof object !== "object" || object === null) {
                return object;
            }
            Object.freeze(object);
            var property, propertyName;
            for (propertyName in object) {
                if (object.hasOwnProperty(propertyName)) {
                    property = object[propertyName];
                    // be aware, arrays are 'object', too
                    if (typeof property === "object") {
                        deepFreeze(property);
                    }
                }
            }
            return object;
        }

        function deepFreeze (object) {
            if (typeof Object.freeze === 'function') {
                return deepFreezeUsingObjectFreeze(object);
            }
            return object;
        }


        return {
            isArray: isArray,
            isString: isString,
            isNumber: isNumber,
            isBoolean: isBoolean,
            join: join,
            map: map,
            filter: filter,
            deepFreeze: deepFreeze
        };
    }());

    var charHelper = (function () {

        function isAlpha (chr) {
            return (chr >= 'a' && chr <= 'z') || ((chr >= 'A' && chr <= 'Z'));
        }

        function isDigit (chr) {
            return chr >= '0' && chr <= '9';
        }

        function isHexDigit (chr) {
            return isDigit(chr) || (chr >= 'a' && chr <= 'f') || (chr >= 'A' && chr <= 'F');
        }

        return {
            isAlpha: isAlpha,
            isDigit: isDigit,
            isHexDigit: isHexDigit
        };
    }());

    var pctEncoder = (function () {
        var utf8 = {
            encode: function (chr) {
                // see http://ecmanaut.blogspot.de/2006/07/encoding-decoding-utf8-in-javascript.html
                return unescape(encodeURIComponent(chr));
            },
            numBytes: function (firstCharCode) {
                if (firstCharCode <= 0x7F) {
                    return 1;
                }
                else if (0xC2 <= firstCharCode && firstCharCode <= 0xDF) {
                    return 2;
                }
                else if (0xE0 <= firstCharCode && firstCharCode <= 0xEF) {
                    return 3;
                }
                else if (0xF0 <= firstCharCode && firstCharCode <= 0xF4) {
                    return 4;
                }
                // no valid first octet
                return 0;
            },
            isValidFollowingCharCode: function (charCode) {
                return 0x80 <= charCode && charCode <= 0xBF;
            }
        };

        /**
         * encodes a character, if needed or not.
         * @param chr
         * @return pct-encoded character
         */
        function encodeCharacter (chr) {
            var
                result = '',
                octets = utf8.encode(chr),
                octet,
                index;
            for (index = 0; index < octets.length; index += 1) {
                octet = octets.charCodeAt(index);
                result += '%' + (octet < 0x10 ? '0' : '') + octet.toString(16).toUpperCase();
            }
            return result;
        }

        /**
         * Returns, whether the given text at start is in the form 'percent hex-digit hex-digit', like '%3F'
         * @param text
         * @param start
         * @return {boolean|*|*}
         */
        function isPercentDigitDigit (text, start) {
            return text.charAt(start) === '%' && charHelper.isHexDigit(text.charAt(start + 1)) && charHelper.isHexDigit(text.charAt(start + 2));
        }

        /**
         * Parses a hex number from start with length 2.
         * @param text a string
         * @param start the start index of the 2-digit hex number
         * @return {Number}
         */
        function parseHex2 (text, start) {
            return parseInt(text.substr(start, 2), 16);
        }

        /**
         * Returns whether or not the given char sequence is a correctly pct-encoded sequence.
         * @param chr
         * @return {boolean}
         */
        function isPctEncoded (chr) {
            if (!isPercentDigitDigit(chr, 0)) {
                return false;
            }
            var firstCharCode = parseHex2(chr, 1);
            var numBytes = utf8.numBytes(firstCharCode);
            if (numBytes === 0) {
                return false;
            }
            for (var byteNumber = 1; byteNumber < numBytes; byteNumber += 1) {
                if (!isPercentDigitDigit(chr, 3*byteNumber) || !utf8.isValidFollowingCharCode(parseHex2(chr, 3*byteNumber + 1))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Reads as much as needed from the text, e.g. '%20' or '%C3%B6'. It does not decode!
         * @param text
         * @param startIndex
         * @return the character or pct-string of the text at startIndex
         */
        function pctCharAt(text, startIndex) {
            var chr = text.charAt(startIndex);
            if (!isPercentDigitDigit(text, startIndex)) {
                return chr;
            }
            var utf8CharCode = parseHex2(text, startIndex + 1);
            var numBytes = utf8.numBytes(utf8CharCode);
            if (numBytes === 0) {
                return chr;
            }
            for (var byteNumber = 1; byteNumber < numBytes; byteNumber += 1) {
                if (!isPercentDigitDigit(text, startIndex + 3 * byteNumber) || !utf8.isValidFollowingCharCode(parseHex2(text, startIndex + 3 * byteNumber + 1))) {
                    return chr;
                }
            }
            return text.substr(startIndex, 3 * numBytes);
        }

        return {
            encodeCharacter: encodeCharacter,
            isPctEncoded: isPctEncoded,
            pctCharAt: pctCharAt
        };
    }());

    var rfcCharHelper = (function () {

        /**
         * Returns if an character is an varchar character according 2.3 of rfc 6570
         * @param chr
         * @return (Boolean)
         */
        function isVarchar (chr) {
            return charHelper.isAlpha(chr) || charHelper.isDigit(chr) || chr === '_' || pctEncoder.isPctEncoded(chr);
        }

        /**
         * Returns if chr is an unreserved character according 1.5 of rfc 6570
         * @param chr
         * @return {Boolean}
         */
        function isUnreserved (chr) {
            return charHelper.isAlpha(chr) || charHelper.isDigit(chr) || chr === '-' || chr === '.' || chr === '_' || chr === '~';
        }

        /**
         * Returns if chr is an reserved character according 1.5 of rfc 6570
         * or the percent character mentioned in 3.2.1.
         * @param chr
         * @return {Boolean}
         */
        function isReserved (chr) {
            return chr === ':' || chr === '/' || chr === '?' || chr === '#' || chr === '[' || chr === ']' || chr === '@' || chr === '!' || chr === '$' || chr === '&' || chr === '(' ||
                chr === ')' || chr === '*' || chr === '+' || chr === ',' || chr === ';' || chr === '=' || chr === "'";
        }

        return {
            isVarchar: isVarchar,
            isUnreserved: isUnreserved,
            isReserved: isReserved
        };

    }());

    /**
     * encoding of rfc 6570
     */
    var encodingHelper = (function () {

        function encode (text, passReserved) {
            var
                result = '',
                index,
                chr = '';
            if (typeof text === "number" || typeof text === "boolean") {
                text = text.toString();
            }
            for (index = 0; index < text.length; index += chr.length) {
                chr = text.charAt(index);
                result += rfcCharHelper.isUnreserved(chr) || (passReserved && rfcCharHelper.isReserved(chr)) ? chr : pctEncoder.encodeCharacter(chr);
            }
            return result;
        }

        function encodePassReserved (text) {
            return encode(text, true);
        }

        function encodeLiteralCharacter (literal, index) {
            var chr = pctEncoder.pctCharAt(literal, index);
            if (chr.length > 1) {
                return chr;
            }
            else {
                return rfcCharHelper.isReserved(chr) || rfcCharHelper.isUnreserved(chr) ? chr : pctEncoder.encodeCharacter(chr);
            }
        }

        function encodeLiteral (literal) {
            var
                result = '',
                index,
                chr = '';
            for (index = 0; index < literal.length; index += chr.length) {
                chr = pctEncoder.pctCharAt(literal, index);
                if (chr.length > 1) {
                    result += chr;
                }
                else {
                    result += rfcCharHelper.isReserved(chr) || rfcCharHelper.isUnreserved(chr) ? chr : pctEncoder.encodeCharacter(chr);
                }
            }
            return result;
        }

        return {
            encode: encode,
            encodePassReserved: encodePassReserved,
            encodeLiteral: encodeLiteral,
            encodeLiteralCharacter: encodeLiteralCharacter
        };

    }());


// the operators defined by rfc 6570
    var operators = (function () {

        var
            bySymbol = {};

        function create (symbol) {
            bySymbol[symbol] = {
                symbol: symbol,
                separator: (symbol === '?') ? '&' : (symbol === '' || symbol === '+' || symbol === '#') ? ',' : symbol,
                named: symbol === ';' || symbol === '&' || symbol === '?',
                ifEmpty: (symbol === '&' || symbol === '?') ? '=' : '',
                first: (symbol === '+' ) ? '' : symbol,
                encode: (symbol === '+' || symbol === '#') ? encodingHelper.encodePassReserved : encodingHelper.encode,
                toString: function () {
                    return this.symbol;
                }
            };
        }

        create('');
        create('+');
        create('#');
        create('.');
        create('/');
        create(';');
        create('?');
        create('&');
        return {
            valueOf: function (chr) {
                if (bySymbol[chr]) {
                    return bySymbol[chr];
                }
                if ("=,!@|".indexOf(chr) >= 0) {
                    return null;
                }
                return bySymbol[''];
            }
        };
    }());


    /**
     * Detects, whether a given element is defined in the sense of rfc 6570
     * Section 2.3 of the RFC makes clear defintions:
     * * undefined and null are not defined.
     * * the empty string is defined
     * * an array ("list") is defined, if it is not empty (even if all elements are not defined)
     * * an object ("map") is defined, if it contains at least one property with defined value
     * @param object
     * @return {Boolean}
     */
    function isDefined (object) {
        var
            propertyName;
        if (object === null || object === undefined) {
            return false;
        }
        if (objectHelper.isArray(object)) {
            // Section 2.3: A variable defined as a list value is considered undefined if the list contains zero members
            return object.length > 0;
        }
        if (typeof object === "string" || typeof object === "number" || typeof object === "boolean") {
            // falsy values like empty strings, false or 0 are "defined"
            return true;
        }
        // else Object
        for (propertyName in object) {
            if (object.hasOwnProperty(propertyName) && isDefined(object[propertyName])) {
                return true;
            }
        }
        return false;
    }

    var LiteralExpression = (function () {
        function LiteralExpression (literal) {
            this.literal = encodingHelper.encodeLiteral(literal);
        }

        LiteralExpression.prototype.expand = function () {
            return this.literal;
        };

        LiteralExpression.prototype.toString = LiteralExpression.prototype.expand;

        return LiteralExpression;
    }());

    var parse = (function () {

        function parseExpression (expressionText) {
            var
                operator,
                varspecs = [],
                varspec = null,
                varnameStart = null,
                maxLengthStart = null,
                index,
                chr = '';

            function closeVarname () {
                var varname = expressionText.substring(varnameStart, index);
                if (varname.length === 0) {
                    throw new UriTemplateError({expressionText: expressionText, message: "a varname must be specified", position: index});
                }
                varspec = {varname: varname, exploded: false, maxLength: null};
                varnameStart = null;
            }

            function closeMaxLength () {
                if (maxLengthStart === index) {
                    throw new UriTemplateError({expressionText: expressionText, message: "after a ':' you have to specify the length", position: index});
                }
                varspec.maxLength = parseInt(expressionText.substring(maxLengthStart, index), 10);
                maxLengthStart = null;
            }

            operator = (function (operatorText) {
                var op = operators.valueOf(operatorText);
                if (op === null) {
                    throw new UriTemplateError({expressionText: expressionText, message: "illegal use of reserved operator", position: index, operator: operatorText});
                }
                return op;
            }(expressionText.charAt(0)));
            index = operator.symbol.length;

            varnameStart = index;

            for (; index < expressionText.length; index += chr.length) {
                chr = pctEncoder.pctCharAt(expressionText, index);

                if (varnameStart !== null) {
                    // the spec says: varname =  varchar *( ["."] varchar )
                    // so a dot is allowed except for the first char
                    if (chr === '.') {
                        if (varnameStart === index) {
                            throw new UriTemplateError({expressionText: expressionText, message: "a varname MUST NOT start with a dot", position: index});
                        }
                        continue;
                    }
                    if (rfcCharHelper.isVarchar(chr)) {
                        continue;
                    }
                    closeVarname();
                }
                if (maxLengthStart !== null) {
                    if (index === maxLengthStart && chr === '0') {
                        throw new UriTemplateError({expressionText: expressionText, message: "A :prefix must not start with digit 0", position: index});
                    }
                    if (charHelper.isDigit(chr)) {
                        if (index - maxLengthStart >= 4) {
                            throw new UriTemplateError({expressionText: expressionText, message: "A :prefix must have max 4 digits", position: index});
                        }
                        continue;
                    }
                    closeMaxLength();
                }
                if (chr === ':') {
                    if (varspec.maxLength !== null) {
                        throw new UriTemplateError({expressionText: expressionText, message: "only one :maxLength is allowed per varspec", position: index});
                    }
                    if (varspec.exploded) {
                        throw new UriTemplateError({expressionText: expressionText, message: "an exploeded varspec MUST NOT be varspeced", position: index});
                    }
                    maxLengthStart = index + 1;
                    continue;
                }
                if (chr === '*') {
                    if (varspec === null) {
                        throw new UriTemplateError({expressionText: expressionText, message: "exploded without varspec", position: index});
                    }
                    if (varspec.exploded) {
                        throw new UriTemplateError({expressionText: expressionText, message: "exploded twice", position: index});
                    }
                    if (varspec.maxLength) {
                        throw new UriTemplateError({expressionText: expressionText, message: "an explode (*) MUST NOT follow to a prefix", position: index});
                    }
                    varspec.exploded = true;
                    continue;
                }
                // the only legal character now is the comma
                if (chr === ',') {
                    varspecs.push(varspec);
                    varspec = null;
                    varnameStart = index + 1;
                    continue;
                }
                throw new UriTemplateError({expressionText: expressionText, message: "illegal character", character: chr, position: index});
            } // for chr
            if (varnameStart !== null) {
                closeVarname();
            }
            if (maxLengthStart !== null) {
                closeMaxLength();
            }
            varspecs.push(varspec);
            return new VariableExpression(expressionText, operator, varspecs);
        }

        function parse (uriTemplateText) {
            // assert filled string
            var
                index,
                chr,
                expressions = [],
                braceOpenIndex = null,
                literalStart = 0;
            for (index = 0; index < uriTemplateText.length; index += 1) {
                chr = uriTemplateText.charAt(index);
                if (literalStart !== null) {
                    if (chr === '}') {
                        throw new UriTemplateError({templateText: uriTemplateText, message: "unopened brace closed", position: index});
                    }
                    if (chr === '{') {
                        if (literalStart < index) {
                            expressions.push(new LiteralExpression(uriTemplateText.substring(literalStart, index)));
                        }
                        literalStart = null;
                        braceOpenIndex = index;
                    }
                    continue;
                }

                if (braceOpenIndex !== null) {
                    // here just { is forbidden
                    if (chr === '{') {
                        throw new UriTemplateError({templateText: uriTemplateText, message: "brace already opened", position: index});
                    }
                    if (chr === '}') {
                        if (braceOpenIndex + 1 === index) {
                            throw new UriTemplateError({templateText: uriTemplateText, message: "empty braces", position: braceOpenIndex});
                        }
                        try {
                            expressions.push(parseExpression(uriTemplateText.substring(braceOpenIndex + 1, index)));
                        }
                        catch (error) {
                            if (error.prototype === UriTemplateError.prototype) {
                                throw new UriTemplateError({templateText: uriTemplateText, message: error.options.message, position: braceOpenIndex + error.options.position, details: error.options});
                            }
                            throw error;
                        }
                        braceOpenIndex = null;
                        literalStart = index + 1;
                    }
                    continue;
                }
                throw new Error('reached unreachable code');
            }
            if (braceOpenIndex !== null) {
                throw new UriTemplateError({templateText: uriTemplateText, message: "unclosed brace", position: braceOpenIndex});
            }
            if (literalStart < uriTemplateText.length) {
                expressions.push(new LiteralExpression(uriTemplateText.substr(literalStart)));
            }
            return new UriTemplate(uriTemplateText, expressions);
        }

        return parse;
    }());

    var VariableExpression = (function () {
        // helper function if JSON is not available
        function prettyPrint (value) {
            return (JSON && JSON.stringify) ? JSON.stringify(value) : value;
        }

        function isEmpty (value) {
            if (!isDefined(value)) {
                return true;
            }
            if (objectHelper.isString(value)) {
                return value === '';
            }
            if (objectHelper.isNumber(value) || objectHelper.isBoolean(value)) {
                return false;
            }
            if (objectHelper.isArray(value)) {
                return value.length === 0;
            }
            for (var propertyName in value) {
                if (value.hasOwnProperty(propertyName)) {
                    return false;
                }
            }
            return true;
        }

        function propertyArray (object) {
            var
                result = [],
                propertyName;
            for (propertyName in object) {
                if (object.hasOwnProperty(propertyName)) {
                    result.push({name: propertyName, value: object[propertyName]});
                }
            }
            return result;
        }

        function VariableExpression (templateText, operator, varspecs) {
            this.templateText = templateText;
            this.operator = operator;
            this.varspecs = varspecs;
        }

        VariableExpression.prototype.toString = function () {
            return this.templateText;
        };

        function expandSimpleValue(varspec, operator, value) {
            var result = '';
            value = value.toString();
            if (operator.named) {
                result += encodingHelper.encodeLiteral(varspec.varname);
                if (value === '') {
                    result += operator.ifEmpty;
                    return result;
                }
                result += '=';
            }
            if (varspec.maxLength !== null) {
                value = value.substr(0, varspec.maxLength);
            }
            result += operator.encode(value);
            return result;
        }

        function valueDefined (nameValue) {
            return isDefined(nameValue.value);
        }

        function expandNotExploded(varspec, operator, value) {
            var
                arr = [],
                result = '';
            if (operator.named) {
                result += encodingHelper.encodeLiteral(varspec.varname);
                if (isEmpty(value)) {
                    result += operator.ifEmpty;
                    return result;
                }
                result += '=';
            }
            if (objectHelper.isArray(value)) {
                arr = value;
                arr = objectHelper.filter(arr, isDefined);
                arr = objectHelper.map(arr, operator.encode);
                result += objectHelper.join(arr, ',');
            }
            else {
                arr = propertyArray(value);
                arr = objectHelper.filter(arr, valueDefined);
                arr = objectHelper.map(arr, function (nameValue) {
                    return operator.encode(nameValue.name) + ',' + operator.encode(nameValue.value);
                });
                result += objectHelper.join(arr, ',');
            }
            return result;
        }

        function expandExplodedNamed (varspec, operator, value) {
            var
                isArray = objectHelper.isArray(value),
                arr = [];
            if (isArray) {
                arr = value;
                arr = objectHelper.filter(arr, isDefined);
                arr = objectHelper.map(arr, function (listElement) {
                    var tmp = encodingHelper.encodeLiteral(varspec.varname);
                    if (isEmpty(listElement)) {
                        tmp += operator.ifEmpty;
                    }
                    else {
                        tmp += '=' + operator.encode(listElement);
                    }
                    return tmp;
                });
            }
            else {
                arr = propertyArray(value);
                arr = objectHelper.filter(arr, valueDefined);
                arr = objectHelper.map(arr, function (nameValue) {
                    var tmp = encodingHelper.encodeLiteral(nameValue.name);
                    if (isEmpty(nameValue.value)) {
                        tmp += operator.ifEmpty;
                    }
                    else {
                        tmp += '=' + operator.encode(nameValue.value);
                    }
                    return tmp;
                });
            }
            return objectHelper.join(arr, operator.separator);
        }

        function expandExplodedUnnamed (operator, value) {
            var
                arr = [],
                result = '';
            if (objectHelper.isArray(value)) {
                arr = value;
                arr = objectHelper.filter(arr, isDefined);
                arr = objectHelper.map(arr, operator.encode);
                result += objectHelper.join(arr, operator.separator);
            }
            else {
                arr = propertyArray(value);
                arr = objectHelper.filter(arr, function (nameValue) {
                    return isDefined(nameValue.value);
                });
                arr = objectHelper.map(arr, function (nameValue) {
                    return operator.encode(nameValue.name) + '=' + operator.encode(nameValue.value);
                });
                result += objectHelper.join(arr, operator.separator);
            }
            return result;
        }


        VariableExpression.prototype.expand = function (variables) {
            var
                expanded = [],
                index,
                varspec,
                value,
                valueIsArr,
                oneExploded = false,
                operator = this.operator;

            // expand each varspec and join with operator's separator
            for (index = 0; index < this.varspecs.length; index += 1) {
                varspec = this.varspecs[index];
                value = variables[varspec.varname];
                // if (!isDefined(value)) {
                // if (variables.hasOwnProperty(varspec.name)) {
                if (value === null || value === undefined) {
                    continue;
                }
                if (varspec.exploded) {
                    oneExploded = true;
                }
                valueIsArr = objectHelper.isArray(value);
                if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
                    expanded.push(expandSimpleValue(varspec, operator, value));
                }
                else if (varspec.maxLength && isDefined(value)) {
                    // 2.4.1 of the spec says: "Prefix modifiers are not applicable to variables that have composite values."
                    throw new Error('Prefix modifiers are not applicable to variables that have composite values. You tried to expand ' + this + " with " + prettyPrint(value));
                }
                else if (!varspec.exploded) {
                    if (operator.named || !isEmpty(value)) {
                        expanded.push(expandNotExploded(varspec, operator, value));
                    }
                }
                else if (isDefined(value)) {
                    if (operator.named) {
                        expanded.push(expandExplodedNamed(varspec, operator, value));
                    }
                    else {
                        expanded.push(expandExplodedUnnamed(operator, value));
                    }
                }
            }

            if (expanded.length === 0) {
                return "";
            }
            else {
                return operator.first + objectHelper.join(expanded, operator.separator);
            }
        };

        return VariableExpression;
    }());

    var UriTemplate = (function () {
        function UriTemplate (templateText, expressions) {
            this.templateText = templateText;
            this.expressions = expressions;
            objectHelper.deepFreeze(this);
        }

        UriTemplate.prototype.toString = function () {
            return this.templateText;
        };

        UriTemplate.prototype.expand = function (variables) {
            // this.expressions.map(function (expression) {return expression.expand(variables);}).join('');
            var
                index,
                result = '';
            for (index = 0; index < this.expressions.length; index += 1) {
                result += this.expressions[index].expand(variables);
            }
            return result;
        };

        UriTemplate.parse = parse;
        UriTemplate.UriTemplateError = UriTemplateError;
        return UriTemplate;
    }());

    exportCallback(UriTemplate);

}(function (UriTemplate) {
        "use strict";
        // export UriTemplate, when module is present, or pass it to window or global
        if (typeof define === "function") {
            define("_uritemplate",[],function() {
                return UriTemplate;
            });
        }
        else if (typeof window !== "undefined") {
            window.UriTemplate = UriTemplate;
        }
        else {
            global.UriTemplate = UriTemplate;
        }
    }
));
require(["_dollar", "_rpc"], function ($, rpc) {

  "use strict";

  // Note that if it's desireable to publish host-level events to add-ons, this would be a good place to wire
  // up host listeners and publish to each add-on, rather than using each XdmRpc.events object directly.

    var _channels = {};

  // Tracks all channels (iframes with an XDM bridge) for a given add-on key, managing event propagation
  // between bridges, and potentially between add-ons.

    rpc.extend(function () {

        var self = {
            _emitEvent: function (event) {
                $.each(_channels[event.source.key], function (id, channel) {
                    channel.bus._emitEvent(event);
                });
            },
            remove: function (xdm) {
                var channel = _channels[xdm.addonKey][xdm.id];
                if (channel) {
                    channel.bus.offAny(channel.listener);
                }
                delete _channels[xdm.addonKey][xdm.id];
                return this;
            },
            init: function (config, xdm) {
                if(!_channels[xdm.addonKey]){
                    _channels[xdm.addonKey] = {};
                }
                var channel = _channels[xdm.addonKey][xdm.id] = {
                    bus: xdm.events,
                    listener: function () {
                        var event = arguments[arguments.length - 1];
                        var trace = event.trace = event.trace || {};
                        var traceKey = xdm.id + "|addon";
                        if (!trace[traceKey]) {
                            // Only forward an event once in this listener
                            trace[traceKey] = true;
                            self._emitEvent(event);
                        }
                    }
                };
                channel.bus.onAny(channel.listener); //forward add-on events.

                // Remove reference to destroyed iframes such as closed dialogs.
                channel.bus.on("ra.iframe.destroy", function(){
                    self.remove(xdm);
                }); 
            }
        };
        return self;
    });

});

define("analytics/analytics", ["_dollar"], function($){
    "use strict";

    /**
     * Blacklist certain bridge functions from being sent to analytics
     * @const
     * @type {Array}
     */
    var BRIDGEMETHODBLACKLIST = [
        "resize",
        "init"
    ];

    /**
     * Timings beyond 20 seconds (connect's load timeout) will be clipped to an X.
     * @const
     * @type {int}
     */
    var THRESHOLD = 20000;

    /**
     * Trim extra zeros from the load time.
     * @const
     * @type {int}
     */
    var TRIMPPRECISION = 100;

    function time() {
        return window.performance && window.performance.now ? window.performance.now() : new Date().getTime();
    }

    /**
     * Initialises Connect analytics module. This module provides events for iframe performance (loaded, timed out, canceled).
     *
     * @param viewData.addonKey - addon's key
     * @param viewData.moduleKey - module's key.
     */
    function Analytics(viewData) {
        var metrics = {};
        this.addonKey = viewData.addonKey;
        this.moduleKey = viewData.moduleKey;
        this.iframePerformance = {
            start: function(){
                metrics.startLoading = time();
            },
            end: function(){
                var value = time() - metrics.startLoading;
                proto.track('iframe.performance.load', {
                    addonKey: viewData.addonKey,
                    moduleKey: viewData.moduleKey,
                    value: value > THRESHOLD ? 'x' : Math.ceil((value) / TRIMPPRECISION)
                });
                delete metrics.startLoading;
            },
            timeout: function(){
                proto.track('iframe.performance.timeout', {
                    addonKey: viewData.addonKey,
                    moduleKey: viewData.moduleKey
                });
                //track an end event during a timeout so we always have complete start / end data.
                this.end();
            },
            // User clicked cancel button during loading
            cancel: function(){
                proto.track('iframe.performance.cancel', {
                    addonKey: viewData.addonKey,
                    moduleKey: viewData.moduleKey
                });
            }
        };

    }

    var proto = Analytics.prototype;

    proto.getKey = function () {
        return this.addonKey + ':' + this.moduleKey;
    };

    proto.track = function (name, data) {
        var prefixedName = "connect.addon." + name;
        if(AJS.Analytics){
            AJS.Analytics.triggerPrivacyPolicySafeEvent(prefixedName, data);
        } else if(AJS.trigger) {
            // BTF fallback
            AJS.trigger('analyticsEvent', {
                name: prefixedName,
                data: data
            });
        } else {
            return false;
        }

        return true;
    };

    proto.trackBridgeMethod = function(name){
        if($.inArray(name, BRIDGEMETHODBLACKLIST) !== -1){
            return false;
        }
        this.track('bridge.invokemethod', {
            name: name,
            addonKey: this.addonKey,
            moduleKey: this.moduleKey
        });
    };

    return {
        get: function (viewData) {
            return new Analytics(viewData);
        }
    };

});

(function(context){
  "use strict";

  define('host/create', ["_dollar","host/_util", "_rpc", "_ui-params", "analytics/analytics"], function($, utils, rpc, uiParams, analytics){

      var defer = window.requestAnimationFrame || function (f) {setTimeout(f,10); };

      function contentDiv(ns) {
          if(!ns){
            throw new Error("ns undefined");
          }
          return $("#embedded-" + utils.escapeSelector(ns));
      }

      /**
      * @name Options
      * @class
      * @property {String}  ns            module key
      * @property {String}  src           url of the iframe
      * @property {String}  w             width of the iframe
      * @property {String}  h             height of the iframe
      * @property {String}  dlg           is a dialog (disables the resizer)
      * @property {String}  simpleDlg     deprecated, looks to be set when a confluence macro editor is being rendered as a dialog
      * @property {Boolean} general       is a page that can be resized
      * @property {String}  productCtx    context to pass back to the server (project id, space id, etc)
      * @property {String}  key           addon key from the descriptor
      * @property {String}  uid           id of the current user
      * @property {String}  ukey          user key
      * @property {String}  data.timeZone timezone of the current user
      * @property {String}  cp            context path
      * @property {String}  origin        origin address of the add-on, required when src does not point directly to the add-on
      */

      /**
      * @param {Options} options These values come from the velocity template and can be overridden using uiParams
      */
      function create(options) {
      if(typeof options.uiParams !== "object"){
        options.uiParams = uiParams.fromUrl(options.src);
      }

      var ns = options.ns,
          contentId = "embedded-" + ns,
          channelId = "channel-" + ns,
          initWidth = options.w || "100%",
          initHeight = options.h || "0";

      if(typeof options.uiParams !== "object"){
        options.uiParams = {};
      }

      if(!!options.general) {
        options.uiParams.isGeneral = true;
      }

      var xdmOptions = {
        remote: options.src,
        remoteOrigin: options.origin,
        remoteKey: options.key,
        container: contentId,
        channel: channelId,
        props: {width: initWidth, height: initHeight},
        uiParams: options.uiParams
      };

      if(options.productCtx && !options.productContext){
        options.productContext = JSON.parse(options.productCtx);
      }

      rpc.extend({
        init: function(opts, xdm){
          xdm.analytics = analytics.get({addonKey: xdm.addonKey, moduleKey: ns});
          xdm.analytics.iframePerformance.start();
          xdm.productContext = options.productContext;
        }
      });

      rpc.init(options, xdmOptions);

    }

    return function (options) {

      var attemptCounter = 0;
      function doCreate() {
          //If the element we are going to append the iframe to doesn't exist in the dom (yet). Wait for it to appear.
          if(contentDiv(options.ns).length === 0 && attemptCounter < 10){
              setTimeout(function(){
                  attemptCounter++;
                  doCreate();
              }, 50);
              return;
          }

        // create the new iframe
        create(options);
      }
      if (AJS.$.isReady) {
        // if the dom is ready then this is being run during an ajax update;
        // in that case, defer creation until the next event loop tick to ensure
        // that updates to the desired container node's parents have completed
        defer(doCreate);
      } else {
        $(doCreate);
      }

    };

  });

}(this));


    var rpc = require("_rpc");

    return {
        extend: rpc.extend,
        init: rpc.init,
        uiParams: require("_ui-params"),
        create: require('host/create'),
        _uriHelper: require('_uri'),
        _statusHelper: require('host/_status_helper'),
        webItemHelper: require('host/content')
    };
}));