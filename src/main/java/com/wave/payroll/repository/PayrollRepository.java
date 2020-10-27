package com.wave.payroll.repository;

import com.wave.payroll.model.Payroll;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PayrollRepository extends CrudRepository<Payroll, Long>{

    @Query(value = "SELECT pm FROM Payroll pm " +
            "WHERE pm.employeeid = ?1 " +
            "AND pm.startdate = ?2")
    public Payroll findExistingPayroll(int employeeId, Date startdate);
}
