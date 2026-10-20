# Clínica Dental (Servlets)

Aplicación web Java para gestionar pacientes y citas de una clínica dental. La versión
actual está organizada con el patrón **MVC**: los servlets reciben las peticiones,
los DAO encapsulan el acceso JDBC y las páginas JSP se limitan a presentar los datos.

## Tecnologías

- Java 21 y Maven.
- Servlets 4.0 (`javax.servlet`), JSP 2.3 y JSTL 1.2.
- MySQL y MySQL Connector/J 8.2.0.
- Apache Tomcat 9.0.x. Tomcat 9 utiliza el espacio de nombres `javax.*`; Tomcat 10+
  utiliza `jakarta.*` y no es compatible con esta configuración.

## Estructura

```text
src/
├── main/
│   ├── java/es/dwes/dentista/
│   │   ├── config/ConexionBD.java        # configuración y obtención de conexiones
│   │   ├── dao/                          # consultas e inserciones JDBC
│   │   ├── model/                        # beans Paciente, Cita y Odontologo
│   │   └── servlet/                      # controladores y validación de peticiones
│   ├── resources/dentista.sql            # creación y datos de ejemplo
│   └── webapp/
│       ├── WEB-INF/vistas/               # JSP de presentación, no accesibles directamente
│       ├── estilos.css
│       └── index.jsp                     # página de inicio
└── ...
```

Las vistas usan JSTL/EL y `c:out` para mostrar los valores escapados; no contienen
scriptlets ni consultas SQL. Las conexiones y recursos JDBC se cierran con
*try-with-resources*.

## Funcionalidad y rutas

| Ruta | Método | Función |
|---|---|---|
| `/` | GET | Menú principal |
| `/pacientes` | GET | Listado de pacientes y número de citas |
| `/pacientes/nuevo` | GET / POST | Formulario y alta de paciente |
| `/pacientes/eliminar` | POST | Baja de paciente (elimina sus citas por `ON DELETE CASCADE`) |
| `/citas` | GET | Próximas citas; con `?todas=1`, muestra el histórico |
| `/citas/nueva` | GET / POST | Formulario y alta de cita |
| `/citas/eliminar` | POST | Anulación de cita |

Las operaciones de alta y baja siguen el patrón **Post/Redirect/Get**. Los borrados
se envían mediante POST y las consultas de escritura usan `PreparedStatement`.

## Base de datos

Crear la base de datos `dentista` y cargar los datos de ejemplo:

```bash
mysql -u user -puser < src/main/resources/dentista.sql
```

El script crea las tablas `paciente`, `odontologos` y `cita`. Las citas de ejemplo
usan `DATE_ADD(CURDATE(), INTERVAL n DAY)`, por lo que incluye fechas próximas y
pasadas con independencia del día en que se importe.

Por defecto, la aplicación conecta con MySQL en `localhost:3306`, base de datos
`dentista`, usuario `user` y contraseña `user`, manteniendo la configuración local
del proyecto original. Para cambiarla sin modificar el código, configura estas
variables de entorno antes de iniciar Tomcat:

| Variable | Valor predeterminado |
|---|---|
| `DENTISTA_DB_URL` | `jdbc:mysql://localhost:3306/dentista?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8` |
| `DENTISTA_DB_USER` | `user` |
| `DENTISTA_DB_PASSWORD` | `user` |

## Compilar y desplegar

Empaquetar el proyecto:

```bash
mvn clean package
```

Se genera `target/servlet_dentista.war`. Despliega ese WAR en Tomcat 9.0.x; el contexto
por defecto será `/servlet_dentista`. El conector de MySQL se incorpora al WAR desde
Maven, y la API de Servlet se proporciona desde Tomcat.

Para crear el ZIP de entrega, excluye los artefactos generados, por ejemplo:

```bash
zip -r ../servlet_dentista.zip . -x 'target/*' 'target/' 'build/*' 'build/' '.git/*' '.git/'
```
