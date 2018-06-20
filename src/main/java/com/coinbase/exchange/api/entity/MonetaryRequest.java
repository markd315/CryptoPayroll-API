package com.coinbase.exchange.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public abstract class MonetaryRequest {
  @JsonProperty("amount")
  protected BigDecimal amount;
  @JsonProperty("currency")
  protected String currency;

  protected MonetaryRequest(BigDecimal amount, String currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
