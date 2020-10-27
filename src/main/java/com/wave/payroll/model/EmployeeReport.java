package com.wave.payroll.model;

import lombok.Data;

@Data
public class EmployeeReport {
    private int employeeId;
    private PayPeriod payPeriod;
    private String amountPaid;
}
