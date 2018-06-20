(function() {
    'use strict';
    console.log("hello");

    const host = "http://localhost:8080/v2/";
    const user_order_ids = [];

    $.postJSON = function (url, data, callback) {
        return jQuery.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'type': 'POST',
            'url': url,
            'data': JSON.stringify(data),
            'dataType': 'json',
            'success': callback
        });
    };

    function createRecurringOrder(data) {
        let url = host + "recurring/";
        $.postJSON(url, data, function (res, status) {
            alert("Response: " + res + "\nStatus: " + status);
        })
    }

    function getNumberUserCurrencies() {
        let num = 0;
        if ($("#coinbase-acc-radio").is(":checked") && $("#coinbase-acc-id-input").val()) {
            if ($("#cb-btc-chk").is(":checked")) num++;
            if ($("#cb-eth-chk").is(":checked")) num++;
            if ($("#cb-ltc-chk").is(":checked")) num++;
            return num;
        }
        return null;
    }

// BTC, ETH, LTC
    window.submitForm = function submitForm() {
        let order;
        const orders = [];
        let btcOrder;
        const dollarPerPaycheck = $("#per-paycheck-amt").val();
        const cyclePeriod = 1;
        const cyclesSinceLast = 0;

        if ($("#coinbase-acc-radio").is(":checked") && $("#coinbase-acc-id-input").val()) {
            let dest = $("#coinbase-acc-id-input").val();
            let coinbaseCurrencyTypes = [
                {
                    name: 'BTC',
                    percentage: $('#cb-btc-percentage').val() / 100.0,
                    checked: $("#cb-btc-chk").is(":checked")
                },
                {
                    name: 'LTC',
                    percentage: $('#cb-ltc-percentage').val() / 100.0,
                    checked: $("#cb-ltc-chk").is(":checked")
                },
                {
                    name: 'ETH',
                    percentage: $('#cb-eth-percentage').val() / 100.0,
                    checked: $("#cb-eth-chk").is(":checked")
                },
            ];

            for (let currency of coinbaseCurrencyTypes) {
                if (currency.checked) {
                    order = {
                        currency: currency.name,
                        quantity: currency.percentage * dollarPerPaycheck,
                        destination: dest,
                        destinationType: "coinbase",
                        cyclePeriod: cyclePeriod,
                        cyclesSinceLast: cyclesSinceLast
                    };
                    orders.push(order)
                }
            }
        }
        else if ($("#bitcoin-wallet-radio").is(":checked") && $("#bitcoin-wallet-id-input").val()) {
            let dest = $("#bitcoin-wallet-id-input").val();
            btcOrder = {
                currency: "BTC",
                quantity: dollarPerPaycheck,
                destination: dest,
                destinationType: "wallet",
                filled: false,
                cyclePeriod: cyclePeriod,
                cyclesSinceLast: cyclesSinceLast
            };
            orders.push(btcOrder);
        } else return null;

        for (let i = 0; i < orders.length; i++) {
            console.log(orders[i]);
            createRecurringOrder(orders[i]);
        }
    };

    function getRecurringOrder() {

    }

    function getOrder() {

    }

    window.onRadioSelect = function onRadioSelect(obj) {
        const btc_id = "bitcoin-wallet-radio";
        const coinb_id = "coinbase-acc-radio";
        if (obj.id === btc_id) {
            document.getElementById("bitcoin-wallet-id-input").disabled = false;
            document.getElementById("coinbase-coin-choices").style.visibility = 'hidden';
            document.getElementById("coinbase-coin-choices").style.height = '0px';
            document.getElementById("coinbase-coin-choices").style.opacity = '0';
            document.getElementById("coinbase-acc-id-input").disabled = true;
            document.getElementById("coinbase-acc-id-input").value = "";
        } else if (obj.id === coinb_id) {
            document.getElementById("coinbase-acc-id-input").disabled = false;
            document.getElementById("coinbase-coin-choices").style.visibility = 'visible';
            document.getElementById("coinbase-coin-choices").style.height = '100%';
            document.getElementById("coinbase-coin-choices").style.opacity = '1';
            document.getElementById("bitcoin-wallet-id-input").disabled = true;
            document.getElementById("bitcoin-wallet-id-input").value = "";
        }
    }
})();