package org.gestoriarr.appgestoriarr.model;

import lombok.*;
import org.gestoriarr.appgestoriarr.model.enums.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder


public class ClienteApp {

    private String nombre;
    private String nifCif;
    private Date fechaNacimiento;
    private String referencia;
    private String casilla505anterior;
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

}
