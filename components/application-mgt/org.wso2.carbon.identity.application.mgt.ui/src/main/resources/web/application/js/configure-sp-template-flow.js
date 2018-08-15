/**
 * Temporary function that serves as the local popup customization till carbon-kernel release
 * This has also been moved to proper codebase in carbon.ui as this is reusable.
 */
function showPopupConfirm(htmlMessage, title, windowHeight, windowWidth, okButton, cancelButton, callback, closeCallback) {
    if (!isHTML(htmlMessage)) {
        htmlMessage = htmlEncode(htmlMessage);
    }
    var strDialog = "<div id='dialog' title='" + title + "'><div id='popupDialog'></div>" + htmlMessage + "</div>";
    var requiredWidth = 750;
    if (windowWidth) {
        requiredWidth = windowWidth;
    }
    var func = function () {
        jQuery("#dcontainer").html(strDialog);
        if (okButton) {
            jQuery("#dialog").dialog({
                close: function () {
                    jQuery(this).dialog('destroy').remove();
                    jQuery("#dcontainer").empty();
                    return false;
                },
                buttons: {
                    "Save": function () {
                        if (callback && typeof callback == "function")
                            callback();
                        jQuery(this).dialog("destroy").remove();
                        jQuery("#dcontainer").empty();
                        return false;
                    },
                    "Cancel": function () {
                        jQuery(this).dialog('destroy').remove();
                        jQuery("#dcontainer").empty();
                        if (closeCallback && typeof closeCallback == "function") {
                            closeCallback();
                        }
                        return false;
                    },
                },
                height: windowHeight,
                width: requiredWidth,
                minHeight: windowHeight,
                minWidth: requiredWidth,
                modal: true
            });
        } else {
            jQuery("#dialog").dialog({
                close: function () {
                    jQuery(this).dialog('destroy').remove();
                    jQuery("#dcontainer").empty();
                    if (closeCallback && typeof closeCallback == "function") {
                        closeCallback();
                    }
                    return false;
                },
                height: windowHeight,
                width: requiredWidth,
                minHeight: windowHeight,
                minWidth: requiredWidth,
                modal: true
            });
        }

        if (okButton) {
            $('.ui-dialog-buttonpane button:contains(OK)').attr("id", "dialog-confirm_ok-button");
            $('#dialog-confirm_ok-button').html(okButton);
        }
        if (cancelButton) {
            $('.ui-dialog-buttonpane button:contains(Cancel)').attr("id", "dialog-confirm_cancel-button");
            $('#dialog-confirm_cancel-button').html(cancelButton);
        }


        jQuery('.ui-dialog-titlebar-close').click(function () {
            jQuery('#dialog').dialog("destroy").remove();
            jQuery("#dcontainer").empty();
            jQuery("#dcontainer").html('');
            if (closeCallback && typeof closeCallback == "function") {
                closeCallback();
            }
        });

    };
    if (!pageLoaded) {
        jQuery(document).ready(func);
    } else {
        func();
    }

    function isHTML(str) {
        var a = document.createElement('div');
        a.innerHTML = str;

        for (var c = a.childNodes, i = c.length; i--;) {
            if (c[i].nodeType == 1) return true;
        }

        return false;
    }
}