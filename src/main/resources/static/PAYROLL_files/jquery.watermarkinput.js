(function ($) {
    $.fn.Watermark = function (text) {
        return this.each(
			function () {
			    var label;
			    var input = $(this);
			    var inputParent;

			    function addMessage() {
			        var id = input.attr('id');

			        label = $(document.createElement('label'));
			        label.html(text);
			        label.css('font-weight', 'normal');
			        label.attr('for', id);


			        input.wrap('<span class="waterMark" id="spn' + id + '" />');
			        inputParent = input.parent();
			        input.before(label);

			        input.bind('click focus', function () { inputParent.addClass('blurred'); });
			        input.bind('focusout', function () { inputParent.removeClass('blurred'); });
			        input.bind('keyup change', function () { updateFields($(this)); });

			        updateFields(input);
			    }

			    function addMessageWithPlaceHolder() {
			        input.attr('Placeholder', text);
			    }

			    function updateFields(obj) {
			        obj.each(function () {
			            if ($(this).val() != "") {
			                label.css('display', 'none');
			            } else {
			                label.css('display', '');
			            }
			        });
			    }

			    if (jQuery.browser.msie && parseFloat(jQuery.browser.version) < 10) {
			        addMessage();
			    }
			    else {
			        addMessageWithPlaceHolder();
			    }
			}
        );
    };
})(jQuery);