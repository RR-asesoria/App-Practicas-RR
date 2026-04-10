# OpenApiDefinition.UserControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**actualizar**](UserControllerApi.md#actualizar) | **PUT** /user/actualizarusuario/{uid} | 
[**admin**](UserControllerApi.md#admin) | **GET** /user/test | 
[**cambiarPassword**](UserControllerApi.md#cambiarPassword) | **PUT** /user/actualizarpassword/{uid} | 
[**crearUsuario**](UserControllerApi.md#crearUsuario) | **POST** /user/crearusuario | 
[**eliminarUsuario**](UserControllerApi.md#eliminarUsuario) | **DELETE** /user/eliminarusuario/{uid} | 
[**encontrarPorEmail**](UserControllerApi.md#encontrarPorEmail) | **GET** /user/email/{email} | 
[**encontrarPorId**](UserControllerApi.md#encontrarPorId) | **GET** /user/id/{uid} | 
[**encontrarPorNombre**](UserControllerApi.md#encontrarPorNombre) | **GET** /user/nombre/{nombre} | 
[**obtenerTodos**](UserControllerApi.md#obtenerTodos) | **GET** /user/todoslosusuarios | 



## actualizar

> String actualizar(uid, usuarioActualizarDTO)



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
let uid = "uid_example"; // String | 
let usuarioActualizarDTO = new OpenApiDefinition.UsuarioActualizarDTO(); // UsuarioActualizarDTO | 
apiInstance.actualizar(uid, usuarioActualizarDTO, (error, data, response) => {
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
 **uid** | **String**|  | 
 **usuarioActualizarDTO** | [**UsuarioActualizarDTO**](UsuarioActualizarDTO.md)|  | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*


## admin

> String admin()



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
apiInstance.admin((error, data, response) => {
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


## cambiarPassword

> String cambiarPassword(uid, cambioPasswordDTO)



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
let uid = "uid_example"; // String | 
let cambioPasswordDTO = new OpenApiDefinition.CambioPasswordDTO(); // CambioPasswordDTO | 
apiInstance.cambiarPassword(uid, cambioPasswordDTO, (error, data, response) => {
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
 **uid** | **String**|  | 
 **cambioPasswordDTO** | [**CambioPasswordDTO**](CambioPasswordDTO.md)|  | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*


## crearUsuario

> String crearUsuario(usuarioCreacionDTO)



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
let usuarioCreacionDTO = new OpenApiDefinition.UsuarioCreacionDTO(); // UsuarioCreacionDTO | 
apiInstance.crearUsuario(usuarioCreacionDTO, (error, data, response) => {
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
 **usuarioCreacionDTO** | [**UsuarioCreacionDTO**](UsuarioCreacionDTO.md)|  | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*


## eliminarUsuario

> String eliminarUsuario(uid)



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
let uid = "uid_example"; // String | 
apiInstance.eliminarUsuario(uid, (error, data, response) => {
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
 **uid** | **String**|  | 

### Return type

**String**

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## encontrarPorEmail

> UsuarioRespuestaDTO encontrarPorEmail(email)



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
let email = "email_example"; // String | 
apiInstance.encontrarPorEmail(email, (error, data, response) => {
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
 **email** | **String**|  | 

### Return type

[**UsuarioRespuestaDTO**](UsuarioRespuestaDTO.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## encontrarPorId

> UsuarioRespuestaDTO encontrarPorId(uid)



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
let uid = "uid_example"; // String | 
apiInstance.encontrarPorId(uid, (error, data, response) => {
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
 **uid** | **String**|  | 

### Return type

[**UsuarioRespuestaDTO**](UsuarioRespuestaDTO.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## encontrarPorNombre

> UsuarioRespuestaDTO encontrarPorNombre(nombre)



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
let nombre = "nombre_example"; // String | 
apiInstance.encontrarPorNombre(nombre, (error, data, response) => {
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
 **nombre** | **String**|  | 

### Return type

[**UsuarioRespuestaDTO**](UsuarioRespuestaDTO.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*


## obtenerTodos

> [UsuarioRespuestaDTO] obtenerTodos()



### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.UserControllerApi();
apiInstance.obtenerTodos((error, data, response) => {
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

[**[UsuarioRespuestaDTO]**](UsuarioRespuestaDTO.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

