Name,	Role
Mark Davis	Architect, docs, Backend Developer
David Sargent	Backend Developer
Yuli Liu	Backend Developer
Nick Young	QA, Backend Developer
Luke Bickell	Frontend developer

## Business Case:

To illustrate demand, a standalone service called BitWage has processed $30m in wages this year for 20,000 users in the US, Europe, Latin America and Asia including staff from Google, Facebook, GE, Philips, the United Nations and the US Navy. Many of these workers had signed up for the service on their own. Not only would our HCM integrations give us a competitive advantage over BitWage by reducing the amount of transactions, but this new crypto paycheck would help distinguish us from other cloud HCM providers.

We can deliver around 50 cents in absolute-value per enabled customer per pay-period just because of the network transaction fees our microservice circumvents (averaging over last 3 mo)

The convenience our unrivaled configurable direct-deposit provides is worth at least $2.50/enabled employee/pay period until HCM competitors can catch up.

We assume biweekly pay periods.

Our transactional operating costs of this microservice work out to be only a fixed $10/pay period plus server costs.

At the 5 million employee mark with an adoption rate of only 3%, this service would represent an additional $.18 PEPM of revenue, totaling over $2.7 million quarterly.

Instead of being an add-on, the service could also be bundled, be pitched to cryptocurrency-related press for free publicity, or offered for-free or at-cost to win clients. I suggest that we heavily target tech companies with our marketing regardless.

Some groups of employees and corporate clients will want direct deposit to cryptocurrency integrated with their regular pay-period, including some of our own employees. We already handle this abstraction for our clients in US dollars, so why not support cryptocurrencies?


## Repository: https://github.com/markd315/CryptoPayroll-API

Contact for judges: Slack channel #nu-cryptoroll

Demo video:


Proposed solution:

HR-facing: Added to the pay-hub/earnings/add there should be an option for some top cryptocurrencies under the tax category dropdown. This is not in scope for this microservice (and I don't even have that code, I'm on a different team).

Business logic: When one-off or scheduled transaction requests are submitted by individual x-tenants, the cryptocurrency valued owed to the payees is calculated and stored on a backend ledger. Biweekly on payday, this ledger is flushed, with the correct amount of cryptocurrency being programmatically ordered from GDAX exchange DIRECTLY to all configured wallets on the next block.

These are the endpoints that our service will support. The ordinary flow of control will be that HR clients interact with their frontends, sending requests to the Payroll team endpoints, which can send requests to our order-management and execution service. Our service places the requested orders and returns confirming HTTP codes to the payroll team, which can inform users according to their design requirements.

I architected our API to be as flexible as possible in allowing flexibility to the payroll team, as it can support one-time bonuses as well as recurring payments that will occur once every x pay-cycles with an offset of y.

Our service does not send money automatically on a cron-job either, it purchases and sends the correct batch of orders once for every time the v2/execute endpoint is POSTed.

API endpoints

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

MAKER/TAKER note: When you place an order which is not immediately matched by an existing order, that order is placed on the order book. If another customer places an order that matches yours, you are considered the maker and your fee will be 0%. In other words, MAKER purchase orders will not execute in a deterministic timeline, is subject to market forces, and requires the price to at least have a local trend downwards, but can always be canceled and replaced.

The idea is that we will always have our order as close to the market price as possible so it will execute the instant the market price drops even a hundredth of a percent, keeping the speed high.

I propose a flow as follows to minimize the incidence of that $10 wire deposit overhead and keep our orders MAKER. While buying, we want to constantly have our orders placed at the very right edge of the green in the GDAX depth chart: at the highest bid.



Batch together all orders for the week in order to calculate an input transaction for our GDAX account. This value should be the summation of all payroll orders we have received during the pay period for that individual cryptocoin.

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
These requests to the API will send cryptocurrency from our wallet to those of our customers, paying the portion of the paycheck they elected to recieve through cryptocurrency. 

Future Enhancements:

Providing tax-compliance related features related to this service.

Sandbox notes:

We configured our repository with sandbox API keys to do testing with fake money.

This needs to be changed for prod, just generate the keys according to the GDAX site, replace them in application.yml, and replace the API endpoints with ones that do not sandbox.

GDAX API docs
https://docs.gdax.com/#coinbase48
