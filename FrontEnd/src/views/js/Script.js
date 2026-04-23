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
        cargarClientes();
    } catch (error) {
        console.error(error);
        alert("Error al eliminar el cliente");
    }
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

        let clientes;

        if (Object.keys(filtros).length > 0) {
            const response = await fetchConToken('http://localhost:8080/api/clientes/buscarporfiltros', {
                method: 'POST',
                body: JSON.stringify(filtros)
            });
            if (!response.ok) throw new Error("Error al obtener clientes");
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
               <td>${cliente.telefono ?? ''}</td>
               <td>${cliente.correoElectronico ?? ''}</td>
               <td>${cliente.fechaNacimiento ? new Date(cliente.fechaNacimiento).toLocaleDateString('es-ES') : ''}</td>
               <td>${cliente.tipoCliente ?? ''}</td>
               <td>${cliente.estadoCliente ?? ''}</td>
               <td>${cliente.importe ?? ''}</td>
               <td>${cliente.cobrado ?? ''}</td>
               <td>
                   <a href="editarCliente.html?id=${cliente.nifCif}&modo=editar" class="btn-editar">
                       <i class="fa-solid fa-pen"></i> Editar
                   </a>
                   <button class="btn-eliminar" title="Eliminar cliente">
                       <i class="fa-solid fa-trash"></i>
                   </button>
               </td>
           `;

           // Fila de detalle (oculta por defecto)
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
                ['Casilla 505 Anterior', cliente.casilla505anterior],
                ['Casilla 505 Actual', cliente.casilla505Actual],
                ['Números CC', cliente.numerosCC],
                ['Datos Fiscales', cliente.datosFiscalesDescargados ? 'Sí' : 'No'],
                ['Excel Elaboración', cliente.excelDatosElaboracion ? 'Sí' : 'No'],
                ['Tipo Facturado', cliente.tipoFacturado],
                ['Recogida Datos', cliente.recogidaDatos],
                ['Borrador', cliente.borrador],
                ['Presentada', cliente.presentada],
                ['NIF Anterior', cliente.nifAnterior],
                ['NIF Histórico', cliente.nifHistorico?.join(', ')]
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
           // Clic en la fila para expandir/colapsar
           fila.style.cursor = 'pointer';
           fila.addEventListener('click', (e) => {
               // Que el clic en Editar no expanda la fila
               if (e.target.closest('.btn-editar')) return;

               const visible = filaDetalle.style.display !== 'none';
               filaDetalle.style.display = visible ? 'none' : 'table-row';
               fila.style.background = visible ? '' : 'var(--button-hover-bg)';
           });

fila.querySelector('.btn-eliminar').addEventListener('click', (e) => {
    e.stopPropagation(); // evita que expanda la fila detalle
    const aviso = `⚠️ ATENCIÓN\n\n` +
        `Vas a eliminar a "${cliente.nombre}" (${cliente.nifCif}).\n\n` +
        `Este cliente será eliminado permanentemente de la campaña actual. ` +
        `No podrás recuperar su estado actual una vez eliminado.\n\n` +
        `¿Confirmas la eliminación?`;
    if (confirm(aviso)) {
        eliminarCliente(cliente.nifCif, cliente.nombre);
    }
});
           tabla.appendChild(fila);
           tabla.appendChild(filaDetalle);
       });
    } catch (error) {
        console.error("Error al cargar clientes:", error);
        alert("Error al cargar los clientes");
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
             <td>
                 <a href="verDatosClientes.html" class="btn-editar">
                     <i class="fa-solid fa-eye"></i> Ver datos
                 </a>
             </td>
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
                ['Fecha Nacimiento', cliente.fechaNacimiento ? new Date(cliente.fechaNacimiento).toLocaleDateString('es-ES') : null],
                ['Números CC', cliente.numerosCC],
                ['Datos Fiscales', cliente.datosFiscalesDescargados ? 'Sí' : 'No'],
                ['Excel Elaboración', cliente.excelDatosElaboracion ? 'Sí' : 'No'],
                ['Tipo Facturado', cliente.tipoFacturado],
                ['Recogida Datos', cliente.recogidaDatos],
                ['Borrador', cliente.borrador],
                ['Presentada', cliente.presentada],
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
        const response = await fetchConToken('http://localhost:8080/api/clientesHistorico');
        if (!response.ok) return;
        const todos = await response.json();

        const selectAnio = document.getElementById('filtroCampania');
        if (!selectAnio) return;

        const aniosUnicos = [...new Set(todos.map(c => c.anioFiscal).filter(a => a))].sort().reverse();
        selectAnio.innerHTML = '<option value="">Todos los años</option>';
        aniosUnicos.forEach(anio => {
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
    this.cargarClientesCambiarNif();

    const btnBuscar = document.getElementById('btnBuscar');
    if (btnBuscar) {
        btnBuscar.addEventListener('click', () => this.cargarClientesCambiarNif());
    }

    const inputBuscar = document.getElementById('buscarCliente');
    if (inputBuscar) {
        inputBuscar.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') this.cargarClientesCambiarNif();
        });
    }
}

async cargarClientesCambiarNif() {
    try {
        const busqueda = document.getElementById('buscarCliente')?.value;

        let clientes;

        if (busqueda) {
            const response = await fetchConToken(
                `http://localhost:8080/api/clientes/buscar/nombre?nombre=${encodeURIComponent(busqueda)}`
            );
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
                    <button class="btn-editar btn-cambiar-nif" data-nif="${cliente.nifCif}" data-nombre="${cliente.nombre}">
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
            alert("Error: " + mensaje);
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
        inicializarSelectAnios().then(() => cargarHistorico());

        const botonBuscar = document.querySelector(".consultas-aceptar");
        if (botonBuscar) {
            botonBuscar.addEventListener("click", () => cargarHistorico());
        }

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
            resetButton.addEventListener("click", () => this.resetFiscalYear());
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
        document.getElementById('nifCif').value            = cliente.nifCif ?? '';
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
    }

    // ===== CAMBIAR CONTRASEÑA =====
    initCambiarPassword() {
        const botonAceptar = document.querySelector(".password-aceptar");
        if (!botonAceptar) return;

        botonAceptar.addEventListener("click", async () => {
            const correo = document.getElementById("correoUsuario").value;
            const nuevaPassword = document.getElementById("nuevaContrasena").value;

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