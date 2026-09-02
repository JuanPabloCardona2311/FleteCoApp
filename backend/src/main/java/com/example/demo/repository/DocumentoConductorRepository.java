package com.example.demo.repository;
import com.example.demo.entity.DocumentoConductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoConductorRepository extends JpaRepository<DocumentoConductor, Long> {}
