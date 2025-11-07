#!/bin/bash

# Script de verificación de build para Railway
# Simula exactamente lo que hará Railway

set -e

echo "=========================================="
echo "VERIFICACIÓN DE BUILD PARA RAILWAY"
echo "=========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

success() {
    echo -e "${GREEN}✓${NC} $1"
}

error() {
    echo -e "${RED}✗${NC} $1"
}

warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# ==========================================
# VERIFICACIÓN 1: Archivos necesarios
# ==========================================
echo "1. Verificando archivos necesarios..."

if [ -f "package.json" ]; then
    success "package.json existe"
else
    error "package.json NO encontrado"
    exit 1
fi

if [ -f "tailwind.config.js" ]; then
    success "tailwind.config.js existe"
else
    error "tailwind.config.js NO encontrado"
    exit 1
fi

if [ -f "src/main/resources/static/css/input.css" ]; then
    success "input.css existe"
else
    error "input.css NO encontrado"
    exit 1
fi

if [ -f "railway-build.sh" ]; then
    success "railway-build.sh existe"
else
    error "railway-build.sh NO encontrado"
    exit 1
fi

if [ -f "nixpacks.toml" ]; then
    success "nixpacks.toml existe"
else
    error "nixpacks.toml NO encontrado"
    exit 1
fi

echo ""

# ==========================================
# VERIFICACIÓN 2: Node.js y npm
# ==========================================
echo "2. Verificando Node.js y npm..."

if command -v node &> /dev/null; then
    NODE_VERSION=$(node --version)
    success "Node.js instalado: $NODE_VERSION"
else
    error "Node.js NO instalado"
    echo "  Instalar desde: https://nodejs.org/"
    exit 1
fi

if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm --version)
    success "npm instalado: $NPM_VERSION"
else
    error "npm NO instalado"
    exit 1
fi

echo ""

# ==========================================
# VERIFICACIÓN 3: Java
# ==========================================
echo "3. Verificando Java..."

if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    success "Java instalado: $JAVA_VERSION"
else
    error "Java NO instalado"
    echo "  Instalar JDK 21 desde: https://adoptium.net/"
    exit 1
fi

echo ""

# ==========================================
# VERIFICACIÓN 4: Simular build de Railway
# ==========================================
echo "4. Simulando build de Railway..."
echo ""

# PASO 1: npm install
echo "=========================================="
echo "PASO 1: npm install"
echo "=========================================="
npm install

if [ $? -eq 0 ]; then
    success "npm install exitoso"
else
    error "npm install falló"
    exit 1
fi

echo ""

# PASO 2: npm run build:css
echo "=========================================="
echo "PASO 2: npm run build:css"
echo "=========================================="
npm run build:css

if [ $? -eq 0 ]; then
    success "npm run build:css exitoso"
else
    error "npm run build:css falló"
    exit 1
fi

echo ""

# Verificar que el CSS se generó
if [ -f "src/main/resources/static/css/tailwind.min.css" ]; then
    CSS_SIZE=$(wc -c < src/main/resources/static/css/tailwind.min.css)
    if [ $CSS_SIZE -gt 1000 ]; then
        success "CSS generado: ${CSS_SIZE} bytes"
    else
        warning "CSS generado pero tamaño sospechosamente pequeño: ${CSS_SIZE} bytes"
    fi
else
    error "CSS NO se generó en src/main/resources/static/css/tailwind.min.css"
    exit 1
fi

echo ""

# PASO 3: Maven build
echo "=========================================="
echo "PASO 3: mvn clean package -Pproduction"
echo "=========================================="
./mvnw clean package -DskipTests -Pproduction

if [ $? -eq 0 ]; then
    success "Maven build exitoso"
else
    error "Maven build falló"
    exit 1
fi

echo ""

# ==========================================
# VERIFICACIÓN 5: JAR generado
# ==========================================
echo "5. Verificando JAR generado..."

if [ -f "target/gestion-tareas-0.0.1-SNAPSHOT.jar" ]; then
    JAR_SIZE=$(ls -lh target/gestion-tareas-0.0.1-SNAPSHOT.jar | awk '{print $5}')
    success "JAR generado: ${JAR_SIZE}"
else
    error "JAR NO generado"
    exit 1
fi

echo ""

# ==========================================
# VERIFICACIÓN 6: CSS dentro del JAR
# ==========================================
echo "6. Verificando que CSS está en el JAR..."

CSS_IN_JAR=$(jar -tf target/gestion-tareas-0.0.1-SNAPSHOT.jar | grep "tailwind.min.css" || echo "")

if [ -n "$CSS_IN_JAR" ]; then
    success "CSS encontrado en JAR: $CSS_IN_JAR"
else
    error "CSS NO encontrado en JAR"
    warning "El CSS no se empaquetó correctamente"
    exit 1
fi

echo ""

# ==========================================
# VERIFICACIÓN 7: Templates usan CSS local
# ==========================================
echo "7. Verificando que templates NO usan CDN..."

CDN_USAGE=$(grep -r "cdn.tailwindcss.com" src/main/resources/templates/ || echo "")

if [ -z "$CDN_USAGE" ]; then
    success "Templates NO usan CDN de Tailwind"
else
    warning "Algunos templates aún usan CDN:"
    echo "$CDN_USAGE"
fi

echo ""

# ==========================================
# RESUMEN FINAL
# ==========================================
echo "=========================================="
echo "RESUMEN DE VERIFICACIÓN"
echo "=========================================="
echo ""
success "Todos los archivos necesarios están presentes"
success "Node.js y npm están instalados"
success "Java está instalado"
success "npm install funciona"
success "Tailwind CSS se compila correctamente"
success "Maven build funciona con perfil production"
success "JAR se genera correctamente"
success "CSS está incluido en el JAR"

echo ""
echo "=========================================="
echo -e "${GREEN}✓ BUILD VERIFICADO EXITOSAMENTE${NC}"
echo "=========================================="
echo ""
echo "El proyecto está listo para deployar en Railway."
echo ""
echo "Próximos pasos:"
echo "1. git add ."
echo "2. git commit -m \"fix: Optimizar build para Railway\""
echo "3. git push"
echo ""
echo "Railway ejecutará automáticamente railway-build.sh"
echo "que hace exactamente lo mismo que este script."
echo ""
