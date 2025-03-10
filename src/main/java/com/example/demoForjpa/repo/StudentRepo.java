package com.example.demoForjpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demoForjpa.entity.Student;

public interface StudentRepo extends JpaRepository<Student, Long> {

}
