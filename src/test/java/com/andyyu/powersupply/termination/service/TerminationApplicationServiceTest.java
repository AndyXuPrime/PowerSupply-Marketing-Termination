package com.andyyu.powersupply.termination.service;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.domain.WorkflowStatus;
import com.andyyu.powersupply.termination.repository.TerminationApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TerminationApplicationService.class)
class TerminationApplicationServiceTest {

    @Autowired
    private TerminationApplicationService service;

    @Autowired
    private TerminationApplicationRepository repository;

    @Test
    void calculateFeeShouldRefundHalfBeforeHalfPeriod() {
        FeeCalculationResult result = service.calculateFee(new BigDecimal("3600"), 30, new BigDecimal("14"));

        assertThat(result.refundAmount()).isEqualByComparingTo("1800.00");
        assertThat(result.receivableAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    void calculateFeeShouldChargeWhenOverTerm() {
        FeeCalculationResult result = service.calculateFee(new BigDecimal("3600"), 30, new BigDecimal("35"));

        assertThat(result.refundAmount()).isEqualByComparingTo("0.00");
        assertThat(result.receivableAmount()).isEqualByComparingTo("600.00");
        assertThat(result.terminalFee()).isEqualByComparingTo("600.00");
    }

    @Test
    void workflowShouldReachArchivedState() {
        TerminationApplication created = service.create(sampleApplication());
        Long id = created.getId();

        service.dispatch(id, "张三");
        service.survey(id, "现场核实通过", null);
        service.terminateContract(id, null);
        service.powerOff(id);
        service.determineFee(id, new BigDecimal("14"));
        service.settleFee(id, "V20260513", new BigDecimal("1800"), BigDecimal.ZERO);
        service.archiveInfo(id, "档案室-A-03");
        service.callback(id, "已告知退费结果");
        TerminationApplication archived = service.archive(id);

        assertThat(archived.getStatus()).isEqualTo(WorkflowStatus.ARCHIVED);
        assertThat(archived.getRefundAmount()).isEqualByComparingTo("1800.00");
        assertThat(archived.getReceivableAmount()).isEqualByComparingTo("0.00");
        assertThat(archived.getArchiveLocation()).isEqualTo("档案室-A-03");
        assertThat(archived.getCallbackResult()).isEqualTo("已告知退费结果");
    }

    @Test
    void invalidTransitionShouldBeRejected() {
        TerminationApplication created = service.create(sampleApplication());

        assertThatThrownBy(() -> service.powerOff(created.getId()))
                .isInstanceOf(WorkflowException.class)
                .hasMessageContaining("当前状态");
    }

    private TerminationApplication sampleApplication() {
        TerminationApplication application = new TerminationApplication();
        application.setCustomerName("测试客户");
        application.setContactName("测试联系人");
        application.setContactPhone("13800000000");
        application.setCustomerAddress("测试地址");
        application.setDistrict("测试区");
        application.setPowerSupplyUnit("测试供电所");
        application.setProjectName("测试项目");
        application.setProjectType("基建工地");
        application.setUseNature("无表临时用电");
        application.setIdentityNo("TEST-ID-001");
        application.setApplicationSource("营业厅受理");
        application.setAcceptanceRegion("测试区");
        application.setContractNo("CN-TEST-001");
        application.setContractCapacityKw(new BigDecimal("120"));
        application.setPrepaidFee(new BigDecimal("3600"));
        application.setAgreedDays(30);
        application.setRequestedTerminationDate(LocalDate.of(2026, 5, 13));
        application.setRemark("测试业务");
        return application;
    }
}
