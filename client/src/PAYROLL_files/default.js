notifyPageLoaded();
var isChangingCompany = false;
function changeCompany() {
    isChangingCompany = true;
    hideTimer();
    var confirmed = confirm(lstrChangeCoWarning);
    if (confirmed) {
        WindowManager.closeAllChildren();
        window.location.href = "pages/utility/CompanySelector.aspx";
    }
}
function hideTimer() {
    document.getElementById("timer").className = "hide";
    attachToLinks();
}
function showTimer() {
    document.getElementById("timer").className = "timerContainer";
}
function launchAcceptIdeas(page) {
    parent.hideTimer();
    window.open(page, "acceptIdea");
}

jQuery(document).ready(function () {
    $("a[id$=lnkSkip]").focus();
    $("a[id$=lnkSkip]").keyup(skipToContent);
});

function skipToContent(e) {
    var fmTop = findMainFrame(windowTop()).frames['DisplayContentIFrame'];
    if (fmTop != null && fmTop.length > 0) {
        var fm = fmTop.frames[0];
        if (fm != null && isExactKey(e, 13)) {
            pm
			(
				  {
				      target: fm, //Window Object
				      type: "GadgetMessageIDFocus"
				  }
			);
        }
    }
}


var currenMousePosition = { X: -1, Y: -1 };
function Document_MouseMove(theEvent) {
    currenMousePosition =
    {
        X: theEvent.pageX,
        Y: theEvent.pageY
    };
}

function MenuOrderCancelCmd_ClientClick() {
    CancelMenuOrderEditor();
}

function CancelMenuOrderEditor() {
    //Restore starting checked state:
    $("[id$='ShowQuickFindChk']")[0].checked = initialQuickFindState;

    //Hide editor:
    HideMenuOrderEditor();

    return false;
}

function HideMenuOrderEditor() {
    menuElementCache["theMenuEditorItemsUL"] = null;
    menuEditorItems = "";
    $("#theMenuEditorItemsUL").remove();

    $("#MenuOrderMask").addClass("hide");
    $("#MenuOrderEditor").addClass("hide");
    $("#MenuOrderEditorArea").addClass("hide");

    $("#dividerContainer").removeClass("hide");
    $("#mainSearchAndDividerButtonContainer").removeClass("hide");

    $(window).unbind("resize", EditorWindowResize);
}

var editorItemsWidthTotal;

function showMenuOrderEditor() {
    GetMenuEditorItems();
    $("#MenuOrderMask").removeClass("hide");
    $("#MenuOrderEditor").removeClass("hide");
    $("#MenuOrderEditorArea").removeClass("hide");

    $("#MenuOrderEditor").css({ left: $("#MenuOrderEditorArea").offset().left, top: $("#MenuOrderEditorArea").offset().top });

    $("[id^='MenuOrderEditor']").animate
    (
        {
            height: 200
        },
        500
    );

    $("#timer").addClass("hide");
    $("#dividerContainer").addClass("hide");
    $("#mainSearchAndDividerButtonContainer").addClass("hide");;
    $("#MenuOrderMask").fadeTo(500, 0.6);

    CalculateEditorItemsWidthTotal();

    //Resizing layout routines:
    $(window).resize(EditorWindowResize);
    CheckEditorNavigationButtons();

    InitQuickFindState();
    InitUseDefaultState();
    RewindScrollLeft();

    $('li[id*=theMenuEditorItemIL]').hover(
        function () {
            $(this).addClass('menuOrderEditorIEHover');
        },
        function () {
            $(this).removeClass('menuOrderEditorIEHover');
        });
}

function RewindScrollLeft() {
    while ($($("#theMenuEditorItemsUL").children()[0]).offset().left < 16) {
        EditorScrollLeft();
    }
}

function InitUseDefaultState() {
    //Initially disable "Use the defaults" button:
    ToggleUseDefault(false);

    var theAjaxRequest = new AjaxRequest();
    theAjaxRequest.Service = "AdHocSqlService";
    theAjaxRequest.Method = "Hrms_Read_RbsUserMenuPref";
    theAjaxRequest.CallBackMethod = InitUseDefaultState_Callback;
    theAjaxRequest.Params["UserID"] = $("[id$='MenuOrderUserID']").val();
    theAjaxRequest.Params["CompanyID"] = $("[id$='MenuOrderCompanyID']").val();

    theAjaxRequest.send(false);
}

