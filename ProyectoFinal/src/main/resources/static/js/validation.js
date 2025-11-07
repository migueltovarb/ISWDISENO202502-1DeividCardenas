/**
 * validation.js - Sistema de Validación del Lado del Cliente
 *
 * PROPÓSITO:
 * Validar formularios en el navegador ANTES de enviarlos al servidor.
 * Proporciona feedback instantáneo al usuario y reduce carga del servidor.
 *
 * VENTAJAS:
 * - Experiencia de usuario mejorada (feedback inmediato)
 * - Menos peticiones HTTP fallidas
 * - Reducción de carga en el servidor
 * - Validación interactiva en tiempo real
 *
 * IMPORTANTE:
 * La validación del lado del cliente NO reemplaza la validación del servidor.
 * Es una capa adicional de UX, pero la seguridad real está en el backend.
 *
 * COMPATIBILIDAD:
 * Vanilla JavaScript (ES6+), no requiere librerías externas.
 * Compatible con Chrome, Firefox, Safari, Edge modernos.
 *
 * @author Sistema de Gestión de Tareas
 * @version 2.0
 * @since 2025-11-06
 */

// ==================== REGLAS DE VALIDACIÓN ====================

/**
 * Objeto con las reglas de validación para cada tipo de campo
 *
 * ESTRUCTURA:
 * {
 *   nombreCampo: {
 *     pattern: RegExp,      // Expresión regular de validación
 *     minLength: Number,    // Longitud mínima
 *     maxLength: Number,    // Longitud máxima
 *     message: String       // Mensaje de error
 *   }
 * }
 */
const ValidationRules = {
    email: {
        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        message: 'Por favor, ingresa un email válido (ejemplo: usuario@dominio.com)'
    },

    password: {
        minLength: 8,
        // Patrón: Al menos 8 caracteres, una mayúscula, una minúscula, un número
        pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
        message: 'La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número'
    },

    fullName: {
        minLength: 3,
        maxLength: 100,
        pattern: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/,
        message: 'El nombre debe tener al menos 3 caracteres y solo puede contener letras y espacios'
    },

    projectName: {
        minLength: 3,
        maxLength: 100,
        message: 'El nombre del proyecto debe tener entre 3 y 100 caracteres'
    },

    description: {
        minLength: 10,
        maxLength: 500,
        message: 'La descripción debe tener entre 10 y 500 caracteres'
    }
};

// ==================== FUNCIONES DE VALIDACIÓN ====================

/**
 * Valida un campo individual según sus reglas
 *
 * @param {HTMLInputElement|HTMLTextAreaElement} field - Campo a validar
 * @param {Object} rules - Reglas de validación
 * @returns {boolean} - true si es válido, false si no
 */
function validateField(field, rules) {
    const value = field.value.trim();
    const errorContainer = getOrCreateErrorContainer(field);

    // Validar si el campo es requerido y está vacío
    if (field.hasAttribute('required') && value === '') {
        showError(field, errorContainer, 'Este campo es obligatorio');
        return false;
    }

    // Si el campo está vacío y no es requerido, es válido
    if (value === '') {
        hideError(field, errorContainer);
        return true;
    }

    // Validar longitud mínima
    if (rules.minLength && value.length < rules.minLength) {
        showError(field, errorContainer, rules.message);
        return false;
    }

    // Validar longitud máxima
    if (rules.maxLength && value.length > rules.maxLength) {
        showError(field, errorContainer, rules.message);
        return false;
    }

    // Validar patrón (expresión regular)
    if (rules.pattern && !rules.pattern.test(value)) {
        showError(field, errorContainer, rules.message);
        return false;
    }

    // Todas las validaciones pasaron
    hideError(field, errorContainer);
    return true;
}

/**
 * Muestra un error en el campo
 *
 * @param {HTMLElement} field - Campo con error
 * @param {HTMLElement} errorContainer - Contenedor del mensaje de error
 * @param {string} message - Mensaje de error
 */
