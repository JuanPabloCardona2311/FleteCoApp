# Especificación del proyecto: FleteCo (plataforma de fletes tipo InDrive/Uber)

## 1. Descripción general del proyecto

Desarrollar una **plataforma web** (prototipo académico, proyecto universitario a un semestre) que conecta a **despachadores** (empresas o personas que necesitan transportar carga) con **conductores independientes** (muleros y camioneros) en Colombia, de forma similar a como InDrive/Uber conectan pasajeros con conductores.

### Problema que resuelve
- Actualmente la conexión entre despachadores y conductores independientes es mayormente "voz a voz" y depende de contactos personales, lo que genera desigualdad de oportunidades.
- Los conductores muchas veces deben regresar de vacío (sin carga) a su ciudad de origen después de completar un viaje, perdiendo dinero en el trayecto de regreso.
- A diferencia de InDrive/Uber, el despachador (quien paga) no viaja físicamente con el conductor, lo que genera un problema de confianza adicional en el pago.

### Solución propuesta
- Los despachadores publican solicitudes de flete (origen, destino, tipo de carga, precio ofrecido).
- El precio es **fijo** (definido por el despachador al publicar, tipo Uber): el conductor solo puede aceptar o no la solicitud, no hay negociación ni contraofertas.
- Los conductores ven las solicitudes disponibles y aceptan la que más les convenga (incluyendo, idealmente, viajes de regreso hacia su origen), con sugerencias automáticas de viajes que coincidan con su destino y fechas actuales.
- Se muestra la ruta en un mapa y se hace seguimiento simplificado de la ubicación del conductor durante el viaje.
- El pago se gestiona dentro de la plataforma con un modelo de **comisión por viaje** y un flujo tipo **escrow** (el dinero queda retenido hasta que se confirma la entrega), simulado con una pasarela de pagos en modo de pruebas (sandbox).

### Restricciones del proyecto
- **Debe desarrollarse como aplicación web** (no app móvil nativa), ya que el equipo aún no tiene experiencia en desarrollo móvil. Se busca que el diseño sea responsive para que funcione bien también desde el celular.
- **Cero costos**: no se puede usar ningún servicio que requiera pago o tarjeta de crédito (ni siquiera niveles "gratuitos" que pidan registrar tarjeta, como Google Maps API).
- Tiempo de desarrollo: un semestre académico completo.

---

## 2. Stack tecnológico definido

| Capa | Tecnología | Notas |
|---|---|---|
| Frontend | React | El equipo ya tiene experiencia previa |
| Backend | Spring Boot (Java) | El equipo ya tiene experiencia previa |
| Base de datos | MySQL | Relacional — elegida por familiaridad del equipo (evaluamos PostgreSQL pero se descartó por la misma razón de experiencia previa) |
| ORM | Spring Data JPA / Hibernate | |
| Mapas | Leaflet.js + OpenStreetMap | Gratis, sin necesidad de API key ni tarjeta de crédito |
| Rutas | OSRM (Open Source Routing Machine) | Cálculo de rutas gratuito, alternativa a Google Directions API |
| Comunicación en tiempo real | WebSocket (spring-boot-starter-websocket) | Nativo de Spring Boot, sin costo ni API externa |
| Geolocalización del conductor | Geolocation API del navegador (`navigator.geolocation`) | Nativa de JavaScript, sin costo |
| Pagos | Pasarela tipo Wompi/PayU/ePayco en modo sandbox (pruebas) | Sin costo ni dinero real; simula el flujo de retención y liberación de pago |

### Estrategia de tracking (ubicación del conductor)
Se optó por un **tracking simplificado** (no en tiempo real completo tipo Uber), por ser más viable en un semestre:
1. El navegador del conductor obtiene su ubicación (`navigator.geolocation`) cada 15-20 segundos.
2. La envía al backend vía un endpoint REST (o WebSocket).
3. El backend sobrescribe la última ubicación conocida del conductor (no se guarda historial completo de posiciones, para no saturar la base de datos).
4. El despachador consulta esa ubicación (polling o vía WebSocket) y la plataforma mueve un marcador en el mapa de Leaflet.

