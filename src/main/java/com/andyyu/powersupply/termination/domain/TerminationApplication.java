package com.andyyu.powersupply.termination.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "termination_applications")
public class TerminationApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String applicationNo;

    @Column(length = 32)
    private String customerNo;

    @NotBlank
    @Column(nullable = false, length = 128)
    private String customerName;

    @Column(length = 128)
    private String contactName;

    @Column(length = 32)
    private String contactPhone;

    @Column(length = 200)
    private String customerAddress;

    @Column(length = 100)
    private String district;

    @Column(length = 128)
    private String powerSupplyUnit;

    @Column(length = 128)
    private String projectName;

    @Column(length = 128)
    private String projectType;

    @Column(length = 64)
    private String useNature;

    @Column(length = 64)
    private String contractNo;

    @Column(length = 128)
    private String identityNo;

    @Column(length = 128)
    private String applicationSource;

    @Column(length = 64)
    private String acceptanceRegion;

    @Column(length = 64)
    private String assignedSurveyor;

    @Column(length = 2000)
    private String remark;

    @Column(length = 2000)
    private String surveyResult;

    @Column(length = 2000)
    private String feeRemark;

    @Column(length = 2000)
    private String archiveLocation;

    @Column(length = 2000)
    private String callbackResult;

    @Column(length = 2000)
    private String documentStatus;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal contractCapacityKw;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prepaidFee;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer agreedDays;

    @Column(precision = 12, scale = 2)
    private BigDecimal actualDays;

    @Column(precision = 12, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal receivableAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal settledAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal arrearsAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal terminalFee = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    private LocalDate requestedTerminationDate;

    private LocalDate acceptedDate;
    private LocalDate dispatchDate;
    private LocalDate surveyDate;
    private LocalDate contractTerminatedDate;
    private LocalDate powerOffDate;
    private LocalDate feeDeterminedDate;
    private LocalDate feeSettledDate;
    private LocalDate infoArchivedDate;
    private LocalDate callbackDate;
    private LocalDate archivedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WorkflowStatus status = WorkflowStatus.ACCEPTED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public BigDecimal getDailyFee() {
        if (agreedDays == null || agreedDays <= 0 || prepaidFee == null) {
            return BigDecimal.ZERO;
        }
        return prepaidFee.divide(BigDecimal.valueOf(agreedDays), 2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPowerSupplyUnit() {
        return powerSupplyUnit;
    }

    public void setPowerSupplyUnit(String powerSupplyUnit) {
        this.powerSupplyUnit = powerSupplyUnit;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getUseNature() {
        return useNature;
    }

    public void setUseNature(String useNature) {
        this.useNature = useNature;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getApplicationSource() {
        return applicationSource;
    }

    public void setApplicationSource(String applicationSource) {
        this.applicationSource = applicationSource;
    }

    public String getAcceptanceRegion() {
        return acceptanceRegion;
    }

    public void setAcceptanceRegion(String acceptanceRegion) {
        this.acceptanceRegion = acceptanceRegion;
    }

    public String getAssignedSurveyor() {
        return assignedSurveyor;
    }

    public void setAssignedSurveyor(String assignedSurveyor) {
        this.assignedSurveyor = assignedSurveyor;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSurveyResult() {
        return surveyResult;
    }

    public void setSurveyResult(String surveyResult) {
        this.surveyResult = surveyResult;
    }

    public String getFeeRemark() {
        return feeRemark;
    }

    public void setFeeRemark(String feeRemark) {
        this.feeRemark = feeRemark;
    }

    public String getArchiveLocation() {
        return archiveLocation;
    }

    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }

    public String getCallbackResult() {
        return callbackResult;
    }

    public void setCallbackResult(String callbackResult) {
        this.callbackResult = callbackResult;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public BigDecimal getContractCapacityKw() {
        return contractCapacityKw;
    }

    public void setContractCapacityKw(BigDecimal contractCapacityKw) {
        this.contractCapacityKw = contractCapacityKw;
    }

    public BigDecimal getPrepaidFee() {
        return prepaidFee;
    }

    public void setPrepaidFee(BigDecimal prepaidFee) {
        this.prepaidFee = prepaidFee;
    }

    public Integer getAgreedDays() {
        return agreedDays;
    }

    public void setAgreedDays(Integer agreedDays) {
        this.agreedDays = agreedDays;
    }

    public BigDecimal getActualDays() {
        return actualDays;
    }

    public void setActualDays(BigDecimal actualDays) {
        this.actualDays = actualDays;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getReceivableAmount() {
        return receivableAmount;
    }

    public void setReceivableAmount(BigDecimal receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    public BigDecimal getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(BigDecimal settledAmount) {
        this.settledAmount = settledAmount;
    }

    public BigDecimal getArrearsAmount() {
        return arrearsAmount;
    }

    public void setArrearsAmount(BigDecimal arrearsAmount) {
        this.arrearsAmount = arrearsAmount;
    }

    public BigDecimal getTerminalFee() {
        return terminalFee;
    }

    public void setTerminalFee(BigDecimal terminalFee) {
        this.terminalFee = terminalFee;
    }

    public LocalDate getRequestedTerminationDate() {
        return requestedTerminationDate;
    }

    public void setRequestedTerminationDate(LocalDate requestedTerminationDate) {
        this.requestedTerminationDate = requestedTerminationDate;
    }

    public LocalDate getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(LocalDate acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public LocalDate getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(LocalDate dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public LocalDate getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(LocalDate surveyDate) {
        this.surveyDate = surveyDate;
    }

    public LocalDate getContractTerminatedDate() {
        return contractTerminatedDate;
    }

    public void setContractTerminatedDate(LocalDate contractTerminatedDate) {
        this.contractTerminatedDate = contractTerminatedDate;
    }

    public LocalDate getPowerOffDate() {
        return powerOffDate;
    }

    public void setPowerOffDate(LocalDate powerOffDate) {
        this.powerOffDate = powerOffDate;
    }

    public LocalDate getFeeDeterminedDate() {
        return feeDeterminedDate;
    }

    public void setFeeDeterminedDate(LocalDate feeDeterminedDate) {
        this.feeDeterminedDate = feeDeterminedDate;
    }

    public LocalDate getFeeSettledDate() {
        return feeSettledDate;
    }

    public void setFeeSettledDate(LocalDate feeSettledDate) {
        this.feeSettledDate = feeSettledDate;
    }

    public LocalDate getInfoArchivedDate() {
        return infoArchivedDate;
    }

    public void setInfoArchivedDate(LocalDate infoArchivedDate) {
        this.infoArchivedDate = infoArchivedDate;
    }

    public LocalDate getCallbackDate() {
        return callbackDate;
    }

    public void setCallbackDate(LocalDate callbackDate) {
        this.callbackDate = callbackDate;
    }

    public LocalDate getArchivedDate() {
        return archivedDate;
    }

    public void setArchivedDate(LocalDate archivedDate) {
        this.archivedDate = archivedDate;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
