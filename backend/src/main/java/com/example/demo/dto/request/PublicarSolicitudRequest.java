package com.example.demo.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PublicarSolicitudRequest {
    @NotBlank(message = "El origen es obligatorio")
    private String origen;
    @NotBlank(message = "El destino es obligatorio")
    private String destino;
    @NotNull(message = "La latitud de origen es obligatoria")
    private BigDecimal origenLat;
    @NotNull(message = "La longitud de origen es obligatoria")
    private BigDecimal origenLng;
    @NotNull(message = "La latitud de destino es obligatoria")
    private BigDecimal destinoLat;
    @NotNull(message = "La longitud de destino es obligatoria")
    private BigDecimal destinoLng;
    @NotBlank(message = "El tipo de carga es obligatorio")
    private String tipoCarga;
    @NotBlank(message = "El tipo de vehículo requerido es obligatorio")
    private String tipoVehiculoRequerido;
    @NotNull(message = "El peso es obligatorio")
    private BigDecimal peso;
    @NotNull(message = "El precio ofrecido es obligatorio")
    private BigDecimal precioOfrecido;
    @NotNull(message = "La fecha de recogida es obligatoria")
    private LocalDateTime fechaRecogida;
    @NotNull(message = "La fecha estimada de entrega es obligatoria")
    private LocalDateTime fechaEntregaEstimada;
    @NotNull(message = "Debe indicar si requiere cita en puerto")
    private Boolean requiereCitaPuerto;
    private String numeroCita;
}
