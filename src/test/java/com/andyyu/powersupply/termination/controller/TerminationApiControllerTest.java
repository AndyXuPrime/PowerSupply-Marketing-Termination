package com.andyyu.powersupply.termination.controller;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.domain.WorkflowStatus;
import com.andyyu.powersupply.termination.service.TerminationApplicationService;
import com.andyyu.powersupply.termination.service.WorkflowException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TerminationApiController.class)
@Import(GlobalExceptionHandler.class)
class TerminationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TerminationApplicationService service;

    @Test
    void shouldReturnJsonDetail() throws Exception {
        TerminationApplication application = sampleApplication();
        when(service.get(1L)).thenReturn(application);

        mockMvc.perform(get("/api/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("POWER_OFF"));
    }

    @Test
    void shouldMapWorkflowExceptionToBadRequest() throws Exception {
        when(service.get(99L)).thenThrow(new WorkflowException("申请不存在"));

        mockMvc.perform(get("/api/applications/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("申请不存在"));
    }

    @Test
    void shouldCallDetermineFeeEndpoint() throws Exception {
        TerminationApplication application = sampleApplication();
        application.setStatus(WorkflowStatus.FEE_DETERMINED);
        when(service.determineFee(1L, new BigDecimal("14"))).thenReturn(application);

        mockMvc.perform(post("/api/applications/1/determine-fee")
                        .param("actualDays", "14")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FEE_DETERMINED"));
    }

    private TerminationApplication sampleApplication() {
        TerminationApplication application = new TerminationApplication();
        application.setId(1L);
        application.setApplicationNo("BM01_009-20260513120000");
        application.setCustomerNo("C20260513120000");
        application.setCustomerName("测试客户");
        application.setContractNo("CN-TEST-001");
        application.setContractCapacityKw(new BigDecimal("120"));
        application.setPrepaidFee(new BigDecimal("3600"));
        application.setAgreedDays(30);
        application.setRequestedTerminationDate(LocalDate.of(2026, 5, 13));
        application.setCreatedAt(LocalDateTime.of(2026, 5, 13, 12, 0));
        application.setUpdatedAt(LocalDateTime.of(2026, 5, 13, 12, 0));
        application.setStatus(WorkflowStatus.POWER_OFF);
        return application;
    }
}
