package com.example.demo.repository;
import com.example.demo.entity.HistorialEstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoSolicitudRepository extends JpaRepository<HistorialEstadoSolicitud, Long> {}