function showError(field, errorContainer, message) {
    // Añadir clases de error al campo
    field.classList.add('border-red-500', 'focus:ring-red-500', 'focus:border-red-500');
    field.classList.remove('border-gray-300', 'focus:ring-indigo-500', 'focus:border-indigo-500');
    field.classList.remove('dark:border-gray-600');

    // Mostrar mensaje de error
    if (errorContainer) {
        errorContainer.textContent = message;
        errorContainer.classList.remove('hidden');
        errorContainer.classList.add('text-red-600', 'dark:text-red-400', 'text-sm', 'mt-1');
    }

    // Añadir atributo aria para accesibilidad
    field.setAttribute('aria-invalid', 'true');
    field.setAttribute('aria-describedby', errorContainer.id);
}

/**
 * Oculta el error del campo
 *
 * @param {HTMLElement} field - Campo sin error
 * @param {HTMLElement} errorContainer - Contenedor del mensaje de error
 */
function hideError(field, errorContainer) {
    // Quitar clases de error
    field.classList.remove('border-red-500', 'focus:ring-red-500', 'focus:border-red-500');
    field.classList.add('border-gray-300', 'focus:ring-indigo-500', 'focus:border-indigo-500');
    field.classList.add('dark:border-gray-600');

    // Ocultar mensaje de error
    if (errorContainer) {
        errorContainer.classList.add('hidden');
    }

    // Quitar atributos aria
    field.setAttribute('aria-invalid', 'false');
    field.removeAttribute('aria-describedby');
}

/**
 * Obtiene o crea el contenedor de errores para un campo
 *
 * @param {HTMLElement} field - Campo input
 * @returns {HTMLElement} - Contenedor de errores
 */
function getOrCreateErrorContainer(field) {
    const errorId = field.id + '-error';
    let errorContainer = document.getElementById(errorId);

    if (!errorContainer) {
        errorContainer = document.createElement('div');
        errorContainer.id = errorId;
        errorContainer.className = 'error-message hidden text-red-600 dark:text-red-400 text-sm mt-1';
        errorContainer.setAttribute('role', 'alert');

        // Insertar después del campo
        field.parentNode.insertBefore(errorContainer, field.nextSibling);
    }

    return errorContainer;
}

/**
 * Valida una contraseña confirmada (password confirmation)
 *
 * @param {HTMLInputElement} passwordField - Campo de contraseña
 * @param {HTMLInputElement} confirmField - Campo de confirmación
 * @returns {boolean} - true si coinciden
 */
function validatePasswordConfirmation(passwordField, confirmField) {
    const errorContainer = getOrCreateErrorContainer(confirmField);

    if (confirmField.value !== passwordField.value) {
        showError(confirmField, errorContainer, 'Las contraseñas no coinciden');
        return false;
    }

    hideError(confirmField, errorContainer);
    return true;
}

// ==================== VALIDACIÓN DE FORMULARIOS ESPECÍFICOS ====================

/**
 * Valida el formulario de registro
 *
 * @param {HTMLFormElement} form - Formulario de registro
 * @returns {boolean} - true si el formulario es válido
 */
function validateRegistrationForm(form) {
    let isValid = true;

    // Validar nombre completo
    const fullNameField = form.querySelector('#fullName, input[name="fullName"]');
    if (fullNameField) {
        isValid = validateField(fullNameField, ValidationRules.fullName) && isValid;
    }

    // Validar email
    const emailField = form.querySelector('#email, input[name="email"]');
    if (emailField) {
        isValid = validateField(emailField, ValidationRules.email) && isValid;
    }

    // Validar contraseña
    const passwordField = form.querySelector('#password, input[name="password"]');
    if (passwordField) {
        isValid = validateField(passwordField, ValidationRules.password) && isValid;
    }

    // Validar confirmación de contraseña (si existe)
    const confirmPasswordField = form.querySelector('#confirmPassword, input[name="confirmPassword"]');
    if (confirmPasswordField && passwordField) {
        isValid = validatePasswordConfirmation(passwordField, confirmPasswordField) && isValid;
    }

    return isValid;
}

/**
 * Valida el formulario de creación de proyecto
 *
 * @param {HTMLFormElement} form - Formulario de proyecto
 * @returns {boolean} - true si el formulario es válido
 */
function validateProjectForm(form) {
    let isValid = true;

    // Validar nombre del proyecto
    const nameField = form.querySelector('#name, input[name="name"]');
    if (nameField) {
        isValid = validateField(nameField, ValidationRules.projectName) && isValid;
    }

    // Validar descripción
    const descriptionField = form.querySelector('#description, textarea[name="description"]');
    if (descriptionField) {
        isValid = validateField(descriptionField, ValidationRules.description) && isValid;
    }

    return isValid;
}

