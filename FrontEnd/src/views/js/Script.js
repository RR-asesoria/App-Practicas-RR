// ===== FIREBASE CONFIG =====
const firebaseConfig = {
    apiKey: "AIzaSyDKSKYSKcO6Gw0IkXEskYMFeYQBEohqAFk",
    authDomain: "appgestoriarr-a5638.firebaseapp.com",
    projectId: "appgestoriarr-a5638"
};

firebase.initializeApp(firebaseConfig);

// ===== HELPER: fetch con token =====
async function fetchConToken(url, options = {}) {
    const token = localStorage.getItem('token');
    return fetch(url, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            ...options.headers
        }
    });
}

// ===== LOGOUT =====
function logout() {
    firebase.auth().signOut();
    localStorage.clear();
    window.location.href = "../html/index.html";
}


// ===== ELIMINAR CLIENTE =====
async function eliminarCliente(nifCif, nombre) {
    if (!confirm(`¿Seguro que deseas eliminar a "${nombre}"?`)) return;

    try {
        const response = await fetchConToken(`http://localhost:8080/api/clientes/eliminarcliente/${nifCif}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error("Error al eliminar");
        alert("Cliente eliminado correctamente");
        invalidarCache();
        cargarClientes();
    } catch (error) {
        console.error(error);
        alert("Error al eliminar el cliente");
    }
}

// ===== TRADUCIR ENUMS =====
function traducirEnum(valor) {
    const traducciones = {
        // EstadoCliente
        'CONTACTADONO': 'No Contactado',
        'CONTACTADOSISEHACENO': 'Contactado - No se hace',
        'CONTACTADOSISEHACESI': 'Contactado - Se hace',
        'CONTACTADOSISEHACEPENDIENTE': 'Contactado - Pendiente',

        // TipoFacturado
        'FACTURADONO': 'No Facturado',
        'FACTIRADOSIENVIADANO': 'Facturado - Enviada No',
        'FACTURADOSIENVIADASI': 'Facturado - Enviada Sí',

        // TipoRecogidaDatos
        'FACTURARELLENANO': 'No Rellenada',
        'FACTURARELLENASIENVIADANO': 'Rellenada - Enviada No',
        'FACTURARELLENASIENVIADASIFIRMADANO': 'Rellenada - Enviada Sí - Firmada No',
        'FACTURARELLENASIENVIADASIFIRMADASI': 'Rellenada - Enviada Sí - Firmada Sí',

        // TipoBorrador
        'BORRADORCREADONO': 'No Creado',
        'BORRADORCREADOSIENVIADONO': 'Creado - Enviado No',
        'BORRADORCREADOSIENVIADOSI': 'Creado - Enviado Sí',

        // TipoPresentada
        'CONFIRMADOPRESENTARNO': 'No Presentada',
        'CONFIRMADOPRESENTARSIPRESENTADANO': 'Confirmada - Presentada No',
        'CONFIRMADOPRESENTARSIPRESENTADASIENVIADANO': 'Confirmada - Presentada Sí - Enviada No',
        'CONFIRMADOPRESENTARSIPRESENTADASIENVIADASI': 'Confirmada - Presentada Sí - Enviada Sí',

        // TipoCliente
        'AUTONOMO': 'Autónomo',
        'PARTICULARES': 'Particulares',
        'NORESIDENTE': 'No Residente',

        // Cobrado
        'NO': 'No',
        'SI': 'Sí'
    };

    return traducciones[valor] ?? valor ?? '-';
}
// ===== CACHÉ DE CLIENTES =====
let clientesCache = null;
let cacheTimestamp = null;
const CACHE_DURACION = 5 * 60 * 1000; // 5 minutos

function cacheValida() {
    return clientesCache !== null &&
           cacheTimestamp !== null &&
           (Date.now() - cacheTimestamp) < CACHE_DURACION;
}

function invalidarCache() {
    clientesCache = null;
    cacheTimestamp = null;
}

// ===== CARGAR CLIENTES =====
async function cargarClientes() {
    try {
        const nombre = document.getElementById('filtroNombre')?.value;
        const dni = document.getElementById('filtroDni')?.value;
        const tipoCliente = document.getElementById('filtroTipoCliente')?.value;
        const estadoCliente = document.getElementById('filtroEstadoCliente')?.value;
        const tipoFacturado = document.getElementById('filtroTipoFacturado')?.value;
        const recogidaDatos = document.getElementById('filtroRecogidaDatos')?.value;
        const borrador = document.getElementById('filtroBorrador')?.value;
        const presentada = document.getElementById('filtroPresentada')?.value;
        const cobrado = document.getElementById('filtroCobrado')?.value;
        const datosFiscales = document.getElementById('filtroDatosFiscales')?.value;
        const excelElaboracion = document.getElementById('filtroExcelElaboracion')?.value;

        const filtros = {};
        if (nombre) filtros.nombre = nombre;
        if (dni) filtros.nifCif = dni;
        if (tipoCliente) filtros.tipoCliente = tipoCliente;
        if (estadoCliente) filtros.estadoCliente = estadoCliente;
        if (tipoFacturado) filtros.tipoFacturado = tipoFacturado;
        if (recogidaDatos) filtros.recogidaDatos = recogidaDatos;
        if (borrador) filtros.borrador = borrador;
        if (presentada) filtros.presentada = presentada;
        if (cobrado) filtros.cobrado = cobrado;
        if (datosFiscales) filtros.datosFiscalesDescargados = datosFiscales === 'true';
        if (excelElaboracion) filtros.excelDatosElaboracion = excelElaboracion === 'true';

        const hayFiltros = Object.keys(filtros).length > 0;
        let clientes;

        if (hayFiltros) {
            const response = await fetchConToken('http://localhost:8080/api/clientes/buscarporfiltros', {
                method: 'POST',
                body: JSON.stringify(filtros)
            });
            if (!response.ok) throw new Error("Error al obtener clientes");
            clientes = await response.json();
        } else {
            if (cacheValida()) {
                clientes = clientesCache;
            } else {
                const response = await fetchConToken('http://localhost:8080/api/clientes/obtenerTodos');
                if (!response.ok) throw new Error("Error al obtener clientes");
                clientes = await response.json();
                clientesCache = clientes;
                cacheTimestamp = Date.now();
            }
        }

        clientes.sort((a, b) => (a.nombre ?? '').localeCompare(b.nombre ?? '', 'es'));
        renderizarConPaginacion(clientes);

    } catch (error) {
        console.error("Error al cargar clientes:", error);
        alert("Error al cargar los clientes");
    }
}

function renderizarConPaginacion(clientes) {
    const pageSize = 10;
    let paginaActual = 0;

    function renderPagina() {
        const tabla = document.getElementById('tablaClientes');
        if (!tabla) return;
        tabla.innerHTML = '';

        const inicio = paginaActual * pageSize;
        const pagina = clientes.slice(inicio, inicio + pageSize);

        pagina.forEach(cliente => {
            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td>${cliente.nombre ?? ''}</td>
                <td>${cliente.nifCif ?? ''}</td>
                <td>${cliente.telefono ?? ''}</td>
                <td>${cliente.correoElectronico ?? ''}</td>
                <td>${cliente.fechaNacimiento ? new Date(cliente.fechaNacimiento).toLocaleDateString('es-ES') : ''}</td>
                <td>${traducirEnum(cliente.tipoCliente)}</td>
                <td>${traducirEnum(cliente.estadoCliente)}</td>
                <td>${cliente.importe ?? ''}</td>
                <td>${traducirEnum(cliente.cobrado)}</td>
                <td>
                    <a href="editarCliente.html?id=${cliente.nifCif}&modo=editar" class="btn-editar">
                        <i class="fa-solid fa-pen"></i> Editar
                    </a>
                    <button class="btn-eliminar" title="Eliminar cliente">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            `;

            const filaDetalle = document.createElement('tr');
            filaDetalle.classList.add('fila-detalle');
            filaDetalle.style.display = 'none';
            filaDetalle.innerHTML = `
                <td colspan="10" style="padding:0; border-bottom: 2px solid var(--color-primary);">
                    <div style="display:grid; grid-template-columns: repeat(4, 1fr); gap:0; background: var(--color-card); border-top: 1px solid var(--color-border);">
                        ${[
                            ['Referencia', cliente.referencia],
                            ['Casilla 505 Anterior', cliente.casilla505anterior],
                            ['Casilla 505 Actual', cliente.casilla505Actual],
                            ['Números CC', cliente.numerosCC],
                            ['Datos Fiscales', cliente.datosFiscalesDescargados ? 'Sí' : 'No'],
                            ['Excel Elaboración', cliente.excelDatosElaboracion ? 'Sí' : 'No'],
                            ['Tipo Facturado', traducirEnum(cliente.tipoFacturado)],
                            ['Recogida Datos', traducirEnum(cliente.recogidaDatos)],
                            ['Borrador', traducirEnum(cliente.borrador)],
                            ['Presentada', traducirEnum(cliente.presentada)],
                            ['NIF Anterior', cliente.nifAnterior],
                            ['NIF Histórico', cliente.nifHistorico?.join(', ')]
                        ].map(([label, valor]) => `
                            <div style="padding: 10px 16px; border-right: 1px solid var(--color-border); border-bottom: 1px solid var(--color-border);">
                                <div style="font-size:11px; text-transform:uppercase; letter-spacing:0.5px; color:#888; margin-bottom:3px;">${label}</div>
                                <div style="font-size:13px; font-weight:500; color:var(--color-text);">${valor ?? '-'}</div>
                            </div>
                        `).join('')}
                    </div>
                </td>
            `;

            fila.style.cursor = 'pointer';
            fila.addEventListener('click', (e) => {
                if (e.target.closest('.btn-editar') || e.target.closest('.btn-eliminar')) return;
                const visible = filaDetalle.style.display !== 'none';
                filaDetalle.style.display = visible ? 'none' : 'table-row';
                fila.style.background = visible ? '' : 'var(--button-hover-bg)';
            });

            fila.querySelector('.btn-eliminar').addEventListener('click', (e) => {
                e.stopPropagation();
                const aviso = `⚠️ ATENCIÓN\n\nVas a eliminar a "${cliente.nombre}" (${cliente.nifCif}).\n\n¿Confirmas la eliminación?`;
                if (confirm(aviso)) eliminarCliente(cliente.nifCif, cliente.nombre);
            });

            tabla.appendChild(fila);
            tabla.appendChild(filaDetalle);
        });

        // Controles de paginación
        let controles = document.getElementById('paginacion-clientes');
        if (!controles) {
            controles = document.createElement('div');
            controles.id = 'paginacion-clientes';
            controles.style.cssText = 'display:flex; justify-content:center; align-items:center; gap:6px; padding:16px;';
            document.querySelector('.consultasActuales-tabla-container')?.after(controles);
        }

        const totalPaginas = Math.ceil(clientes.length / pageSize);
        controles.innerHTML = '';

        const btnAnterior = document.createElement('button');
        btnAnterior.innerHTML = '← Anterior';
        btnAnterior.disabled = paginaActual === 0;
        btnAnterior.style.cssText = `
            padding: 6px 12px; border-radius: 6px; cursor: pointer; font-size: 13px;
            border: 1px solid var(--color-border);
            background: ${paginaActual === 0 ? 'var(--button-bg)' : 'var(--color-primary)'};
            color: ${paginaActual === 0 ? '#aaa' : 'white'};
            opacity: ${paginaActual === 0 ? '0.5' : '1'};
        `;
        btnAnterior.addEventListener('click', () => {
            if (paginaActual > 0) { paginaActual--; renderPagina(); }
        });

        const indicador = document.createElement('span');
        indicador.style.cssText = 'font-size:13px; color:var(--color-text); padding: 0 8px;';
        indicador.textContent = `Página ${paginaActual + 1} de ${totalPaginas} (${clientes.length} clientes)`;

        const btnSiguiente = document.createElement('button');
        btnSiguiente.innerHTML = 'Siguiente →';
        btnSiguiente.disabled = paginaActual >= totalPaginas - 1;
        btnSiguiente.style.cssText = `
            padding: 6px 12px; border-radius: 6px; cursor: pointer; font-size: 13px;
            border: 1px solid var(--color-border);
            background: ${paginaActual >= totalPaginas - 1 ? 'var(--button-bg)' : 'var(--color-primary)'};
            color: ${paginaActual >= totalPaginas - 1 ? '#aaa' : 'white'};
            opacity: ${paginaActual >= totalPaginas - 1 ? '0.5' : '1'};
        `;
        btnSiguiente.addEventListener('click', () => {
            if (paginaActual < totalPaginas - 1) { paginaActual++; renderPagina(); }
        });

        controles.appendChild(btnAnterior);
        controles.appendChild(indicador);
        controles.appendChild(btnSiguiente);
    }

    renderPagina();
}

