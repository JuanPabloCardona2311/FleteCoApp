package com.example.demo.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AceptarSolicitudRequest {
    @NotNull(message = "El id de la solicitud es obligatorio")
    private Long solicitudId;
}
