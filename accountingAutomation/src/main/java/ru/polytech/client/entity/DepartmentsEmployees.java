package ru.polytech.client.entity;

import androidx.annotation.Nullable;

public class DepartmentsEmployees {
    private Long id;
    private Departments departments;
    private Employees employees;

    public DepartmentsEmployees(Long id, Departments departments, Employees employees) {
        this.id = id;
        this.departments = departments;
        this.employees = employees;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Departments getDepartments() {
        return departments;
    }

    public void setDepartments(Departments departments) {
        this.departments = departments;
    }

    public Employees getEmployees() {
        return employees;
    }

    public void setEmployees(Employees employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DepartmentsEmployees de = (DepartmentsEmployees) obj;
        return departments.equals(de.departments) &&
                employees.equals(de.employees);
    }
}