function actualizarBotonesPaginacion() {
    let contenedor = document.getElementById('paginacion-clientes');
    if (!contenedor) {
        contenedor = document.createElement('div');
        contenedor.id = 'paginacion-clientes';
        contenedor.style.cssText = 'display:flex; justify-content:center; gap:8px; padding:12px;';
        document.querySelector('.consultasActuales-tabla-container')?.after(contenedor);
    }

    contenedor.innerHTML = '';

    if (hayMasPaginas) {
        const btnSiguiente = document.createElement('button');
        btnSiguiente.textContent = 'Siguiente →';
        btnSiguiente.style.cssText = 'padding:8px 16px; border-radius:6px; border:1px solid var(--color-border); background:var(--color-primary); color:white; cursor:pointer; font-size:13px;';
        btnSiguiente.addEventListener('click', () => cargarClientes(false));
        contenedor.appendChild(btnSiguiente);
    }
}

async function cargarHistorico() {
    try {
        const anioSeleccionado = document.getElementById('filtroCampania')?.value;
        const nombreFiltro = document.querySelector('.consultas-input[placeholder="Nombre"]')?.value;
        const dniFiltro = document.querySelector('.consultas-input[placeholder="DNI / NIE"]')?.value;
        const tipoCliente = document.getElementById('filtroTipoCliente')?.value;
        const estadoCliente = document.getElementById('filtroEstadoCliente')?.value;
        const tipoFacturado = document.getElementById('filtroTipoFacturado')?.value;
        const recogidaDatos = document.getElementById('filtroRecogidaDatos')?.value;
        const borrador = document.getElementById('filtroBorrador')?.value;
        const presentada = document.getElementById('filtroPresentada')?.value;
        const cobrado = document.getElementById('filtroCobrado')?.value;
        const datosFiscales = document.getElementById('filtroDatosFiscales')?.value;
        const excelElaboracion = document.getElementById('filtroExcelElaboracion')?.value;

        const filtros = {};
        if (anioSeleccionado) filtros.anioFiscal = anioSeleccionado;
        if (nombreFiltro) filtros.nombre = nombreFiltro;
        if (dniFiltro) filtros.nifCif = dniFiltro;
        if (tipoCliente) filtros.tipoCliente = tipoCliente;
        if (estadoCliente) filtros.estadoCliente = estadoCliente;
        if (tipoFacturado) filtros.tipoFacturado = tipoFacturado;
        if (recogidaDatos) filtros.recogidaDatos = recogidaDatos;
        if (borrador) filtros.borrador = borrador;
        if (presentada) filtros.presentada = presentada;
        if (cobrado) filtros.cobrado = cobrado;
        if (datosFiscales) filtros.datosFiscalesDescargados = datosFiscales === 'true';
        if (excelElaboracion) filtros.excelDatosElaboracion = excelElaboracion === 'true';

        let clientes;

        if (Object.keys(filtros).length > 0) {
            const response = await fetchConToken('http://localhost:8080/api/clientesHistorico/buscar', {
                method: 'POST',
                body: JSON.stringify(filtros)
            });
            if (!response.ok) throw new Error("Error al obtener histórico");
            clientes = await response.json();
        } else {
            const response = await fetchConToken('http://localhost:8080/api/clientesHistorico');
            if (!response.ok) throw new Error("Error al obtener histórico");
            clientes = await response.json();
        }

        const tabla = document.getElementById('tablaHistorico');
        if (!tabla) return;
        tabla.innerHTML = '';

     clientes.forEach(cliente => {
         const fila = document.createElement('tr');
         fila.innerHTML = `
             <td>${cliente.nombre ?? ''}</td>
             <td>${cliente.nifCif ?? ''}</td>
             <td>${cliente.telefono ?? ''}</td>
             <td>${cliente.correoElectronico ?? ''}</td>
             <td>${cliente.anioFiscal ?? ''}</td>
             <td>${cliente.tipoCliente ?? ''}</td>
             <td>${cliente.estadoCliente ?? ''}</td>
             <td>${cliente.importe ?? ''}</td>
             <td>${cliente.cobrado ?? ''}</td>
         `;

         // ===== FILA DETALLE =====
         const filaDetalle = document.createElement('tr');
         filaDetalle.classList.add('fila-detalle');
         filaDetalle.style.display = 'none';

filaDetalle.innerHTML = `
    <td colspan="10" style="padding:0; border-bottom: 2px solid var(--color-primary);">
        <div style="
            display:grid;
            grid-template-columns: repeat(4, 1fr);
            gap:0;
            background: var(--color-card);
            border-top: 1px solid var(--color-border);
        ">
            ${[
                ['Referencia', cliente.referencia],
                ['NIF Anterior', cliente.nifAnterior],
                ['NIF Histórico', cliente.nifHistorico?.join(', ')],
                ['Fecha Nacimiento', cliente.fechaNacimiento ? new Date(cliente.fechaNacimiento).toLocaleDateString('es-ES') : null],
                ['Números CC', cliente.numerosCC],
                ['Datos Fiscales', cliente.datosFiscalesDescargados ? 'Sí' : 'No'],
                ['Excel Elaboración', cliente.excelDatosElaboracion ? 'Sí' : 'No'],
                ['Tipo Facturado', traducirEnum(cliente.tipoFacturado)],
                ['Recogida Datos', traducirEnum(cliente.recogidaDatos)],
                ['Borrador', traducirEnum(cliente.borrador)],
                ['Presentada', traducirEnum(cliente.presentada)],
                ['Casilla 505 Actual', cliente.casilla505Actual],
            ].map(([label, valor]) => `
                <div style="
                    padding: 10px 16px;
                    border-right: 1px solid var(--color-border);
                    border-bottom: 1px solid var(--color-border);
                ">
                    <div style="font-size:11px; text-transform:uppercase; letter-spacing:0.5px; color:#888; margin-bottom:3px;">${label}</div>
                    <div style="font-size:13px; font-weight:500; color:var(--color-text);">${valor ?? '-'}</div>
                </div>
            `).join('')}
        </div>
    </td>
`;

         // ===== TOGGLE =====.
         fila.style.cursor = 'pointer';
         fila.addEventListener('click', (e) => {
             if (e.target.closest('.btn-editar')) return;

             const visible = filaDetalle.style.display !== 'none';
             filaDetalle.style.display = visible ? 'none' : 'table-row';
             fila.style.background = visible ? '' : 'var(--button-hover-bg)';
         });

         tabla.appendChild(fila);
         tabla.appendChild(filaDetalle);
     });
    } catch (error) {
        console.error("Error al cargar histórico:", error);
        alert("Error al cargar el histórico");
    }
}

