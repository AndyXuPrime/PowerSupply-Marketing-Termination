package com.andyyu.powersupply.termination.controller;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.domain.WorkflowStatus;
import com.andyyu.powersupply.termination.service.TerminationApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = TerminationPageController.class)
class TerminationPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TerminationApplicationService service;

    @Test
    void listPageShouldRender() throws Exception {
        when(service.all()).thenReturn(Collections.emptyList());
        when(service.list(null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("applications/list"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("业务台账")));
    }

    @Test
    void detailPageShouldRender() throws Exception {
        when(service.get(1L)).thenReturn(sampleApplication());

        mockMvc.perform(get("/applications/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("applications/detail"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("测试客户")));
    }

    @Test
    void createActionShouldRedirectToDetail() throws Exception {
        TerminationApplication created = sampleApplication();
        created.setId(10L);
        when(service.create(any(TerminationApplication.class))).thenReturn(created);

        mockMvc.perform(post("/applications")
                        .param("customerName", "测试客户")
                        .param("contactName", "测试联系人")
                        .param("contactPhone", "13800000000")
                        .param("customerAddress", "测试地址")
                        .param("district", "测试区")
                        .param("powerSupplyUnit", "测试供电所")
                        .param("projectName", "测试项目")
                        .param("projectType", "基建工地")
                        .param("useNature", "无表临时用电")
                        .param("contractNo", "CN-TEST-001")
                        .param("identityNo", "TEST-ID-001")
                        .param("applicationSource", "营业厅受理")
                        .param("acceptanceRegion", "测试区")
                        .param("contractCapacityKw", "120")
                        .param("prepaidFee", "3600")
                        .param("agreedDays", "30")
                        .param("requestedTerminationDate", "2026-05-13")
                        .param("remark", "测试业务"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications/10"));
    }

    private TerminationApplication sampleApplication() {
        TerminationApplication application = new TerminationApplication();
        application.setId(1L);
        application.setApplicationNo("BM01_009-20260513120000");
        application.setCustomerNo("C20260513120000");
        application.setCustomerName("测试客户");
        application.setContactName("测试联系人");
        application.setContactPhone("13800000000");
        application.setCustomerAddress("测试地址");
        application.setDistrict("测试区");
        application.setPowerSupplyUnit("测试供电所");
        application.setProjectName("测试项目");
        application.setProjectType("基建工地");
        application.setUseNature("无表临时用电");
        application.setContractNo("CN-TEST-001");
        application.setIdentityNo("TEST-ID-001");
        application.setApplicationSource("营业厅受理");
        application.setAcceptanceRegion("测试区");
        application.setContractCapacityKw(new BigDecimal("120"));
        application.setPrepaidFee(new BigDecimal("3600"));
        application.setAgreedDays(30);
        application.setRequestedTerminationDate(LocalDate.of(2026, 5, 13));
        application.setCreatedAt(LocalDateTime.of(2026, 5, 13, 12, 0));
        application.setUpdatedAt(LocalDateTime.of(2026, 5, 13, 12, 0));
        application.setStatus(WorkflowStatus.ACCEPTED);
        return application;
    }
}
