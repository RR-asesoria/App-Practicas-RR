package org.gestoriarr.appgestoriarr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gestoriarr.appgestoriarr.model.enums.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClienteAppHistorico {

    private String nifCif;
    private List<String> nifHistorico;
    private String nifAnterior;
    private String nombre;
    private Date fechaNacimiento;
    private String telefono;
    private String correoElectronico;
    private String referencia;
    private String numerosCC;
    private boolean datosFiscalesDescargados;
    private String importe;
    private TipoFacturado tipoFacturado;
    private TipoRecogidaDatos recogidaDatos;
    private boolean excelDatosElaboracion;
    private TipoBorrador borrador;
    private TipoPresentada presentada;
    private String cobrado;
    private TipoCliente tipoCliente;
    private EstadoCliente estadoCliente;
    private String casilla505Actual;
    private String anioFiscal;
}
