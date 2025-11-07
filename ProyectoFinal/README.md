# 🚀 Sistema de Gestión de Proyectos y Tareas

Sistema web completo para la gestión de proyectos y tareas con Spring Boot, MongoDB Atlas y autenticación basada en roles.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Inicio Rápido](#inicio-rápido)
- [Credenciales de Acceso](#credenciales-de-acceso)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Documentación](#documentación)
- [Deploy a Railway](#deploy-a-railway)
- [Testing](#testing)

---

## ✨ Características

### 🔐 Autenticación y Autorización
- ✅ Login seguro con Spring Security
- ✅ Registro público de usuarios (rol COLABORADOR)
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Usuario administrador creado automáticamente al iniciar

### 👥 Gestión de Usuarios (3 Roles)
- **👑 Administrador:** Control total del sistema
  - Crear usuarios con cualquier rol
  - Crear proyectos
  - Asignar líderes a proyectos
  - Ver estadísticas del sistema

- **👨‍💼 Líder:** Gestión de proyectos
  - Ver proyectos asignados
  - Crear tareas en sus proyectos
  - Asignar tareas a colaboradores
  - Monitorear progreso

- **👨‍💻 Colaborador:** Ejecución de tareas
  - Ver tareas asignadas
  - Actualizar estado de tareas
  - Marcar tareas como completadas

### 📁 Gestión de Proyectos y Tareas
- ✅ Crear y gestionar proyectos
- ✅ Asignar líderes a proyectos
- ✅ Crear tareas con prioridades (BAJA, MEDIA, ALTA, CRÍTICA)
- ✅ Asignar tareas a colaboradores
- ✅ Seguimiento de estado de tareas
- ✅ Dashboard personalizado según rol

### 🎨 Interfaz de Usuario
- ✅ Diseño moderno con Tailwind CSS
- ✅ Responsivo (móvil, tablet, escritorio)
- ✅ Mensajes de feedback claros
- ✅ Validaciones en tiempo real
- ✅ Navegación intuitiva

---

## 🛠️ Tecnologías

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.5.7** - Framework principal
- **Spring Security 6** - Autenticación y autorización
- **Spring Data MongoDB** - Persistencia de datos
- **Lombok** - Reducción de boilerplate
- **Jakarta Validation** - Validación de datos

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Tailwind CSS** - Framework de estilos
- **HTML5 + JavaScript** - UI interactiva

### Base de Datos
- **MongoDB Atlas** - Base de datos en la nube
- **Colecciones:** users, projects, tasks

### Herramientas
- **Maven** - Gestión de dependencias
- **Git** - Control de versiones
- **Railway** - Platform as a Service (PaaS) para deployment

---

## 🚀 Inicio Rápido

### Prerrequisitos

- Java 21 o superior
- MongoDB Atlas (cuenta gratuita)
- Maven (incluido como `mvnw`)
- Git

### Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/DeividCardenas/Dise-o_Software.git
   cd Dise-o_Software
   ```

2. **Configurar MongoDB Atlas**

   El proyecto ya está configurado para usar MongoDB Atlas. Verifica que la conexión en `application.properties` sea correcta:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://usuario:password@cluster.mongodb.net/gestion_tareas
   ```

3. **Compilar el proyecto**
   ```bash
   ./mvnw clean install
   ```

4. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Acceder a la aplicación**
   ```
   http://localhost:8080
   ```

---

## 🔑 Credenciales de Acceso

### Usuario Administrador (Creado Automáticamente)

Al ejecutar la aplicación por primera vez, se crea automáticamente un usuario administrador:

- **Email:** `admin@gestion.com`
- **Contraseña:** `admin123`
- **Rol:** ADMINISTRADOR

⚠️ **IMPORTANTE:** Cambia esta contraseña después del primer login en producción.

### Crear Nuevos Usuarios

**Opción 1: Registro Público** (Solo COLABORADORES)
- Ve a: http://localhost:8080/register
- Completa el formulario
- Los usuarios se registran como COLABORADORES

**Opción 2: Crear desde Admin** (Cualquier rol)
- Login como admin
- Ve a: `/admin/crear-usuario`
- Puedes crear ADMIN, LÍDER o COLABORADOR

---

## 📂 Estructura del Proyecto

```
Dise-o_Software/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gestionproyectos/gestion_tareas/
│   │   │       ├── config/           # Configuraciones
│   │   │       │   ├── DataSeeder.java           # Datos iniciales
│   │   │       │   └── SecurityConfig.java       # Seguridad
│   │   │       ├── controller/       # Controladores MVC
│   │   │       │   ├── AuthController.java       # Login/Registro
│   │   │       │   ├── AdminController.java      # Panel admin
│   │   │       │   ├── DashboardController.java  # Dashboard
│   │   │       │   ├── LiderController.java      # Gestión proyectos
│   │   │       │   └── TaskController.java       # Gestión tareas
│   │   │       ├── dto/              # Data Transfer Objects
│   │   │       ├── enums/            # Enumeraciones (Role, TaskPriority, etc.)
│   │   │       ├── model/            # Entidades (User, Project, Task)
│   │   │       ├── repository/       # Repositorios MongoDB
│   │   │       └── service/          # Lógica de negocio
│   │   └── resources/
│   │       ├── application.properties  # Configuración
│   │       └── templates/             # Vistas Thymeleaf
│   │           ├── auth/              # Login, Registro
│   │           ├── admin/             # Vistas de admin
│   │           └── colaborador/       # Vistas de colaborador
│   └── test/                          # Tests
├── FUNCIONALIDADES.md                 # Guía completa de funcionalidades
├── MONGODB_ATLAS_CONFIG.md           # Configuración de MongoDB
├── GUIA_PRUEBAS.md                   # Guía de testing
├── pom.xml                            # Dependencias Maven
└── README.md                          # Este archivo
```

---

## 📚 Documentación

El proyecto incluye documentación exhaustiva:

### 📖 [FUNCIONALIDADES.md](FUNCIONALIDADES.md)
Guía completa de todas las funcionalidades del sistema:
- Roles y permisos detallados
- Flujos de trabajo
- Funcionalidades por rol
- Roadmap de futuras features

### 🔧 [MONGODB_ATLAS_CONFIG.md](MONGODB_ATLAS_CONFIG.md)
Configuración de MongoDB Atlas:
- Setup de cluster
- Network Access
- Variables de entorno
- Solución de problemas
- Seguridad

### 🧪 [GUIA_PRUEBAS.md](GUIA_PRUEBAS.md)
Guía completa de testing:
- Checklist de verificación
- Pruebas funcionales paso a paso
- Pruebas de seguridad
- Verificación de MongoDB
- Preparación para deployment

---

## 🚂 Deploy a Railway

### Paso 1: Preparación

1. **Asegúrate de que todo esté commiteado**
   ```bash
   git status  # Debe mostrar "nothing to commit"
   ```

2. **Push a GitHub**
   ```bash
   git push origin main
   ```

### Paso 2: Configurar Railway

1. Ve a [Railway.app](https://railway.app)
2. Conecta tu cuenta de GitHub
3. Crea un nuevo proyecto
4. Selecciona tu repositorio `Dise-o_Software`

### Paso 3: Configurar Variables de Entorno

En Railway, ve a Variables y agrega:

```
MONGODB_URI=mongodb+srv://usuario:password@cluster.mongodb.net/gestion_tareas
```

(Usa tu URI completa de MongoDB Atlas)

### Paso 4: Deploy

Railway detectará automáticamente que es Spring Boot y:
1. Ejecutará `./mvnw clean install`
2. Creará el JAR
3. Lo desplegará

### Paso 5: Verificar

1. Railway te dará una URL: `https://tu-app.railway.app`
2. Accede a la URL
3. Deberías ver la página de login
4. Prueba login con `admin@gestion.com` / `admin123`

---

## 🧪 Testing

### Ejecutar Tests

```bash
./mvnw test
```

### Pruebas Manuales

Sigue la guía completa en [GUIA_PRUEBAS.md](GUIA_PRUEBAS.md) que incluye:

✅ Pruebas de autenticación
✅ Pruebas de registro
✅ Pruebas de creación de usuarios
✅ Pruebas de roles y permisos
✅ Pruebas de validaciones
✅ Verificación de MongoDB
✅ Escenarios de integración completos

### Checklist Rápido

- [ ] Compilación exitosa: `./mvnw clean install`
- [ ] Aplicación arranca: `./mvnw spring-boot:run`
- [ ] Usuario admin se crea automáticamente
- [ ] Login funciona
- [ ] Registro público funciona
- [ ] MongoDB Atlas muestra los usuarios

---

## 🔒 Seguridad

### Características de Seguridad

- ✅ **Contraseñas encriptadas** con BCrypt
- ✅ **CSRF Protection** activado
- ✅ **Validación de entrada** en múltiples capas
- ✅ **Control de acceso basado en roles** (RBAC)
- ✅ **Sesiones seguras** con cookies HttpOnly
- ✅ **Email único** con índice en MongoDB
- ✅ **Soft delete** para usuarios

### Mejoras Recomendadas para Producción

- [ ] Cambiar credenciales de admin por defecto
- [ ] Usar variables de entorno para credenciales
- [ ] Implementar rate limiting
- [ ] Agregar 2FA (Two-Factor Authentication)
- [ ] Implementar HTTPS obligatorio
- [ ] Logs de auditoría
- [ ] Políticas de contraseñas más fuertes

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📝 Notas de Versión

### v1.0.0 - 2025-11-03

**Funcionalidades Principales:**
- ✅ Sistema de autenticación completo
- ✅ Registro público de usuarios
- ✅ Usuario administrador automático (DataSeeder)
- ✅ Gestión de usuarios por admin
- ✅ Gestión de proyectos
- ✅ Gestión de tareas
- ✅ Dashboard por rol
- ✅ Interfaz moderna con Tailwind CSS

**Tecnologías:**
- Spring Boot 3.5.7
- Spring Security 6
- MongoDB Atlas
- Java 21

**Documentación:**
- Guía de funcionalidades completa
- Configuración de MongoDB Atlas
- Guía de pruebas exhaustiva

---

## 📞 Soporte

### Problemas Comunes

**Error: "Unable to connect to MongoDB"**
- Solución: Verifica Network Access en MongoDB Atlas (permitir todas las IPs: 0.0.0.0/0)

**Error: "Email ya está registrado"**
- Causa: Email duplicado
- Solución: Usa un email diferente

**Error: 403 Forbidden**
- Causa: No tienes permisos para esa ruta
- Solución: Verifica que estés logueado con el rol correcto

### Documentación

- Ver [FUNCIONALIDADES.md](FUNCIONALIDADES.md)
- Ver [MONGODB_ATLAS_CONFIG.md](MONGODB_ATLAS_CONFIG.md)
- Ver [GUIA_PRUEBAS.md](GUIA_PRUEBAS.md)

---

## 📄 Licencia

Este proyecto es parte de un trabajo académico.

---

## 👨‍💻 Autores

- **Deivid Cardenas** - [GitHub](https://github.com/DeividCardenas)

---

## 🙏 Agradecimientos

- Spring Boot team
- MongoDB team
- Tailwind CSS team
- Railway team

---

**¿Listo para comenzar?** Sigue la [Guía de Inicio Rápido](#inicio-rápido) ⬆️

**¿Problemas?** Revisa la [Guía de Pruebas](GUIA_PRUEBAS.md) 🧪

**¿Deploy a producción?** Ve a [Deploy a Railway](#deploy-a-railway) 🚂

---

<div align="center">

**Hecho con ❤️ usando Spring Boot**

⭐ Si este proyecto te ayudó, considera darle una estrella en GitHub

</div>
