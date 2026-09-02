package com.example.demo.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SolicitudAceptadaResponse {
    private Long pagoId;
    private Long solicitudId;
    private Long conductorId;
    private String telefonoContacto;
    private String estado;
    private LocalDateTime fechaAceptacion;
    private String mensaje;
}
