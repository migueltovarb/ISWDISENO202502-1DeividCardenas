# 📚 Documentación Arquitectónica - Sistema de Gestión de Proyectos y Tareas

**Versión:** 2.0
**Fecha:** 2025-11-06
**Estado:** Completo y Actualizado

---

## 📋 Índice de Documentación

Esta carpeta contiene toda la documentación arquitectónica y técnica del Sistema de Gestión de Proyectos y Tareas. Los documentos están organizados y numerados para facilitar la navegación.

---

## 🗂️ Documentos Disponibles

### 📖 Documento Principal

| # | Documento | Descripción | Tipo |
|---|-----------|-------------|------|
| 00 | **[Wiki del Proyecto](00-WIKI-PROYECTO.md)** | Guía completa del proyecto con toda la información necesaria para desarrolladores | 📘 Wiki |

### 🏗️ Vistas Arquitectónicas

| # | Documento | Descripción | Tipo |
|---|-----------|-------------|------|
| 01 | **[Vista de Contexto](01-VISTA-CONTEXTO.md)** | Límites del sistema, actores externos, sistemas externos, flujos de información | 🌐 Arquitectura |
| 02 | **[Vista Funcional](02-VISTA-FUNCIONAL.md)** | Componentes del sistema, responsabilidades, interfaces, flujos de datos | 🔧 Arquitectura |
| 03 | **[Vista Conceptual](03-VISTA-CONCEPTUAL.md)** | Modelo de dominio, entidades, relaciones, reglas de negocio | 💡 Arquitectura |

### 📝 Requisitos y Planificación

| # | Documento | Descripción | Tipo |
|---|-----------|-------------|------|
| 04 | **[Historias de Usuario](04-HISTORIAS-USUARIO.md)** | Requisitos funcionales en formato de historias de usuario con criterios de aceptación | 📋 Requisitos |
| 06 | **[User Story Mapping](06-USER-STORY-MAPPING.md)** | Mapa visual de historias, priorización, roadmap de releases | 🗺️ Planificación |

### ⚡ Calidad y No Funcionales

| # | Documento | Descripción | Tipo |
|---|-----------|-------------|------|
| 05 | **[Escenarios de Calidad](05-ESCENARIOS-CALIDAD.md)** | Atributos de calidad, escenarios, tácticas arquitectónicas, métricas | 🎯 Calidad |

### 🖼️ Diagramas Visuales

| Archivo | Descripción | Tipo |
|---------|-------------|------|
| **Diagrama de Contexto.jpg** | Diagrama visual del contexto del sistema | 📊 Imagen |
| **Diagrama de Componentes.jpg** | Diagrama de componentes y arquitectura | 📊 Imagen |
| **Modelo Conceptual.jpg** | Diagrama del modelo conceptual de dominio | 📊 Imagen |

### 📊 Otros Documentos

| Archivo | Descripción | Tipo |
|---------|-------------|------|
| **User history maping.xlsx** | Mapa de historias de usuario en formato Excel | 📈 Excel |

---

## 🎯 Guía de Lectura Recomendada

### Para Nuevos Desarrolladores

Si eres nuevo en el proyecto, te recomendamos leer en este orden:

1. **[Wiki del Proyecto](00-WIKI-PROYECTO.md)** - Comienza aquí para obtener una visión general completa
2. **[Vista de Contexto](01-VISTA-CONTEXTO.md)** - Entiende los límites y el entorno del sistema
3. **[Vista Conceptual](03-VISTA-CONCEPTUAL.md)** - Familiarízate con el modelo de dominio
4. **[Historias de Usuario](04-HISTORIAS-USUARIO.md)** - Comprende los requisitos funcionales
5. **[Vista Funcional](02-VISTA-FUNCIONAL.md)** - Profundiza en los componentes técnicos

### Para Arquitectos

1. **[Vista de Contexto](01-VISTA-CONTEXTO.md)** - Contexto y límites del sistema
2. **[Vista Funcional](02-VISTA-FUNCIONAL.md)** - Arquitectura de componentes
3. **[Vista Conceptual](03-VISTA-CONCEPTUAL.md)** - Modelo de dominio y reglas de negocio
4. **[Escenarios de Calidad](05-ESCENARIOS-CALIDAD.md)** - Requisitos no funcionales y tácticas

### Para Product Owners / Scrum Masters

1. **[Historias de Usuario](04-HISTORIAS-USUARIO.md)** - Requisitos funcionales detallados
2. **[User Story Mapping](06-USER-STORY-MAPPING.md)** - Roadmap y priorización
3. **[Wiki del Proyecto](00-WIKI-PROYECTO.md)** - Información general y estado del proyecto