### Estrategia de pagos (escrow simulado)
Dado que el despachador y el conductor no están físicamente juntos (a diferencia de InDrive), el pago se maneja dentro de la plataforma para generar confianza:
1. El despachador "paga" a través de la pasarela (en modo sandbox, sin dinero real) al momento en que se acepta la solicitud.
2. El dinero queda en estado `retenido`.
3. Cuando el despachador confirma la entrega de la carga, el estado pasa a `liberado` (simulando la transferencia al conductor, descontando la comisión de la plataforma).
4. Para producción real, se necesitaría un convenio formal con una pasarela habilitada para manejar pagos de terceros (marketplace/escrow); eso queda fuera del alcance del semestre, pero el flujo completo se demuestra con el sandbox.

### Estrategia de sugerencia de viajes (evitar el regreso vacío)
No requiere Machine Learning: se resuelve con un algoritmo de coincidencia basado en reglas.
1. Cuando el conductor tiene un viaje `en_curso`, el sistema conoce su `destino` y `fecha_entrega_estimada`.
2. El sistema busca automáticamente solicitudes nuevas cuyo `origen` esté cerca de ese destino, y cuya `fecha_recogida` sea posterior (con margen) a la `fecha_entrega_estimada` del viaje actual.
3. Las coincidencias se le muestran al conductor como sugeridas, y se registran en la tabla `notificaciones`.

---

## 3. Tipos de usuario

1. **Despachador**: publica solicitudes de flete.
2. **Conductor** (mulero/camionero): ve solicitudes disponibles y las acepta.
3. **Administrador**: verifica documentos de conductores y vehículos, modera usuarios y resuelve disputas (rol de gestión de la plataforma).

Los tres tipos se diferencian mediante un campo `tipo_usuario` en la tabla `usuarios` (no hay tablas separadas de permisos para el administrador, por simplicidad del alcance académico).

---

## 4. Modelo de datos (entidades / tablas)

### 4.1 `usuarios`
Tabla base para cualquier persona registrada en el sistema.

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK, autoincremental) | |
| nombre | VARCHAR(150) | |
| email | VARCHAR(150), único | |
| password_hash | VARCHAR(255) | Contraseña encriptada (BCrypt) |
| telefono | VARCHAR(20) | |
| tipo_documento_identidad | ENUM('CC','CE','pasaporte') | |
| numero_documento_identidad | VARCHAR(20), único | Dato de identidad real de la persona; el `id` sigue siendo la llave interna usada en las relaciones (no se expone ni se usa como PK, por seguridad y rendimiento) |
| tipo_usuario | ENUM('despachador','conductor','administrador') | |
| fecha_registro | DATETIME | |
| estado | ENUM('activo','inactivo') | |

### 4.2 `conductores`
Extiende a un usuario cuando su tipo es "conductor" (relación 1 a 1). Contiene solo información de la persona; los datos del vehículo van en `vehiculos`.

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| usuario_id | BIGINT (FK → usuarios.id) | |
| calificacion_promedio | DECIMAL(3,2) | Calculado a partir de `calificaciones` |
| cancelaciones_totales | INT (default 0) | Contador de solicitudes aceptadas y luego canceladas por el conductor |
| ubicacion_lat | DECIMAL(10,7) | Última ubicación conocida (se sobrescribe) |
| ubicacion_lng | DECIMAL(10,7) | Última ubicación conocida (se sobrescribe) |

### 4.3 `vehiculos`
Datos del vehículo del conductor (separado porque los documentos legales del vehículo van ligados a él, no a la persona).

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| conductor_id | BIGINT (FK → conductores.id) | Un conductor puede tener uno o más vehículos registrados a lo largo del tiempo |
| tipo_vehiculo | VARCHAR(50) | Ej: mula, camión, camioneta |
| placa | VARCHAR(10) | |
| capacidad_carga | DECIMAL(10,2) | En toneladas |
| estado_verificacion | ENUM('pendiente','verificado','rechazado') | Verificación general del vehículo |
| activo | BOOLEAN | Si es el vehículo que el conductor está usando actualmente |

### 4.4 `despachadores`
Extiende a un usuario cuando su tipo es "despachador" (relación 1 a 1).

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| usuario_id | BIGINT (FK → usuarios.id) | |
| nombre_empresa | VARCHAR(150) | Opcional, puede ser persona natural |
| nit | VARCHAR(20) | Opcional |