// ==================== INICIALIZACIÓN AUTOMÁTICA ====================

/**
 * Inicializa la validación cuando el DOM está listo
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('🔍 Sistema de validación cargado');

    // ==================== FORMULARIOS DE REGISTRO ====================
    const registerForms = document.querySelectorAll('form[action*="register"]');
    registerForms.forEach(form => {
        console.log('📝 Validación habilitada en formulario de registro');

        // Validar al enviar
        form.addEventListener('submit', function(e) {
            if (!validateRegistrationForm(this)) {
                e.preventDefault();
                console.warn('❌ Formulario no válido');

                // Scroll al primer error
                const firstError = this.querySelector('[aria-invalid="true"]');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstError.focus();
                }
            }
        });

        // Validación en tiempo real (blur event)
        const fields = form.querySelectorAll('input[type="email"], input[type="password"], input[name="fullName"]');
        fields.forEach(field => {
            field.addEventListener('blur', function() {
                if (this.name === 'fullName') {
                    validateField(this, ValidationRules.fullName);
                } else if (this.type === 'email') {
                    validateField(this, ValidationRules.email);
                } else if (this.type === 'password') {
                    validateField(this, ValidationRules.password);
                }
            });

            // Quitar error al empezar a escribir
            field.addEventListener('input', function() {
                if (this.classList.contains('border-red-500')) {
                    const errorContainer = document.getElementById(this.id + '-error');
                    if (errorContainer) {
                        errorContainer.classList.add('hidden');
                    }
                }
            });
        });
    });

    // ==================== FORMULARIOS DE PROYECTOS ====================
    const projectForms = document.querySelectorAll('form[action*="proyecto"], form[action*="project"]');
    projectForms.forEach(form => {
        console.log('📋 Validación habilitada en formulario de proyecto');

        form.addEventListener('submit', function(e) {
            if (!validateProjectForm(this)) {
                e.preventDefault();
                console.warn('❌ Formulario de proyecto no válido');

                const firstError = this.querySelector('[aria-invalid="true"]');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstError.focus();
                }
            }
        });

        // Validación en tiempo real
        const nameField = form.querySelector('#name, input[name="name"]');
        if (nameField) {
            nameField.addEventListener('blur', () => validateField(nameField, ValidationRules.projectName));
        }

        const descriptionField = form.querySelector('#description, textarea[name="description"]');
        if (descriptionField) {
            descriptionField.addEventListener('blur', () => validateField(descriptionField, ValidationRules.description));
        }
    });

    // ==================== VALIDACIÓN GENÉRICA PARA TODOS LOS FORMULARIOS ====================
    // Validar campos requeridos en todos los formularios
    const allForms = document.querySelectorAll('form');
    allForms.forEach(form => {
        const requiredFields = form.querySelectorAll('[required]');
        requiredFields.forEach(field => {
            field.addEventListener('blur', function() {
                if (this.value.trim() === '') {
                    const errorContainer = getOrCreateErrorContainer(this);
                    showError(this, errorContainer, 'Este campo es obligatorio');
                } else {
                    const errorContainer = getOrCreateErrorContainer(this);
                    hideError(this, errorContainer);
                }
            });
        });
    });
});

/**
 * MEJORAS FUTURAS:
 *
 * 1. VALIDACIÓN DE ARCHIVOS:
 *    Validar tipo, tamaño, dimensiones de imágenes
 *
 * 2. VALIDACIÓN ASÍNCRONA:
 *    Verificar email único en tiempo real (sin esperar submit)
 *    ```javascript
 *    async function checkEmailUnique(email) {
 *        const response = await fetch(`/api/users/check-email?email=${email}`);
 *        return await response.json();
 *    }
 *    ```
 *
 * 3. MENSAJES PERSONALIZADOS:
 *    Leer mensajes de error desde atributos data-*
 *
 * 4. INTERNACIONALIZACIÓN:
 *    Soporte para múltiples idiomas
 *
 * 5. VALIDACIÓN DE TARJETAS DE CRÉDITO:
 *    Algoritmo de Luhn, validación de CVV, etc.
 *
 * 6. INTEGRACIÓN CON BACKEND:
 *    Parsear mensajes de error del servidor y mostrarlos
 */
