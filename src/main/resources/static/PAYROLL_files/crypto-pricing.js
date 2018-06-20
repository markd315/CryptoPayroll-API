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

    $(document).ready(function() {
        $('#per-paycheck-amt').on('input', function () {
            let paycheckAmt = {
                total: Number.parseFloat($(this).val()),
                btc: 0,
                ltc: 0,
                eth: 0,
            };

            if (isNaN(paycheckAmt.total)) paycheckAmt.total = 0;

            if ($('#bitcoin-wallet-radio').is(':checked')) {
                paycheckAmt.btc = paycheckAmt.total;
            } else if ($('#coinbase-acc-radio').is(':checked')) {
                let ethPercent = $('#cb-eth-chk').is(':checked') ? Number.parseFloat($('#cb-eth-percentage').val()) : 0;
                let ltcPercent = $('#cb-ltc-chk').is(':checked') ? Number.parseFloat($('#cb-ltc-percentage').val()) : 0;
                let btcPercent = $('#cb-btc-chk').is(':checked') ? Number.parseFloat($('#cb-btc-percentage').val()) : 0;

                paycheckAmt.eth = ethPercent * paycheckAmt.total;
                paycheckAmt.ltc = ltcPercent * paycheckAmt.total;
                paycheckAmt.btc = btcPercent * paycheckAmt.total;
            }

            let btcCoinEstimate = Math.trunc(paycheckAmt.btc / currencies.BTC.price * 1000) / 1000;
            let ethCoinEstimate = Math.trunc(paycheckAmt.eth / currencies.ETH.price * 1000) / 1000;
            let ltcCoinEstimate = Math.trunc(paycheckAmt.ltc / currencies.LTC.price * 1000) / 1000;
            $('#btc-coin-estimate').html(`<label><strong>${btcCoinEstimate}</strong> Bitcoin at <strong>${currencies.BTC.price}/BTC </strong></label>`);
            $('#ltc-coin-estimate').html(`<label><strong>${ltcCoinEstimate}</strong> Litecoin at <strong>${currencies.LTC.price}/LTC </strong></label>`);
            $('#eth-coin-estimate').html(`<label><strong>${ethCoinEstimate}</strong> Ethereum at <strong>${currencies.ETH.price}/ETH </strong></label>`)
        });
    });
})();