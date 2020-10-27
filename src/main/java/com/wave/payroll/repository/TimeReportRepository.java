package com.wave.payroll.repository;

import com.wave.payroll.model.LoggedTimeReport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeReportRepository  extends CrudRepository<LoggedTimeReport, Long>{

}