function ToggleUseDefault(makeEnabled) {
    if (makeEnabled) {
        $("[id$='_MenuOrderUseDefaultsCmd']").removeAttr("disabled", "disabled");
    }
    else {
        $("[id$='_MenuOrderUseDefaultsCmd']").attr("disabled", "disabled");
    }
}

function InitUseDefaultState_Callback(theResult) {
    if (theResult.length > 0) {
        ToggleUseDefault(true);
    }
}

function EditorWindowResize() {
    CheckEditorNavigationButtons();
}

function CheckEditorNavigationButtons() {
    $("#editorNavigationButtons").css("display", $("#MenuEditorItemsOuterContainerDiv").width() > editorItemsWidthTotal ? "none" : "inherit");
}

function CalculateEditorItemsWidthTotal() {
    editorItemsWidthTotal = 0;
    $("#theMenuEditorItemsUL").children().each(function (theIndex, theEditorItem) { editorItemsWidthTotal += $(theEditorItem).outerWidth(true); });
}

var initialQuickFindState;
function InitQuickFindState() {
    //Get Quick Find State from Main Quick Find:
    $("[id$='ShowQuickFindChk']")[0].checked = $("[id$='_searchContainer']").css("display") != "none";

    //Save starting checked state:
    initialQuickFindState = $("[id$='ShowQuickFindChk']")[0].checked;

    //Show / hide Finder based on checkbox's checked state:
    HandleShowQuickFindState($("[id$='ShowQuickFindChk']")[0].checked);

    //Event to handle hide / show quick search:
    $("[id$='ShowQuickFindChk']").click(ShowQuickFindChk_Click);
}

function ShowQuickFindChk_Click() {
    HandleShowQuickFindState(this.checked);
}

function HandleShowQuickFindState(theCheckedState) {
    $("#MenuEditorFinderAndNoteDiv").toggle(theCheckedState);
    if (theCheckedState) {
        $("#MenuEditorItemsMain").css("width", "");
        $("#MenuEditorItemsMain").addClass("size80");
    }
    else {
        $("#MenuEditorItemsMain").removeClass("size80");
        $("#MenuEditorItemsMain").css("width", "96%");
    }
}

var menuEditorItems = "";
function GetMenuEditorItems() {
    $("#megamenu").children().each(AddMenuEditorItem);
    $("#MenuEditorItemsInnerContainerDiv").append("<ul id = 'theMenuEditorItemsUL' class = 'megamenu'>" + menuEditorItems + "</ul>");
    $("#theMenuEditorItemsUL").children().children(0).css("color", $("#megamenu").children(0).children(0).css("color"));
    $("#theMenuEditorItemsUL").children().css("background-color", $("#dividerContainer").css("background-color"));

    //Add an extra item to allow space after the last item:
    $("#extraRightEditorDiv").remove(); //After the first time, [extraRightEditorDiv] shows on a wrong place. Remove and add again.
    $("#MenuEditorItemsInnerContainerDiv").append("<div id = 'extraRightEditorDiv' style = 'width:240px; height:30px;float:left;'>&nbsp;</div>");

    $("#theMenuEditorItemsUL").sortable
    (
        {
            //Options:
            containment: $("#MenuEditorItemsOuterContainerDiv"),
            cursor: "move",
            axis: "x",
            cursorAt: { left: 15 },

            //Events:
            sort: theMenuEditorItemsUL_Sort,
            start: theMenuEditorItemsUL_start,
            stop: theMenuEditorItemsUL_stop
        }
    );
}

function theMenuEditorItemsUL_start(theEvent, theElement) {
    $(theElement.item).unbind('mouseenter mouseleave');
    $(theElement.item).addClass("menuOrderEditorIEHover"); //Needed in IE
}

function theMenuEditorItemsUL_stop(theEvent, theElement) {
    $(theElement.item).removeClass("menuOrderEditorIEHover");

    $(theElement.item).hover(
        function () {
            $(this).addClass('menuOrderEditorIEHover');
        },
        function () {
            $(this).removeClass('menuOrderEditorIEHover');
        });
    ToggleUseDefault(true);
}