### 4.5 `solicitudes`
El corazón del sistema: cada flete publicado.

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| despachador_id | BIGINT (FK → despachadores.id) | |
| conductor_id | BIGINT (FK → conductores.id), nullable | Se llena cuando se acepta |
| origen | VARCHAR(200) | Nombre/dirección textual |
| destino | VARCHAR(200) | Nombre/dirección textual |
| origen_lat | DECIMAL(10,7) | Necesario para mostrar el mapa/ruta |
| origen_lng | DECIMAL(10,7) | |
| destino_lat | DECIMAL(10,7) | |
| destino_lng | DECIMAL(10,7) | |
| tipo_carga | VARCHAR(100) | |
| tipo_vehiculo_requerido | VARCHAR(50) | Debe coincidir con `tipo_vehiculo` y `capacidad_carga` del vehículo del conductor; se usa para filtrar qué solicitudes puede ver/aceptar cada conductor |
| peso | DECIMAL(10,2) | En toneladas o kg |
| precio_ofrecido | DECIMAL(12,2) | En pesos colombianos |
| fecha_publicacion | DATETIME | |
| fecha_recogida | DATETIME | Fecha en que el despachador necesita que se recoja la carga en el origen |
| fecha_entrega_estimada | DATETIME | Fecha esperada de llegada de la carga al destino |
| requiere_cita_puerto | BOOLEAN | Indica si el destino exige agendar cita con la terminal portuaria (ej. Buenaventura) |
| numero_cita | VARCHAR(50), nullable | Número de cita si el despachador ya la gestionó con el puerto; la gestión de la cita en sí queda fuera del alcance del sistema, la maneja directamente la terminal portuaria |
| estado | ENUM('publicada','aceptada','en_curso','completada','cancelada','expirada') | |

### 4.6 `calificaciones`
Calificación bidireccional (despachador ↔ conductor) por cada solicitud completada.

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| solicitud_id | BIGINT (FK → solicitudes.id) | |
| calificador_id | BIGINT (FK → usuarios.id) | Quien califica |
| calificado_id | BIGINT (FK → usuarios.id) | Quien es calificado |
| puntuacion | INT | 1 a 5 |
| comentario | VARCHAR(500) | Opcional |
| fecha | DATETIME | |

### 4.7 `documentos_conductor`
Documentos de la persona (no del vehículo).

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| conductor_id | BIGINT (FK → conductores.id) | |
| tipo_documento | ENUM('licencia_conduccion') | Debe ser de la categoría correspondiente al tipo de vehículo (ej. C2, C3) |
| archivo_url | VARCHAR(255) | Ruta o URL del archivo subido |
| fecha_vencimiento | DATE | |
| estado_verificacion | ENUM('pendiente','verificado','rechazado') | |

### 4.8 `documentos_vehiculo`
Documentos legales que van ligados al vehículo (placa), no a la persona.

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| vehiculo_id | BIGINT (FK → vehiculos.id) | |
| tipo_documento | ENUM('SOAT','tecnomecanica','tarjeta_propiedad','tarjeta_operacion') | Documentos reales exigidos en Colombia para transporte de carga |
| archivo_url | VARCHAR(255) | Ruta o URL del archivo subido |
| fecha_vencimiento | DATE | |
| estado_verificacion | ENUM('pendiente','verificado','rechazado') | |

**Nota sobre los tipos de documento:**
- **SOAT**: Seguro Obligatorio de Accidentes de Tránsito.
- **tecnomecanica**: Revisión Técnico-Mecánica y de Emisiones Contaminantes.
- **licencia_conduccion**: documento de la persona, no del vehículo.
- **tarjeta_propiedad**: acredita quién es el propietario legal del vehículo.
- **tarjeta_operacion**: expedida por el Ministerio de Transporte, autoriza al vehículo a prestar servicio público de transporte de carga.

### 4.9 `historial_estados_solicitud`
Guarda la trazabilidad de los cambios de estado de una solicitud. Aporta trazabilidad y datos para analítica/sustentación.

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| solicitud_id | BIGINT (FK → solicitudes.id) | |
| estado_anterior | VARCHAR(50) | |
| estado_nuevo | VARCHAR(50) | |
| fecha_cambio | DATETIME | |

