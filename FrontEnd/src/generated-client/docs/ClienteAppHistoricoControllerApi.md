# OpenApiDefinition.ClienteAppHistoricoControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**buscarPorFiltros**](ClienteAppHistoricoControllerApi.md#buscarPorFiltros) | **POST** /api/clientesHistorico/buscar | Buscar clientes
[**buscarPorNombre**](ClienteAppHistoricoControllerApi.md#buscarPorNombre) | **GET** /api/clientesHistorico/buscar/nombre | Buscar clientes en el historico por nombre
[**eliminarCliente**](ClienteAppHistoricoControllerApi.md#eliminarCliente) | **DELETE** /api/clientesHistorico/borrarcliente/{nifCif} | Eliminar cliente historico
[**eliminarTodosClientes**](ClienteAppHistoricoControllerApi.md#eliminarTodosClientes) | **DELETE** /api/clientesHistorico/eliminartodos | Elimina todos los clientes del historico
[**obtenerCliente**](ClienteAppHistoricoControllerApi.md#obtenerCliente) | **GET** /api/clientesHistorico/buscarcliente/{nifCif} | Obtener cliente
[**obtenerClientes**](ClienteAppHistoricoControllerApi.md#obtenerClientes) | **GET** /api/clientesHistorico | Obtener todos los clientes



## buscarPorFiltros

> [ClienteAppHistorico] buscarPorFiltros(clienteAppHistorico)

Buscar clientes

Busca clientes en el historico según filtros exactos seleccionados

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppHistoricoControllerApi();
let clienteAppHistorico = new OpenApiDefinition.ClienteAppHistorico(); // ClienteAppHistorico | Filtros de búsqueda exacta por atributos (tipoCliente, nifCif, estadoCliente...)
apiInstance.buscarPorFiltros(clienteAppHistorico, (error, data, response) => {
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
 **clienteAppHistorico** | [**ClienteAppHistorico**](ClienteAppHistorico.md)| Filtros de búsqueda exacta por atributos (tipoCliente, nifCif, estadoCliente...) | 

### Return type

[**[ClienteAppHistorico]**](ClienteAppHistorico.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*


## buscarPorNombre

> ClienteAppHistorico buscarPorNombre(nombre)

Buscar clientes en el historico por nombre

Busca clientes por coincidencia parcial de nombre

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppHistoricoControllerApi();
let nombre = "nombre_example"; // String | Nombre a buscar
apiInstance.buscarPorNombre(nombre, (error, data, response) => {
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
 **nombre** | **String**| Nombre a buscar | 

### Return type

[**ClienteAppHistorico**](ClienteAppHistorico.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## eliminarCliente

> String eliminarCliente(nifCif)

Eliminar cliente historico

Elimina un cliente del historico por su NIF/CIF

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppHistoricoControllerApi();
let nifCif = "nifCif_example"; // String | NIF/CIF del cliente a eliminar
apiInstance.eliminarCliente(nifCif, (error, data, response) => {
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


## eliminarTodosClientes

> String eliminarTodosClientes()

Elimina todos los clientes del historico

Elimina todos los clientes del historico

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppHistoricoControllerApi();
apiInstance.eliminarTodosClientes((error, data, response) => {
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


## obtenerCliente

> ClienteAppHistorico obtenerCliente(nifCif)

Obtener cliente

Obtener un cliente mediante su Nif/Cif

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppHistoricoControllerApi();
let nifCif = "nifCif_example"; // String | 
apiInstance.obtenerCliente(nifCif, (error, data, response) => {
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
 **nifCif** | **String**|  | 

### Return type

[**ClienteAppHistorico**](ClienteAppHistorico.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## obtenerClientes

> ClienteAppHistorico obtenerClientes()

Obtener todos los clientes

Devuelve la lista completa de clientes en el historico

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ClienteAppHistoricoControllerApi();
apiInstance.obtenerClientes((error, data, response) => {
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

[**ClienteAppHistorico**](ClienteAppHistorico.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

