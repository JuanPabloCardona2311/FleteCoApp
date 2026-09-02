package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "vehiculos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conductor_id", nullable = false)
    @ToString.Exclude
    private Conductor conductor;

    @OneToMany(mappedBy = "vehiculo")
    @Builder.Default
    @ToString.Exclude
    private List<DocumentoVehiculo> documentosVehiculo = new ArrayList<>();

    @Column(name = "tipo_vehiculo", nullable = false, length = 50)
    private String tipoVehiculo;

    @Column(nullable = false, length = 10)
    private String placa;

    @Column(name = "capacidad_carga", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacidadCarga;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_verificacion", nullable = false, length = 10)
    private EstadoVerificacion estadoVerificacion = EstadoVerificacion.PENDIENTE;

    @Column(nullable = false)
    private Boolean activo;

    public enum EstadoVerificacion {
        PENDIENTE,
        VERIFICADO,
        RECHAZADO
    }
}