### Para QA / Testers

1. **[Historias de Usuario](04-HISTORIAS-USUARIO.md)** - Criterios de aceptación y escenarios de prueba
2. **[Escenarios de Calidad](05-ESCENARIOS-CALIDAD.md)** - Requisitos de calidad y métricas
3. **[Wiki del Proyecto](00-WIKI-PROYECTO.md)** - Sección de Testing (#7)

---

## 📚 Contenido de Cada Documento

### 00 - Wiki del Proyecto
**Propósito:** Guía completa del proyecto
**Contenido:**
- Información general del proyecto
- Inicio rápido y setup
- Arquitectura del sistema
- Guías de desarrollo
- Deployment y operaciones
- Testing y troubleshooting
- FAQ

**Audiencia:** Todos (desarrolladores, QA, PM)
**Tamaño:** ~12,000 palabras

---

### 01 - Vista de Contexto
**Propósito:** Definir límites del sistema y su entorno
**Contenido:**
- Diagrama de contexto
- Actores del sistema (Admin, Líder, Colaborador)
- Sistemas externos (MongoDB Atlas, Railway)
- Flujos de información principales
- Dependencias externas
- Atributos de calidad relevantes al contexto

**Audiencia:** Arquitectos, desarrolladores, stakeholders
**Tamaño:** ~6,000 palabras

---

### 02 - Vista Funcional
**Propósito:** Describir componentes y responsabilidades
**Contenido:**
- Arquitectura en capas (MVC + Services)
- Diagrama de componentes
- Descripción detallada de cada componente
- Interfaces entre componentes
- Flujos de datos y control
- Patrones arquitectónicos aplicados

**Audiencia:** Arquitectos, desarrolladores senior
**Tamaño:** ~14,000 palabras

---

### 03 - Vista Conceptual
**Propósito:** Definir modelo de dominio
**Contenido:**
- Diagrama del modelo conceptual
- Entidades principales (User, Project, Task)
- Enumeraciones (Role, TaskStatus, TaskPriority, ProjectStatus)
- Relaciones entre entidades
- Reglas de negocio
- Restricciones de integridad
- Agregados (DDD)

**Audiencia:** Arquitectos, desarrolladores, analistas
**Tamaño:** ~10,000 palabras

---

### 04 - Historias de Usuario
**Propósito:** Documentar requisitos funcionales
**Contenido:**
- 12 historias de usuario detalladas
- Épicas del sistema (4)
- Criterios de aceptación específicos
- Escenarios de prueba en formato Gherkin
- Reglas de negocio asociadas
- Trazabilidad con requisitos
- Backlog de historias futuras

**Audiencia:** Product Owners, desarrolladores, QA
**Tamaño:** ~8,000 palabras

---

### 05 - Escenarios de Calidad
**Propósito:** Definir atributos de calidad y tácticas
**Contenido:**
- Árbol de utilidad de atributos de calidad
- 12 escenarios de calidad detallados
  - Seguridad (3)
  - Rendimiento (2)
  - Disponibilidad (2)
  - Usabilidad (2)
  - Mantenibilidad (2)
  - Fiabilidad (2)
- Tácticas arquitectónicas aplicadas
- Métricas de calidad
- Trade-offs arquitectónicos
- Riesgos y mitigaciones

**Audiencia:** Arquitectos, líderes técnicos, QA
**Tamaño:** ~11,000 palabras

---

### 06 - User Story Mapping
**Propósito:** Visualizar y priorizar historias
**Contenido:**
- Mapa visual de user stories
- 5 actividades principales del sistema
- Priorización MoSCoW (Must/Should/Could/Won't)
- Roadmap de releases (1.0, 1.1, 2.0, 3.0)
- Flujos de usuario por rol
- Backlog completo (37 historias)
- Métricas del backlog

**Audiencia:** Product Owners, Scrum Masters, equipo completo
**Tamaño:** ~5,000 palabras

---

## 🔍 Búsqueda Rápida por Tema

### Autenticación y Seguridad
- [Vista de Contexto](01-VISTA-CONTEXTO.md) - Sección 6.1: Flujo de Autenticación
- [Vista Funcional](02-VISTA-FUNCIONAL.md) - Sección 3.3: Capa de Seguridad
- [Escenarios de Calidad](05-ESCENARIOS-CALIDAD.md) - SC-001, SC-002, SC-003
- [Historias de Usuario](04-HISTORIAS-USUARIO.md) - HU-001, HU-002, HU-003

### Gestión de Proyectos
- [Vista Conceptual](03-VISTA-CONCEPTUAL.md) - Sección 3.2: Entidad Project
- [Vista Funcional](02-VISTA-FUNCIONAL.md) - AdminProjectController, LiderController
- [Historias de Usuario](04-HISTORIAS-USUARIO.md) - Épica 2: Gestión de Proyectos
- [User Story Mapping](06-USER-STORY-MAPPING.md) - Actividad 3: Gestionar Proyectos

### Gestión de Tareas
- [Vista Conceptual](03-VISTA-CONCEPTUAL.md) - Sección 3.3: Entidad Task
- [Vista Funcional](02-VISTA-FUNCIONAL.md) - TaskController, TaskService
- [Historias de Usuario](04-HISTORIAS-USUARIO.md) - Épica 3: Gestión de Tareas
- [User Story Mapping](06-USER-STORY-MAPPING.md) - Actividad 4: Gestionar Tareas

### Rendimiento y Caché
- [Vista Funcional](02-VISTA-FUNCIONAL.md) - Sección 6.1: Caché
- [Escenarios de Calidad](05-ESCENARIOS-CALIDAD.md) - PE-001: Tiempo de Respuesta
- [Wiki](00-WIKI-PROYECTO.md) - Sección 1.2: Alto Rendimiento

### Deployment
- [Vista de Contexto](01-VISTA-CONTEXTO.md) - Sección 11: Diagrama de Deployment
- [Wiki](00-WIKI-PROYECTO.md) - Sección 5: Deployment y Operaciones

---

## 📊 Estadísticas de la Documentación

### Documentos Creados
- **Total de documentos:** 7
- **Documentos Markdown:** 7
- **Diagramas visuales:** 3
- **Archivos Excel:** 1

### Cobertura de Contenido
- **Vistas arquitectónicas:** 3/3 (100%)
- **Requisitos funcionales:** 12 historias completadas + 22 planificadas
- **Escenarios de calidad:** 12 escenarios (6 atributos)
- **Guías técnicas:** Completas

### Tamaño Total
- **Palabras totales:** ~66,000+
- **Líneas de código de ejemplo:** ~500+
- **Diagramas ASCII:** 15+
- **Tablas:** 80+

---

## 🔄 Versionamiento de Documentos

Todos los documentos en esta carpeta siguen versionamiento semántico:

- **Versión 1.0:** Documentación inicial (Noviembre 2024)
- **Versión 2.0:** Actualización completa con nuevas vistas arquitectónicas (Noviembre 2025)

**Próxima revisión planificada:** Con cada release mayor del sistema

---

## 🤝 Mantenimiento de la Documentación

### Responsabilidades

| Documento | Responsable | Frecuencia de Actualización |
|-----------|-------------|----------------------------|
| Wiki del Proyecto | Tech Lead | Cada sprint |
| Vista de Contexto | Arquitecto | Cuando cambien dependencias externas |
| Vista Funcional | Arquitecto | Cuando cambien componentes principales |
| Vista Conceptual | Arquitecto / Analista | Cuando cambien entidades o reglas de negocio |
| Historias de Usuario | Product Owner | Continua (backlog vivo) |
| Escenarios de Calidad | Arquitecto | Cuando cambien requisitos de calidad |
| User Story Mapping | Product Owner | Cada sprint / planning |

### Proceso de Actualización

1. **Cambios menores:** Actualizar documento directamente
2. **Cambios mayores:**
   - Crear PR con cambios
   - Solicitar revisión del equipo
   - Actualizar número de versión
   - Documentar cambios en el historial

### Historial de Cambios

**2025-11-06 - Versión 2.0**
- ✅ Creación completa de documentación arquitectónica
- ✅ 7 documentos nuevos creados
- ✅ Actualización de diagramas
- ✅ Sincronización con código actual

**2024-11-04 - Versión 1.0**
- ✅ Documentación inicial del proyecto
- ✅ README básico
- ✅ Guías de setup

---

## 📞 Contacto

Para preguntas sobre la documentación:

- **GitHub Issues:** https://github.com/DeividCardenas/Dise-o_Software/issues
- **Etiqueta:** `documentation`

---

## 📖 Licencia

Esta documentación es parte del proyecto Sistema de Gestión de Proyectos y Tareas y sigue la misma licencia que el proyecto principal.

---

**Última Actualización:** 2025-11-06
**Mantenido por:** Equipo de Arquitectura y Desarrollo
**Estado:** ✅ Completo y Actualizado
