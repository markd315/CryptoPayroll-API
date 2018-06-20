//
// manage the functionality to show/hide the right panel, TICD/QuickTours
// 
function RightPanelManager(elementId, resizeFunction) {
    this.controller = $(getCachedElement("toggleRight"));
    this.rightContainer = $(getCachedElement("containerRight"));

    this.rootElement = $(getCachedElement(elementId));
    this.resize = resizeFunction;
}

// call service to update user page preferences
RightPanelManager.prototype.updatePagePreference = function (pageId, value) {
    $.usService("CommonService", "SetPageRightContentPreference", { "pageId": pageId, "value": value }, null);
};

// toggle display of right content
RightPanelManager.prototype.toggleRightContent = function () {
    if (this.controller.hasClass("right")) {
        this.hideRightContent();
    }
    else {
        this.showRightContent();
    }
};

// show right panel
RightPanelManager.prototype.showRightContent = function () {
    this.controller.addClass("right").removeClass("left");
    this.rootElement.addClass("contentContainer").removeClass("contentContainerNoRight");
    this.rightContainer.show();
    this.resize();
    this.updatePagePreference(window.location.pathname, false);
};

// hide right panel
RightPanelManager.prototype.hideRightContent = function () {
    this.controller.addClass("left").removeClass("right");
    this.rootElement.addClass("contentContainerNoRight").removeClass("contentContainer");
    this.rightContainer.hide();
    this.resize();
    this.updatePagePreference(window.location.pathname, true);
};