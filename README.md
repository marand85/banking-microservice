# Banking & Gaming Microservice

A Java REST microservice running on port **8080** that solves three independent algorithmic challenges:
bank transaction reporting, ATM service scheduling, and online game clan grouping.

The project was built as part of a programming competition and is designed to handle large input volumes efficiently.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Running the Application](#running-the-application)
- [API Overview](#api-overview)
- [Endpoint 1 — Transactions Report](#endpoint-1--transactions-report)
- [Endpoint 2 — ATM Service Order](#endpoint-2--atm-service-order)
- [Endpoint 3 — Online Game Clan Grouping](#endpoint-3--online-game-clan-grouping)

---

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Maven**
- OpenAPI 3.0 specification

---

## Running the Application

```bash
mvn spring-boot:run
```

The service starts at `http://localhost:8080`.

Interactive API docs (Swagger UI) available at:
```
http://localhost:8080/swagger-ui.html
```

---

## API Overview

| Method | Endpoint                  | Description                              |
|--------|---------------------------|------------------------------------------|
| POST   | `/transactions/report`    | Aggregate and sort bank account balances |
| POST   | `/atms/calculateOrder`    | Schedule ATM service visits by priority  |
| POST   | `/onlinegame/calculate`   | Group clans into optimised entry batches |

---

## Endpoint 1 — Transactions Report

### Problem

A large bank processes thousands of transactions every day — both incoming credits and outgoing debits.
One department wants to know how the balance on every account changes after all daily transactions are processed,
with results sorted in **ascending order by account number**.

The system must handle up to **100 000 transactions** efficiently.

Each transaction moves `amount` from `debitAccount` (charged) to `creditAccount` (receives funds).
The starting balance of every account is **0**.

### Algorithm

- Iterate over all transactions and aggregate per account: increment debit/credit counters and update the running balance.
- Sort the resulting account list lexicographically in ascending order.

### OpenAPI Schema

```yaml
POST /transactions/report

Request body — array of Transaction (max 100 000 items):
  Transaction:
    debitAccount:  string  # exactly 26 characters
    creditAccount: string  # exactly 26 characters
    amount:        number (float)

Response 200 — array of Account (sorted ascending by account number):
  Account:
    account:      string   # exactly 26 characters
    debitCount:   integer  # number of times this account was debited
    creditCount:  integer  # number of times this account was credited
    balance:      number   # final balance (starting from 0)
```

### Example

**Request** `POST /transactions/report`

```json
[
  {
    "debitAccount": "32309111922661937852684864",
    "creditAccount": "06105023389842834748547303",
    "amount": 10.90
  },
  {
    "debitAccount": "31074318698137062235845814",
    "creditAccount": "66105036543749403346524547",
    "amount": 200.90
  },
  {
    "debitAccount": "66105036543749403346524547",
    "creditAccount": "32309111922661937852684864",
    "amount": 50.10
  }
]
```

**Response** `200 OK`

```json
[
  {
    "account": "06105023389842834748547303",
    "debitCount": 0,
    "creditCount": 1,
    "balance": 10.90
  },
  {
    "account": "31074318698137062235845814",
    "debitCount": 1,
    "creditCount": 0,
    "balance": -200.90
  },
  {
    "account": "32309111922661937852684864",
    "debitCount": 1,
    "creditCount": 1,
    "balance": 39.20
  },
  {
    "account": "66105036543749403346524547",
    "debitCount": 1,
    "creditCount": 1,
    "balance": 150.80
  }
]
```

---

## Endpoint 2 — ATM Service Order

### Problem

An ATM service convoy returns to work after a long weekend.
The ticketing system holds a backlog of requests that must be arranged into a single ordered queue.

The convoy travels through **regions in ascending order** of their numbers.
Within each region, requests are prioritised by urgency:

| Priority | Request Type      | Reason                                                               |
|----------|-------------------|----------------------------------------------------------------------|
| 1st      | `FAILURE_RESTART` | ATM is down and cannot be restarted remotely — handle immediately    |
| 2nd      | `PRIORITY`        | Planned high-priority refill (high cash consumption trend)           |
| 3rd      | `SIGNAL_LOW`      | Unplanned low-cash alert — handle right after all PRIORITY tasks     |
| 4th      | `STANDARD`        | Regular planned refill                                               |

Additional constraint: **each ATM may appear only once** in the output.
If the same ATM has multiple requests, only the highest-priority one determines its position.

### Algorithm

- Group all service tasks by region.
- Within each region, deduplicate ATMs keeping only the highest-priority request per ATM.
- Sort ATMs within the region by priority (FAILURE_RESTART → PRIORITY → SIGNAL_LOW → STANDARD).
- Concatenate regions in ascending order.

### OpenAPI Schema

```yaml
POST /atms/calculateOrder

Request body — array of Task:
  Task:
    region:      integer  # 1 – 9999
    requestType: enum     # STANDARD | PRIORITY | SIGNAL_LOW | FAILURE_RESTART
    atmId:       integer  # 1 – 9999

Response 200 — array of ATM (ordered queue):
  ATM:
    region: integer
    atmId:  integer
```

### Example 1

**Request** `POST /atms/calculateOrder`

```json
[
  { "region": 4, "requestType": "STANDARD",         "atmId": 1 },
  { "region": 1, "requestType": "STANDARD",         "atmId": 1 },
  { "region": 2, "requestType": "STANDARD",         "atmId": 1 },
  { "region": 3, "requestType": "PRIORITY",         "atmId": 2 },
  { "region": 3, "requestType": "STANDARD",         "atmId": 1 },
  { "region": 2, "requestType": "SIGNAL_LOW",       "atmId": 1 },
  { "region": 5, "requestType": "STANDARD",         "atmId": 2 },
  { "region": 5, "requestType": "FAILURE_RESTART",  "atmId": 1 }
]
```

**Response** `200 OK`

```json
[
  { "region": 1, "atmId": 1 },
  { "region": 2, "atmId": 1 },
  { "region": 3, "atmId": 2 },
  { "region": 3, "atmId": 1 },
  { "region": 4, "atmId": 1 },
  { "region": 5, "atmId": 1 },
  { "region": 5, "atmId": 2 }
]
```

> ATM 1 in region 2 had both STANDARD and SIGNAL\_LOW requests — it appears once, placed at the SIGNAL\_LOW priority slot.
> In region 5, ATM 1 (FAILURE\_RESTART) comes before ATM 2 (STANDARD).

### Example 2

**Request** `POST /atms/calculateOrder`

```json
[
  { "region": 1, "requestType": "STANDARD",         "atmId": 2 },
  { "region": 1, "requestType": "STANDARD",         "atmId": 1 },
  { "region": 2, "requestType": "PRIORITY",         "atmId": 3 },
  { "region": 3, "requestType": "STANDARD",         "atmId": 4 },
  { "region": 4, "requestType": "STANDARD",         "atmId": 5 },
  { "region": 5, "requestType": "PRIORITY",         "atmId": 2 },
  { "region": 5, "requestType": "STANDARD",         "atmId": 1 },
  { "region": 3, "requestType": "SIGNAL_LOW",       "atmId": 2 },
  { "region": 2, "requestType": "SIGNAL_LOW",       "atmId": 1 },
  { "region": 3, "requestType": "FAILURE_RESTART",  "atmId": 1 }
]
```

**Response** `200 OK`

```json
[
  { "region": 1, "atmId": 2 },
  { "region": 1, "atmId": 1 },
  { "region": 2, "atmId": 3 },
  { "region": 2, "atmId": 1 },
  { "region": 3, "atmId": 1 },
  { "region": 3, "atmId": 2 },
  { "region": 3, "atmId": 4 },
  { "region": 4, "atmId": 5 },
  { "region": 5, "atmId": 2 },
  { "region": 5, "atmId": 1 }
]
```

---

## Endpoint 3 — Online Game Clan Grouping

### Problem

A popular online game holds special events where players earn the most points.
Due to **platform performance limitations**, players are admitted in batches called *groups*, each with a maximum size of **m** players.

Players can form **clans**. The entry order is determined by total clan points.
The grouping must follow these rules:

- Each group holds **at most m players**.
- Clan members **must enter together** — clans cannot be split across groups.
- Groups are **greedily packed**: fit as many players as possible into each group.
  If the next clan in the sorted order doesn't fit, scan further down the list for a smaller clan that does.
- **Tie-breaking**: if two clans have equal points, the one with **fewer players** has higher priority
  (its members are individually stronger).
- **All clans must enter** the event — no clan is left out.
- Input may contain up to **20 000 clans**.

### Algorithm

1. Sort clans by `points` descending; break ties by `numberOfPlayers` ascending.
2. Greedily fill each group: iterate the remaining sorted clans and add the first one that fits in the remaining space.
3. Repeat until all clans are placed.

### OpenAPI Schema

```yaml
POST /onlinegame/calculate

Request body — Players:
  Players:
    groupCount: integer   # maximum players per group; 1 – 1000
    clans:      array     # max 20 000 items
      Clan:
        numberOfPlayers: integer  # 1 – 1000
        points:          integer  # 1 – 100 000

Response 200 — array of Group (ordered list of groups):
  Group: array of Clan   # clans ordered by points desc within the group

Constraint: numberOfPlayers <= groupCount (every clan fits in at least one group)
```

### Example

**Request** `POST /onlinegame/calculate`

```json
{
  "groupCount": 6,
  "clans": [
    { "numberOfPlayers": 4, "points": 50 },
    { "numberOfPlayers": 2, "points": 70 },
    { "numberOfPlayers": 6, "points": 60 },
    { "numberOfPlayers": 1, "points": 15 },
    { "numberOfPlayers": 5, "points": 40 },
    { "numberOfPlayers": 3, "points": 45 },
    { "numberOfPlayers": 1, "points": 12 },
    { "numberOfPlayers": 4, "points": 40 }
  ]
}
```

**Response** `200 OK`

```json
[
  [
    { "numberOfPlayers": 2, "points": 70 },
    { "numberOfPlayers": 4, "points": 50 }
  ],
  [
    { "numberOfPlayers": 6, "points": 60 }
  ],
  [
    { "numberOfPlayers": 3, "points": 45 },
    { "numberOfPlayers": 1, "points": 15 },
    { "numberOfPlayers": 1, "points": 12 }
  ],
  [
    { "numberOfPlayers": 4, "points": 40 }
  ],
  [
    { "numberOfPlayers": 5, "points": 40 }
  ]
]
```

**Step-by-step walkthrough** (`m = 6`):

Sorted clans (points ↓, players ↑ on tie):

| # | Players | Points |
|---|---------|--------|
| 1 | 2       | 70     |
| 2 | 6       | 60     |
| 3 | 4       | 50     |
| 4 | 3       | 45     |
| 5 | 4       | 40     |
| 6 | 5       | 40     |
| 7 | 1       | 15     |
| 8 | 1       | 12     |

| Group | Clans added                       | Total players | Space left |
|-------|-----------------------------------|---------------|------------|
| 1     | clan#1 (2 pts:70) + clan#3 (4 pts:50) | 6         | 0 — clan#2 (6) didn't fit, skipped |
| 2     | clan#2 (6 pts:60)                 | 6             | 0          |
| 3     | clan#4 (3) + clan#7 (1) + clan#8 (1) | 5          | 1 — no remaining clan has ≤1 player |
| 4     | clan#5 (4 pts:40)                 | 4             | 2 — clan#6 (5) doesn't fit         |
| 5     | clan#6 (5 pts:40)                 | 5             | 1          |

---

## Quick Test with curl

Start the application and test each endpoint from the command line.

### Transactions Report

```bash
curl -s -X POST http://localhost:8080/transactions/report \
  -H "Content-Type: application/json" \
  -d '[
    {"debitAccount":"32309111922661937852684864","creditAccount":"06105023389842834748547303","amount":10.90},
    {"debitAccount":"31074318698137062235845814","creditAccount":"66105036543749403346524547","amount":200.90},
    {"debitAccount":"66105036543749403346524547","creditAccount":"32309111922661937852684864","amount":50.10}
  ]'
```

### ATM Service Order

```bash
curl -s -X POST http://localhost:8080/atms/calculateOrder \
  -H "Content-Type: application/json" \
  -d '[
    {"region":4,"requestType":"STANDARD","atmId":1},
    {"region":1,"requestType":"STANDARD","atmId":1},
    {"region":2,"requestType":"STANDARD","atmId":1},
    {"region":3,"requestType":"PRIORITY","atmId":2},
    {"region":3,"requestType":"STANDARD","atmId":1},
    {"region":2,"requestType":"SIGNAL_LOW","atmId":1},
    {"region":5,"requestType":"STANDARD","atmId":2},
    {"region":5,"requestType":"FAILURE_RESTART","atmId":1}
  ]'
```

Pipe through `jq` for formatted output (install with `sudo apt install jq`).

---

## OpenAPI Specification Files

The full OpenAPI 3.0.3 specification for each endpoint is available in the repository:

- <a href="tasks/transactions/transactions.json" target="_blank"><code>tasks/transactions/transactions.json</code></a>
- <a href="tasks/atmservice/atmservice.json" target="_blank"><code>tasks/atmservice/atmservice.json</code></a>
- <a href="tasks/onlinegame/onlinegame.json" target="_blank"><code>tasks/onlinegame/onlinegame.json</code></a>

To explore the specs interactively, paste any of these files into <a href="https://editor.swagger.io" target="_blank">Swagger Editor</a>.
