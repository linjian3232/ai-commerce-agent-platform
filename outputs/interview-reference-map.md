# Interview Reference Map

This project uses a runnable backend project as the main learning vehicle, and uses high-quality Java interview knowledge maps as reference material.

## Primary Reference

Primary source:

- JavaGuide: https://javaguide.cn/

Reason:

- Broad coverage for Java backend interviews.
- Includes Java basics, collections, concurrency, JVM, Spring, MySQL, Redis, MQ, distributed systems, system design, and AI application development.
- Suitable as the main checklist while this project evolves.

## Supplementary References

- Xiao Lin Coding: https://xiaolincoding.com/
  - Use mainly for MySQL, Redis, networking, operating systems, and illustrated explanations.
- Pdai Java Full Stack Knowledge System: https://pdai.tech/
  - Use mainly for deeper Java, Spring, architecture, and source-code-level reading.

## How To Use This Map

For every learning section, Codex should connect the project task with interview knowledge points:

1. Project scenario: what code or SQL we are touching.
2. Interview topic: which Java backend interview point it maps to.
3. Core principle: what must be explained clearly.
4. Common traps: what interviewers may challenge.
5. Practice task: what the user should implement, analyze, or explain.

The project should not become pure interview-question memorization. Interview questions are used as a checklist to make sure project learning covers real backend fundamentals.

## Current Stage Mapping

### Stage 1.2 - Order Status Concurrency Control

Project scenario:

- Order payment, cancellation, shipping, and status transitions.

Interview topics:

- Spring transaction basics.
- Database atomic update.
- Concurrency control.
- Idempotency.
- Why not use `synchronized` or Redis distributed lock first.

Core questions:

- What is the race condition in "select then update"?
- Why is `UPDATE ... WHERE id = ? AND status = ?` atomic?
- What does `updatedRows == 0` mean?
- How should repeated payment or repeated cancellation be handled?

### Stage 1.3 - MyBatis Query And MySQL Index

Project scenario:

- Order list query.
- Order item batch loading.
- N+1 query optimization.
- EXPLAIN and index decisions.

Interview topics:

- MyBatis query design.
- N+1 query problem.
- MySQL composite index.
- Leftmost prefix principle.
- Covering index and back-to-table.
- `EXPLAIN` fields: `type`, `possible_keys`, `key`, `rows`, `Extra`.
- `Using where`, `Using filesort`, backward index scan.
- Redundant indexes.

Core questions:

- Why does N+1 happen?
- Why can two SQL queries be better than one query per order?
- Why might MySQL not use a newly created index?
- Can `(user_id, status, created_at)` replace `(user_id, created_at)`?
- How does `ORDER BY` affect index choice?

## Future Stage Mapping

### Java Basics

Must cover:

- HashMap structure and resize.
- ArrayList vs LinkedList.
- `equals` and `hashCode`.
- Generics and type erasure.
- Exceptions and checked exceptions.

### Java Concurrency

Must cover:

- Java Memory Model.
- `volatile`.
- `synchronized` and `ReentrantLock`.
- Thread pool parameters.
- `CompletableFuture`.
- CAS and AQS basics.

### Spring And Spring Boot

Must cover:

- Bean lifecycle.
- AOP proxy.
- Transaction propagation and rollback rules.
- Same-class method call transaction failure.
- Controller / Service / Mapper boundaries.

### MySQL

Must cover:

- Index structure.
- Composite index and leftmost prefix.
- Covering index and back-to-table.
- `EXPLAIN`.
- MVCC.
- Isolation levels.
- Row locks, gap locks, next-key locks.
- Deadlock analysis.

### Redis

Must cover:

- Cache penetration, breakdown, avalanche.
- Cache consistency.
- Distributed lock.
- Lua script atomicity.
- Hot key and big key.
- Rate limiting.

### MQ / Kafka

Must cover:

- Consumer group and partition.
- Offset and ACK.
- Duplicate consumption.
- Message loss.
- Idempotent consumer.
- Ordered message.
- Dead letter and retry.

### Distributed Systems And Engineering

Must cover:

- Idempotency.
- Distributed transaction patterns.
- Rate limiting.
- Circuit breaking and degradation.
- TraceId and logging.
- Observability.
- Load testing.

### AI Engineering

Must cover:

- RAG pipeline.
- Embedding and vector retrieval.
- Rerank.
- MCP tool calling.
- LangGraph4j agent orchestration.
- Multi-agent workflow risks.
