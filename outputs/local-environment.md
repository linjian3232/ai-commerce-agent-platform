# Local Development Environment

This project now targets the local machine environment instead of the previous embedded H2 setup.

## MySQL

Recommended version:

- MySQL Community Server 8.4 LTS

Alternative:

- MySQL 8.0.x, if you want to stay closer to many existing company environments.

Suggested local database:

```sql
CREATE DATABASE ai_commerce_order
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;
```

Default application connection:

```text
jdbc:mysql://localhost:3306/ai_commerce_order
username: root
password: root
```

If your local password is different, set environment variables before starting the service:

```powershell
$env:ORDER_DB_USERNAME="root"
$env:ORDER_DB_PASSWORD="your_password"
```

You can also override the full JDBC URL:

```powershell
$env:ORDER_DB_URL="jdbc:mysql://localhost:3306/ai_commerce_order?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
```

## Maven Repository

The project Maven settings now use:

```text
D:\mvnRepository
```

Command:

```powershell
mvn -s work/maven-settings.xml test
```

## Codex Execution Notes

Codex still runs with a filesystem and network sandbox for safety. For this project:

- Commands that write to `D:\mvnRepository` require elevated execution.
- Commands that download dependencies or push to GitHub require network/elevated execution.
- The code and configuration now target your local MySQL and Maven repository, so Codex and your own terminal will use the same project-level settings when running the same Maven command.
