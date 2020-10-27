package com.wave.payroll.model;
import lombok.Data;

import java.util.List;

@Data
public class PayrollReport {
    private List<EmployeeReport> employeeReports;
}
