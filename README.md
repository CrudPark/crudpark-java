# CrudPark Java - Aplicación Operativa de Parqueadero

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)
![Swing](https://img.shields.io/badge/GUI-Swing-green.svg)

## 📋 Descripción del Proyecto

**CrudPark Java** es una aplicación de escritorio desarrollada en Java con interfaz Swing, diseñada para la gestión operativa de un sistema de parqueadero. Permite a los operadores registrar ingresos y salidas de vehículos, aplicar tarifas automáticas, gestionar mensualidades y generar tickets con códigos QR.

Esta aplicación es parte del proyecto **CrudPark**, que integra una solución completa de parqueadero combinando:
- **Java (Swing + JDBC)** → Aplicación operativa de escritorio
- **C# (ASP.NET Core)** → Aplicación administrativa web
- **PostgreSQL** → Base de datos compartida

## 🎯 Objetivo

Proporcionar una herramienta eficiente para que los operadores de parqueadero puedan:
- Autenticar operadores activos
- Registrar ingresos de vehículos con detección automática de mensualidades
- Gestionar salidas aplicando reglas de negocio (tiempo de gracia, tarifas)
- Imprimir tickets con información detallada y código QR
- Calcular y registrar pagos automáticamente

---

## ✨ Funcionalidades Principales

### 1. 🔐 Inicio de Sesión de Operador
- Validación directa contra la tabla `operadores` en PostgreSQL
- Solo operadores con estado `activo = true` pueden acceder
- Sesión persistente durante el uso de la aplicación

### 2. 🚗 Ingreso de Vehículo
- **Entrada por placa**: El operador ingresa la placa del vehículo
- **Detección automática**:
  - ✅ Si la placa tiene **mensualidad vigente** → Entrada sin cobro
  - ✅ Si no tiene mensualidad → Registro como **invitado**
- **Validaciones**:
  - No permite duplicar tickets abiertos para la misma placa
  - Verifica vigencia de mensualidades (fecha actual entre `fecha_inicio` y `fecha_fin`)
- **Generación de ticket**:
  - Número de folio único (formato: `TKT000001`)
  - Fecha y hora de ingreso
  - Nombre del operador
  - Tipo de ingreso (Mensualidad / Invitado)
  - Código QR con formato: `TICKET:{folio}|PLATE:{placa}|DATE:{timestamp}`
- **Impresión automática** del ticket

### 3. 🚪 Salida de Vehículo
- **Búsqueda de ticket abierto** por placa
- **Cálculo de estadía**: Tiempo total en minutos desde el ingreso
- **Reglas de negocio**:
  - ⏱️ **Tiempo de gracia**: Los primeros 30 minutos no generan cobro
  - 💳 **Mensualidades**: Salida automática sin cobro
  - 💵 **Invitados**: Aplica tarifa activa después del tiempo de gracia
- **Cálculo de tarifa**:
  - Cobra por **horas completas** según `valor_base_hora`
  - Cobra **fracción adicional** por minutos restantes según `valor_fraccion`
  - Aplica **tope diario** si el monto supera el límite configurado
- **Registro de pago**:
  - Método de pago: Efectivo, Tarjeta o Transferencia
  - Monto calculado automáticamente
  - Registro en tabla `pagos` con operador y fecha

### 4. 🖨️ Impresión de Tickets
- Formato de ticket térmico estándar
- Información completa del ingreso
- Código QR integrado para validación
- Diseño simple y legible

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue el patrón **MVC (Model-View-Controller)** con separación de responsabilidades:

```
src/
├── config/
│   ├── DatabaseConfig.java        # Configuración de conexión a PostgreSQL
│   └── PropertiesConfig.java      # Lectura de archivo config.properties
├── models/
│   ├── Operador.java               # Entidad Operador
│   ├── Ticket.java                 # Entidad Ticket
│   ├── Mensualidad.java            # Entidad Mensualidad
│   ├── Tarifa.java                 # Entidad Tarifa
│   ├── Pago.java                   # Entidad Pago
│   └── Turno.java                  # Entidad Turno (opcional)
├── dao/
│   ├── OperadorDAO.java            # Acceso a datos de operadores
│   ├── TicketDAO.java              # Acceso a datos de tickets
│   ├── MensualidadDAO.java         # Acceso a datos de mensualidades
│   ├── TarifaDAO.java              # Acceso a datos de tarifas
│   └── PagoDAO.java                # Acceso a datos de pagos
├── services/
│   ├── AutenticacionService.java   # Lógica de autenticación
│   ├── TicketService.java          # Lógica de negocio de tickets
│   └── TicketPrinterService.java   # Servicio de impresión
├── controllers/
│   ├── LoginController.java        # Controlador de login
│   └── TicketController.java       # Controlador de tickets
└── views/
    ├── LoginView.java              # Vista de inicio de sesión
    └── MainView.java               # Vista principal del operador
```

### 📦 Flujo de Datos

```
View (Swing) → Controller → Service → DAO → PostgreSQL
```

---

## 🛠️ Requisitos del Sistema

### Software Necesario
- **Java JDK**: 17 o superior
- **PostgreSQL**: 15 o superior
- **IDE recomendado**: IntelliJ IDEA, Eclipse o NetBeans
- **Maven**: 3.8+ (para gestión de dependencias)

### Dependencias (Maven)
```xml
<!-- PostgreSQL JDBC Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>

<!-- Generación de códigos QR -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.1</version>
</dependency>

<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.1</version>
</dependency>
```

---

## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/crudpark-java.git
cd crudpark-java
```

### 2. Configurar la base de datos PostgreSQL

#### Crear la base de datos
```sql
CREATE DATABASE crudpark_db;
```

#### Ejecutar el script de creación de tablas
Ejecuta el archivo `database/schema.sql` proporcionado en el repositorio:
```bash
psql -U postgres -d crudpark_db -f database/schema.sql
```

#### Insertar datos iniciales (opcional)
```bash
psql -U postgres -d crudpark_db -f database/seed.sql
```

### 3. Configurar archivo de propiedades

Crea o edita el archivo `src/main/resources/config.properties`:

```properties
# Configuración de Base de Datos PostgreSQL
db.url=jdbc:postgresql://localhost:5432/crudpark_db
db.user=postgres
db.password=tu_password_aqui

# Configuración de la Aplicación
app.name=CrudPark - Crudzaso
app.version=1.0.0

# Configuración de Tickets
ticket.tiempo_gracia=30

# Configuración de Impresión
printer.enabled=true
printer.name=default
```

### 4. Compilar el proyecto

#### Con Maven:
```bash
mvn clean install
```

#### Con IDE:
- Importar el proyecto como proyecto Maven
- Configurar JDK 17+
- Build → Build Project

### 5. Ejecutar la aplicación

#### Desde Maven:
```bash
mvn exec:java -Dexec.mainClass="Main"
```

#### Desde IDE:
- Ejecutar la clase `Main.java`

#### Generar JAR ejecutable:
```bash
mvn package
java -jar target/crudpark-java-1.0.0.jar
```

---

## 📊 Configuración de la Base de Datos

### Esquema de Tablas Principales

#### `operadores`
```sql
CREATE TABLE operadores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### `tickets`
```sql
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    numero_folio VARCHAR(20) NOT NULL UNIQUE,
    placa VARCHAR(10) NOT NULL,
    tipo_ingreso VARCHAR(20) CHECK (tipo_ingreso IN ('Mensualidad', 'Invitado')),
    fecha_ingreso TIMESTAMP NOT NULL,
    fecha_salida TIMESTAMP,
    operador_ingreso_id INTEGER REFERENCES operadores(id),
    operador_salida_id INTEGER REFERENCES operadores(id),
    tiempo_estadia_minutos INTEGER,
    monto_cobrado DECIMAL(10,2) DEFAULT 0,
    pagado BOOLEAN DEFAULT false,
    activo BOOLEAN DEFAULT true,
    qr_code TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### `mensualidades`
```sql
CREATE TABLE mensualidades (
    id SERIAL PRIMARY KEY,
    nombre_propietario VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    placa VARCHAR(10) NOT NULL UNIQUE,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activa BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_fecha_mensualidad CHECK (fecha_fin > fecha_inicio)
);
```

#### `tarifas`
```sql
CREATE TABLE tarifas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    valor_base_hora DECIMAL(10,2) NOT NULL,
    valor_fraccion DECIMAL(10,2) NOT NULL,
    tope_diario DECIMAL(10,2),
    tiempo_gracia_minutos INTEGER DEFAULT 30,
    activa BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### `pagos`
```sql
CREATE TABLE pagos (
    id SERIAL PRIMARY KEY,
    ticket_id INTEGER REFERENCES tickets(id),
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(20) CHECK (metodo_pago IN ('Efectivo', 'Tarjeta', 'Transferencia')),
    operador_id INTEGER REFERENCES operadores(id),
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT
);
```

---

## 🚀 Flujo General de Uso

### 1️⃣ Inicio de Sesión
```
Usuario → Ingresa nombre de operador → Sistema valida en BD → Acceso concedido/denegado
```

### 2️⃣ Registrar Ingreso
```
Operador → Ingresa placa → Sistema verifica mensualidad → Crea ticket → Imprime ticket
```

### 3️⃣ Registrar Salida
```
Operador → Ingresa placa → Sistema calcula tiempo → Aplica tarifa → Registra pago → Cierra ticket
```

### Ejemplo de Cálculo de Tarifa

**Configuración:**
- Valor base por hora: $5,000
- Valor fracción: $2,000
- Tiempo de gracia: 30 minutos
- Tope diario: $50,000

**Escenarios:**

| Tiempo de estadía | Cobro | Explicación |
|-------------------|-------|-------------|
| 20 minutos | $0 | Dentro del tiempo de gracia |
| 30 minutos | $0 | Exactamente el tiempo de gracia |
| 45 minutos | $2,000 | 15 min cobrables = 1 fracción |
| 1 hora 30 min | $5,000 | 1 hora cobrable (los 30 min extra no cuentan por gracia) |
| 2 horas 15 min | $12,000 | 1h 45min cobrables = 1 hora ($5,000) + 1 fracción ($2,000) |
| 12 horas | $50,000 | Se aplica tope diario |

---

## 🔑 Reglas de Negocio Implementadas

✅ **Tiempo de gracia**: Los primeros 30 minutos no generan cobro  
✅ **Un solo ticket abierto por placa**: No se permite duplicar registros activos  
✅ **Mensualidad vigente**: Entrada y salida sin cobro dentro del período activo  
✅ **Validación de fechas**: Mensualidades con `fecha_fin > fecha_inicio`  
✅ **Pagos solo para invitados**: Mensualidades no registran pagos  
✅ **Operadores activos**: Solo operadores con `activo = true` pueden autenticarse  
✅ **Folios únicos**: Generación automática secuencial `TKT000001`, `TKT000002`, etc.  

---

## 🎨 Formato del Ticket Impreso

```
==============================
     CrudPark - Crudzaso
==============================
Ticket #: TKT000123
Placa: ABC123
Tipo: Invitado
Ingreso: 2025-10-17 09:45 AM
Operador: Juan Pérez
------------------------------
[CÓDIGO QR AQUÍ]
TICKET:TKT000123|PLATE:ABC123|DATE:1729166700
------------------------------
Gracias por su visita.
==============================
```

---

## 👥 Equipo de Desarrollo

### Integrantes
- **[Tu Nombre]** - Desarrollador Java (Equipo Berners-Lee)
- **[Nombre Compañero 1]** - Desarrollador C# (Equipo Van Rossum)
- **[Nombre Compañero 2]** - Desarrollador C# (Equipo Van Rossum)

### Registro del equipo
👉 **Equipo registrado en**: [https://teams.crudzaso.com](https://teams.crudzaso.com)

---

## 🐛 Solución de Problemas

### Error de conexión a la base de datos
```
Verificar:
1. PostgreSQL está ejecutándose
2. Credenciales en config.properties son correctas
3. Base de datos crudpark_db existe
4. Firewall no bloquea puerto 5432
```

### Error al generar QR
```
Verificar:
1. Dependencia zxing está en pom.xml
2. Maven dependencies están actualizadas (mvn clean install)
```

### Ticket no imprime
```
Verificar:
1. printer.enabled=true en config.properties
2. Impresora configurada en el sistema operativo
```

---

## 📝 Notas Técnicas

- **Patrón de diseño**: MVC (Model-View-Controller)
- **Manejo de conexiones**: Singleton pattern en DatabaseConfig
- **Transacciones**: Auto-commit habilitado por defecto
- **Encoding**: UTF-8 para soportar caracteres especiales
- **Logs**: System.out/err para desarrollo (considerar log4j en producción)

---

## 📄 Licencia

Este proyecto es parte de un ejercicio académico para **Crudzaso**.

---

## 🔗 Enlaces Relacionados

- **Repositorio C# Front**: [crudpark-csharp-front](https://github.com/tu-org/crudpark-csharp-front)
- **Repositorio C# Back**: [crudpark-csharp-back](https://github.com/tu-org/crudpark-csharp-back)
- **Documentación del proyecto**: [CrudPark Docs](https://docs.crudzaso.com)
- **Registro de equipos**: [https://teams.crudzaso.com](https://teams.crudzaso.com)

---

## 📧 Contacto

Para dudas o sugerencias sobre este proyecto:
- **Email**: tu-email@ejemplo.com
- **GitHub**: [@tu-usuario](https://github.com/tu-usuario)

---

**Desarrollado con ☕ y 💻 por el equipo CrudPark**
