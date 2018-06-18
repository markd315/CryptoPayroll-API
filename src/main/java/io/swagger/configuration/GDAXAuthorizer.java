package io.swagger.configuration;

import com.mongodb.util.JSON;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class GDAXAuthorizer {
  private String cbAccessKey;
  private String timestamp;
  private String cbAccessPassphrase;

  public GDAXAuthorizer(File gdaxfile) {
    Scanner fi = null;
    try {
      fi = new Scanner(gdaxfile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    cbAccessKey = fi.nextLine();
    cbAccessPassphrase = fi.nextLine();
    updateTimestamp();
  }

  public String getCBSignature(JSON gdaxRequestBody) {
    updateTimestamp();
    /*
    like this but in Java
    function sign(bodyvar){
    var crypto = require('crypto-js');

var secret = 'UnUebsUpYTG+pl6VU7pD1US1GxFkmupok5xE4Y9RZ+u0YPrzJlamzxyrur5SgnZHHiL55uR9j+xmqF6qlRmZYA==';

var timestamp = Date.now() / 1000;
var requestPath = 'deposits/payment-method';

var body = JSON.stringify(bodyvar);

var method = 'POST';

// create the prehash string by concatenating required parts
var what = timestamp + method + requestPath + body;

// decode the base64 secret
var key = Buffer(secret, 'base64');

// create a sha256 hmac with the secret
var hmac = crypto.HmacSHA256(what.toString(), key);

// sign the require message with the hmac
// and finally base64 encode the result
return hmac.update(what).digest('base64');
}

pm.environment.set("timestampHeader", Date.now()/1000);
pm.environment.set("sig", sign({
    "amount": 10.00,
    "currency": "USD",
    "payment_method_id": "bc677162-d934-5f1a-968c-a496b1c1270b"
}));
     */
    String signature = sign(gdaxRequestBody.toString());

    return signature;
  }

  private String sign(String s) {

  }

  public String getCBAccessKey() {
    return cbAccessKey;
  }

  public String getCBAccessPassphrase() {
    return cbAccessPassphrase;
  }

  public String getCBAccessTimestamp() {
    updateTimestamp();
    return this.timestamp;
  }

  private void updateTimestamp() {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    timestamp = df.format(new Date() / 1000);
  }
}