### 4.10 `pagos`
Registra el ciclo de vida del pago de cada solicitud (comisión + flujo de retención/liberación tipo escrow, simulado con pasarela en sandbox).

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| solicitud_id | BIGINT (FK → solicitudes.id) | |
| monto | DECIMAL(12,2) | Precio del flete |
| comision_plataforma | DECIMAL(12,2) | Calculada automáticamente (ej. % del monto) |
| monto_neto_conductor | DECIMAL(12,2) | monto - comisión |
| estado | ENUM('pendiente','retenido','liberado','reembolsado') | |
| referencia_pasarela | VARCHAR(100) | ID de la transacción en la pasarela (sandbox) |
| foto_evidencia_entrega | VARCHAR(255), nullable | Foto que sube el conductor al marcar la solicitud como entregada |
| fecha_limite_confirmacion | DATETIME | Si el despachador no confirma ni abre una disputa antes de esta fecha, el pago se libera automáticamente |
| fecha_pago | DATETIME | |
| fecha_liberacion | DATETIME | |

### 4.11 `notificaciones`
Guarda las sugerencias de viajes mostradas a cada conductor (y notificaciones generales del sistema).

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| conductor_id | BIGINT (FK → conductores.id) | |
| solicitud_id | BIGINT (FK → solicitudes.id) | La solicitud sugerida |
| tipo | ENUM('sugerencia_viaje','sistema') | |
| leida | BOOLEAN | |
| fecha | DATETIME | |

### 4.12 `disputas`
Registra reclamos entre despachador y conductor (ej. desacuerdo sobre si la carga fue entregada), para que el administrador los resuelva.

| Campo | Tipo de dato | Notas |
|---|---|---|
| id | BIGINT (PK) | |
| solicitud_id | BIGINT (FK → solicitudes.id) | |
| usuario_reporta_id | BIGINT (FK → usuarios.id) | Quien abre la disputa |
| motivo | VARCHAR(500) | |
| estado | ENUM('abierta','en_revision','resuelta') | |
| resolucion | VARCHAR(500), nullable | Decisión del administrador |
| fecha_apertura | DATETIME | |
| fecha_resolucion | DATETIME, nullable | |

---

## 5. Relaciones entre tablas

- `usuarios` 1---1 `conductores` (opcional, solo si tipo_usuario = conductor)
- `usuarios` 1---1 `despachadores` (opcional, solo si tipo_usuario = despachador)
- `conductores` 1---N `vehiculos` (puede tener uno o varios a lo largo del tiempo)
- `despachadores` 1---N `solicitudes` (publica)
- `conductores` 1---N `solicitudes` (acepta)
- `solicitudes` 1---N `calificaciones` (genera, dos calificaciones por viaje completado)
- `conductores` 1---N `documentos_conductor` (tiene)
- `vehiculos` 1---N `documentos_vehiculo` (tiene)
- `solicitudes` 1---N `historial_estados_solicitud` (registra cambios)
- `solicitudes` 1---1 `pagos` (genera un registro de pago)
- `conductores` 1---N `notificaciones` (recibe)
- `solicitudes` 1---N `disputas` (puede tener reclamos)

---

## 6. Flujo funcional principal (ciclo de vida de una solicitud)

1. Un **despachador** se registra/inicia sesión y publica una solicitud de flete (origen, destino, tipo de carga, precio, fecha de recogida, fecha estimada de entrega, y si el destino requiere cita en puerto).
2. La solicitud queda en estado `publicada` y visible para los conductores.
3. Un **conductor** ve el listado de solicitudes disponibles, filtrado automáticamente para mostrar solo aquellas que su vehículo activo puede transportar (`capacidad_carga` del vehículo ≥ `peso` de la solicitud, y `tipo_vehiculo_requerido` compatible). El sistema le sugiere automáticamente (vía `notificaciones`) las solicitudes cuyo origen coincide con el destino de su viaje actual y cuyas fechas encajan, para evitar el regreso vacío.
4. El conductor acepta una solicitud → estado cambia a `aceptada`, se asigna `conductor_id`, se crea el registro en `pagos` con estado `retenido` (el despachador "paga" vía la pasarela en sandbox), y se revela el teléfono de contacto entre despachador y conductor para coordinar detalles (dirección exacta, instrucciones).
5. Durante el viaje, el estado pasa a `en_curso`; el conductor comparte periódicamente su ubicación; el despachador puede ver el avance en un mapa (Leaflet).
6. Al finalizar, el conductor marca la solicitud como entregada y sube una foto de evidencia. El despachador confirma la entrega → el estado de la solicitud pasa a `completada`, el pago pasa a `liberado`, y ambas partes (despachador y conductor) se califican mutuamente. Si el despachador no confirma ni abre una disputa antes de `fecha_limite_confirmacion`, el pago se libera automáticamente.
7. Si hay desacuerdo (ej. el despachador dice que no recibió la carga), cualquiera de las partes puede abrir una **disputa**, que un administrador revisa y resuelve.
8. Si nadie acepta a tiempo o el despachador cancela, el estado pasa a `expirada` o `cancelada` (y el pago, si ya existía, a `reembolsado`).

