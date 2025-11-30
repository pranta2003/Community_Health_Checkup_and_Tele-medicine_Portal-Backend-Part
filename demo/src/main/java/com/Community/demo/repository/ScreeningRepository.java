// src/main/java/com/Community/demo/repository/ScreeningRepository.java
package com.Community.demo.repository;

import com.Community.demo.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningRepository extends JpaRepository<Screening, Long> { }