async function inicializarSelectAnios() {
    try {
        const response = await fetchConToken('http://localhost:8080/api/clientesHistorico/anios');
        if (!response.ok) return;
        const anios = await response.json();

        const selectAnio = document.getElementById('filtroCampania');
        if (!selectAnio) return;

        selectAnio.innerHTML = '<option value="">Selecciona un año</option>';
        anios.forEach(anio => {
            const option = document.createElement('option');
            option.value = anio;
            option.textContent = anio;
            selectAnio.appendChild(option);
        });
    } catch (error) {
        console.error("Error al cargar años:", error);
    }
}


// ===== APP =====
class App {

    constructor() {
        this.body = document.body;
        this.init();
    }

    init() {
        this.initDarkMode();

        if (this.body.classList.contains("login-page")) {
            this.initLogin();
        }
        if (this.body.classList.contains("password-page")) {
            this.initCambiarPassword();
        }
        if (this.body.classList.contains("eliminar-page")) {
            this.initEliminarUsuario();
        }
        if (this.body.classList.contains("agregarUsuario-page")) {
            this.initAgregarUsuario();
        }
        if (this.body.classList.contains("menu-page")) {
            this.initMenuPrincipal();
        }
        if (this.body.classList.contains("agregarCliente-page")) {
            this.initAgregarCliente();
        }
        if (this.body.classList.contains("consultarTablas-page")) {
            this.initConsultarTablas();
        }
        if (this.body.classList.contains("historicoTablas-page")) {
            this.initHistorico();
        }
        if (this.body.classList.contains("cambiarNif-page")) {
            this.initCambiarNif();
        }

        if (this.body.classList.contains("editarCliente-page")) {
            this.initEditarCliente();
        }
    }


//cambiar nif
initCambiarNif() {
//    this.cargarClientesCambiarNif(); se quita para que no se cargue automaticamente y no se consuman querys

    const btnBuscar = document.getElementById('btnBuscar');
    if (btnBuscar) {
        btnBuscar.addEventListener('click', () => this.cargarClientesCambiarNif());
    }

    document.getElementById('buscarNombre')?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') this.cargarClientesCambiarNif();
    });
    document.getElementById('buscarDni')?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') this.cargarClientesCambiarNif();
    });
}

