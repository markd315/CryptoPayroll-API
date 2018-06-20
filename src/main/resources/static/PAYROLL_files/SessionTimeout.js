function USSessionTimeout() { }
USSessionTimeout.OriginalDuration = 120 * 1000;
USSessionTimeout.TimeRemaining = USSessionTimeout.OriginalDuration;
USSessionTimeout.WakeUpAndCheckInterval = 5 * 1000;
USSessionTimeout.PingSessionInterval = 50 * 1000;
USSessionTimeout.PingTimeRemaining = USSessionTimeout.PingSessionInterval;
USSessionTimeout.WarningThreshold = 120 * 1000;
USSessionTimeout.UserWarned = false;
USSessionTimeout.KeepSessionAlive = false;
USSessionTimeout.HasComplementaryPageInSession = false;

var pingSessionCallback = function (response) {
    if (response === "maxSessionAgeLimit")
        windowMain().USSessionTimeout.shutDown(false);
};

USSessionTimeout.pingSession = function () {
    $.usService("CommonService", "PingSession", { "ping": "true" }, pingSessionCallback);
};

USSessionTimeout.updateTimeout = function () {
    USSessionTimeout.TimeRemaining = USSessionTimeout.OriginalDuration;
    USSessionTimeout.clearSessionWarnings();
};

USSessionTimeout.shutDown = function (timedout) {
    var location = GlobalVars["RootVD"] + "/logout.aspx";
    if (timedout) 
        location += "?r=sessiontimeout";
    window.location = location;
};

USSessionTimeout.runDownTime = function () {
    USSessionTimeout.PingTimeRemaining -= USSessionTimeout.WakeUpAndCheckInterval;
    USSessionTimeout.TimeRemaining -= USSessionTimeout.WakeUpAndCheckInterval;

    if (USSessionTimeout.PingTimeRemaining <= 0) {
        USSessionTimeout.pingSession();
        USSessionTimeout.PingTimeRemaining = USSessionTimeout.PingSessionInterval;
    }

    if (USSessionTimeout.TimeRemaining <= USSessionTimeout.WarningThreshold) {
        if (!USSessionTimeout.UserWarned) {
            USSessionTimeout.warnSessionTimeout();
        }
        
        if (USSessionTimeout.TimeRemaining < 0) {
            USSessionTimeout.shutDown(true);
        }
    }
};

USSessionTimeout.warnSessionTimeout = function () {
    USSessionTimeout.pingSession();

    var container = $('<div>').append($('#timeOutDialogContainer').clone()).html();

    function initialize(win) {
        $('body').trigger('warning.sessiontimeout');
        var timeoutDialog = win.$('#timeOutDialog');
        timeoutDialog.modal('show');
        timeoutDialog.find("#theCountDownDiv").countdown(
            {
                until: USSessionTimeout.TimeRemaining / 1000,
                compact: true,
                layout: '{mn}:{snn}',
                description: '',
                onExpiry: function () { USSessionTimeout.shutDown(true); }
            }
        );
    }

    initialize(window);

    WindowManager.executeOnChildrenRecursively(function () {
        this.$('body').append(container);
        initialize(this);
    });

    USSessionTimeout.UserWarned = true;
};

USSessionTimeout.clearSessionWarnings = function () {
    $('#timeOutDialog').modal('hide');
    $("#theCountDownDiv").countdown('destroy');

    WindowManager.executeOnChildrenRecursively(function () {
        this.$('#timeOutDialog').modal('hide');
        this.$("#theCountDownDiv").countdown('destroy');
        this.$('#timeOutDialogContainer').remove();
    });

    USSessionTimeout.pingSession();

    USSessionTimeout.UserWarned = false;
};
