package ru.polytech.client.entity;

import androidx.annotation.Nullable;

import java.sql.Date;

public class Projects {
    private Long id;
    private String name;
    private Float cost;
    private Departments departments;
    private Date dateBeg;
    private Date dateEnd;
    private Date dateEndReal;

    public Projects(Long id, String name, Float cost, Departments departments, Date dateBeg, Date dateEnd, Date dateEndReal) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.departments = departments;
        this.dateBeg = dateBeg;
        this.dateEnd = dateEnd;
        this.dateEndReal = dateEndReal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Departments getDepartments() {
        return departments;
    }

    public void setDepartments(Departments departments) {
        this.departments = departments;
    }

    public Date getDateBeg() {
        return dateBeg;
    }

    public void setDateBeg(Date dateBeg) {
        this.dateBeg = dateBeg;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Date getDateEndReal() {
        return dateEndReal;
    }

    public void setDateEndReal(Date dateEndReal) {
        this.dateEndReal = dateEndReal;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Projects proj = (Projects) obj;
        return name.equals(proj.name) &&
                cost.equals(proj.cost) &&
                departments.equals(proj.departments) &&
                dateBeg.equals(proj.dateBeg) &&
                dateEnd.equals(proj.dateEnd) &&
                dateEndReal.equals(proj.dateEndReal);
    }
}
