package com.andyyu.powersupply.termination.config;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.service.TerminationApplicationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initSampleData(TerminationApplicationService service) {
        return args -> {
            if (!service.all().isEmpty()) {
                return;
            }

            TerminationApplication sample = new TerminationApplication();
            sample.setCustomerName("城南临时施工项目部");
            sample.setContactName("李工");
            sample.setContactPhone("13800000000");
            sample.setCustomerAddress("东城区建设路 8 号临时工地");
            sample.setDistrict("东城区");
            sample.setPowerSupplyUnit("东城供电所");
            sample.setProjectName("城南道路施工临时供电");
            sample.setProjectType("基建工地");
            sample.setUseNature("无表临时用电");
            sample.setIdentityNo("91110000M00000000A");
            sample.setApplicationSource("营业厅受理");
            sample.setAcceptanceRegion("东城区");
            sample.setContractNo("LJ-2026-001");
            sample.setContractCapacityKw(new BigDecimal("120"));
            sample.setPrepaidFee(new BigDecimal("3600"));
            sample.setAgreedDays(30);
            sample.setActualDays(new BigDecimal("14"));
            sample.setRequestedTerminationDate(LocalDate.now());
            sample.setRemark("样例业务，用于演示流程流转。");
            service.create(sample);
        };
    }
}
