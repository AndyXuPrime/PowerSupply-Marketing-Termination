# 02 架构设计

## 架构选型

本课程设计采用单体 Java Web 应用：

- 后端：Spring Boot、Spring MVC、Spring Data JPA
- 前端：Thymeleaf 服务端渲染页面
- 数据库：H2 文件数据库，便于本地运行和课程演示

## 分层结构

```text
controller  页面控制器与 REST 接口
service     业务流程、费用计算、状态流转
domain      JPA 实体与流程枚举
repository  数据访问
templates   Thymeleaf 页面
static      CSS 与少量交互脚本
```

## 核心实体

`TerminationApplication` 表示无表临时用电终止申请，包含：

- 客户与联系人信息
- 临时用电合同与容量信息
- 受理、派工、勘查、合同终止、停电、费用、归档和回访信息
- 当前流程状态和时间戳

## 流程状态

```text
ACCEPTED -> DISPATCHED -> FIELD_INSPECTED -> CONTRACT_TERMINATED
-> POWER_OFF -> FEE_DETERMINED -> FEE_SETTLED -> INFO_ARCHIVED
-> CALLBACK_DONE -> ARCHIVED
```

## 费用计算设计

系统按需求规格说明书规则实现：

- `actualDays < agreedDays / 2`：退还预收电费的一半。
- `actualDays >= agreedDays / 2` 且 `actualDays <= agreedDays`：不退费、不补收。
- `actualDays > agreedDays`：按 `prepaidFee / agreedDays * (actualDays - agreedDays)` 补收。

金额统一保留两位小数。
