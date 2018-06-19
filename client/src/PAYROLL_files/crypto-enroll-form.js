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
        console.log("aa")
    } 
    else if (obj.id===coinb_id) {
        document.getElementById("coinbase-acc-id-input").disabled = false;
        document.getElementById("coinbase-coin-choices").style.visibility = 'visible';
        document.getElementById("coinbase-coin-choices").style.height = '100%';
        document.getElementById("coinbase-coin-choices").style.opacity = '1';
        document.getElementById("bitcoin-wallet-id-input").disabled = true;
        document.getElementById("bitcoin-wallet-id-input").value = "";
        console.log("bb")
    }
    console.log("hello");
}

