package com.andyyu.powersupply.termination.service;

import com.andyyu.powersupply.termination.domain.TerminationApplication;
import com.andyyu.powersupply.termination.domain.WorkflowStatus;
import com.andyyu.powersupply.termination.repository.TerminationApplicationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TerminationApplicationService {

    private final TerminationApplicationRepository repository;

    public TerminationApplicationService(TerminationApplicationRepository repository) {
        this.repository = repository;
    }

    public List<TerminationApplication> list(String keyword, WorkflowStatus status) {
        if (keyword != null && !keyword.isBlank()) {
            return repository.findByCustomerNameContainingIgnoreCaseOrApplicationNoContainingIgnoreCaseOrderByCreatedAtDesc(keyword, keyword);
        }
        if (status != null) {
            return repository.findByStatusOrderByCreatedAtDesc(status);
        }
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public TerminationApplication get(Long id) {
        return repository.findById(id).orElseThrow(() -> new WorkflowException("申请不存在"));
    }

    public TerminationApplication create(TerminationApplication application) {
        application.setApplicationNo(generateApplicationNo());
        application.setCustomerNo(generateCustomerNo());
        application.setStatus(WorkflowStatus.ACCEPTED);
        application.setAcceptedDate(LocalDate.now());
        application.setDocumentStatus("用电申请书已录入，待上传现场勘查单与终止合同");
        application.setRemark(normalizeRemark(application.getRemark()));
        return repository.save(application);
    }

    public TerminationApplication dispatch(Long id, String assignedSurveyor) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.ACCEPTED);
        app.setAssignedSurveyor(blankToDefault(assignedSurveyor, "待分配"));
        app.setDispatchDate(LocalDate.now());
        app.setStatus(WorkflowStatus.DISPATCHED);
        app.setRemark(appendRemark(app.getRemark(), "已派工至勘查环节"));
        return repository.save(app);
    }

    public TerminationApplication survey(Long id, String surveyResult, String customerAddress) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.DISPATCHED);
        app.setSurveyDate(LocalDate.now());
        app.setSurveyResult(surveyResult);
        if (customerAddress != null && !customerAddress.isBlank()) {
            app.setCustomerAddress(customerAddress);
        }
        app.setStatus(WorkflowStatus.FIELD_INSPECTED);
        app.setDocumentStatus("已完成现场勘查，等待合同终止");
        return repository.save(app);
    }

    public TerminationApplication terminateContract(Long id, String contractNo) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.FIELD_INSPECTED);
        if (contractNo != null && !contractNo.isBlank()) {
            app.setContractNo(contractNo);
        }
        app.setContractTerminatedDate(LocalDate.now());
        app.setStatus(WorkflowStatus.CONTRACT_TERMINATED);
        app.setDocumentStatus("临时供用电合同已终止");
        return repository.save(app);
    }

    public TerminationApplication powerOff(Long id) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.CONTRACT_TERMINATED);
        app.setPowerOffDate(LocalDate.now());
        app.setStatus(WorkflowStatus.POWER_OFF);
        app.setRemark(appendRemark(app.getRemark(), "已组织停电"));
        return repository.save(app);
    }

    public TerminationApplication determineFee(Long id, BigDecimal actualDays) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.POWER_OFF);
        if (actualDays == null || actualDays.signum() <= 0) {
            throw new WorkflowException("请填写实际使用天数");
        }
        app.setActualDays(actualDays.setScale(2, RoundingMode.HALF_UP));
        FeeCalculationResult result = calculateFee(app.getPrepaidFee(), app.getAgreedDays(), app.getActualDays());
        app.setRefundAmount(result.refundAmount());
        app.setReceivableAmount(result.receivableAmount());
        app.setTerminalFee(result.terminalFee());
        app.setFeeDeterminedDate(LocalDate.now());
        app.setStatus(WorkflowStatus.FEE_DETERMINED);
        app.setFeeRemark(buildFeeRemark(app));
        return repository.save(app);
    }

    public TerminationApplication settleFee(Long id, String voucherNo, BigDecimal settledAmount, BigDecimal arrearsAmount) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.FEE_DETERMINED);
        app.setSettledAmount(settledAmount == null ? BigDecimal.ZERO : settledAmount.setScale(2, RoundingMode.HALF_UP));
        app.setArrearsAmount(arrearsAmount == null ? BigDecimal.ZERO : arrearsAmount.setScale(2, RoundingMode.HALF_UP));
        app.setFeeSettledDate(LocalDate.now());
        app.setStatus(WorkflowStatus.FEE_SETTLED);
        app.setFeeRemark(appendRemark(app.getFeeRemark(), "收费凭证：" + blankToDefault(voucherNo, "未填写")));
        return repository.save(app);
    }

    public TerminationApplication archiveInfo(Long id, String archiveLocation) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.FEE_SETTLED);
        app.setArchiveLocation(blankToDefault(archiveLocation, "档案室-待登记"));
        app.setInfoArchivedDate(LocalDate.now());
        app.setStatus(WorkflowStatus.INFO_ARCHIVED);
        app.setDocumentStatus("申请书、勘查单、终止合同已归档到信息层");
        return repository.save(app);
    }

    public TerminationApplication callback(Long id, String callbackResult) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.INFO_ARCHIVED);
        app.setCallbackResult(blankToDefault(callbackResult, "已回访"));
        app.setCallbackDate(LocalDate.now());
        app.setStatus(WorkflowStatus.CALLBACK_DONE);
        return repository.save(app);
    }

    public TerminationApplication archive(Long id) {
        TerminationApplication app = requireStatus(id, WorkflowStatus.CALLBACK_DONE);
        app.setArchivedDate(LocalDate.now());
        app.setStatus(WorkflowStatus.ARCHIVED);
        app.setDocumentStatus("客户档案注销完成");
        return repository.save(app);
    }

    public List<TerminationApplication> all() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private TerminationApplication requireStatus(Long id, WorkflowStatus expected) {
        TerminationApplication app = get(id);
        if (app.getStatus() != expected) {
            throw new WorkflowException("当前状态为 " + app.getStatus().getLabel() + "，不能执行该操作");
        }
        return app;
    }

    private String generateApplicationNo() {
        return "BM01_009-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String generateCustomerNo() {
        return "C" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String normalizeRemark(String remark) {
        return remark == null ? "" : remark.trim();
    }

    private String appendRemark(String origin, String addition) {
        String base = origin == null || origin.isBlank() ? "" : origin.trim() + "；";
        return base + addition;
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private String buildFeeRemark(TerminationApplication app) {
        return "约定天数" + app.getAgreedDays()
                + "天，实际使用" + app.getActualDays()
                + "天，预收电费" + formatMoney(app.getPrepaidFee())
                + "，退费" + formatMoney(app.getRefundAmount())
                + "，补收" + formatMoney(app.getReceivableAmount());
    }

    private String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public FeeCalculationResult calculateFee(BigDecimal prepaidFee, Integer agreedDays, BigDecimal actualDays) {
        BigDecimal zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal pre = prepaidFee == null ? zero : prepaidFee.setScale(2, RoundingMode.HALF_UP);
        int expectedDays = agreedDays == null ? 0 : agreedDays;
        BigDecimal actual = actualDays == null ? zero : actualDays.setScale(2, RoundingMode.HALF_UP);

        if (expectedDays <= 0) {
            return new FeeCalculationResult(zero, zero, zero);
        }

        BigDecimal halfPoint = BigDecimal.valueOf(expectedDays).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        if (actual.compareTo(halfPoint) < 0) {
            BigDecimal refund = pre.multiply(BigDecimal.valueOf(0.5)).setScale(2, RoundingMode.HALF_UP);
            return new FeeCalculationResult(refund, zero, zero);
        }

        if (actual.compareTo(BigDecimal.valueOf(expectedDays)) > 0) {
            BigDecimal extraDays = actual.subtract(BigDecimal.valueOf(expectedDays));
            BigDecimal dailyFee = pre.divide(BigDecimal.valueOf(expectedDays), 2, RoundingMode.HALF_UP);
            BigDecimal receivable = dailyFee.multiply(extraDays).setScale(2, RoundingMode.HALF_UP);
            return new FeeCalculationResult(zero, receivable, receivable);
        }

        return new FeeCalculationResult(zero, zero, zero);
    }
}
