# AI Commerce Agent Platform Chapter Log

This file records chapter-level project changes for GitHub history, review, and interview preparation.

## Chapter Recording Rules

Each chapter should record:

1. Stage and chapter name.
2. Learning goal.
3. Code changes.
4. Tests added or changed.
5. Verification command and result.
6. Interview talking points.
7. Next task.

## Stage 0 - Capability Diagnosis And Project Skeleton

### Goal

Set up a sustainable Java backend + AI engineering learning project and record the initial capability diagnosis.

### Changes

- Created a Maven multi-module project.
- Created `order-service`.
- Selected Java 17, Spring Boot 3.3.6, MyBatis Plus 3.5.7, H2, JUnit 5.
- Recorded the initial backend and AI engineering capability diagnosis in `outputs/learning-profile.md`.

### Verification

- Project structure created successfully.

### Talking Points

- This project is designed as a long-term portfolio project instead of a copy-following course project.
- The learning route covers Java, Spring Boot, MySQL, Redis, Kafka, distributed systems, engineering practice, RAG, MCP, and LangGraph4j.

## Stage 1.1 - Order Service Single-Module Basics

### Goal

Build the first runnable order-service version and practice Spring Boot basic business development.

### Changes

- Implemented order creation.
- Implemented order detail query.
- Implemented user order list query.
- Implemented user status-based order query.
- Implemented order payment, cancellation, shipping, and completion.
- Added unified response object `ApiResponse`.
- Added global exception handling.
- Added `BusinessException` and `ErrorCode`.
- Added request validation.
- Added `OrderStatus` enum to centralize status transition rules.
- Added H2 schema for `orders` and `order_items`.
- Added MyBatis Plus mappers.

### Verification

```text
mvn -s work/maven-settings.xml test
Tests run: 5, Failures: 0, Errors: 0
BUILD SUCCESS
```

### Talking Points

- `@Transactional` protects order creation across order master and item inserts.
- `OrderStatus` keeps status transition rules centralized and readable.
- Current list query has an N+1 query risk, which will be optimized in a later chapter.

## Stage 1.2 - Order Status Concurrency Control

### Goal

Upgrade order status changes from "select then update" to atomic database conditional updates.

### Changes

- Added `OrderMapper#updateStatusByIdAndStatus`.
- Changed `pay` to use conditional update: `CREATED -> PAID`.
- Changed `ship` to use conditional update: `PAID -> SHIPPED`.
- Changed `cancel` to use conditional update: `CREATED -> CANCELED`.
- Added common private method `changeStatusAtomically`.
- Added `IdempotencyPolicy` to make idempotency behavior explicit.
- Made repeated cancellation idempotent: repeated `cancel` on a `CANCELED` order returns the current order.
- Kept `complete` on the older `changeStatus` path for later refactoring practice.

### Tests

- Added rejection test for repeated payment.
- Added rejection test for shipping an unpaid order.
- Added rejection test for canceling a paid order.
- Added concurrent payment test: 10 concurrent requests, exactly 1 success.
- Added repeated cancellation idempotency test.

### Verification

```text
mvn test
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Talking Points

- `UPDATE ... WHERE id = ? AND status = ?` moves the state check into the database.
- A single-row conditional update is atomic and suitable for multi-instance deployment.
- This approach is simpler and more direct than using `synchronized` or Redis distributed locks for single-row state transitions.
- `updatedRows == 0` can mean either the order does not exist or the state has already changed.
- Idempotency must be designed by business semantics, not added blindly.

### Next Task

Enter Stage 1.3: optimize order list query N+1 problem and prepare for MySQL index and query analysis.
