# FleteCoApp

Plataforma web que conecta despachadores de carga con conductores independientes en Colombia.

---

## 🚀 Cómo correr el backend localmente

> **Nota importante sobre la base de datos:**  
> El proyecto ahora utiliza **PostgreSQL compartido en la nube mediante Supabase**, en lugar de una base de datos MySQL local. Esto asegura que todo el equipo trabaje con la misma base de datos, esquemas y tablas sincronizadas en tiempo real sin necesidad de instalar motores de base de datos locales.

### Pasos para iniciar el backend:

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/JuanPabloCardona2311/FleteCoApp.git
   cd FleteCoApp
   ```

2. **Crear el archivo de entorno `.env`:**
   Copia la plantilla de ejemplo `backend/.env.example` a un nuevo archivo llamado `backend/.env` (o en la raíz del proyecto `.env`):
   ```bash
   cp backend/.env.example backend/.env
   ```
   *(En Windows PowerShell puedes copiar y pegar el archivo directamente o usar `Copy-Item backend/.env.example backend/.env`)*.

3. **Configurar las credenciales de Supabase:**
   Abre el archivo `backend/.env` recién creado y solicita a tu equipo la contraseña real de la base de datos para asignar el valor de `SUPABASE_DB_PASSWORD`:
   ```env
   SUPABASE_DB_URL=jdbc:postgresql://aws-0-us-east-2.pooler.supabase.com:5432/postgres?sslmode=require
   SUPABASE_DB_USERNAME=postgres.mjowsnxbjeqnpyormvko
   SUPABASE_DB_PASSWORD=AQUI_VA_LA_CONTRASEÑA_DEL_EQUIPO
   ```

4. **Correr el proyecto desde VS Code:**
   * Abre la carpeta del proyecto en **VS Code**.
   * Ve a la pestaña **Ejecutar y depurar** (Run and Debug, `Ctrl + Shift + D`) o al panel del **Spring Boot Dashboard**.
   * Inicia la aplicación seleccionando el perfil `Spring Boot-DemoApplication<demo>`.
   * El archivo `.vscode/launch.json` está configurado para cargar automáticamente las variables de entorno de tu archivo `.env`.
   * Una vez iniciado, el backend estará disponible y escuchando peticiones en:
     ```text
     http://localhost:8080
     ```
