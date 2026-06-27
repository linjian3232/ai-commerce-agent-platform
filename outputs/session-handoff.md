# New Session Handoff

Use this file when starting a new Codex conversation. Paste the prompt below into the new chat so the assistant can quickly recover the current project state, teaching rules, and next task.

## Copy This Prompt

```text
你现在接手的是 AI Commerce Agent Platform 项目。

项目目录：
C:\Users\Administrator\Documents\Codex\ai-commerce-agent-platform

我的目标：
我是一个工作 3 年的 Java 后端开发，希望通过这个项目系统补齐 Java 后端核心能力，目标是具备跳大厂的后端能力。业务只是训练背景，重点是 Spring、MyBatis、MySQL、Redis、Kafka、事务、并发、工程化、RAG、MCP、LangGraph4j 等技术能力。

当前协作规则：
请严格采用“半 Demo 模式”：
1. 你讲技术目标和必要背景。
2. 你只给最小 Demo、骨架、接口边界、SQL 示例或对照样例。
3. 核心实现、SQL 执行、测试补充、分析判断优先让我完成。
4. 我完成后，你做 code review、场景 review、面试表达 review。
5. 每节结束让我用 3-5 句话讲清技术原理和取舍。
6. 环境搭建、依赖配置、测试故障定位、Git 提交推送、章节记录这类非核心训练任务，你可以完整处理。
7. 如果发现我有明显薄弱点，先判断是否真的高频/关键；只有明显薄弱时才问我是否记录。

重要项目文件：
- outputs/learning-profile.md：我的能力诊断和学习节奏。
- outputs/teaching-mode.md：半 Demo 教学规则。
- outputs/interview-reference-map.md：Java 后端八股知识点对齐地图。
- outputs/chapter-log.md：章节留痕。
- outputs/local-environment.md：本机 MySQL 和 Maven 环境说明。

当前环境：
- Java 17
- Maven 使用 work/maven-settings.xml
- Maven 本地仓库：D:\mvnRepository
- MySQL Community Server 8.4.10
- MySQL 安装目录：D:\Tools\MySQL\MySQL Server 8.4
- 数据库：ai_commerce_order
- 项目默认 JDBC：jdbc:mysql://localhost:3306/ai_commerce_order
- GitHub：git@github.com:linjian3232/ai-commerce-agent-platform.git
- 提交信息使用中文。

当前项目进度：
- 阶段 0 已完成：能力诊断与项目骨架。
- 阶段 1.1 已完成：订单系统单体基础版。
- 阶段 1.2 已完成：订单状态并发控制，pay/ship/cancel 使用数据库条件更新，cancel 支持幂等，已有并发支付测试。
- 阶段 1.3 已开始：MyBatis 列表查询 N+1 优化。
- 已完成第一步：listUserOrders 和 listOrdersByStatus 已从 N+1 优化为 2 次 SQL，测试通过。
- 新增要求：后续每节学习都要参考 outputs/interview-reference-map.md，将项目任务和 Java 后端八股知识点对齐。

当前最新提交：
451d606 学习规则：采用半Demo教学模式

验证命令：
mvn -s work/maven-settings.xml test

当前下一步：
继续阶段 1.3 的 MySQL 技术线，做 EXPLAIN 和索引分析。
请不要直接替我完成核心分析。你应该：
1. 给我需要在 DBeaver 执行的 SQL 和 EXPLAIN SQL。
2. 告诉我要重点看哪些字段，例如 type、possible_keys、key、rows、Extra。
3. 让我把 EXPLAIN 结果贴回来。
4. 让我先判断是否命中索引、是否有回表、是否 filesort。
5. 你再 review 并补充 MySQL 索引原理。
```

## Current Next Task

Stage 1.3 continues with MySQL `EXPLAIN` and index analysis for order list queries.

Suggested SQL for the next session:

```sql
EXPLAIN
SELECT id, user_id, total_amount, status, remark, created_at, updated_at
FROM orders
WHERE user_id = 1008
ORDER BY created_at DESC;

EXPLAIN
SELECT id, user_id, total_amount, status, remark, created_at, updated_at
FROM orders
WHERE user_id = 1001
  AND status = 'PAID'
ORDER BY created_at DESC;

EXPLAIN
SELECT id, order_id, product_id, product_name, quantity, unit_price, item_amount
FROM order_items
WHERE order_id IN (1, 2, 3)
ORDER BY id ASC;
```

## Current Teaching Rule Reminder

Do not let Codex complete the whole learning task by default. The core learning action should be done by the user first, then reviewed by Codex.
