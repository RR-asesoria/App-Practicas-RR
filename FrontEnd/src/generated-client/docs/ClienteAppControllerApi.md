# OpenApiDefinition.ClienteAppControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**actualizarCliente**](ClienteAppControllerApi.md#actualizarCliente) | **PUT** /api/clientes/actualizarcliente/{nifCif} | Actualizar cliente
[**buscarPorFiltros1**](ClienteAppControllerApi.md#buscarPorFiltros1) | **POST** /api/clientes/buscarporfiltros | Buscar clientes
[**buscarPorNombre1**](ClienteAppControllerApi.md#buscarPorNombre1) | **GET** /api/clientes/buscar/nombre | Buscar clientes por nombre
[**cambiarNif**](ClienteAppControllerApi.md#cambiarNif) | **PUT** /api/clientes/cambiar-nif | Cambiar NIF/CIF de un cliente
[**cierreEjercicio**](ClienteAppControllerApi.md#cierreEjercicio) | **POST** /api/clientes/cierre-ejercicio | Cierre de ejercicio
[**crearCliente**](ClienteAppControllerApi.md#crearCliente) | **POST** /api/clientes/crearcliente | Crear cliente
[**eliminarCliente1**](ClienteAppControllerApi.md#eliminarCliente1) | **DELETE** /api/clientes/eliminarcliente/{nifCif} | Eliminar cliente
[**eliminarTodos**](ClienteAppControllerApi.md#eliminarTodos) | **DELETE** /api/clientes/todos | Eliminar todos los clientes
[**obtenerCliente1**](ClienteAppControllerApi.md#obtenerCliente1) | **GET** /api/clientes/obtenerpornif/{nifCif} | Obtener cliente
[**obtenerTodos1**](ClienteAppControllerApi.md#obtenerTodos1) | **GET** /api/clientes/obtenerTodos | Obtener todos los clientes



## actualizarCliente

> String actualizarCliente(nifCif, clienteApp)

Actualizar cliente

Actualiza los datos de un cliente existente

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
let nifCif = "nifCif_example"; // String | NIF/CIF del cliente a actualizar
let clienteApp = new OpenApiDefinition.ClienteApp(); // ClienteApp | Datos del cliente a actualizar
apiInstance.actualizarCliente(nifCif, clienteApp, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **nifCif** | **String**| NIF/CIF del cliente a actualizar | 
 **clienteApp** | [**ClienteApp**](ClienteApp.md)| Datos del cliente a actualizar | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*


## buscarPorFiltros1

> [ClienteApp] buscarPorFiltros1(clienteApp)

Buscar clientes

Busca clientes según filtros exactos seleccionados

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
let clienteApp = new OpenApiDefinition.ClienteApp(); // ClienteApp | Filtros de búsqueda exacta por atributos (tipoCliente, nifCif, estadoCliente...)
apiInstance.buscarPorFiltros1(clienteApp, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clienteApp** | [**ClienteApp**](ClienteApp.md)| Filtros de búsqueda exacta por atributos (tipoCliente, nifCif, estadoCliente...) | 

### Return type

[**[ClienteApp]**](ClienteApp.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*


## buscarPorNombre1

> ClienteApp buscarPorNombre1(nombre)

Buscar clientes por nombre

Busca clientes por coincidencia parcial de nombre

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
let nombre = "nombre_example"; // String | Texto del nombre a buscar
apiInstance.buscarPorNombre1(nombre, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **nombre** | **String**| Texto del nombre a buscar | 

### Return type

[**ClienteApp**](ClienteApp.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## cambiarNif

> String cambiarNif(nifViejo, nifNuevo)

Cambiar NIF/CIF de un cliente

Migra todos los datos de un cliente a un nuevo NIF/CIF

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
let nifViejo = "nifViejo_example"; // String | 
let nifNuevo = "nifNuevo_example"; // String | 
apiInstance.cambiarNif(nifViejo, nifNuevo, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **nifViejo** | **String**|  | 
 **nifNuevo** | **String**|  | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## cierreEjercicio

> String cierreEjercicio()

Cierre de ejercicio

Mueve todos los clientes a histórico y actualiza casilla505

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
apiInstance.cierreEjercicio((error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

This endpoint does not need any parameter.

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## crearCliente

> String crearCliente(clienteApp)

Crear cliente

Crea un nuevo cliente en la base de datos

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
let clienteApp = new OpenApiDefinition.ClienteApp(); // ClienteApp | Datos del cliente a crear
apiInstance.crearCliente(clienteApp, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **clienteApp** | [**ClienteApp**](ClienteApp.md)| Datos del cliente a crear | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*


## eliminarCliente1

> String eliminarCliente1(nifCif)

Eliminar cliente

Elimina un cliente por su NIF/CIF

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
let nifCif = "nifCif_example"; // String | NIF/CIF del cliente a eliminar
apiInstance.eliminarCliente1(nifCif, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **nifCif** | **String**| NIF/CIF del cliente a eliminar | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## eliminarTodos

> String eliminarTodos()

Eliminar todos los clientes

Elimina todos los clientes de la base de datos. Solo para testing.

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
apiInstance.eliminarTodos((error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

This endpoint does not need any parameter.

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## obtenerCliente1

> ClienteApp obtenerCliente1(nifCif)

Obtener cliente

Obtiene un cliente por su NIF/CIF

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
let nifCif = "nifCif_example"; // String | NIF/CIF del cliente
apiInstance.obtenerCliente1(nifCif, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **nifCif** | **String**| NIF/CIF del cliente | 

### Return type

[**ClienteApp**](ClienteApp.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## obtenerTodos1

> ClienteApp obtenerTodos1()

Obtener todos los clientes

Devuelve la lista completa de clientes

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppControllerApi();
apiInstance.obtenerTodos1((error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**ClienteApp**](ClienteApp.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

