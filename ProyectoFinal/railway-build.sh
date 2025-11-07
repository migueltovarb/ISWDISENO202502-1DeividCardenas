#!/bin/bash
set -e

echo "=========================================="
echo "RAILWAY BUILD SCRIPT"
echo "=========================================="

# Verificar versiones instaladas
echo "Verificando versiones de herramientas..."
java -version
node --version
npm --version

# Dar permisos de ejecución al wrapper de Maven
chmod +x ./mvnw
echo "✓ Maven wrapper permisos configurados"

# ==========================================
# PASO 1: COMPILAR TAILWIND CSS
# ==========================================
echo ""
echo "=========================================="
echo "PASO 1: Instalando dependencias npm"
echo "=========================================="

if [ -f "package.json" ]; then
    npm install
    echo "✓ Dependencias npm instaladas"
else
    echo "⚠ WARNING: package.json no encontrado"
fi

echo ""
echo "=========================================="
echo "PASO 2: Compilando Tailwind CSS"
echo "=========================================="

if [ -f "package.json" ]; then
    npm run build:css
    echo "✓ Tailwind CSS compilado exitosamente"

    # Verificar que el CSS se generó correctamente
    if [ -f "src/main/resources/static/css/tailwind.min.css" ]; then
        CSS_SIZE=$(wc -c < src/main/resources/static/css/tailwind.min.css)
        echo "✓ CSS generado: ${CSS_SIZE} bytes"
    else
        echo "⚠ WARNING: tailwind.min.css no se generó"
    fi
else
    echo "⚠ WARNING: No se puede compilar CSS, package.json no existe"
fi

# ==========================================
# PASO 3: BUILD DE MAVEN
# ==========================================
echo ""
echo "=========================================="
echo "PASO 3: Construyendo proyecto con Maven"
echo "=========================================="

# Limpiar y construir el proyecto (sin ejecutar npm de nuevo porque ya lo hicimos)
# Usamos el perfil production que excluye devtools
./mvnw clean package -DskipTests -Pproduction

echo ""
echo "=========================================="
echo "BUILD COMPLETADO EXITOSAMENTE"
echo "=========================================="

# Verificar que el JAR se creó
if [ -f "target/gestion-tareas-0.0.1-SNAPSHOT.jar" ]; then
    JAR_SIZE=$(ls -lh target/gestion-tareas-0.0.1-SNAPSHOT.jar | awk '{print $5}')
    echo "✓ JAR generado: ${JAR_SIZE}"
else
    echo "❌ ERROR: JAR no generado"
    exit 1
fi

echo "=========================================="
echo "Proyecto listo para deployment"
echo "=========================================="
