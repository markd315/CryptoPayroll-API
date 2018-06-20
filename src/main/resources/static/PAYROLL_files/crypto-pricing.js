(function() {
    'use strict';

    let getPriceForCurrency = function getPriceForCurrency(currency) {
        return new Promise(((resolve, reject) => {
            $.get(`https://api-public.sandbox.gdax.com/products/${currency}-USD/book?level=1`)
                .done(function(data) { resolve(data.bids[0][0]); })
                .fail(function () {
                    reject('request failed');
                });
        }));
    };

    let getCurrencyData = function(name) {
        let x = { name: name, price: NaN };
        getPriceForCurrency(name).then(price => x.price = price);
        return x;
    };

    let currencies = {
        BTC: getCurrencyData('BTC'),
        LTC: getCurrencyData('LTC'),
        ETH: getCurrencyData('ETH')
    };

    $('#per-paycheck-amt').on('input', function() {
        console.log('moo');
        let paycheckAmt = Number.parseFloat($(this).val());
        $('#btc-coin-estimate').html(`<label><strong>$${paycheckAmt / currencies.BTC.price}</strong> Bitcoin at <strong>${currencies.BTC.price}/BTC </strong></label>`)
    });
})();