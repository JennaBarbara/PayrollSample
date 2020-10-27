package com.wave.payroll.service;

import com.wave.payroll.model.LoggedTimeReport;
import com.wave.payroll.model.Payroll;

import com.wave.payroll.repository.PayrollRepository;
import java.util.List;

import com.wave.payroll.repository.TimeReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayrollService {

    @Autowired
    PayrollRepository payrollRepository;

    @Autowired
    TimeReportRepository timeReportRepository;


    public List<Payroll> findAllPayroll() {
        List<Payroll> payroll = (List<Payroll>) payrollRepository.findAll();
        return payroll;
    }

    public Payroll savePayroll(Payroll payroll) {
       return payrollRepository.save(payroll);
    }

    public Payroll findExistingPayroll(Payroll payroll) {
        return payrollRepository.findExistingPayroll(payroll.getEmployeeid(), payroll.getStartdate());
    }

    public LoggedTimeReport saveTimeReport(LoggedTimeReport loggedTimeReport){
        return timeReportRepository.save(loggedTimeReport);
    }

    public Boolean findExistingTimeReport(long id){
        return timeReportRepository.existsById(id);
    }
}
