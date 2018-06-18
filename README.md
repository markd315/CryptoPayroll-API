
Name	Role
Mark Davis	Lone Wolf Intern In Over His Head


Current problem:

Simply put, some companies will accept the volatility and still want to pay their employees bonuses or regular wages in cryptocurrencies, but don't want to deal with the related tax reporting issues or configuring transactions. We already handle this abstraction for our clients in US dollars, so why not support cryptocurrencies?

Proposed solution:

HR-facing: Added to the pay-hub/earnings/add there should be an option for some top cryptocurrencies under the tax category dropdown. This is not in scope for this microservice (and I don't even have that code, I'm on a different team).

Business logic: When one-off or scheduled transaction requests are submitted by individual x-tenants, the cryptocurrency valued owed to the payees is calculated and stored on a backend ledger. Biweekly on payday, this ledger is flushed, with the correct amount of cryptocurrency being programmatically ordered from GDAX exchange to an Ultimate payroll dispersal wallet, and then sent out DIRECTLY to all configured wallets on the next block.

6/15/2018 design edit:

Endpoints and depth chart

Based on the current GDAX fee structure I'm going to suggest some changes to the above for the BalanceOwed POST endpoint (which should be running on a cron job and is by far the bulk of getting this µService to work, as it's the only one talking to GDAX and has to send multiple requests with complicated business logic):

Deposits and Withdrawals


TYPE	FEE
Crypto Deposit	Free
Crypto Withdrawal	Free
ACH Deposit	Free
ACH Withdrawal	Free
SEPA Deposit	€0.15 EUR
SEPA Withdrawal	€0.15 EUR
USD Wire Deposit	$10 USD
USD Wire Withdrawal	$25 USD


Transactional Fee Schedule

PRICING TIER	TAKER FEE	MAKER FEE
$0m - $10m	0.30 %	0 %
$10m - $100m	0.20 %	0 %
$100m+	0.10 %	0 %
Crypto withdrawals being free to an unlimited number of addresses, and MAKER fees being 0%, the only transaction fee bottleneck we have to be concerned about is the wire deposit and ensuring our orders add liquidity to GDAX.

MAKER/TAKER note: When you place an order which is not immediately matched by an existing order, that order is placed on the order book. If another customer places an order that matches yours, you are considered the maker and your fee will be 0%. In other words, MAKER purchase orders will not execute in a deterministic timeline, is subject to market forces, and requires the price to at least have a local trend downwards, but can always be cancelled and replaced.

The idea is that we will always have our order as close to the market price as possible so it will execute the instant the market price drops even a hundredth of a percent.

I propose a flow as follows to minimize the incidence of that $10 wire deposit overhead and keep our orders MAKER.

Determine an aggregate amount of currency to purchase at the designated payroll timestamp, denominated in that cryptocurrency (not USD). This value should be the summation of all payroll orders we have received during the pay period = BTC

Convert that BTC quote to USD at the current exchange rate = a

Determine our current GDAX balance in USD = b

Wire and confirm ((a*1.1)-b) USD to the corporate GDAX account (Not in scope for this project, I don't know who to talk to about this)

LOOP:

Place a limit order just [above/below] market price for BTC in cryptocurrency.

Delay 200ms (rate limits)

BRANCH A (Order is not completely filled): Kill remaining order from books, GOTO LOOP.

BRANCH B (Insufficient funds to purchase adequate cryptocurrency because the price has risen by 10 or more percent): Wire more or throw error for human intervention.

BRANCH C: END LOOP (Order is completely filled because the market price has shifted past the price of the limit order):

At this point our server has purchased all of the necessary cryptocurrency to pay its employees.

Queue up POST requests to the /withdrawals/crypto or /withdrawals/coinbase-account GDAX endpoints with the customer information included as follows.



{
    "amount": 10.00,
    "currency": "BTC",
    "coinbase_account_id": "c13cd0fc-72ca-55e9-843b-b84ef628c198",
}
{
    "amount": 10.00,
    "currency": "BTC",
    "crypto_address": "0x5ad5769cd04681FeD900BCE3DDc877B50E83d469"
}
Advantages:

Employee experience: Within their company, they can enroll with HR to have a certain percentage of their paycheck or bonuses automatically transacted to their cryptocurrency wallet and their tax data documented according to applicable laws, improving morale, saving time, and combating money-laundering.

Press: Someone would definitely write an article about this, garnering positive PR for Ultimate.

Profitability: Availability to charge a premium for managing transactions and automating tax compliance for clients.

Future Enhancements:

Extending idea to other cryptocurrency chains.



Dev notes:

GDAX API docs
https://docs.gdax.com/#coinbase48

Sandbox URLs
When testing your API connectivity, make sure to use the following URLs.

Website
https://public.sandbox.gdax.com

REST API
https://api-public.sandbox.gdax.com

Websocket Feed
wss://ws-feed-public.sandbox.gdax.com

FIX API
tcp+ssl://fix-public.sandbox.gdax.com:4198

We can use this library to fill our account with funds and talk with the order book endpoints, although it doesn't seem to support withdrawal functionality so we might have to extend it for that. https://github.com/irufus/gdax-java

We are using Swagger for documentation and stubgen:

https://github.com/swagger-api/swagger-codegen

https://github.com/swagger-api/swagger-codegen/wiki/server-stub-generator-howto#java-springboot



Example code resource:

https://github.com/springframeworkguru/springboot_swagger_example/tree/master/src/main/java/guru/springframework