async cargarClientesCambiarNif() {
    try {
        const nombre = document.getElementById('buscarNombre')?.value;
        const dni = document.getElementById('buscarDni')?.value;

        const filtros = {};
        if (nombre) filtros.nombre = nombre;
        if (dni) filtros.nifCif = dni;

        let clientes;

        if (Object.keys(filtros).length > 0) {
            const response = await fetchConToken('http://localhost:8080/api/clientes/buscarporfiltros', {
                method: 'POST',
                body: JSON.stringify(filtros)
            });
            if (!response.ok) throw new Error("Error al buscar clientes");
            clientes = await response.json();
        } else {
            const response = await fetchConToken('http://localhost:8080/api/clientes/obtenerTodos');
            if (!response.ok) throw new Error("Error al obtener clientes");
            clientes = await response.json();
        }

        const tabla = document.getElementById('tablaClientes');
        if (!tabla) return;
        tabla.innerHTML = '';

        clientes.forEach(cliente => {
            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td>${cliente.nombre ?? ''}</td>
                <td>${cliente.nifCif ?? ''}</td>
                <td>
                    <button class="btn-editar btn-cambiar-nif">
                        <i class="fa-solid fa-pen"></i> Cambiar NIF
                    </button>
                </td>
            `;

            fila.querySelector('.btn-cambiar-nif').addEventListener('click', () => {
                this.ejecutarCambioNif(cliente.nifCif, cliente.nombre);
            });

            tabla.appendChild(fila);
        });

    } catch (error) {
        console.error(error);
        alert("Error al cargar clientes");
    }
}

async ejecutarCambioNif(nifViejo, nombre) {
    const aviso = `⚠️ Vas a cambiar el NIF/CIF de "${nombre}" (${nifViejo}).\n\nRecuerda revisar el Excel y el CRM tras este cambio para evitar inconsistencias.\n\nEscribe el nuevo NIF/CIF:`;
    const nifNuevo = prompt(aviso);

    if (nifNuevo === null) return;

    if (!nifNuevo.trim()) {
        alert("El nuevo NIF/CIF no puede estar vacío.");
        return;
    }

    const confirmacion = confirm(`¿Confirmas cambiar el NIF de "${nombre}" de ${nifViejo} a ${nifNuevo}?`);
    if (!confirmacion) return;

    try {
        const response = await fetchConToken(
            `http://localhost:8080/api/clientes/cambiar-nif?nifViejo=${encodeURIComponent(nifViejo)}&nifNuevo=${encodeURIComponent(nifNuevo.trim())}`, {
            method: 'PUT'
        });

        const mensaje = await response.text();

        if (!response.ok) {
            alert("Error: " + "Los usuarios sin permisos de administrador no pueden realizar esta acción, consulta con tu administrador");
            return;
        }

        alert(mensaje);
        this.cargarClientesCambiarNif();

    } catch (error) {
        console.error(error);
        alert("Error al cambiar el NIF: " + error.message);
    }
}


    // ===== LOGIN =====
    initLogin() {
        const botonLogin = document.querySelector(".login-aceptar");
        if (!botonLogin) return;

        const handleLogin = async () => {
            const email = document.querySelector(".login-usuario")?.value;
            const password = document.querySelector(".login-password")?.value;

            try {
                const userCredential = await firebase.auth().signInWithEmailAndPassword(email, password);
                const token = await userCredential.user.getIdToken();
                localStorage.setItem('token', token);
                window.location.href = "../html/menu.html";
            } catch (error) {
                console.error("Error al iniciar sesión:", error);
                alert("Usuario o contraseña incorrectos");
            }
        };

        botonLogin.addEventListener("click", handleLogin);

        document.querySelector(".login-usuario")?.addEventListener("keydown", (e) => {
            if (e.key === "Enter") handleLogin();
        });
        document.querySelector(".login-password")?.addEventListener("keydown", (e) => {
            if (e.key === "Enter") handleLogin();
        });

        const toggle = document.getElementById("togglePassword");
        const input = document.getElementById("password");

        if (toggle && input) {
            toggle.addEventListener("click", () => {
                const isPassword = input.type === "password";
                input.type = isPassword ? "text" : "password";
                toggle.classList.toggle("fa-eye");
                toggle.classList.toggle("fa-eye-slash");
            });
        }
    }

    // ===== HISTORICO =====
