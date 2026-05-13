package com.andyyu.powersupply.termination.service;

import java.math.BigDecimal;

public record FeeCalculationResult(BigDecimal refundAmount, BigDecimal receivableAmount, BigDecimal terminalFee) {
}
