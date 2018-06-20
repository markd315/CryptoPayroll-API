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
        const coinbaseCurrencyTypes = [
            {
                name: 'BTC',
                percentageField: '#cb-btc-percentage',
                checkedField: "#cb-btc-chk",
                wallet: "#bitcoin-wallet-id-input"
            },
            {
                name: 'LTC',
                percentageField: '#cb-ltc-percentage',
                checkedField: "#cb-ltc-chk",
                wallet: "#litecoin-wallet-id-input"
            },
            {
                name: 'ETH',
                percentageField: '#cb-eth-percentage',
                checkedField: "#cb-eth-chk",
                wallet: "#ethereum-wallet-id-input"
            },
        ];
        console.log("start");
        if ($("#coinbase-acc-radio").is(":checked") && $("#coinbase-acc-id-input").val()) {
            let dest = $("#coinbase-acc-id-input").val();

            for (let currency of coinbaseCurrencyTypes) {
                if ($(currency.checkedField).is(":checked")) {
                    this.console.log(currency.checkedField)
                    let percentage = $(currency.percentageField).val() / 100.0;
                    order = {
                        currency: currency.name,
                        quantity: percentage * dollarPerPaycheck,
                        destination: dest,
                        destinationType: "coinbase",
                        cyclePeriod: cyclePeriod,
                        cyclesSinceLast: cyclesSinceLast
                    };
                    orders.push(order)
                }
            }
        }
        else if ($("#individual-wallet-radio").is(":checked")) {
            console.log("yes")
            for (let currency of coinbaseCurrencyTypes) {
                if ($(currency.checkedField + "B").is(":checked") && $(currency.wallet).val()) {
                    let dest = $(currency.wallet).val();
                    let percentage = $(currency.percentageField + "B").val() / 100.0;
                    order = {
                        currency: currency.name,
                        quantity: percentage * dollarPerPaycheck,
                        destination: dest,
                        destinationType: "wallet",
                        cyclePeriod: cyclePeriod,
                        cyclesSinceLast: cyclesSinceLast
                    };
                    orders.push(order)
                }
            }
        } else return null;
        console.log("end");
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
        const btc_id = "individual-wallet-radio";
        const coinb_id = "coinbase-acc-radio";
        const coinbase_input = $("#coinbase-acc-id-input");
        const coinbase_options = $("#coinbase-coin-choices");
        const wallet_options = $("#wallet-coin-choices");
        if (obj.id === btc_id) {
            coinbase_options.removeClass("options-visible");
            coinbase_options.addClass("options-hidden");
            wallet_options.removeClass("options-hidden");
            wallet_options.addClass("options.visible");         
            document.getElementById("coinbase-acc-id-input").disabled = true;
            document.getElementById("coinbase-acc-id-input").value = "";
        } else if (obj.id === coinb_id) {
            document.getElementById("coinbase-acc-id-input").disabled = false;
            coinbase_options.removeClass("options-hidden");
            coinbase_options.addClass("options-visible");
            wallet_options.removeClass("options-visible");
            wallet_options.addClass("options-hidden");       
        }
    }
})();