initHistorico() {
    inicializarSelectAnios(); // ← solo carga los años, sin cargar clientes

    const selectAnio = document.getElementById('filtroCampania');
    if (selectAnio) {
        selectAnio.addEventListener('change', () => {
            if (selectAnio.value) cargarHistorico();
        });
    }

    const botonBuscar = document.querySelector(".consultas-aceptar");
    if (botonBuscar) {
        botonBuscar.addEventListener("click", () => cargarHistorico());
    }

    document.querySelector('.consultas-input[placeholder="Nombre"]')?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') cargarHistorico();
    });
    document.querySelector('.consultas-input[placeholder="DNI / NIE"]')?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') cargarHistorico();
    });

    const btnAvanzados = document.getElementById('btnFiltrosAvanzados');
    const panel = document.getElementById('panelFiltrosAvanzados');
    if (btnAvanzados && panel) {
        btnAvanzados.addEventListener("click", () => {
            const visible = panel.style.display !== 'none';
            panel.style.display = visible ? 'none' : 'flex';
            btnAvanzados.textContent = visible ? 'Filtros avanzados ▼' : 'Filtros avanzados ▲';
        });
    }
}

    // ===== MENU PRINCIPAL =====
  initMenuPrincipal() {
      const resetButton = document.getElementById("resetButton");
      const fileInput = document.getElementById("excelFile");
      const importButton = document.getElementById("importButton");

    if (resetButton) {
        resetButton.addEventListener("click", async () => {
            const confirmacion = prompt("Esta acción es irreversible. Escribe ACEPTAR para confirmar el cierre de ejercicio:");

            if (confirmacion === null) return; // canceló

            if (confirmacion !== "ACEPTAR") {
                alert("Texto incorrecto. Debes escribir exactamente ACEPTAR. El uso de mayúsculas es necesario.");
                return;
            }

            // Segunda confirmación
            const seguro = confirm("¿Seguro que quieres realizar esta acción?");

            if (!seguro) return; // usuario canceló aquí

            try {
                const response = await fetchConToken(
                    "http://localhost:8080/api/clientes/cierre-ejercicio", {
                    method: "POST"
                });

                if (!response.ok) throw new Error("Error en el cierre de ejercicio");

                alert("Cierre de ejercicio realizado correctamente.");
            } catch (error) {
                console.error(error);
                alert("Error al realizar el cierre: " + error.message);
            }
        });
    }

      if (importButton) {
          importButton.addEventListener("click", () => fileInput.click());
      }
      if (fileInput) {
          fileInput.addEventListener("change", (e) => this.handleFile(e));
      }
  }

    // ===== AGREGAR CLIENTE =====
    initAgregarCliente() {
        const form = document.querySelector(".cliente-form");
        if (!form) return;

        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            let cliente = {
                nombre: document.getElementById("nombre").value || null,
                nifCif: document.getElementById("nifCif").value || null,
                telefono: document.getElementById("telefono").value || null,
                correoElectronico: document.getElementById("correoElectronico").value || null,
                fechaNacimiento: document.getElementById("fechaNacimiento").value || null,
                referencia: document.getElementById("referencia").value || null,
                casilla505anterior: document.getElementById("casilla505anterior").value || null,
                numerosCC: document.getElementById("numerosCC").value || null,
                datosFiscalesDescargados: document.getElementById("datosFiscalesDescargados").value === "true",
                importe: document.getElementById("importe").value || "0",
                tipoFacturado: document.getElementById("tipoFacturado").value || null,
                recogidaDatos: document.getElementById("recogidaDatos").value || null,
                excelDatosElaboracion: document.getElementById("excelDatosElaboracion").value === "true",
                borrador: document.getElementById("borrador").value || null,
                presentada: document.getElementById("presentada").value || null,
                cobrado: document.getElementById("cobrado").value || "NO",
                tipoCliente: document.getElementById("tipoCliente").value || null,
                estadoCliente: document.getElementById("estadoCliente").value || null,
                casilla505Actual: document.getElementById("casilla505Actual").value || null
            };

            try {
                const response = await fetchConToken("http://localhost:8080/api/clientes/crearcliente", {
                    method: "POST",
                    body: JSON.stringify(cliente)
                });

                if (!response.ok) throw new Error("Error al guardar cliente");

                alert("Cliente guardado correctamente");
                window.location.href = "../html/campaniaActual.html";
            } catch (error) {
                console.error(error);
                alert("Error al guardar cliente");
            }
        });
    }



