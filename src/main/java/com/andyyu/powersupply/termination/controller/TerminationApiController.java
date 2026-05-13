package com.andyyu.powersupply.termination.controller;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.domain.WorkflowStatus;
import com.andyyu.powersupply.termination.service.TerminationApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class TerminationApiController {

    private final TerminationApplicationService service;

    public TerminationApiController(TerminationApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<TerminationApplication> list(@RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) WorkflowStatus status) {
        return service.list(keyword, status);
    }

    @GetMapping("/{id}")
    public TerminationApplication detail(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public TerminationApplication create(@RequestBody TerminationApplication application) {
        return service.create(application);
    }

    @PostMapping("/{id}/dispatch")
    public TerminationApplication dispatch(@PathVariable Long id, @RequestParam(required = false) String assignedSurveyor) {
        return service.dispatch(id, assignedSurveyor);
    }

    @PostMapping("/{id}/survey")
    public TerminationApplication survey(@PathVariable Long id,
                                          @RequestParam(required = false) String surveyResult,
                                          @RequestParam(required = false) String customerAddress) {
        return service.survey(id, surveyResult, customerAddress);
    }

    @PostMapping("/{id}/contract-terminate")
    public TerminationApplication contractTerminate(@PathVariable Long id, @RequestParam(required = false) String contractNo) {
        return service.terminateContract(id, contractNo);
    }

    @PostMapping("/{id}/power-off")
    public TerminationApplication powerOff(@PathVariable Long id) {
        return service.powerOff(id);
    }

    @PostMapping("/{id}/determine-fee")
    public TerminationApplication determineFee(@PathVariable Long id, @RequestParam BigDecimal actualDays) {
        return service.determineFee(id, actualDays);
    }

    @PostMapping("/{id}/settle-fee")
    public TerminationApplication settleFee(@PathVariable Long id,
                                            @RequestParam(required = false) String voucherNo,
                                            @RequestParam(required = false) BigDecimal settledAmount,
                                            @RequestParam(required = false) BigDecimal arrearsAmount) {
        return service.settleFee(id, voucherNo, settledAmount, arrearsAmount);
    }

    @PostMapping("/{id}/archive-info")
    public TerminationApplication archiveInfo(@PathVariable Long id, @RequestParam(required = false) String archiveLocation) {
        return service.archiveInfo(id, archiveLocation);
    }

    @PostMapping("/{id}/callback")
    public TerminationApplication callback(@PathVariable Long id, @RequestParam(required = false) String callbackResult) {
        return service.callback(id, callbackResult);
    }

    @PostMapping("/{id}/archive")
    public TerminationApplication archive(@PathVariable Long id) {
        return service.archive(id);
    }
}
