function navBySearchKeypress(theEventData) {
    var theResult = true;
    if ((theEventData.which) == 13 && $("#ui-active-menuitem:visible").attr("data-initial") == "true") {
        $("#ui-active-menuitem").click();
        theResult = false;
    }

    return theResult;
}

function navBySearchFocus(theEventData, theUi) {
    var uiActiveItem = $("#ui-active-menuitem[data-initial='true']");
    if (theItemValue == theUi.item.value) {
        uiActiveItem.removeAttr("data-initial");
    } else {
        uiActiveItem.removeClass("ui-state-focus").removeAttr("data-initial");
    }
}
var defaultText = "";
var defaultFocusText = "";
var theItemValue;
var suggestions;
var accessibleRerIds;

$(document).ready(function () {
    defaultText = defaultNavBySearchText;
    defaultFocusText = defaultNavBySearchFocusText;

    var autoTextbox = $("input[id$=navBySearch]");

    autoTextbox.keypress(navBySearchKeypress);
    autoTextbox.autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "services/JsonService.asmx/NavBySearch",
                data: "{ searchText: '" + request.term.replace(/\\/g, "\\\\").replace(/'/g, "\\'") + "', allComponents: '" + allComponents + "' }",
                dataType: "json",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    var employees = getEmployeeItems(data);
                    var contentItems = getContentItems(data);
                    var navItems = typeof unifiedUI !== 'undefined' ? getNavServiceItems() : getNavItems();
                    response($.merge(employees, $.merge(navItems, contentItems)));
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert(XMLHttpRequest.responseText);
                }
            });
        },
        focus: navBySearchFocus,
        minLength: 2,
        autoFocus: false,
        open: function (event, ui) {
            suggestions = $(".ui-autocomplete");
            var divSearch = $("div[id=searchResults]");

            divSearch.append(suggestions);
            divSearch.find("ul, li").removeAttr("style");
            suggestions.zIndex(100000);
            applyGroupings(suggestions);
            $("#ui-active-menuitem").addClass("ui-state-focus").attr("data-initial", "true");
            theItemValue = $("#ui-active-menuitem").attr("title");
        },
        select: function (event, ui) {
            navSelect(event, ui, autoTextbox.val());
        }
    })
    .data("ui-autocomplete")
    ._renderItem = function (ul, item) {
        var CssClass;
        var id;
        var truncThreshold = 25;
        var childrenSize = ul.children().size();
        switch (item.linkType) {
            case "Nav":
                //CssClass = getNavId(item.id);
                CssClass = "pagesIcon";
                id = "navLi" + childrenSize;
                break;
            case "Person":
                CssClass = "personIcon";
                id = "personLi" + childrenSize;
                break;
            case "Content":
                switch (item.itemType) {
                    case "E":
                        CssClass = "contentEmailIcon";
                        break;
                    case "F":
                        CssClass = "contentDocIcon";
                        break;
                    case "H":
                        CssClass = "contentLinkIcon";
                        break;
                    default:
                        CssClass = "contentTextIcon";
                        break;
                }
                id = "contentLi" + childrenSize;
                break;
        }
        var firstItemId = '';
        if (childrenSize == 0)
            firstItemId = 'id="ui-active-menuitem"';

        var truncatedLabel = item.label;

        if (item.label.length > truncThreshold)
            truncatedLabel = item.label.substring(0, truncThreshold - 1) + "...";

        truncatedLabel = truncatedLabel.replace(new RegExp("(" + $.ui.autocomplete.escapeRegex(autoTextbox.val()) + ")", "ig"), "<strong>$1</strong>");

        return $('<li id="' + id + '" ></li>')
            .data("item.autocomplete", item)
            .append($('<a ' + firstItemId + ' class="' + CssClass + '" title="' + item.label + '" ></a>').html(truncatedLabel))
            .appendTo(ul);
    };

    autoTextbox.val(defaultText);
    autoTextbox.css("color", "grey");
    autoTextbox.focusout(function () {
        if (this.value.length == 0 || this.value.indexOf(defaultFocusText) > -1) {
            this.value = defaultText;
            $("input[id$=clearSearch]").addClass('hide');
        }
    });

    autoTextbox.bind('keydown keyup', function (e) {
        if (e.keyCode == 9 || e.keyCode == 16) return;
        if (this.value.indexOf(defaultFocusText) > -1) this.value = "";

        if (this.value.length == 0) {
            $("input[id$=clearSearch]").addClass('hide');
        }
        else {
            $("input[id$=clearSearch]").removeClass('hide');
        }
    });

    autoTextbox.focusin(function () {
        var obj = this;
        setTimeout(function () { navSearchFocused(obj); }, 100);
    });

    autoTextbox.click(function () {
        if (this.value.indexOf(defaultText) == -1 && this.value.indexOf(defaultFocusText) == -1)
            $(this).autocomplete('search');
    });
});