// ===== EDITAR CLIENTE =====
async initEditarCliente() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (!id) return;

    try {
        const response = await fetchConToken(`http://localhost:8080/api/clientes/obtenerpornif/${id}`);
        if (!response.ok) throw new Error("Error al obtener cliente");
        const cliente = await response.json();

        document.getElementById('nombre').value            = cliente.nombre ?? '';
        const nifInput = document.getElementById('nifCif');
        nifInput.value = cliente.nifCif ?? '';
        nifInput.setAttribute('readonly', true);
        document.getElementById('telefono').value          = cliente.telefono ?? '';
        document.getElementById('correoElectronico').value = cliente.correoElectronico ?? '';
        document.getElementById('fechaNacimiento').value   = cliente.fechaNacimiento?.split('T')[0] ?? '';
        document.getElementById('referencia').value        = cliente.referencia ?? '';
        document.getElementById('casilla505anterior').value = cliente.casilla505anterior ?? '';
        document.getElementById('importe').value           = cliente.importe ?? '';
        document.getElementById('casilla505Actual').value  = cliente.casilla505Actual ?? '';
        document.getElementById('datosFiscalesDescargados').value = cliente.datosFiscalesDescargados ? 'true' : 'false';
        document.getElementById('excelDatosElaboracion').value    = cliente.excelDatosElaboracion ? 'true' : 'false';
        document.getElementById('tipoFacturado').value     = cliente.tipoFacturado ?? '';
        document.getElementById('recogidaDatos').value     = cliente.recogidaDatos ?? '';
        document.getElementById('borrador').value          = cliente.borrador ?? '';
        document.getElementById('presentada').value        = cliente.presentada ?? '';
        document.getElementById('cobrado').value           = cliente.cobrado ?? '';
        document.getElementById('tipoCliente').value       = cliente.tipoCliente ?? '';
        document.getElementById('estadoCliente').value     = cliente.estadoCliente ?? '';

    } catch (error) {
        console.error(error);
        alert("Error al cargar los datos del cliente");
    }

    const form = document.querySelector(".cliente-form");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const clienteActualizado = {
            nombre: document.getElementById("nombre").value || null,
            nifCif: document.getElementById("nifCif").value || null,
            telefono: document.getElementById("telefono").value || null,
            correoElectronico: document.getElementById("correoElectronico").value || null,
            fechaNacimiento: document.getElementById("fechaNacimiento").value || null,
            referencia: document.getElementById("referencia").value || null,
            casilla505anterior: document.getElementById("casilla505anterior").value || null,
            datosFiscalesDescargados: document.getElementById("datosFiscalesDescargados").value === "true",
            importe: document.getElementById("importe").value || "0",
            tipoFacturado: document.getElementById("tipoFacturado").value || null,
            recogidaDatos: document.getElementById("recogidaDatos").value || null,
            excelDatosElaboracion: document.getElementById("excelDatosElaboracion").value === "true",
            borrador: document.getElementById("borrador").value || null,
            presentada: document.getElementById("presentada").value || null,
            cobrado: document.getElementById("cobrado").value || "NO",
            tipoCliente: document.getElementById("tipoCliente").value || null,
            estadoCliente: document.getElementById("estadoCliente").value || null,
            casilla505Actual: document.getElementById("casilla505Actual").value || null
        };

        try {
            const response = await fetchConToken(`http://localhost:8080/api/clientes/actualizarcliente/${id}`, {
                method: "PUT",
                body: JSON.stringify(clienteActualizado)
            });

            if (!response.ok) throw new Error("Error al actualizar cliente");

            alert("Cliente actualizado correctamente");
            window.location.href = "../html/campaniaActual.html";
        } catch (error) {
            console.error(error);
            alert("Error al actualizar el cliente");
        }
    });
}




    // ===== CONSULTAR TABLAS =====
