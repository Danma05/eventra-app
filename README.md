# Eventra - Plataforma de Gestión de Eventos de Running

## Descripción General

Eventra es una aplicación móvil Android desarrollada para la gestión y participación en eventos deportivos de running. La plataforma permite a organizadores crear y administrar eventos, mientras que los corredores pueden registrarse, participar y consultar sus resultados.

El proyecto fue desarrollado utilizando una arquitectura basada en microservicios, permitiendo una mayor escalabilidad, mantenibilidad y separación de responsabilidades.

---

## Objetivo General

Desarrollar una aplicación móvil que facilite la organización, administración y participación en eventos deportivos de running mediante una arquitectura moderna basada en microservicios y una interfaz móvil intuitiva.

---

## Objetivos Específicos

* Gestionar usuarios mediante autenticación segura.
* Permitir el registro e inicio de sesión de corredores y organizadores.
* Crear y administrar eventos deportivos.
* Gestionar inscripciones de participantes.
* Registrar actividades durante una carrera.
* Generar resultados automáticos.
* Publicar rankings de eventos finalizados.
* Visualizar estadísticas e información relevante de los eventos.

---

## Tecnologías Utilizadas

### Frontend

* Android Studio
* Java
* XML
* Material Design

### Backend

* Node.js
* Express.js

### Base de Datos

* PostgreSQL

### Infraestructura

* Render
* GitHub
* Jira
* Confluence

---

## Arquitectura del Proyecto

El sistema está compuesto por los siguientes microservicios:

### Auth Service

Responsable de:

* Registro de usuarios
* Inicio de sesión
* Gestión de JWT
* Seguridad de autenticación

### User Service

Responsable de:

* Perfil de usuario
* Datos personales
* Información del corredor

### Events Service

Responsable de:

* Creación de eventos
* Edición de eventos
* Gestión de estados de carrera

### Registrations Service

Responsable de:

* Inscripción de corredores
* Gestión de participantes

### Activity Service

Responsable de:

* Seguimiento de actividades
* Registro de posiciones
* Control de carrera

### Results Service

Responsable de:

* Generación de resultados
* Rankings
* Publicación de clasificaciones

---

## Funcionalidades Implementadas

### Corredor

* Registro de usuario
* Inicio de sesión
* Visualización de eventos
* Inscripción a eventos
* Seguimiento de carrera
* Consulta de resultados
* Edición de perfil

### Organizador

* Creación de eventos
* Edición de eventos
* Eliminación lógica de eventos
* Visualización de participantes
* Control de carrera
* Finalización de eventos
* Publicación de rankings

---

## Identidad Visual

### Logo Oficial

Eventra utiliza una identidad visual basada en:

* Color principal: Morado
* Color secundario: Azul Cyan
* Concepto: Velocidad, movimiento, energía y running

El logo representa una "E" estilizada con efecto de velocidad acompañada por la silueta de un corredor dentro de un círculo dinámico.

---

## Estado Actual del Proyecto

### Implementado

* Arquitectura de microservicios
* Aplicación Android funcional
* Gestión de usuarios
* Gestión de eventos
* Inscripciones
* Actividades
* Resultados
* Ranking de eventos
* Splash Screen
* Identidad visual corporativa

### Pendiente para futuras versiones

* Notificaciones Push avanzadas
* Estadísticas avanzadas
* Reportes administrativos
* Integración con dispositivos wearables
* Modo offline

---

## Instalación Local

### Backend

1. Clonar el repositorio.
2. Configurar variables de entorno.
3. Instalar dependencias.

```bash
npm install
```

4. Ejecutar microservicios.

```bash
npm start
```

### Frontend Android

1. Abrir Android Studio.
2. Sincronizar Gradle.
3. Ejecutar la aplicación en emulador o dispositivo físico.

---

## Documentación

La documentación completa del proyecto se encuentra en la carpeta:

```text
/Documentación
```

Incluye:

* Documento General
* Documento de Visión
* Manual Técnico
* Diagramas
* EDT/WBS
* Arquitectura de Microservicios

---

## Autor

Daniel Alejandro Martínez Escandón

Tecnología en Desarrollo de Software

Proyecto académico - Eventra 2026