function navSearchFocused(obj) {
    if (obj.value.indexOf(defaultText) == -1 && obj.value.indexOf(defaultFocusText) == -1) {
        obj.select();
    }
    else {
        obj.value = defaultFocusText;
        setTimeout(function() {
            obj.selectionStart = 0;
            obj.selectionEnd = 0;
        }, 100);
    }
}

function getShowMenuParameters(html) {
    html = html.replace("(", "");
    html = html.replace(")", "");
    html = html.replace(/'/g, "");
    return html.split(",");
}

//This section searches the megmenu HTML for matching references to the search term.
$.expr[":"].containsNoCase = function (el, i, m) {
    var search = m[3];
    if (!search) return false;
    search = search.replace(/\//g, '\\/');
    return eval("/" + search + "/i").test($(el).text());
};

//The element that gets passed coubld be a leaf or contain leaves
//if the onclick event does not contain (this), then its a leaf and can return its onclick event
//else its a branch with many leaves and we need the first leaf's onclick event
function getOnClickEvent(element) {
    var html = element.parent().html().match(new RegExp("\\(.*\\)"))[0];

    if (html.match(/\(this\)/i) == null) {
        return html;
    }

    return element.parent().find("a").eq(1).parent().html().match(new RegExp("\\(.*\\)"))[0];
}

//
// get people from search results
//
function getEmployeeItems(data) {
    var items = $.grep(data.d, function (x) {
        return x.LinkType === "Person";
    });

    return $.map(items, function (item) {
        return {
            value: item.Value
            , label: item.Label
            , html: item.Html
            , linkType: item.LinkType
            , itemType: null
        }
    });
}

//
// parse mega menu to get pages 
//
function getNavItems() {
    var searchVal = $("input[id$=navBySearch]").val().replace(/\\/g, "\\\\");

    //The selector leverages the parent LI's class of primary to not include those in the search suggestions
    //since they do not link anywhere
    var searchElements = $("ul[id$=megamenu]").children().find("a:containsNoCase(" + searchVal + ")").parent().not(".primary");

    return $.map(searchElements, function (obj) {
        var element = $(obj);
        return {
            value: element.find("a:first").text()
                , label: element.find("a:first").text()
                , html: getOnClickEvent(element)
                , id: element.parents(".dropdown").parents("li:first").attr('id')
                , linkType: "Nav"
                , itemType: null
        };
    }).sort(function (a, b) {
        var compA = a.value.toUpperCase();
        var compB = b.value.toUpperCase();
        return (compA < compB) ? -1 : (compA > compB) ? 1 : 0;
    });
}

function getNavServiceItems() {
    var searchElements = unifiedUI.getElementByDescription($("input[id$=navBySearch]").val());

    return $.map(searchElements, function (obj) {
        return {
            value: obj.label
                , label: obj.label
                , html: obj.url
                , id: obj.id
                , linkType: "Nav"
                , itemType: null
        };
    }).sort(function (a, b) {
        var compA = a.value.toUpperCase();
        var compB = b.value.toUpperCase();
        return (compA < compB) ? -1 : (compA > compB) ? 1 : 0;
    });
}

//
// get content items from search results
//
function getContentItems(data) {
    if (!accessibleRerIds) {
        accessibleRerIds = $.map($("ul[id$=megamenu] a.page-link, #newMegaMenu .menuItem"), function (obj) { return $(obj).data("rerid") || $(obj).data("id") });
    }

    var items = $.grep(data.d, function (x) {
        // Content items in the home page (old dashboard comes back with rerid == -1)
        return x.LinkType === "Content"
            && (x.RerId === -1 || $.inArray(x.RerId, accessibleRerIds) !== -1);
    });

    return $.map(items, function (item) {
        return {
            value: item.Value
            , label: item.Label
            , html: null
            , linkType: item.LinkType
            , itemType: item.ItemType
            , rerId: item.RerId
            , contentBoxId: item.ContentBoxId
        }
    });
}

//
// group and paginate search results
//
function applyGroupings(suggestions) {
    var employees = suggestions.find("li[id^=personLi]");
    var employeesLength = employees.size();

    if (employeesLength > 0) {
        suggestions.prepend("<li class=\"searchSection\"><h2 id='groupPeople'>" + lstrpeople + "</h2></li>");
        employees.first().addClass("firstResult");

        if (employeesLength > 5) {
            employees.slice(5).hide();
            employees.last().after("<li id='morePeople' class='moreLi'><a id='lnkMorePeople' href='javascript:showMorePeople();'>" + lstrExpandLink + "</a></li>");
        }
    }

    var navItems = suggestions.find("li[id^=navLi]");
    var navItemsLength = navItems.size();

    if (navItemsLength > 0) {
        navItems.first().addClass("firstResult").before("<li class=\"searchSection\"><h2>" + lstrpages + "</h2></li>");

        if (navItemsLength > 5) {
            navItems.slice(5).hide();
            navItems.last().after("<li id='moreNav' class='moreLi'><a id='lnkMoreNav' href='javascript:showMoreNav();'>" + lstrExpandLink + "</a></li>");
        }
    }

    var contentResults = suggestions.find("li[id^=contentLi]");
    var contentLength = contentResults.size();

    if (contentLength > 0) {
        contentResults.first().addClass("firstResult").before("<li class=\"searchSection\"><h2>" + "Content" + "</h2></li>");;

        if (contentLength > 5) {
            contentResults.slice(5).hide();
            contentResults.last().after("<li id='moreContent' class='moreLi'><a id='lnkMoreContent' href='javascript:showMoreContent();'>" + lstrExpandLink + "</a></li>");
        }
    }
}

//
// navigate to item from search results
//
function navSelect(event, ui, searchTerm) {
    switch (ui.item.linkType) {
        case "Person":
            if (typeof unifiedUI !== 'undefined') {
                window.unifiedUI.setMenuById(150);
            } else {
                hideAllDivsButCurrent("dividerContainer", 'li', 'divLi_62', 'currentDivider');
                hideAllDivsButCurrent("nodes", 'li', 'nav2Li_150', 'currentNode');
                hideAllDivsButCurrent("tabId", 'li', 'tabLi_', 'currentTab');
                hideAllDivsButCurrent("nodes", 'div', 62, 'nav2', 'hide');
                hideAllDivsButCurrent("tabId", 'div', 150, 'tabs', 'hide');
                $("#nodeContainer").css("display", "block");
            }

            showPage('pages/view/eeDirectoryDetail.aspx?USParams=' + ui.item.html + '', false, ui.item.label, true);
            break;

        case "Nav":
            if (typeof unifiedUI !== 'undefined') {
                unifiedUI.navigateToPageById(ui.item.id);
            } else {
                var showMenuParams = getShowMenuParameters(ui.item.html);
                showMenu(showMenuParams[0], showMenuParams[1], showMenuParams[2], showMenuParams[3], jQuery.trim(showMenuParams[4]) == "true", false, ui.item.label, true);
            }
            break;

        case "Content":
            var rerId = ui.item.rerId;
            var contentBoxId = ui.item.contentBoxId;
            if (rerId == -1) {
                // update user preference then navigate to home page
                $.ajax({
                    url: window.GlobalVars["RootVD"] + "/services/JsonService.asmx/SaveUserHomePagePreference",
                    type: "POST",
                    data: '{ "homePage": 2 }',
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    complete: function() {

                        if (!unifiedUI) {
                            var nodeContainerdiv = getCachedElement("nodeContainer");
                            nodeContainerdiv.style.display = "none";
                        }

                        showMenu('HomeContainer.aspx?cbid=' + contentBoxId + "&searchTerm=" + searchTerm, '', '', '');
                        
                        sizeIframe();
                    }
                });
            }
            else {
                // navigate to item in menu specified by rerId
                if (typeof unifiedUI !== 'undefined') {
                    var parameters = [
                        "cbid=" + contentBoxId, 
                        "searchTerm=" + searchTerm
                    ];
                    unifiedUI.navigateToPageById(rerId, parameters);
                } else {
                    var menuItem = $("ul[id$=megamenu]").children().find("a[data-rerid=" + rerId + "]").parent();
                    var ev = getOnClickEvent(menuItem);
                    var itemParams = getShowMenuParameters(ev);
                    showMenu(itemParams[0] + "!cbid=" + contentBoxId + "!searchTerm=" + searchTerm, itemParams[1], itemParams[2], itemParams[3], jQuery.trim(itemParams[4]) == "true", false, ui.item.label, true);
                }
            }
            break;
    }
}

//
// show all people results; hide pages and content
//
function showMorePeople() {
    var employees = suggestions.find("li[id^=personLi]");

    if (employees.size() > 0) {
        employees.show();
        suggestions.find("li[id=morePeople]").addClass("hide").children("a:first").hide();
    }

    hidePages(suggestions);
    hideContent(suggestions);

    $("td[id=tdSearch]").bind("focusout", clearNavBySearch);
}

//
// show all page results; hide people and content
//
function showMoreNav() {
    hidePeople(suggestions);

    var navItems = suggestions.find("li[id^=navLi]");

    if (navItems.size() > 0) {
        navItems.show();

        suggestions.find("li[id=moreNav]").addClass("hide").children("a:first").hide();
    }

    hideContent(suggestions);

    $("td[id=tdSearch]").bind("focusout", clearNavBySearch);
}

//
// show all content results; hide people and pages
//
function showMoreContent() {
    hidePeople(suggestions);
    hidePages(suggestions);

    var contentItems = suggestions.find("li[id^=contentLi]");

    if (contentItems.size() > 0) {
        contentItems.show();

        suggestions.find("li[id=moreContent]").addClass("hide").children("a:first").hide();
    }

    $("td[id=tdSearch]").bind("focusout", clearNavBySearch);
}

//
// hide people results
//
function hidePeople(suggestions) {
    var employees = suggestions.find("li[id^=personLi]");

    if (employees.size() != 0) {
        employees.hide();
        updateMoreLink(suggestions, employees.last(), "morePeople", "showMorePeople", lstrViewPeople);
    }
}

//
// hide pages results
//
function hidePages(suggestions) {
    var pages = suggestions.find("li[id^=navLi]");

    if (pages.size() != 0) {
        pages.hide();
        updateMoreLink(suggestions, pages.last(), "moreNav", "showMoreNav", lstrViewPages);
    }
}

//
// hide content results
//
function hideContent(suggestions) {
    var content = suggestions.find("li[id^=contentLi]");

    if (content.size() != 0) {
        content.hide();
        updateMoreLink(suggestions, content.last(), "moreContent", "showMoreContent", "View Content");
    }
}

//
// add/update more link for search section
//
function updateMoreLink(suggestions, lastItem, moreId, jsFunction, text) {
    //if more link exists change wording, else add the link
    var more = suggestions.find("li[id=" + moreId + "]");
    if (more.size() > 0) {
        more.show().addClass("moreCollapse").removeClass("hide").children("a:first").html(text).show();
    }
    else {
        lastItem.after("<li id='" + moreId + "' class='moreLi moreCollapse'><a href='javascript:" + jsFunction + "();'>" + text + "</a>&nbsp;</li>");
    }
}

//
//used to determine the id
//the id determines the css class that has the associated image
//
function getNavId(id) {
    return id.replace(/div/i, "nav");
}

//
// clear search results
//
function clearNavBySearch() {
    var navBox = $("input[id$=navBySearch]");

    navBox.blur();
    navBox.val(defaultText);
    $("input[id$=clearSearch]").blur().addClass('hide');
    $("td[id=tdSearch]").unbind("focusout", clearNavBySearch);
    postOrNot(false);
}
