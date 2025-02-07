# Proyecto de Aplicación de Consulta de Perros

Esta es una aplicación móvil construida con **Jetpack Compose** para Android. Permite a los usuarios visualizar una imagen aleatoria de un perro obtenida desde una API externa. Los usuarios pueden ver otro perro, regresar a la pantalla de inicio o cerrar la aplicación. Además, incluye funcionalidades de autenticación y notificaciones periódicas.

## Funcionalidades

- **Pantalla de inicio de sesión**: Los usuarios pueden iniciar sesión con sus credenciales.
- **Pantalla principal (Home)**: Los usuarios pueden ver una imagen aleatoria de un perro. La pantalla también muestra un botón para ver otro perro y un botón para regresar a la pantalla de inicio.
- **Pantalla de perro aleatorio**: Muestra una imagen aleatoria de un perro obtenida desde una API externa.
- **Notificaciones periódicas**: Mientras el usuario no haya consultado la API, la aplicación muestra notificaciones periódicas para recordar hacer la consulta.
- **Registro de usuario**: Los usuarios pueden registrarse para crear una cuenta.

### Uso de Jetpack Compose
Se opto por utilizar **Jetpack Compose** para la construcción de la interfaz de usuario  debido a su modernidad y simplicidad

### Autenticación con Firebase
Se utilizó **Firebase Authentication** para gestionar el inicio de sesión y el registro de usuarios

### Manejo de notificaciones periódicas
Se decidió implementar **notificaciones periódicas** utilizando el **Handler** de Android, que ejecuta un **Runnable** cada cierto tiempo si el usuario no ha consultado la API

### Obtención de datos de la API
La información sobre las imágenes de los perros se obtiene de una **API externa**

### Navegación con Jetpack Navigation
Se implementó la navegación utilizando **Jetpack Navigation**, lo que facilita la gestión de pantallas y la implementación de una estructura de navegación clara y predecible
