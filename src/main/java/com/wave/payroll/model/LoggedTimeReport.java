package com.wave.payroll.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="loggedtimereport")
public class LoggedTimeReport {
    @Id
    private long id;
}
