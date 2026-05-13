# PowerSupply-Marketing-Termination

《智能电网信息处理技术》课程设计项目：营销业务中“无表临时用电终止”模块。

系统依据 `F01-SGPMSS需求规格说明书（第二篇 新装、增容及变更用电分册 上）` 中 `BM01_009/无表临时用电终止` 的业务描述、流程环节、费用规则和归档要求实现。

## 技术栈

- Java 21
- Spring Boot 3.3
- Spring MVC + Thymeleaf
- Spring Data JPA
- H2 文件数据库
- Maven

## 业务范围

系统覆盖以下流程：

1. 业务受理
2. 勘查派工
3. 现场勘查
4. 终止合同
5. 停电
6. 确定费用
7. 结清费用
8. 信息归档
9. 客户回访
10. 归档

## 本地运行

```powershell
mvn -s .mvn/settings.xml spring-boot:run
```

启动后访问：

- 业务前端：`http://localhost:8080`
- H2 控制台：`http://localhost:8080/h2-console`

H2 JDBC URL：`jdbc:h2:file:./data/termination-db`

## 测试验证

```powershell
mvn -s .mvn/settings.xml test
```

当前自动化测试覆盖服务层业务规则、REST 接口、页面渲染和 Spring Boot 启动冒烟场景。该测试范围面向课程设计交付，重点验证“无表临时用电终止”业务闭环可准确跑通。

## 过程文档

课程设计过程性文档保存在 `docs/process/`：

- `00-document-control.md`
- `01-requirements-analysis.md`
- `02-architecture-design.md`
- `03-implementation-plan.md`
- `04-test-record.md`
- `05-current-status.md`
- `06-issues-and-solutions.md`
- `07-testing-and-verification.md`
