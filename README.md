# WALLET APP

## ABOUT THIS APP

A wallet app which has deposit and withdraw features. In some conditions transactions will be available for employee approval.

<br><br>

## TECH STACK

- Java 21
- Spring boot 3.5
- Spring security - Basic Authentication
- H2 in-memory DB (it resets data of itself on each app restart)

<br><br>

## 🔨 BUILD THE APP

below frameworks should be installed on your OS:
- Maven 3.9.9
- Java 21

```sh
mvn clean install
```

<br><br>

## ▶️ RUN THE APP

Choose your profile:

| File                       | Environment                           |
|----------------------------|---------------------------------------|
| application.properties     | Common properties for all environment |
| application-dev.properties | DEV                                   |
| application.properties     | PROD                                  |

<br>

### ▶️ Run from IDE

Run __WalletApplication.java__ class.

### ▶️ Run from terminal

```sh
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

<br><br>

## 🧪 EXAMPLE TEST SCENARIOS

You can execute all below scripts from command line one by one.

Note: If you use IntelliJ IDEA you will see an terminal icon on each test screnario below. Just click that icon to run the script.

<br><br>

### 🧪 create wallet by employee (for a customer)

employee can create wallet for any employee (employee may work from call-center and help customers to create their wallets).

```sh
curl -X POST http://localhost:8080/employee/create-wallet \
  -H "Content-Type: application/json" \
  -u mert:444 \
  -d '{
    "customerId": 2,
    "walletName": "Family Wallet",
    "currency": "USD",
    "shopping": true,
    "withdraw": true
  }'
```

### 🧪 create wallet by customer (for himself)

```sh
curl -X POST http://localhost:8080/customer/create-wallet \
  -H "Content-Type: application/json" \
  -u ayse:123 \
  -d '{
    "walletName": "Gift Wallet",
    "currency": "USD",
    "shopping": true,
    "withdraw": true
  }'
```

### 🧪 create wallet by customer (for someone else)

it will fail. because of permission.

```sh
curl -X POST http://localhost:8080/customer/create-wallet \
  -H "Content-Type: application/json" \
  -u ayse:123 \
  -d '{
    "customerId": 1,
    "walletName": "Gift Wallet",
    "currency": "USD",
    "shopping": true,
    "withdraw": true
  }'
```

### 🧪 list wallets

```sh
curl -X GET "http://localhost:8080/employee/list?customerId=2" \
  -u mert:444
```

### 🧪 user can not trigger a transaction for another customers wallet

```sh
curl -X POST "http://localhost:8080/customer/withdraw" \
    -u fatma:567 \
    -G \
    --data-urlencode "walletId=1" \
    --data-urlencode "amount=500" \
    --data-urlencode "oppositePartyType=IBAN"
```

### 🧪 user triggers withdraw transaction

below command will return balance error because the wallet is empty yet.

```sh
curl -X POST "http://localhost:8080/customer/withdraw" \
    -u ayse:123 \
    -G \
    --data-urlencode "walletId=1" \
    --data-urlencode "amount=500" \
    --data-urlencode "oppositePartyType=IBAN"
```

### 🧪 user triggers deposit transactions (which will need to approve)

```sh
curl -X POST "http://localhost:8080/customer/deposit" \
    -u ayse:123 \
    -G \
    --data-urlencode "walletId=1" \
    --data-urlencode "amount=1001" \
    --data-urlencode "oppositePartyType=IBAN"
```

### 🧪 user triggers deposit transactions (which is approved directly)

```sh
curl -X POST "http://localhost:8080/customer/deposit" \
    -u ayse:123 \
    -G \
    --data-urlencode "walletId=1" \
    --data-urlencode "amount=999" \
    --data-urlencode "oppositePartyType=IBAN"
```

### 🧪 approve transaction

```sh
curl -X POST http://localhost:8080/employee/transaction/approve \
    -H "Content-Type: application/json" \
    -u mert:444 \
    -d '{"transactionId": "1", "status": "APPROVED"}'
```

### 🧪 user triggers withdraw transaction

```sh
curl -X POST "http://localhost:8080/customer/withdraw" \
    -u ayse:123 \
    -G \
    --data-urlencode "walletId=1" \
    --data-urlencode "amount=500" \
    --data-urlencode "oppositePartyType=IBAN"
```

### 🧪 user triggers withdraw transaction with no registered/valid user

state will not changed on server after below request.

```sh
curl -X POST "http://localhost:8080/customer/withdraw" \
    -u invalid_user:123 \
    -G \
    --data-urlencode "walletId=1" \
    --data-urlencode "amount=500" \
    --data-urlencode "oppositePartyType=IBAN"
```

### 🧪 list of transactions

```sh 
curl -X GET "http://localhost:8080/customer/1/transactions" \
  -u ayse:123
```