initConsultarTablas() {
    cargarClientes();

    const botonBuscar = document.querySelector(".consultasActuales-aceptar");
    if (botonBuscar) {
        botonBuscar.addEventListener("click", () => cargarClientes());
    }

    document.getElementById('filtroNombre')?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') cargarClientes();
    });
    document.getElementById('filtroDni')?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') cargarClientes();
    });

    const btnAvanzados = document.getElementById('btnFiltrosAvanzadosClientes');
    const panel = document.getElementById('panelFiltrosAvanzadosClientes');
    if (btnAvanzados && panel) {
        btnAvanzados.addEventListener("click", () => {
            const visible = panel.style.display !== 'none';
            panel.style.display = visible ? 'none' : 'flex';
            btnAvanzados.textContent = visible ? 'Filtros avanzados ▼' : 'Filtros avanzados ▲';
        });
    }
}

    // ===== AGREGAR USUARIO =====
initAgregarUsuario() {
    const botonAceptar = document.querySelector(".agregar-aceptar");
    if (!botonAceptar) return;

    botonAceptar.addEventListener("click", async () => {
        const nombre = document.getElementById("nuevoNombre").value;
        const correo = document.getElementById("nuevoCorreo").value;
        const password = document.getElementById("nuevaPassword").value;
        const repetirPassword = document.getElementById("repetirPassword").value;


        if (!nombre || !correo || !password || !repetirPassword) {
            alert("Todos los campos son obligatorios");
            return;
        }

        if (password !== repetirPassword) {
            alert("Las contraseñas no coinciden");
            return;
        }

        if (password.length < 6) {
            alert("La contraseña debe tener al menos 6 caracteres");
            return;
        }


        if (!confirm(`¿Quieres crear el usuario ${correo}?`)) return;

        try {
            const response = await fetchConToken("http://localhost:8080/user/crearusuario", {
                method: "POST",
                body: JSON.stringify({
                    nombre: nombre,
                    correo: correo,
                    psw: password
                })
            });

            if (!response.ok) throw new Error("Error al crear usuario");

            alert("Usuario agregado correctamente");
            window.location.href = "../html/menu.html";

        } catch (error) {
            console.error(error);
            alert("Error al agregar usuario");
        }
    });

    // Ojitos contraseñas agregar usuario
    [['toggleNuevaPassword', 'nuevaPassword'], ['toggleRepetirPassword', 'repetirPassword']]
        .forEach(([toggleId, inputId]) => {
            const toggle = document.getElementById(toggleId);
            const input = document.getElementById(inputId);
            if (toggle && input) {
                toggle.addEventListener('click', () => {
                    input.type = input.type === 'password' ? 'text' : 'password';
                    toggle.classList.toggle('fa-eye');
                    toggle.classList.toggle('fa-eye-slash');
                });
            }
        });

}

    // ===== CAMBIAR CONTRASEÑA =====
