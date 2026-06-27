# AI Commerce Agent Platform Learning Profile

## Current Position

You are currently at the stage of being able to complete ordinary Java backend business development, but the core backend knowledge system needed for large-company interviews is not yet stable enough. The project will avoid pure interview-question drilling and copy-following course projects. Instead, it will grow through a runnable Java backend plus AI engineering system.

## Priority Diagnosis

| Area | Current Level | Priority |
| --- | --- | --- |
| Java basics | Weak, especially collections, concurrency, thread pools, and locks | High |
| Spring Boot development | Usable for business development | High |
| Spring internals | Weak, especially transactions, AOP, and Bean lifecycle | High |
| MySQL | Above beginner, but indexes, transactions, locks, MVCC, Explain, and concurrent deduction need strengthening | High |
| Redis | Beginner, with mixed concepts around cache problems and consistency | High |
| Kafka / MQ | Shallow, mainly used through company wrappers | High |
| Distributed systems | Some intuition, not yet systematic | Medium-high |
| Engineering practice | Weak in testing, load testing, logs, TraceId, observability, idempotency, rate limiting, and resilience | Medium-high |
| AI engineering | Differentiation potential, needs a full RAG + MCP + LangGraph4j implementation | High |

## Learning Rhythm

Each stage follows the half-demo loop:

1. Codex explains the technical goal with a small business context.
2. Codex provides only a minimal demo, skeleton, interface boundary, or comparison sample.
3. You complete the core implementation, SQL, test case, or technical analysis.
4. Codex reviews the result, fixes misunderstandings, and adds deeper technical explanation.
5. You answer the knowledge check.
6. Move to the next stage only after the code, scenario, and interview assessment pass.

Codex should not complete the whole feature by default. Full implementation by Codex is reserved for environment setup, dependency configuration, test failure diagnosis, Git operations, and explicit user requests.

## Assessment Dimensions

1. Code assessment: whether the implementation is reasonable.
2. Scenario assessment: whether it can handle real business problems.
3. Interview assessment: whether the principles, risks, and tradeoffs can be explained clearly.

## Weak Points Found During Project Iteration

### 2026-06-27 - Stage 1.3

- Weak point: MySQL composite index replacement and redundancy judgment.
- Trigger: When comparing `(user_id, created_at)` and `(user_id, status, created_at)`, the initial judgment was that the longer index could replace the shorter one.
- Correction: A longer composite index does not automatically replace a shorter index if the column order changes the usable prefix for filtering and sorting. `(user_id, status, created_at)` can support queries with `user_id` and `status`, but it cannot fully replace `(user_id, created_at)` for `WHERE user_id = ? ORDER BY created_at DESC` because `status` sits between `user_id` and `created_at`.
- Follow-up: Strengthen understanding of leftmost prefix, index order, sorting with indexes, redundant indexes, and `EXPLAIN`-based index decisions.
