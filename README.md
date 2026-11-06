\# VITRALUX ERP



Aplicación web para gestionar clientes, productos y servicios, cotizaciones, ventas,

abonos y órdenes de trabajo de una empresa de vidrio y aluminio. El proyecto

corresponde al Avance 2 del curso.



\## Tecnologías



Java 21, Spring Boot 4.1.2, Spring MVC, Spring Data JPA, Hibernate, MySQL,

Spring Security, Thymeleaf y Bootstrap.



\## Requisitos de ejecución



\- JDK 21 disponible en la terminal.

\- MySQL en ejecución y un usuario con permisos sobre la base de datos.

\- PowerShell en Windows. El proyecto incluye Maven Wrapper.



\## Ejecutar localmente



1\. En MySQL Workbench, crear la base de datos:



&#x20;  ```sql

&#x20;  CREATE DATABASE IF NOT EXISTS vitralux\_erp

&#x20;      CHARACTER SET utf8mb4

&#x20;      COLLATE utf8mb4\_unicode\_ci;

&#x20;  ```



2\. En PowerShell, abrir la carpeta que contiene `pom.xml`. Configurar el usuario y

&#x20;  la contraseña local de MySQL:



&#x20;  ```powershell

&#x20;  $env:VITRALUX\_DB\_USER = 'root'

&#x20;  $env:VITRALUX\_DB\_PASSWORD = \[System.Net.NetworkCredential]::new(

&#x20;      '',

&#x20;      (Read-Host 'Contraseña de MySQL' -AsSecureString)

&#x20;  ).Password

&#x20;  ```



&#x20;  Si MySQL está en otro puerto o servidor, definir `VITRALUX\_DB\_URL` con la URL JDBC.



3\. Para crear la primera cuenta administradora en una base nueva, definir una

&#x20;  contraseña de al menos ocho caracteres y hasta 72 bytes:



&#x20;  ```powershell

&#x20;  $env:VITRALUX\_ADMIN\_PASSWORD = \[System.Net.NetworkCredential]::new(

&#x20;      '',

&#x20;      (Read-Host 'Contraseña del administrador inicial' -AsSecureString)

&#x20;  ).Password

&#x20;  ```



4\. Ejecutar:



&#x20;  ```powershell

&#x20;  .\\mvnw.cmd spring-boot:run

&#x20;  ```



5\. Abrir `http://localhost:8080/`. La cuenta inicial es `admin@vitralux.local`.

&#x20;  En una base que ya tiene un administrador, usar las credenciales existentes.



\## Estructura



| Carpeta o paquete | Responsabilidad |

|---|---|

| `controller` | Rutas y datos que reciben las vistas |

| `service` | Reglas de negocio |

| `repository` | Acceso a datos mediante Spring Data JPA |

| `domain` | Entidades y relaciones |

| `config` | Configuración de acceso y permisos |

| `src/main/resources/templates` | Vistas Thymeleaf con Bootstrap |



\## Backlog del Avance 2



El backlog original tiene 25 historias. El mínimo para alcanzar el 50 % es

verificar 13 historias completas. Este avance tiene 14 historias como objetivo.



| Historias | Flujo | Evidencia esperada |

|---|---|---|

| HU-01 y HU-02 | Registrar y editar clientes | El cliente aparece en el listado y conserva su ID después de editarlo |

| HU-03 | Consultar y eliminar clientes | Administrador elimina un cliente libre y el sistema conserva uno asociado a una cotización |

| HU-04 y HU-05 | Registrar, consultar y editar productos y servicios | El catálogo muestra los cambios guardados |

| HU-06 a HU-08 | Crear, consultar y resolver cotizaciones | Se guardan los conceptos y el total. Se comprueban los estados aprobada y rechazada |

| HU-09 y HU-10 | Generar y consultar ventas | La venta aparece vinculada a una cotización aprobada y a su cliente |

| HU-11 y HU-12 | Registrar abonos y consultar saldos | El saldo coincide con el total menos los pagos registrados |

| HU-18 | Crear una orden de trabajo | La orden aparece como pendiente y vinculada a una venta |

| HU-25 | Gestionar usuarios y permisos | Se asigna un rol y se comprueba que una ruta restringida devuelve 403 |



\### Estado de verificación



\- Verificadas en los bloques 1 y 2: HU-01, HU-02 y HU-04 a HU-12.

\- Por confirmar en la prueba del bloque 3: HU-03, HU-18 y HU-25.

\- Compras, inventario, instalaciones y reportes quedan para avances posteriores.

