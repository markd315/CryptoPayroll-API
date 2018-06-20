(function ($) {
    $.fn.USDatePicker = function () {
        return this.each(function () {
            $.USDatePicker(this);
        });
    };

    $.USDatePicker = function (elm) {
        var e = $(elm)[0];
        return e.USDatePicker || (e.USDatePicker = new jQuery._USDatePicker(e));
    };

    $._USDatePicker = function (elm) {
        //#region Initialize the picker
        var separatorChars = US.common.dates.getSeparators();
        var $elm = $(elm);
        var evtNamespace = '.DatePicker';
        var usgDate = USGDates[elm.id];
        var selectedDate;
        var errorLog = log;
        var currentValue;

        var indexes = US.common.dates.getMaskIndexes();

        // User input: 
        //  - User input needs to be validated always on keypress (to have the correct code)
        //  - Keypress does not happen for backspace, tab, etc on IE so we bind keypress in IE to handle those keys.
        var event = ($.browser.opera || $.browser.mozilla) ? 'keypress' : 'keydown';
        var userInput = $elm.find('input[id$="' + elm.id + '_user"]')[0];
        var $userInput = $(userInput);
        $userInput.unbind('keypress' + evtNamespace);

        if ($elm.attr('disableCalendarHotKeys') == "false") {
            $userInput.bind(event + evtNamespace, function (e) { return UserInputKeyPressOrKeyDown(e); });
        }
        else {
            $userInput.bind(event + evtNamespace, function (e) { return UserInputKeyPressOrKeyDownJqGrid(e); });
        }

        if (event != 'keypress')
            $userInput.bind('keypress' + evtNamespace, function (e) { return UserInputKeyPress(e); });

        // Validate on blur instead of change because change will happen for every key stroke in IE
        $userInput.bind('blur' + evtNamespace, function(e) {
            UserInputBlur(e); 
        });

        $userInput.on("input change", function (e) {
            if (currentValue !== userInput.value) {
                currentValue = userInput.value;
                NavigatePickerToDate(userInput.value);
            }           
        });

        $userInput.bind('paste', function (e) {
            var el = $(this);
            setTimeout(function () {
                var text = $(el).val();
                if (!IsValidDate(text)) {
                    $(el).val('');
                }
            }, 0);
        });


        // Hidden input for picker

        var pickerInputID = elm.id + '_picker';
        var pickerInputHtml = '<input type="hidden" id="' + pickerInputID + '" />'
        $elm.append(pickerInputHtml);
        var pickerInput = $elm.find('input[id$="' + pickerInputID + '"]')[0];
        var $pickerInput = $(pickerInput);
        var yearRange = '-' + $elm.attr('minDropdownYears') + ':+' + $elm.attr('maxDropDownYears');
        var mxDate = '+' + $elm.attr('maxDropDownYears') + 'Y';
        var mnDate = '-' + $elm.attr('minDropdownYears') + 'Y';
        var hint = $elm.attr('hint');
        $pickerInput.datepicker({
            showOn: 'none',
            changeMonth: true,
            changeYear: true,
            yearRange: yearRange,
            maxDate: mxDate,
            minDate: mnDate,
            showButtonPanel: true,
            showOtherMonths: true,
            selectOtherMonths: true,
            onSelect: function (dateText, inst) { PickerOnSelect(dateText, inst); },
            beforeShow: function (input, inst) { PickerBeforeShow(input, inst); }
        });

        // Button to open the trigger
        var buttonId = elm.id + '_button';
        $('#' + buttonId, $elm).remove(); // Remove button if already present
        var $button = $('<input id="' + buttonId + '" type="image" src="' + GlobalVars['RootVD'] + '/images/datepicker.jpg"/>');
        $elm.append($button);
        $userInput.attr("hint", hint);
        if (userInput.disabled || IsReadOnly()) {
            disableCalendar();
        }
        else {
            $button.unbind('click' + evtNamespace).bind('click' + evtNamespace, function () { ButtonClick(); return false; });
            $button.unbind(event + evtNamespace).bind(event + evtNamespace, function (e) { ButtonKeyPress(e); });
        }

        // Picker formatting: by language, no need init regional array for english('en', 'uk', 'ca')
        var languageCode = GlobalVars['Language'];
        var regional;
        if ($.inArray(languageCode, ['en', 'uk', 'ca']) == -1) {
            InitDatePickerRegional();
            regional = $.datepicker.regional[languageCode];
        } else {
            regional = $.datepicker.regional[''];
        }

        regional.closeText = lstrCloseButton;
        $.datepicker.setDefaults(regional);
        var localizedFormat = regional.dateFormat;

        // Initialize values.
        var initialValues = GetCurrentValues(userInput.value);
        var initialDate = new Date(initialValues['month'] + '/' + initialValues['day'] + '/' + initialValues['year']);
        UpdateValues(initialDate, false, false);
        $pickerInput.datepicker('setDate', initialDate);
        //#endregion

        // Formatting
        $elm.addClass('usDatePickerSpan');
        $userInput.addClass('usDatePickerTextBox');
        $button.addClass('usDatePickerButton');
        $userInput.Watermark(hint);

        //#endregion

        //#region Events handlers
        function UserInputKeyPressOrKeyDown(e) {
            var keyCode = e.keyCode;

            if (NavigatePickerOnKeyboardShortcut(keyCode, e.ctrlKey))
                return true;

            // Accept backspace (8), tab (9), delete (46), left arrow (37), right arrow(39), end(35) & home(36)
            if (jQuery.inArray(keyCode, [8, 9, 46, 37, 39, 35, 36]) >= 0)
                return true;

            // Accept Ctrl + C, Ctrl + V, Ctrl + X 
            if (e.ctrlKey && jQuery.inArray(99, 118, 120))
                return true;

            // If this event is keypress, validate user input. 
            // User input needs to be validated always on keypress (to have the correct code)
            var event = e.type;
            if (event == 'keypress' && UserInputKeyPress(e)) {
                return true;
            }

            return event != "keypress";
        };

        function UserInputKeyPressOrKeyDownJqGrid(e) {
            var keyCode = e.keyCode;

            // If this event is keypress, validate user input. 
            // User input needs to be validated always on keypress (to have the correct code)

            // Accept backspace (8), tab (9), delete (46), left arrow (37), right arrow(39), end(35) & home(36)
            if (jQuery.inArray(keyCode, [8, 9, 46, 37, 39, 35, 36]) >= 0)
                return true;

            // Accept Ctrl + C, Ctrl + V, Ctrl + X 
            if (e.ctrlKey && jQuery.inArray(99, 118, 120))
                return true;

            var event = e.type;
            if (event == 'keypress' && UserInputKeyPress(e)) {
                return true;
            }

            return event != "keypress";
        };

        function UserInputKeyPress(e) {
            var nextChar = String.fromCharCode(e.which);
            var validKey = elm.USDatePicker.IsValidKey(userInput.value, nextChar);
            
            return validKey;
        };

        function UserInputBlur(e) {
            elm.USDatePicker.Validate();
            if (!IsReadOnly())
                usgDate.updateChangeListeners();
        };

        function PickerOnSelect(dateText, inst) {
            // On close: the dateText is localized because the picker is localized, so we convert the dateText
            // to a Date instance and call setdate which will take into account user preferences instead of localization.
            var date = $.datepicker.parseDate(localizedFormat, dateText);
            elm.USDatePicker.SetDate(date);
            usgDate.updateChangeListeners();
            $userInput.focus();
        };

        function PickerBeforeShow(input, inst) {
            NavigatePickerToDate(userInput.value);
        };

        function ButtonClick() {
            $userInput.focus();
            $pickerInput.datepicker("show");
            $('button.ui-datepicker-current').removeClass('ui-priority-secondary').addClass('ui-priority-primary');
        };

        function ButtonKeyPress(e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13) {
                $userInput.focus();
                $pickerInput.datepicker("show");
            }
            if (code == 9) {
                $pickerInput.datepicker("hide");
            }
        };
        //#endregion

        //#region Public functions
        this.SetRequired = function (value) {
            $userInput.required(value);

            var parentTD = $userInput.parents('td').first();
            if (value) {
                parentTD.addClass("required");

                usgDate.RequiredError = false;
                usgDate.RequiredChecks = 0;
            }
            else {
                parentTD.removeClass("required");
                this.SetErrorState(false);
            }
        };

        this.SetErrorState = function (isError) {
            // Remove the border from the span, as we only want to have a border on the user input
            // not on the the whole span which includes the button. This is an inline style instead 
            // of a class because we cannot change the className property on the span as that 
            // is assigned by Validate.js and validation logic runs of className == 'dateContainerRequired'
            elm.style.border = '0px';

            // This function is supposed to do the same that we do in Validate.js-chgInputColor 
            // but calling that function from here can cause an infinite look on this controls validation.
            if (isError) {
                $userInput.addClass('usDatePickerInvalidInput');
            }
            else {
                $userInput.removeClass('usDatePickerInvalidInput');
            }
        };

        this.Disable = function (value, pageDisabled, isMeta) {
            if (value == null || typeof (value) == "undefined")
                value = true;

            if (!isMeta) {
                $elm.attr('core-disabled', value);

                if ($elm.attr('meta-disabled') !== "true" && $elm.attr('page-disabled') !== "true") {
                    if (value)
                        disableCalendar();
                    else
                        enableCalendar();
                }

                $elm.trigger('legacyPropertyChange');
            } else {
                var fieldDisabled = value;
                $elm.attr('meta-disabled', fieldDisabled);

                $elm.attr('page-disabled', pageDisabled);

                var disabled = fieldDisabled || pageDisabled;
                if ($elm.attr('core-disabled') !== "true") {
                    if (disabled)
                        disableCalendar();
                    else
                        enableCalendar();
                }


            }
        };

        this.SetDate = function (date) {
            UpdateValues(date, date == undefined || date == null, false);
        };

        this.GetDate = function () {
            elm.USDatePicker.Validate();
            return selectedDate;
        };

        this.SetText = function (text) {
            userInput.value = text;
            elm.USDatePicker.Validate();
        };

        this.SetLog = function (aLog) {
            errorLog = aLog;
        };

        this.GetText = function () {
            elm.USDatePicker.Validate();
            return userInput.value;
        };

        this.SetChangeListener = function (listener) {
            usgDate.onchange = listener;
        };

        this.AddChangeListener = function (listener) {
            usgDate.addDateChangedListener(listener);
        };

        this.RemoveChangeListener = function (listener) {
            usgDate.removeDateChangedListener(listener);
        };

        this.HidePicker = function () {
            $pickerInput.datepicker("hide");
        };

        this.SetUserInputFocus = function () {
            $userInput.focus();
        };

        this.UpdateChangeListeners = function () {
            usgDate.updateChangeListeners();
            $userInput.trigger('input');
        };

        this.GetFormValue = function (form) {
            var originalDate = GetCurrentValues(form[userInput.id].value);

            return originalDate['month'] + '/' + originalDate['day'] + '/' + originalDate['year'];
        };
        //#endregion

        //#region Public for testing reasons
        this.Validate = function () {
            var currentValues = GetCurrentValues(userInput.value);
            var newDate = new Date(currentValues['month'] + '/' + currentValues['day'] + '/' + currentValues['year']);
            if (!isNaN(newDate)) {
                // The values on the new date must be equal to the current
                // values because 2/29/2011 translates to 3/1/2011 instead 
                // of being an invalid date
                $.each(GetDateValues(newDate), function (i, val) {
                    if (parseInt(val, 10) != parseInt(currentValues[i], 10)) {
                        newDate = null;
                        return false;
                    }
                });

                var year = currentValues['year'];
                if (newDate != null && (year < usgDate.MinYear || year > usgDate.MaxYear))
                    newDate = null;
            }

            UpdateValues(newDate, false, true);
        };

        this.IsValidKey = function (inputValue, nextChar) {
            var inputLength = inputValue.length;
            var isNumber = $.trim(nextChar) != '' && !isNaN(nextChar);
            var isSeparator = jQuery.inArray(nextChar, separatorChars) >= 0;
            var lastChar = inputValue.charAt(inputValue.length - 1);
            var lastCharIsNumber = $.trim(lastChar) != '' && !isNaN(lastChar);
            var lastCharIsSeparator = lastChar != '' && jQuery.inArray(lastChar, separatorChars) >= 0;
            var numberOfSeparators = $.grep(inputValue.split(''), function (n, i) { return jQuery.inArray(n, separatorChars) >= 0; }).length;

            // First input can only be a number.
            if (inputLength == 0 && isNumber)
                return true;

            if (isNumber && lastCharIsSeparator)
                return true;

            // Only allow 2 separators, not together, not the first character. Mixed separators are ok e.g. 1/2.2010
            // Do not allow separators after the user has gone the join fields format.
            if (inputLength > 0 && isSeparator && numberOfSeparators < 2 && !lastCharIsSeparator && !JoinedFieldsFormat(inputValue)) {
                return true;
            }

            // If this is the second number in a row, we need to validate pairs of numbers.
            if (isNumber && lastCharIsNumber) {
                // Date fields can have at most three values: month, day and year
                var dateFields = ParseText(inputValue + nextChar);
                if (dateFields.length <= 3)
                    return true;
            }

            return false;
        };
        //#endregion

        //#region Private helper functions
        function NavigatePickerToDate(text) {
            var dateValues = GetSuggestedDateValues(text);
            var suggestedDate = new Date(dateValues['month'] + '/' + dateValues['day'] + '/' + dateValues['year']);
            $pickerInput.datepicker('setDate', suggestedDate);
        };

        function GetSuggestedDateValues(text) {
            var currentValues = GetCurrentValues(text);
            var today = GetDateValues(new Date());

            $.each(currentValues, function (i, val) {
                if (val == '')
                    currentValues[i] = today[i];
            });
            return currentValues;
        };

        function NavigatePickerOnKeyboardShortcut(keyCode, ctrl) {
            // Escape (27) - hide
            if (keyCode == 27) {
                $pickerInput.datepicker("hide");
                return true;
            }

            var pageUp = keyCode == 33;
            var pageDown = keyCode == 34;

            // Page up - previous month
            if (pageUp && !ctrl) {
                MoveMonth(-1);
                return true;
            }

            // Page down - next month
            if (pageDown && !ctrl) {
                MoveMonth(1);
                return true;
            }

            // Ctrl + page up - previous year
            if (pageUp && ctrl) {
                MoveYear(-1);
                return true;
            }

            // Ctrl + page down - next year
            if (pageDown && ctrl) {
                MoveYear(1);
                return true;
            }

            // ctrl + left - previous day
            if (keyCode == 37 && ctrl) {
                MoveDay(-1);
                return true;
            }

            // ctrl + right - next day
            if (keyCode == 39 && ctrl) {
                MoveDay(1);
                return true;
            }

            // Enter = 13, select
            if (keyCode == 13) {
                if ($.datepicker._datepickerShowing == true) {
                    $(pickerInput).datepicker("hide");
                    var date = isNaN(new Date(pickerInput.value)) ? new Date() : $.datepicker.parseDate(localizedFormat, pickerInput.value);
                    elm.USDatePicker.SetDate(date);
                }
                else {
                    if (userInput.value == '') {
                        elm.USDatePicker.SetDate(new Date());
                    }
                }
                return true;
            }

            return false;
        };

        function MoveMonth(diff) {
            var dateValues = GetDateValues($.datepicker.parseDate(localizedFormat, pickerInput.value));
            var month = dateValues['month'] + diff;
            var newDate = new Date(month + '/' + dateValues['day'] + '/' + dateValues['year']);
            $pickerInput.datepicker('setDate', newDate);
        };

        function MoveYear(diff) {
            var dateValues = GetDateValues($.datepicker.parseDate(localizedFormat, pickerInput.value));
            var year = dateValues['year'] + diff;
            var newDate = new Date(dateValues['month'] + '/' + dateValues['day'] + '/' + year);
            $pickerInput.datepicker('setDate', newDate);
        };

        function MoveDay(diff) {
            var dateValues = GetDateValues($.datepicker.parseDate(localizedFormat, pickerInput.value));
            var day = dateValues['day'] + diff;
            var newDate = new Date(dateValues['month'] + '/' + day + '/' + dateValues['year']);
            $pickerInput.datepicker('setDate', newDate);
        };

        function UpdateValues(date, clearUserInput, validating) {
            if (!date || date == null || isNaN(date)) {
                selectedDate = null;

                var currentUserText = userInput.value;
                if (currentUserText == $(userInput).attr("hint")) {
                    currentUserText = "";
                }

                // Display the error when the user input has a value.
                if (currentUserText != '')
                    errorLog.displayNow(lstrinvalidDate, usgDate);
                else
                    errorLog.clearMessageNow(lstrinvalidDate, usgDate);

                if (clearUserInput)
                    userInput.value = '';

                // Do not update the values fields, because the user might be setting, month day and year separetly
                // and the values could be cleared out while the date is not fully setup.
                usgDate.MonthField.value = '';
                usgDate.DayField.value = '';
                usgDate.YearField.value = '';

                // Update values to reflect invalid date when validating.
                if (validating && validating == true) {
                    if (currentUserText != '') {
                        usgDate.MonthValue.value = -1;
                        usgDate.DayValue.value = -1;
                        usgDate.YearValue.value = -1;
                    }
                    else {
                        usgDate.MonthValue.value = '';
                        usgDate.DayValue.value = '';
                        usgDate.YearValue.value = '';
                    }
                }
            }
            else {
                selectedDate = date;
                errorLog.clearMessageNow(lstrinvalidDate, usgDate);

                // Values
                var dateValues = GetDateValues(date);
                var month = dateValues['month'];
                var day = dateValues['day'];
                var year = dateValues['year'];

                usgDate.MonthValue.value = month;
                usgDate.DayValue.value = day;
                usgDate.YearValue.value = year;

                // Displayed values
                var displayedValues = $.map([month, day, year], function (number) {
                    var value = number + '';
                    return value.length == 1 ? '0' + value : value;
                });
                var displayedMonth = displayedValues[0];
                var displayedDay = displayedValues[1];
                var displayedYear = displayedValues[2];

                userInput.value = US.common.dates.formatDate(new Date(year, month - 1, day));

                usgDate.MonthField.value = displayedMonth;
                usgDate.DayField.value = displayedDay;
                usgDate.YearField.value = displayedYear;

                $(userInput).trigger('keyup');
            }
        };

        // Return the month, day, and year values according to the current input text
        // some of these values might be empty. We figure out which field correspond to 
        // which date value according to the preferences (i.e month first, then day, then year)
        function GetCurrentValues(text) {
            var fields = ParseText(text);
            var year = GetValueOrDefault(indexes.year, fields);
            if (year.length == 2)
                year = usgDate.processTwoDigitYear(year) + '';

            return { month: GetValueOrDefault(indexes.month, fields), day: GetValueOrDefault(indexes.day, fields), year: year };
        };

        function GetValueOrDefault(position, dateFields) {
            return dateFields.length > position ? dateFields[position] : '';
        };

        function ParseText(text) {
            // If there is no separator (format 04042001), we need to split pairs of numbers for
            // month, day and year. 
            if (JoinedFieldsFormat(text)) {
                var dateFields = new Array();
                var index = 0;
                var field = '';
                $(text.split('')).each(function (i, digit) {
                    field = field + digit;

                    // Year at the end (third field) must have length = 4
                    if (indexes.year == 2 && index == 2 && field.length < 3)
                        return;

                    // Year at the begining (first field) must have length = 4
                    if (indexes.year == 0 && index == 0 && field.length < 3)
                        return;

                    if (i % 2 == 1) {
                        dateFields[index] = field;
                        field = '';
                        index++;
                    }
                });

                if (field != '')
                    dateFields[index] = field;

                return dateFields;
            }

            return ReplaceSeparators(text).split('/');
        };

        function ReplaceSeparators(text) {
            $(separatorChars).each(function (index, s) {
                text = text.replace(new RegExp('\\' + s, "g"), '/');
            });
            return text;
        };

        function JoinedFieldsFormat(text) {
            var separatedText = ReplaceSeparators(text).split('/');
            if (indexes.year == 0)
                return text.length > 4 && separatedText.length == 1;

            return text.length > 2 && separatedText.length == 1;
        };

        function GetDateValues(date) {
            // Yes! we need + 1, getMonth returns values in the range of [0 - 11]
            return { month: date.getMonth() + 1, day: date.getDate(), year: date.getFullYear() };
        };

        function IsReadOnly() {
            var readonlyAttribute = elm.attributes["readOnly"];
            return readonlyAttribute && readonlyAttribute.value == "true";
        };

        function disableCalendar() {
            $userInput.prop('readOnly', true);
            $userInput.addClass('disabled');
            $button.prop('disabled', true);
            $button.addClass('disabled');
        };

        function enableCalendar() {
            $userInput.prop('readOnly', false);
            $userInput.removeClass('disabled');
            $button.prop('disabled', false);
            $button.removeClass('disabled');
        };
        // End: Private functions
    }; // End fn
})(jQuery);

var _defaultGotoToday = jQuery.datepicker._gotoToday;
jQuery.datepicker._gotoToday = function (id) {
    _defaultGotoToday.call(this, id);

    var currentPickerSpan = $($.datepicker._curInst.input).parent('span')[0];
    
    if (currentPickerSpan) {
        var currentPicker = currentPickerSpan.USDatePicker;

        if (currentPicker) {
            currentPicker.SetDate(new Date());
            currentPicker.UpdateChangeListeners();
            currentPicker.HidePicker();
        }
    }
};

var _defaultHideDatepicker = jQuery.datepicker._hideDatepicker;
jQuery.datepicker._hideDatepicker = function (id) {
    _defaultHideDatepicker.call(this, id);

    if (typeof $.datepicker._curInst !== 'undefined' &&
            $.datepicker._curInst !== null &&
            typeof $.datepicker._curInst.input !== 'undefined' &&
            $.datepicker._curInst.input !== null) {

        var currentPickerSpan = $($.datepicker._curInst.input).parent('span')[0];
    
        if (currentPickerSpan) {
            var currentPicker = currentPickerSpan.USDatePicker;
            if (currentPicker) {
                currentPicker.SetUserInputFocus();
            }
        }

    }
}