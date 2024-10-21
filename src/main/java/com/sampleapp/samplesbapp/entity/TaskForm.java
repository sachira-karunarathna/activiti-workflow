package com.sampleapp.samplesbapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TaskForm {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
