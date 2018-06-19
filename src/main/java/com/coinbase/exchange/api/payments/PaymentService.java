package com.coinbase.exchange.api.payments;

import com.coinbase.exchange.api.exchange.GdaxExchange;
import com.coinbase.exchange.api.exchange.GdaxExchangeImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

/**
 * Created by robevansuk on 16/02/2017.
 */
@Component
public class PaymentService {

  private static final String PAYMENT_METHODS_ENDPOINT = "/payment-methods";
  private static final String COINBASE_ACCOUNTS_ENDPOINT = "/coinbase-accounts";

  @Autowired
  GdaxExchange gdaxExchange = new GdaxExchangeImpl();

  public List<PaymentType> getPaymentTypes() {
    return gdaxExchange.getAsList(PAYMENT_METHODS_ENDPOINT, new ParameterizedTypeReference<PaymentType[]>() {
    });
  }

  public List<CoinbaseAccount> getCoinbaseAccounts() {
    return gdaxExchange.getAsList(COINBASE_ACCOUNTS_ENDPOINT, new ParameterizedTypeReference<CoinbaseAccount[]>() {
    });
  }
}