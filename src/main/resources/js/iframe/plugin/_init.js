// @todo make product-specific inclusions (e.g. jira) dynamic
AP.require(
  ["_dollar", "_rpc", "_resize_listener", "env", "request", "dialog", "jira", "confluence"],

  function ($, rpc, resizeListener, env, request, dialog, jira, confluence) {
  "use strict";

  function injectBase() {
    // set the url base
    env.getLocation(function (loc) {
      $("head").append({tag: "base", href: loc, target: "_parent"});
    });
  }

  // This is required for connect to work correctly in IE8
  function injectRenderModeMeta(){
    var meta = document.createElement("meta"),
      head = document.head || document.getElementsByTagName("head")[0],
      tagExists = false;

    //don't stomp on existing meta tag.
    $("meta").each(function(i, m){
      if(m.getAttribute('http-equiv') === 'X-UA-Compatible'){
        tagExists = true;
        return false;
      }
    });

    if(tagExists === false){
      meta.setAttribute("http-equiv","X-UA-Compatible");
      meta.setAttribute("content","IE=edge");
      head.appendChild(meta);
    }
  }

  function injectMargin() {
    // set a context-sensitive margin value
    var margin = dialog.isDialog ? "10px 10px 0 10px" : "0";
    // @todo stylesheet injection here is rather heavy handed -- switch to setting body style
    $("head").append({tag: "style", type: "text/css", $text: "body {margin: " + margin + " !important;}"});
  }

  rpc.extend({

    init: function (options) {
      // integrate the iframe with the host document
      if (options.margin !== false) {
        // inject an appropriate margin value
        injectMargin(options);
      }
      if (options.base === true) {
        // inject an appropriate base tag
        injectBase(options);
      }

      //JSON is undefined if you're in IE8 without the meta tag.
      if(options.injectRenderModeMeta !== false || this.JSON === undefined){
        // sets IE's render mode. It is required for connect and IE8.
        injectRenderModeMeta();
      }

      if (options.sizeToParent) {
        env.sizeToParent(options.hideFooter === true);
      }
      else if (options.resize !== false) {
        var rate = options.resize;
        if(options.resize === undefined){
          rate = "auto";
        }
        rate = rate === "auto" ? 125 : +rate;
        // force rate to an acceptable minimum if it's a number
        if (rate >= 0 && rate < 60) rate = 60;
        if (!dialog.isDialog && rate > 0) {
          // auto-resize when size changes are detected
          $.bind(window, "load", function () {
            var last;
            setInterval(function () {
              var curr = env.size();
              if (!last || last.w !== curr.w || last.h !== curr.h) {
                env.resize(curr.w, curr.h);
                last = curr;
              }
            }, rate);
          });
        }
        else {
          // resize the parent iframe for the size of this document on load
          $.bind(window, "load", function () {
              env.resize();
              var rootElem = env.container();
              if(rootElem) {
                  resizeListener.addListener(rootElem, function(){
                      env.resize();
                  });
              } else {
                  $.log("Your page should have a root block element with an ID called #content or class called .ac-content if you want your page to dynamically resize after the initial load.");
              }
          });
        }
      }
    }

  });

  // @todo remove this before final release once all clients have had a chance to move to AMD
  // deprecated, backward-compatibility extension of AP for pre-AMD plugins
  $.extend(AP, env, jira, {
    Meta: {get: env.meta},
    request: request,
    Dialog: dialog
  });

  // initialization

  // find the script element that imported this code
  var options = {},
      $script = $("script[src*='/atlassian-connect/all']");

  if ( !($script && /\/atlassian-connect\/all(-debug)?\.js($|\?)/.test($script.attr("src"))) ){
    $script = $("#ac-iframe-options");
  }

  if($script && $script.length > 0) {
    // get its data-options attribute, if any
    var optStr = $script.attr("data-options");
    if (optStr) {
      // if found, parse the value into kv pairs following the format of a style element
      $.each(optStr.split(";"), function (i, nvpair) {
        var trim = $.trim;
        nvpair = trim(nvpair);
        if (nvpair) {
          var nv = nvpair.split(":"), k = trim(nv[0]), v = trim(nv[1]);
          if (k && v != null) options[k] = v === "true" || v === "false" ? v === "true" : v;
        }
      });
    }
  }

 rpc.init(options);

});
