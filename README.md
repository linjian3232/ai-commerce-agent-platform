# AI Commerce Agent Platform

这是一个面向 Java 后端进阶和 AI 工程落地的学习项目。项目会按阶段迭代，从订单系统单体版开始，逐步加入 MySQL、Redis、Kafka、工程化、RAG、MCP 和 LangGraph4j 多 Agent 工作流。

## 当前阶段

阶段 1：订单系统单体版。

当前能力点：

- Spring Boot 3 基础工程
- MyBatis Plus 数据访问
- H2 内存数据库
- Controller / Service / Mapper 分层
- 统一响应结构
- 全局异常处理
- 参数校验
- 订单状态流转
- 基础单元测试

## 启动方式

```bash
mvn -pl order-service spring-boot:run
```

服务地址：

```text
http://localhost:8080
```

H2 控制台：

```text
http://localhost:8080/h2-console
```

JDBC URL：

```text
jdbc:h2:mem:orderdb
```

## 创建订单示例

```bash
curl -X POST http://localhost:8080/api/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1001,\"remark\":\"first order\",\"items\":[{\"productId\":2001,\"productName\":\"Java Book\",\"quantity\":2,\"unitPrice\":39.90}]}"
```

## 订单状态

允许的状态流转：

```text
CREATED -> PAID
CREATED -> CANCELED
PAID -> SHIPPED
SHIPPED -> COMPLETED
```

## 你的阶段任务

1. 阅读 `OrderStatus`，解释为什么状态流转适合放在枚举里。
2. 阅读 `OrderServiceImpl#createOrder`，解释为什么需要 `@Transactional`。
3. 新增一个接口：查询指定状态的用户订单列表。
4. 新增一个测试：验证 `PAID -> CANCELED` 会失败。
5. 回答：如果创建订单时订单主表插入成功，但明细表插入失败，会发生什么？
