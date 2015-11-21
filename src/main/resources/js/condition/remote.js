(function($, context){
  var hidden;

  function hide() {
    if (!hidden) {
      hidden = true;
      // Connect any Remotable Plugin hosted Web Items to a dialog that loads the appropriate IFrame Servlet,
      // look for jira issue tabs, and look for jira project tabs
      $(".remote-condition, #issue-tabs a[id$='-remote-condition'], .tabs a[id$='-remote-condition-panel']").each(function (i, el) {
        var el$ = $(el), parent$ = el$.parent();
        el$.addClass("hidden");
        if (parent$[0].tagName == "LI") {
          parent$.addClass("hidden");
        }
      });
    }
  }

  AJS.toInit(hide);

  context.RemoteConditions = {
    /**
     * Hides UI elements that are protected by remote conditions. These are all hacks hiding is supported
     * by the module types directly
     */
    hide: hide
  };

})(AJS.$, _AP);
