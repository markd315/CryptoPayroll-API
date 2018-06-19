console.log("hello")

const host = "http://localhost:8080/v2/";
var user_order_ids = [];

$.postJSON = function(url, data, callback) {
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
    var recurringOrder = {
        order: data.order,
        cyclePeriod: data.cyclePeriod,
        cyclesSinceLast: data.cyclesSinceLast
    }
    url = host + "recurring/"
    $.postJSON(url, recurringOrder, function(res, status){
        alert("Response: " + res + "\nStatus: " + status);
    })
}

function getNumberUserCurrencies() {
    var num = 0;
    if ($("#coinbase-acc-radio").is(":checked") && $("#coinbase-acc-id-input").val()) {
        if ($("#cb-btc-chk").is(":checked")) num++;
        if ($("#cb-eth-chk").is(":checked")) num++;
        if ($("#cb-ltc-chk").is(":checked")) num++;
        return num;
    } 
    return null;
}

// BTC, ETH, LTC
function submitForm() {
    var orders = [];
    var btcOrder;
    var selectedBTC = false;
    var selectedETH = false;
    var selectedLTC = false;
    var dollarPerPaycheck = $("#per-paycheck-amt").val();
    var cyclePeriod = 1;
    var cyclesSinceLast = 0;

    if ($("#coinbase-acc-radio").is(":checked") && $("#coinbase-acc-id-input").val()) {
        var dest = $("#coinbase-acc-id-input").val();
        if ($("#cb-btc-chk").is(":checked")){ 
            selectedBTC = true;
            var percentage = $("#cb-btc-percentage").val() / 100.0;
            var amount = percentage * dollarPerPaycheck;
            var order = {
                currency: "BTC",
                quantity: amount,
                destination: dest,
                destinationType: "coinbase"
            };
            orders.push(order)
        }
        if ($("#cb-eth-chk").is(":checked")) {
            selectedETH = true;
            var percentage = $("#cb-eth-percentage").val() / 100.0;
            var amount = percentage * dollarPerPaycheck;
            var order = {
                currency: "ETH",
                quantity: amount,
                destination: dest,
                destinationType: "coinbase"
            };
            orders.push(order)
        };
        if ($("#cb-ltc-chk").is(":checked")) {
            selectedLTC = true;
            var percentage = $("#cb-ltc-percentage").val() / 100.0;
            var amount = percentage * dollarPerPaycheck;
            var order = {
                currency: "LTC",
                quantity: amount,
                destination: dest,
                destinationType: "coinbase"
            };
            orders.push(order)
        };
    }
    else if ($("#bitcoin-wallet-radio").is(":checked") && $("#bitcoin-wallet-id-input").val()) {
        var dest = $("#bitcoin-wallet-id-input").val();
        btcOrder = {
            currency: "BTC",
            quantity: dollarPerPaycheck,
            destination: dest,
            destinationType: "wallet"
        }
        orders.push(btcOrder);
    } else return null;

    for (var i = 0; i < orders.length; i++) {
        var recurringOrder = {
            order: orders[i],
            cyclePeriod: cyclePeriod,
            cyclesSinceLast: cyclesSinceLast
        }
        console.log(recurringOrder);
        createRecurringOrder(recurringOrder);
        
    }

}

function getRecurringOrder() {

}

function getOrder() {

}

function onRadioSelect(obj) {
    var btc_id = "bitcoin-wallet-radio"
    var coinb_id = "coinbase-acc-radio"
    if (obj.id===btc_id) {
        document.getElementById("bitcoin-wallet-id-input").disabled = false;
        document.getElementById("coinbase-coin-choices").style.visibility = 'hidden';
        document.getElementById("coinbase-coin-choices").style.height = '0px';
        document.getElementById("coinbase-coin-choices").style.opacity = '0';
        document.getElementById("coinbase-acc-id-input").disabled = true;
        document.getElementById("coinbase-acc-id-input").value = "";
    } 
    else if (obj.id===coinb_id) {
        document.getElementById("coinbase-acc-id-input").disabled = false;
        document.getElementById("coinbase-coin-choices").style.visibility = 'visible';
        document.getElementById("coinbase-coin-choices").style.height = '100%';
        document.getElementById("coinbase-coin-choices").style.opacity = '1';
        document.getElementById("bitcoin-wallet-id-input").disabled = true;
        document.getElementById("bitcoin-wallet-id-input").value = "";
    }
}