# OpenApiDefinition.ExcelControllerApi

All URIs are relative to *http://localhost:8080*

Method | HTTP request | Description
------------- | ------------- | -------------
[**importar**](ExcelControllerApi.md#importar) | **POST** /api/clientes/excel/importar | Importar clientes desde Excel



## importar

> ExcelImportResponseDTO importar(file)

Importar clientes desde Excel

Lee un archivo .xlsx y crea los clientes en la base de datos

### Example

```javascript
import OpenApiDefinition from 'open_api_definition';
let defaultClient = OpenApiDefinition.ApiClient.instance;
// Configure Bearer (JWT) access token for authorization: Bearer Auth
let Bearer Auth = defaultClient.authentications['Bearer Auth'];
Bearer Auth.accessToken = "YOUR ACCESS TOKEN"

let apiInstance = new OpenApiDefinition.ExcelControllerApi();
let file = "/path/to/file"; // File | 
apiInstance.importar(file, (error, data, response) => {
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
 **file** | **File**|  | 

### Return type

[**ExcelImportResponseDTO**](ExcelImportResponseDTO.md)

### Authorization

[Bearer Auth](../README.md#Bearer Auth)

### HTTP request headers

- **Content-Type**: multipart/form-data
- **Accept**: */*