var previousMouseX = null;
function theMenuEditorItemsUL_Sort(theEvent, theElement) {
    var draggedItemWidth = parseInt($(theElement.item).css("width"), 10);
    var draggedItemLeft = $(theElement.item).offset().left;
    var OuterContainerWidth = parseInt($("#MenuEditorItemsOuterContainerDiv").css("width"), 10);
    var rightAheadTolerance = 160;

    //Determine drag direction:
    var dragDirection;
    if (previousMouseX == null) {
        dragDirection = "START";
    }
    else if (currenMousePosition.X > previousMouseX) {
        dragDirection = "RIGHT";
    }
    else if (currenMousePosition.X < previousMouseX) {
        dragDirection = "LEFT";
    }
    else {
        dragDirection = "NONE";
    }
    previousMouseX = currenMousePosition.X;

    if ((draggedItemLeft > OuterContainerWidth - draggedItemWidth - rightAheadTolerance) && dragDirection == "RIGHT") {
        EditorScrollRight();
        $("#theMenuEditorItemsUL").sortable("refreshPositions");
    }
    else if (draggedItemLeft < 18 && dragDirection == "LEFT") {
        EditorScrollLeft();
        $("#theMenuEditorItemsUL").sortable("refreshPositions");
    }
}

function AddMenuEditorItem(theIndex, currentElement) {
    var theLiID = "theMenuEditorItemIL_" + theIndex;
    menuEditorItems += "<li data-RerID = '" + $(currentElement).attr("data-RerID") + "' id = '" + theLiID + "' class = 'primary'><a class = 'noTimer' href = 'javascript:void(0);'>" + $($(currentElement).children()[0]).text() + "</a></li>";
}

function getMenuPreferencesXml() {
    var resultXml = '[[MenuPreferencesRoot]]';

    resultXml += '[[ShowQuickFind Checked = "' + $("[id$='ShowQuickFindChk']")[0].checked + '"]][[/ShowQuickFind]]';

    resultXml += '[[MenuItems]]';
    $("#theMenuEditorItemsUL").children().each(
        function (theIndex, currentElement) {
            resultXml += '[[MenuItem RerID = "' + $(currentElement).attr("data-RerID") + '"/]]';
        }
    );
    resultXml += '[[/MenuItems]]';

    resultXml += '[[/MenuPreferencesRoot]]';

    return resultXml;
}

function MenuOrderImDoneCmd_ClientClick() {
    ProcessMenuOrderChanges();
}

function ProcessMenuOrderChanges() {
    //Start process to get the default items order:
    var theAjaxRequest = new AjaxRequest();
    theAjaxRequest.Service = "AdHocSqlService";
    theAjaxRequest.Method = "GetDefaultMenuItems";
    theAjaxRequest.CallBackMethod = ProcessMenuOrderChanges_CallBack;
    theAjaxRequest.send(false);
}

function ProcessMenuOrderChanges_CallBack(theResult) {
    var defaultMenuItemsXmlDoc = $.parseXML(theResult);

    if (!$("[id$='ShowQuickFindChk']")[0].checked || ChangesShouldBeSaved(defaultMenuItemsXmlDoc)) {
        ApplyChanges();
    }
    else {
        RemoveExistingMenuPreferences();
    }

    //Hide / show main Quick Find:
    ToggleMainQuickFind($("[id$='ShowQuickFindChk']")[0].checked);

    //Reorder menu:
    ApplyNewMenuOrder();

    //Hide editor:
    HideMenuOrderEditor();
}

function RemoveExistingMenuPreferences() {
    var theAjaxRequest = new AjaxRequest();
    theAjaxRequest.Service = "AdHocSqlService";
    theAjaxRequest.Method = "Hrms_Delete_RbsUserMenuPref";
    theAjaxRequest.CallBackMethod = RemoveExistingMenuPreferences_Callback;
    theAjaxRequest.Params["CompanyID"] = $("[id$='MenuOrderCompanyID']").val();

    theAjaxRequest.send(false);
}

function RemoveExistingMenuPreferences_Callback() {
}

function ChangesShouldBeSaved(defaultMenuItemsXmlDoc) {
    var itemsOrderHasChanged = false;
    $(defaultMenuItemsXmlDoc).find("MenuItem").each
    (
        function (theDefaultIndex, currentDefault) {
            if ($(currentDefault).attr("RerID") != $($("#theMenuEditorItemsUL").children()[theDefaultIndex]).attr("data-RerID")) {
                itemsOrderHasChanged = true;
                return false;
            }
        }
    );

    return itemsOrderHasChanged;
}

