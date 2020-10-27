package com.wave.payroll.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class TimeReport {
    @CsvBindByName
    private String date;

    @CsvBindByName(column = "hours worked")
    private float hours;

    @CsvBindByName(column = "employee id")
    private int employeeId;

    @CsvBindByName(column = "job group")
    private String job;
}
