package com.wave.payroll;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.wave.payroll.model.*;
import com.wave.payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.*;


@RestController
public class PayrollController {
    @Autowired
    private PayrollService payrollService;


    @PostMapping(value = "/upload")
    @ResponseBody
    public ResponseEntity<String> timeReportUpload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (!fileNameValid(fileName))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or existing time report name.");
        try {
            List<TimeReport> timeReportList = new ArrayList<TimeReport>();
            timeReportList = convertCSVtoList(file);
            List<Payroll> payrollList = new ArrayList<Payroll>();
            for (TimeReport timeReport : timeReportList) {

                Payroll payroll = convertTimeReportToPayrollFormat(timeReport);
                Boolean payrollUnique = true;
                for (Payroll existingPayroll : payrollList) {
                    if (existingPayroll.getEmployeeid() == payroll.getEmployeeid() &&
                            existingPayroll.getStartdate().equals(payroll.getStartdate())) {
                        payrollUnique = false;
                        existingPayroll.setAmount(existingPayroll.getAmount() + payroll.getAmount());
                    }
                }
                if (payrollUnique) payrollList.add(payroll);
            }
            uploadPayrollToDB(payrollList);
            logTimeReport(fileName);
            return ResponseEntity.ok("Successfully uploaded the time report");
        } catch (IOException | ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File could not be interpreted. Please check CSV formatting.");
        }
    }

    @GetMapping(value = "/report")
    public PayrollReport getReport() throws ParseException {
        List<Payroll> payrollList = payrollService.findAllPayroll();
        List<EmployeeReport> employeeReportList = convertPayrollListToEmployeeReportListFormat(payrollList);
        PayrollReport payrollReport = new PayrollReport();
        payrollReport.setEmployeeReports(employeeReportList);
        return payrollReport;
    }

    private Boolean fileNameValid(String fileName) {
        if (fileName.matches("time-report-\\d+\\.csv")) {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher idMatcher = pattern.matcher(fileName);
            if (idMatcher.find()) {
                long id = Long.parseLong(idMatcher.group());
                if (!payrollService.findExistingTimeReport(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private LoggedTimeReport logTimeReport(String fileName) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher idMatcher = pattern.matcher(fileName);
        if (idMatcher.find()) {
            long id = Long.parseLong(idMatcher.group());
            LoggedTimeReport loggedTimeReport = new LoggedTimeReport();
            loggedTimeReport.setId(id);
            return payrollService.saveTimeReport(loggedTimeReport);
        }
        return null;
    }

    private List<TimeReport> convertCSVtoList(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        HeaderColumnNameMappingStrategy<TimeReport> strategy
                = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(TimeReport.class);

        CsvToBean<TimeReport> csvToBean = new CsvToBeanBuilder<TimeReport>(reader)
                .withMappingStrategy(strategy)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<TimeReport> timeReportList = csvToBean.parse();
        return timeReportList;
    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private Payroll convertTimeReportToPayrollFormat(TimeReport timeReport) throws ParseException {
        HashMap<String, Double> jobPay = new HashMap<String, Double>();
        jobPay.put("A", 20.00);
        jobPay.put("B", 30.00);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Payroll payroll = new Payroll();
        Date timeReportDate = new Date();
        timeReportDate = formatter.parse(timeReport.getDate());
        Calendar timeReportCalendar = dateToCalendar(timeReportDate);
        timeReportCalendar.set(Calendar.DAY_OF_MONTH, 16);
        Date cutOffDate = timeReportCalendar.getTime();
        if (timeReportDate.before(cutOffDate)) {
            timeReportCalendar.set(Calendar.DAY_OF_MONTH, 1);
            payroll.setStartdate(timeReportCalendar.getTime());
            timeReportCalendar.set(Calendar.DAY_OF_MONTH, 15);
            payroll.setEnddate(timeReportCalendar.getTime());
        } else {
            payroll.setStartdate(cutOffDate);
            timeReportCalendar.set(Calendar.DAY_OF_MONTH, timeReportCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            payroll.setEnddate(timeReportCalendar.getTime());
        }
        payroll.setEmployeeid(timeReport.getEmployeeId());
        payroll.setAmount(jobPay.get(timeReport.getJob()) * timeReport.getHours());
        return payroll;
    }

    private List<EmployeeReport> convertPayrollListToEmployeeReportListFormat(List<Payroll> payrollList) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<EmployeeReport> employeeReportList = new ArrayList<>();
        for (Payroll payroll : payrollList) {
            EmployeeReport employeeReport = new EmployeeReport();
            PayPeriod payPeriod = new PayPeriod();
            payPeriod.setStartDate(formatter.format(payroll.getStartdate()));
            payPeriod.setEndDate(formatter.format(payroll.getEnddate()));
            employeeReport.setPayPeriod(payPeriod);
            employeeReport.setEmployeeId(payroll.getEmployeeid());
            employeeReport.setAmountPaid(String.format("$%.2f", payroll.getAmount()));
            employeeReportList.add(employeeReport);
        }
        return employeeReportList;
    }

    private void uploadPayrollToDB(List<Payroll> payrollList) {
        for (Payroll payroll : payrollList) {
            Payroll existingPayroll = payrollService.findExistingPayroll(payroll);
            if (existingPayroll != null) {
                existingPayroll.setAmount(existingPayroll.getAmount() + payroll.getAmount());
                payrollService.savePayroll(existingPayroll);
            } else {
                payrollService.savePayroll(payroll);
            }
        }
    }
}
