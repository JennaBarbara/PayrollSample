package com.wave.payroll.model;

import java.util.Date;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "payrollreport")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int employeeid;
    private Date startdate;
    private Date enddate;
    private Double amount;
}
