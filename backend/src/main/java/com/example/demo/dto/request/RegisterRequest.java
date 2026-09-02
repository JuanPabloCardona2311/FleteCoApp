package com.example.demo.dto.request;
import com.example.demo.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    private String telefono;
    @NotNull(message = "Debe indicar el tipo de documento")
    private Usuario.TipoDocumentoIdentidad tipoDocumentoIdentidad;
    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumentoIdentidad;
    @NotNull(message = "Debe indicar el tipo de usuario")
    private Usuario.TipoUsuario tipoUsuario;
}
