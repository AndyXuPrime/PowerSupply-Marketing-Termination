package com.andyyu.powersupply.termination.domain;

public enum WorkflowStatus {
    ACCEPTED("业务受理"),
    DISPATCHED("勘查派工"),
    FIELD_INSPECTED("现场勘查"),
    CONTRACT_TERMINATED("终止合同"),
    POWER_OFF("停电"),
    FEE_DETERMINED("确定费用"),
    FEE_SETTLED("结清费用"),
    INFO_ARCHIVED("信息归档"),
    CALLBACK_DONE("客户回访"),
    ARCHIVED("归档");

    private final String label;

    WorkflowStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
