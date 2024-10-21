package com.sampleapp.samplesbapp.repository;

import com.sampleapp.samplesbapp.entity.TaskForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleProcessRepository extends JpaRepository<TaskForm, Long> {

}
