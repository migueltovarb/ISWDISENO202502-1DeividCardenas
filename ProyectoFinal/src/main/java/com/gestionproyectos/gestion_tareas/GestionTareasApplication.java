package com.gestionproyectos.gestion_tareas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * GestionTareasApplication - Punto de entrada de la aplicación
 *
 * CONFIGURACIÓN:
 * - @SpringBootApplication: Habilita auto-configuración y escaneo de componentes
 * - @EnableCaching: Habilita soporte de caché con Spring Cache
 *
 * CACHÉ:
 * El caché está configurado con Caffeine (cache en memoria de alto rendimiento).
 * Se usa para optimizar consultas frecuentes a MongoDB y reducir latencia.
 *
 * @author Sistema de Gestión de Tareas
 * @version 2.0
 * @since 2025-11-06
 */
@SpringBootApplication
@EnableCaching
public class GestionTareasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionTareasApplication.class, args);
	}

}
