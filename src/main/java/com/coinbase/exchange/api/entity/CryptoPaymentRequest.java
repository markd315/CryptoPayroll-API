package com.coinbase.exchange.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class CryptoPaymentRequest extends MonetaryRequest {
  @JsonProperty("crypto_address")
  private String cryptoAddress;

  public CryptoPaymentRequest(BigDecimal amount, String currency, String cryptoAddress) {
    super(amount, currency);
    this.cryptoAddress = cryptoAddress;
  }

  public String getCryptoAddress() {
    return cryptoAddress;
  }

  public void setCryptoAddress(String cryptoAddress) {
    this.cryptoAddress = cryptoAddress;
  }
}