function ApplyChanges() {
    //Get new menu order preferences:
    var MenuPreferencesXml = getMenuPreferencesXml();

    //Start saving process:
    var theAjaxRequest = new AjaxRequest();
    theAjaxRequest.Service = "AdHocSqlService";
    theAjaxRequest.Method = "Hrms_Edit_RbsUserMenuPref";
    theAjaxRequest.CallBackMethod = MenuOrderSavePreferences_Callback;
    theAjaxRequest.Params["CompanyID"] = $("[id$='MenuOrderCompanyID']").val();
    theAjaxRequest.Params["Preferences"] = MenuPreferencesXml;

    theAjaxRequest.send(false);

    return false;
}

var temporaryMenuHolder;
function ApplyNewMenuOrder() {
    temporaryMenuHolder = document.createElement("ul");
    $("#theMenuEditorItemsUL").children().each(ApplyNewMenuOrderPerItem);
    $("#megamenu").append($(temporaryMenuHolder).children());
}

function ApplyNewMenuOrderPerItem(theIndex, currentReorderedElement) {
    $("#megamenu").children().each
    (
        function (theMegaMenuIndex, currentMegaMenuElement) {
            if ($(currentReorderedElement).attr("data-RerID") == $(currentMegaMenuElement).attr("data-RerID")) {
                $(temporaryMenuHolder).append($(currentMegaMenuElement));
            }
        }
    );
}

function MenuOrderSavePreferences_Callback() {
}

function ToggleMainQuickFind(theViewMode) {
    $("[id$='_searchContainer']").toggle(theViewMode);
}

function MenuOrderUseDefaultsCmd_Click() {
    ApplyDefaultToEditor();
}

function ApplyDefaultToEditor() {
    //Set "Show Quick Find" to checked:
    $("[id$='ShowQuickFindChk']")[0].checked = true;
    HandleShowQuickFindState(true);

    //Start process to get the default items order:
    var theAjaxRequest = new AjaxRequest();
    theAjaxRequest.Service = "AdHocSqlService";
    theAjaxRequest.Method = "GetDefaultMenuItems";
    theAjaxRequest.CallBackMethod = ApplyDefaultToEditor_CallBack;
    theAjaxRequest.send(false);

    //Disable "Use the Defaults" button:
    ToggleUseDefault(false);
}

function ApplyDefaultToEditor_CallBack(theResult) {
    var defaultMenuItemsXmlDoc = $.parseXML(theResult);

    temporaryMenuHolder = document.createElement("ul");
    $(defaultMenuItemsXmlDoc).find("MenuItem").each(ApplyDefaultToEditorPerItem);
    $("#theMenuEditorItemsUL").append($(temporaryMenuHolder).children());
}

function ApplyDefaultToEditorPerItem(theIndex, currentDefaultElement) {
    $("#theMenuEditorItemsUL").children().each
    (
        function (theMenuEditorIndex, currentMenuEditorElement) {
            if ($(currentDefaultElement).attr("RerID") == $(currentMenuEditorElement).attr("data-RerID")) {
                $(temporaryMenuHolder).append($(currentMenuEditorElement));
            }
        }
    );
}

var scrollValue = '20px';
function EditorScrollRight() {
    var OuterContainerWidth = $("#MenuEditorItemsOuterContainerDiv").width();

    var lastItemIndex = $("#theMenuEditorItemsUL").children().length - 1;
    var lastItem = $("#theMenuEditorItemsUL").children()[lastItemIndex];
    var lastItemLeft = $(lastItem).offset().left;
    var lastItemWidth = $(lastItem).width();

    var extraSpaceForDropping = 110;

    //Check that last item is still to the right of the limit defined by OuterContainerWidth - lastItemWidth - extraSpaceForDropping
    if (lastItemLeft > OuterContainerWidth - lastItemWidth - extraSpaceForDropping) {
        $("#MenuEditorItemsOuterContainerDiv").animate({ scrollLeft: '+=' + scrollValue }, 0);
    }
}

var editorScrollRightTimerID;
function editorBtnRightDividers_MouseDown() {
    editorScrollRightTimerID = window.setInterval(EditorScrollRight, 10);
}

function editorBtnRightDividers_MouseUp() {
    window.clearInterval(editorScrollRightTimerID);
}

function EditorScrollLeft() {
    $("#MenuEditorItemsOuterContainerDiv").animate({ scrollLeft: '-=' + scrollValue }, 0);
}

var editorScrollLeftTimerID;
function editorBtnLeftDividers_MouseDown() {
    editorScrollLeftTimerID = window.setInterval(EditorScrollLeft, 10);
}

function editorBtnLeftDividers_MouseUp() {
    window.clearInterval(editorScrollLeftTimerID);
}