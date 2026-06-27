# New Session Handoff

Use this file when starting a new Codex conversation. Paste the prompt below into the new chat so the assistant can quickly recover the current project state, teaching rules, and next task.

## Copy This Prompt

```text
You are taking over the AI Commerce Agent Platform project.

Project directory:
C:\Users\Administrator\Documents\Codex\ai-commerce-agent-platform

User goal:
The user is a Java backend developer with 3 years of experience. The goal is to use this project to systematically build solid Java backend capability for big-company interviews and real engineering work. Business scenarios are only the training background. The focus is Spring, MyBatis, MySQL, Redis, Kafka, transactions, concurrency, engineering practice, RAG, MCP, and LangGraph4j.

Teaching rules:
Use half-demo mode.
1. Explain the technical goal and necessary context.
2. Provide only minimal demos, skeletons, SQL examples, interface boundaries, or comparison samples.
3. Let the user complete the core implementation, SQL execution, tests, or analysis whenever the task is part of core learning.
4. Review the user's work from code, scenario, and interview perspectives.
5. At the end of each section, ask the user to explain the principle and tradeoff in 3-5 sentences.
6. Codex may fully handle environment setup, dependency configuration, test failure diagnosis, Git commit/push, and chapter logs.
7. Do not treat the user like a beginner. The user has 3 years of Java backend experience. Avoid overly mechanical practice tasks.
8. For each major technical section, provide a knowledge coverage checklist:
   - Deeply covered now.
   - Must know but only briefly pointed out.
   - Deferred to a later section.

Important project files:
- outputs/learning-profile.md: capability diagnosis and weak points.
- outputs/teaching-mode.md: teaching rules.
- outputs/interview-reference-map.md: Java backend interview knowledge map.
- outputs/chapter-log.md: chapter history.
- outputs/local-environment.md: local MySQL and Maven setup.

Current environment:
- Java 17
- Maven uses work/maven-settings.xml
- Maven local repository: D:\mvnRepository
- MySQL Community Server 8.4.10
- MySQL install path: D:\Tools\MySQL\MySQL Server 8.4
- Database: ai_commerce_order
- Default JDBC URL: jdbc:mysql://localhost:3306/ai_commerce_order
- GitHub: git@github.com:linjian3232/ai-commerce-agent-platform.git
- Git commit messages should be Chinese.

Current progress:
- Stage 0 complete: capability diagnosis and project skeleton.
- Stage 1.1 complete: basic monolithic order service.
- Stage 1.2 complete: order status concurrency control. pay/ship/cancel use conditional database update. cancel is idempotent. Concurrent payment test exists.
- Stage 1.3 mostly complete: MyBatis list query N+1 optimization and first round MySQL EXPLAIN/index analysis.
- listUserOrders and listOrdersByStatus are optimized from N+1 to 2 SQL queries.
- order_items batch query ordering was adjusted to ORDER BY order_id, id after EXPLAIN comparison.
- Important weak point recorded: a longer composite index cannot automatically replace a shorter one when column order changes filtering or sorting behavior.
- New learning requirement: do not only teach the project slice. For core topics, list the broader knowledge map and mark what is deeply covered, what must be known, and what will be deferred.

Latest known learning direction:
Finish Stage 1.3 by summarizing index strategy, then move to MySQL transactions, isolation levels, MVCC, and locks using order payment as context.

Verification command:
mvn -s work/maven-settings.xml test
```

## Current Next Task

Finish Stage 1.3 with an interview-grade index summary, then start MySQL transaction/MVCC/lock training.

## Current Teaching Rule Reminder

Do not let Codex complete the whole learning task by default. The core learning action should be done by the user first, then reviewed by Codex. Also, do not narrow a technical topic to only the project slice; provide the broader interview-grade knowledge checklist.