initCambiarPassword() {
    const botonAceptar = document.querySelector(".password-aceptar");
    if (!botonAceptar) return;

    botonAceptar.addEventListener("click", async () => {
        const correo = document.getElementById("correoUsuario").value;
        const nuevaPassword = document.getElementById("nuevaContrasena").value;
        const repetirPassword = document.getElementById("repetirContrasena").value;

        // Validaciones previas
        if (!correo || !nuevaPassword || !repetirPassword) {
            alert("Todos los campos son obligatorios");
            return;
        }

        if (nuevaPassword !== repetirPassword) {
            alert("Las contraseñas no coinciden");
            return;
        }

        if (nuevaPassword.length < 6) {
            alert("La contraseña debe tener al menos 6 caracteres");
            return;
        }


        if (!confirm(`¿Seguro que quieres cambiar la contraseña del usuario ${correo}?`)) return;

        try {
            const response = await fetchConToken(
                `http://localhost:8080/user/admin/users/${encodeURIComponent(correo)}/password`, {
                method: "PUT",
                body: JSON.stringify({ passwordNueva: nuevaPassword })
            });

            if (!response.ok) throw new Error("Error al cambiar contraseña");

            alert("Contraseña cambiada correctamente");
            window.location.href = "../html/menu.html";

        } catch (error) {
            console.error(error);
            alert("Error al cambiar contraseña: " + error.message);
        }
    });

    // Ojitos contraseñas cambiar password
    [['toggleNuevaContrasena', 'nuevaContrasena'], ['toggleRepetirContrasena', 'repetirContrasena']]
        .forEach(([toggleId, inputId]) => {
            const toggle = document.getElementById(toggleId);
            const input = document.getElementById(inputId);
            if (toggle && input) {
                toggle.addEventListener('click', () => {
                    input.type = input.type === 'password' ? 'text' : 'password';
                    toggle.classList.toggle('fa-eye');
                    toggle.classList.toggle('fa-eye-slash');
                });
            }
        });

}

    // ===== ELIMINAR USUARIO =====
    initEliminarUsuario() {
        const botonAceptar = document.querySelector(".eliminar-aceptar");
        if (!botonAceptar) return;

        botonAceptar.addEventListener("click", async () => {
            const correo = document.getElementById("correoEliminar").value;

            if (!confirm(`¿Seguro que quieres eliminar el usuario ${correo}?`)) return;

            try {
                const responseUsuario = await fetchConToken(
                    `http://localhost:8080/user/email/${encodeURIComponent(correo)}`
                );

                if (!responseUsuario.ok) throw new Error("Usuario no encontrado");

                const usuario = await responseUsuario.json();

                const responseEliminar = await fetchConToken(
                    `http://localhost:8080/user/eliminarusuario/${usuario.uid}`, {
                    method: "DELETE"
                });

                if (!responseEliminar.ok) throw new Error("Error al eliminar usuario");

                alert("Usuario eliminado correctamente");
                window.location.href = "../html/menu.html";

            } catch (error) {
                console.error(error);
                alert("Error: " + error.message);
            }
        });
    }

    // ===== DARK MODE =====
    initDarkMode() {
        const toggle = document.getElementById("darkModeToggle");

        if (localStorage.getItem("darkMode") === "true") {
            document.body.classList.add("dark-mode");
            if (toggle) {
                toggle.querySelector("i").classList.replace("fa-moon", "fa-sun");
                toggle.querySelector("span").textContent = "Modo Claro";
            }
        }

        if (!toggle) return;

        toggle.addEventListener("click", () => {
            const isDark = document.body.classList.toggle("dark-mode");
            const icon = toggle.querySelector("i");
            const label = toggle.querySelector("span");

            icon.classList.toggle("fa-moon", !isDark);
            icon.classList.toggle("fa-sun", isDark);
            label.textContent = isDark ? "Modo Claro" : "Modo Oscuro";

            localStorage.setItem("darkMode", isDark);
        });
    }

    // ===== FUNCIONES =====
    resetFiscalYear() {
        const confirmacion = confirm("¿Seguro que desea resetear el año fiscal?");
        if (confirmacion) {
            fetchConToken("http://localhost:8080/api/clientes/cierre-ejercicio", {
                method: "POST"
            })
            .then(response => {
                if (!response.ok) throw new Error("Error en cierre de ejercicio");
                alert("Año fiscal reseteado correctamente");
            })
            .catch(error => {
                console.error(error);
                alert("Error al resetear el año fiscal");
            });
        }
    }

handleFile(event) {
    const file = event.target.files[0];
    if (!file) return;

    const aviso = `⚠️ AVISO ANTES DE IMPORTAR ⚠️\n\n` +
        `1. Los clientes sin NIF/CIF en el Excel recibirán un identificador temporal (SIN-NIF-XXXXXX).\n\n` +
        `2. Si un cliente tiene un NIF/CIF diferente en el Excel al de la aplicación, se creará como cliente nuevo y puede generar duplicados. Revisa el Excel y el CRM antes de continuar.\n\n` +
        `3. Al importar, solo se actualizarán los datos básicos del cliente (nombre, teléfono, correo, fecha de nacimiento, tipo de cliente y números CC). El resto de estados se conservarán tal como están en la aplicación.\n\n` +
        `¿Deseas continuar con la importación?`;

    if (!confirm(aviso)) {
        event.target.value = '';
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    const token = localStorage.getItem('token');

    // Mostrar overlay
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.style.display = 'flex';

    fetch("http://localhost:8080/api/clientes/excel/importar", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${token}`
        },
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        // Ocultar overlay
        if (overlay) overlay.style.display = 'none';

        console.log("Resultado importación:", data);

        let mensaje = `✅ Importación completada:\n\n`;
        mensaje += `• Clientes importados: ${data.creados}\n`;
        mensaje += `• Clientes actualizados: ${data.actualizados}\n`;

        if (data.filasSinNif && data.filasSinNif.length > 0) {
            mensaje += `\n⚠️ Clientes sin NIF/CIF a los que se les asignó uno temporal:\n`;
            data.filasSinNif.forEach(fila => {
                mensaje += `  - Fila ${fila} del Excel\n`;
            });
        }

        if (data.yaExistian && data.yaExistian.length > 0) {
            const errores = data.yaExistian.filter(n => n.startsWith('ERROR-'));
            if (errores.length > 0) {
                mensaje += `\n❌ NIFs con error al procesar:\n${errores.map(e => e.replace('ERROR-', '')).join(', ')}`;
            }
        }

        if (data.error) {
            mensaje += `\n\n❌ Error: ${data.error}`;
        }

        alert(mensaje);
        event.target.value = '';
    })
    .catch(error => {
        // Ocultar overlay en caso de error también
        if (overlay) overlay.style.display = 'none';
        console.error(error);
        alert("Error al importar el archivo");
        event.target.value = '';
    });
}
}

document.addEventListener("DOMContentLoaded", () => {
    new App();
});