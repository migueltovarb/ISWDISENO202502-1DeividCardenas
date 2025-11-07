/** @type {import('tailwindcss').Config} */
module.exports = {
  // Archivos a escanear para clases de Tailwind
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/static/js/**/*.js"
  ],

  // Habilitar modo oscuro con clase 'dark'
  darkMode: 'class',

  theme: {
    extend: {
      // Aquí puedes añadir personalizaciones de tema
      colors: {
        // Ejemplo: Añadir colores personalizados de la marca
        // 'brand-primary': '#4F46E5',
        // 'brand-secondary': '#7C3AED',
      },
      fontFamily: {
        // Ejemplo: Añadir fuentes personalizadas
        // 'sans': ['Inter', 'system-ui', 'sans-serif'],
      },
      spacing: {
        // Ejemplo: Añadir espaciados personalizados
        // '128': '32rem',
      },
      animation: {
        // Ejemplo: Añadir animaciones personalizadas
        // 'fade-in': 'fadeIn 0.5s ease-in-out',
      },
      keyframes: {
        // Ejemplo: Definir keyframes personalizados
        // fadeIn: {
        //   '0%': { opacity: '0' },
        //   '100%': { opacity: '1' },
        // },
      }
    },
  },

  plugins: [
    // Aquí puedes añadir plugins de Tailwind
    // Ejemplos:
    // require('@tailwindcss/forms'),
    // require('@tailwindcss/typography'),
    // require('@tailwindcss/aspect-ratio'),
  ],
}
