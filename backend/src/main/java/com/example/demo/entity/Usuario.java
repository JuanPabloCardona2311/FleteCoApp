package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento_identidad", nullable = false, length = 10)
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;

    @Column(name = "numero_documento_identidad", nullable = false, unique = true, length = 20)
    private String numeroDocumentoIdentidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuario tipoUsuario;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoUsuario estado;

    @OneToOne(mappedBy = "usuario", optional = true)
    @ToString.Exclude
    private Conductor conductor;

    @OneToOne(mappedBy = "usuario", optional = true)
    @ToString.Exclude
    private Despachador despachador;

    @OneToMany(mappedBy = "calificador")
    @Builder.Default
    @ToString.Exclude
    private List<Calificacion> calificacionesHechas = new ArrayList<>();

    @OneToMany(mappedBy = "calificado")
    @Builder.Default
    @ToString.Exclude
    private List<Calificacion> calificacionesRecibidas = new ArrayList<>();

    @OneToMany(mappedBy = "usuarioReporta")
    @Builder.Default
    @ToString.Exclude
    private List<Disputa> disputasReportadas = new ArrayList<>();

    public enum TipoDocumentoIdentidad {
        CC,
        CE,
        PASAPORTE
    }

    public enum TipoUsuario {
        DESPACHADOR,
        CONDUCTOR,
        ADMINISTRADOR
    }

    public enum EstadoUsuario {
        ACTIVO,
        INACTIVO
    }
}