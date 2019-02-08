# Java User Guide

## Table of Contents
* [Introduction](#introduction)
* [Setup](#setup)
    * [Target365Client](#target365client)
* [Text messages](#text-messages)
    * [Send an SMS](#send-an-sms)
    * [Schedule an SMS for later sending](#schedule-an-sms-for-later-sending)
    * [Edit a scheduled SMS](#edit-a-scheduled-sms)
    * [Delete a scheduled SMS](#delete-a-scheduled-sms)
* [Payment transactions](#payment-transactions)
    * [Create a Strex payment transaction](#create-a-strex-payment-transaction)
    * [Create a Strex payment transaction with one-time password](#create-a-strex-payment-transaction-with-one-time-password)
    * [Reverse a Strex payment transaction](#reverse-a-strex-payment-transaction)
* [Lookup](#lookup)
    * [Address lookup for mobile number](#address-lookup-for-mobile-number)
* [Keywords](#keywords)
    * [Create a keyword](#create-a-keyword)
    * [Delete a keyword](#delete-a-keyword)
    * [SMS forward](#sms-forward)

## Introduction
The Target365 SDK gives you direct access to our online services like sending and receiving SMS, address lookup and Strex payment transactions.
The SDK provides an appropriate abstraction level for Java and is officially support by Target365.
The SDK also implements very high security (ECDsaP256 HMAC).

## Setup
### Target365Client
```Java
import io.target365.client.StrexClient;
import io.target365.client.Target365Client;
import io.target365.dto.StrexMerchantId;
import io.target365.dto.StrexOneTimePassword;
import io.target365.dto.StrexTransaction;
import io.target365.exception.InvalidInputException;
import io.target365.util.Util;

String baseUrl = "https://shared.target365.io";
String keyName = "YOUR_KEY";
String privateKey = "BASE64_EC_PRIVATE_KEY";
Target365Client serviceClient = Target365Client.getInstance(privateKey, new Target365Client.Parameters(baseUrl, keyName));
```
## Text messages

### Send an SMS
This example sends an SMS to 98079008 (+47 for Norway) from "Target365" with the text "Hello world from SMS!".
```Java
final OutMessage outMessage = new OutMessage()
    .setSender("Target365")
    .setRecipient("+4798079008")
    .setContent("Hello World from SMS!"));
    
final String transactionId = serviceClient.postOutMessage(outMessage).get();
```

### Schedule an SMS for later sending
This example sets up a scheduled SMS. Scheduled messages can be updated or deleted before the time of sending.
```Java
final OutMessage outMessage = new OutMessage()
    .setSender("Target365")
    .setRecipient("+4798079008")
    .setContent("Hello World from SMS!")
    .setSendTime(ZonedDateTime.now().plus(1, ChronoUnit.DAYS)));
    
final String transactionId = serviceClient.postOutMessage(outMessage).get();
```

### Edit a scheduled SMS
This example updates a previously created scheduled SMS.
```Java
final OutMessage outMessage = serviceClient.getOutMessage(transactionId).get();

outMessage
  .setSendTime(outMessage.getSendTime().plus(1, ChronoUnit.HOURS)))
  .setContent(outMessage.getContent() + " An hour later! :)";
  
serviceClient.putOutMessage(outMessage).get();
```

### Delete a scheduled SMS
This example deletes a previously created scheduled SMS.
```Java
serviceClient.deleteOutMessage(transactionId).get();
```

## Payment transactions

### Create a Strex payment transaction
This example creates a 1 NOK Strex payment transaction that the end user will confirm by replying "OK" to an SMS from Strex.
```Java
final StrexTransaction transaction = new StrexTransaction()
    .setTransactionId(UUID.randomUUID().toString())
    .setMerchantId("YOUR_MERCHANT_ID")
    .setShortNumber("2002")
    .setRecipient("+4798079008")
    .setPrice(1d)
    .setServiceCode("10001")
    .setInvoiceText("Dontaion test");

serviceClient.postStrexTransaction(transaction).get();
```

### Create a Strex payment transaction with one-time password
This example creates a Strex one-time password sent to the end user and get completes the payment by using the one-time password.
```Java
final String transactionId = UUID.randomUUID().toString();

final StrexOneTimePassword strexOneTimePassword = new StrexOneTimePassword()
    .setTransactionId(transactionId)
    .setMerchantId("10000002")
    .setRecipient("+4798079008")
    .setRecurring(Boolean.FALSE);

serviceClient.postStrexOneTimePassword(strexOneTimePassword).get();

// *** Get input from end user (eg. via web site) ***

final StrexTransaction transaction = new StrexTransaction()
    .setTransactionId(UUID.randomUUID().toString())
    .setMerchantId("YOUR_MERCHANT_ID")
    .setShortNumber("2002")
    .setRecipient("+4798079008")
    .setPrice(1d)
    .setServiceCode("10001")
    .setInvoiceText("Dontaion test")
    .setOneTimePassword("ONE_TIME_PASSWORD_FROM_USER");

serviceClient.postStrexTransaction(transaction).get();
```

### Reverse a Strex payment transaction
This example reverses a previously billed Strex payment transaction. The original transaction will not change, but a reversal transaction will be created that counters the previous transaction by a negative Price. The reversal is an asynchronous operation that usually takes a few seconds to finish.
```Java
final String reversalTransactionId = serviceClient.reverseStrexTransaction(transactionId).get();
```

## Lookup

### Address lookup for mobile number
This example looks up address information for the mobile number 98079008. Lookup information includes registered name and address.
```Java
final LookupResult lookup = serviceClient.addressLookup("+4798079008").get();
final String firstName = lookup.getFirstName();
final String lastName = lookup.getLastName();
```

## Keywords

### Create a keyword
This example creates a new keyword on short number 2002 that forwards incoming SMS messages to 2002 that starts with "HELLO" to the URL the https://your-site.net/api/receive-sms.
```Java
final Keyword keyword = new Keyword()
    .setShortNumberId("NO-2002")
    .setKeywordText("HELLO")
    .setMode(Keyword.Mode.Text)
    .setForwardUrl("https://your-site.net/api/receive-sms")
    .setEnabled(Boolean.TRUE);

final String keywordId = serviceClient.postKeyword(keyword).get();
```

### Delete a keyword
This example deletes a keyword.
```Java
serviceClient.deleteKeyword(keywordId).get();
```

### SMS forward
This example shows how SMS messages are forwarded to the keywords ForwardUrl. All sms forwards expects a response with status code 200 (OK). If the request times out or response status code differs the forward will be retried several times.
#### Request
```
POST https://your-site.net/api/receive-sms HTTP/1.1
Content-Type: application/json
Host: your-site.net

{
  "transactionId":"00568c6b-7baf-4869-b083-d22afc163059",
  "created":"2019-02-07T21:11:00+00:00",
  "sender":"+4798079008",
  "recipient":"2002",
  "content":"HELLO"
}
```

#### Response
```
HTTP/1.1 200 OK
Date: Thu, 07 Feb 2019 21:13:51 GMT
Content-Length: 0
```