---

## 8. Estructura de carpetas sugerida — Backend (Spring Boot)

Paquete base sugerido: `com.fleteco.api` (ajustar según el nombre de grupo que usen en Spring Initializr).

```
src/main/java/com/fleteco/api/
├── config/                  # SecurityConfig, WebSocketConfig, CorsConfig
├── controller/               # UsuarioController, SolicitudController, PagoController, etc.
├── dto/
│   ├── request/               # Objetos que recibe la API (ej. SolicitudRequest)
│   └── response/              # Objetos que devuelve la API (ej. SolicitudResponse)
├── entity/                   # Entidades JPA: Usuario, Conductor, Vehiculo, Despachador,
│                              # Solicitud, Calificacion, DocumentoConductor, DocumentoVehiculo,
│                              # HistorialEstadoSolicitud, Pago, Notificacion, Disputa
├── repository/                # Interfaces JpaRepository, una por entidad
├── service/
│   ├── (interfaces)            # UsuarioService, SolicitudService, PagoService, etc.
│   └── impl/                   # Implementaciones de cada interfaz
├── security/                  # Filtro JWT, UserDetailsService, utilidades de token
├── exception/                  # Excepciones personalizadas + GlobalExceptionHandler
└── util/                      # Clases de apoyo (ej. cálculo de distancia entre coordenadas)

src/main/resources/
├── application.properties     # Configuración de MySQL, puerto, JWT, etc.
└── application-dev.properties # (opcional) configuración específica de desarrollo
```

## 9. Estructura de carpetas sugerida — Frontend (React)

```
src/
├── components/                # Componentes reutilizables (Navbar, Card, MapaLeaflet, etc.)
├── pages/                     # Pantallas: Login, Registro, PublicarSolicitud, ListadoSolicitudes,
│                              # DetalleSolicitud, PerfilConductor, PerfilDespachador, PanelAdmin
├── services/                  # Llamadas a la API (axios/fetch) — un archivo por entidad
├── context/                   # Contexto de autenticación (usuario logueado, token JWT)
├── hooks/                     # Hooks personalizados (ej. useGeolocation, useWebSocket)
├── utils/                     # Funciones de apoyo (formateo de fechas, validaciones)
└── App.jsx
```

---

## 10. Lo que se le pide a la IA que reciba este documento

Con base en esta especificación, generar:
1. La estructura de carpetas del backend en Spring Boot (entidades JPA, repositorios, servicios, controladores REST) según las tablas descritas y la estructura de la sección 8.
2. La estructura de carpetas del frontend en React (componentes principales por pantalla: login/registro, publicar solicitud, listado de solicitudes disponibles con sugerencias, detalle de solicitud con mapa, flujo de pago, perfil, calificaciones) según la sección 9.
3. Los endpoints REST necesarios para cubrir el flujo funcional descrito en la sección 6.
4. Sugerencias de dónde integrar Leaflet.js (mapa), WebSocket (actualización de ubicación) y la pasarela de pagos en sandbox en el frontend.

---

## 11. Nota sobre datos personales sensibles

La plataforma almacena documentos de identidad, licencias y otros datos personales sensibles (cédula, SOAT, licencia de conducción). En Colombia esto está regulado por la Ley 1581 de 2012 (Habeas Data). Para el alcance del proyecto, se recomienda como mínimo: restringir el acceso a estos documentos solo al rol de administrador, y mencionar en la sustentación que la plataforma tendría una política de tratamiento de datos personales.
