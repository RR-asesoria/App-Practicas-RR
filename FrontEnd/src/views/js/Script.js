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

// ===== CARGAR CLIENTES =====
async function cargarClientes() {
    const token = localStorage.getItem('token');
    console.log('Token:', token); // ← ver si hay token

    try {
        const response = await fetchConToken('http://localhost:8080/api/clientes');
        console.log('Status:', response.status); // ← ver el código de respuesta
        if (!response.ok) throw new Error("Error al obtener clientes");
        const clientes = await response.json();

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
            `;
            tabla.appendChild(fila);
        });
    } catch (error) {
        console.error("Error al cargar clientes:", error);
        alert("Error al cargar los clientes");
    }
}

// ===== APP =====
class App {

    constructor() {
        this.body = document.body;
        this.init();
    }

    init() {
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
    }

    // ===== LOGIN =====
    initLogin() {
        const botonLogin = document.querySelector(".login-aceptar");
        if (!botonLogin) return;

        botonLogin.addEventListener("click", async () => {
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
                nombre: document.getElementById("nombre").value,
                nifCif: document.getElementById("nifCif").value,
                fechaNacimiento: document.getElementById("fechaNacimiento").value,
                referencia: document.getElementById("referencia").value,
                casilla505anterior: document.getElementById("casilla505anterior").value,
                numerosCC: document.getElementById("numerosCC").value,
                datosFiscalesDescargados: document.getElementById("datosFiscalesDescargados").value,
                importe: document.getElementById("importe").value,
                tipoFacturado: document.getElementById("facturado").value,
                recogidaDatos: document.getElementById("recogidaDatos").value,
                excelDatosElaboracion: document.getElementById("excelDatosElaboracion").value,
                borrador: document.getElementById("borrador").value,
                presentada: document.getElementById("presentada").value,
                cobrado: document.getElementById("cobrado").value,
                tipoCliente: document.getElementById("tipoCliente").value,
                estadoCliente: document.getElementById("estadoCliente").value,
                casilla505Actual: document.getElementById("casilla505Actual").value
            };

            try {
                const response = await fetchConToken("http://localhost:8080/api/clientes", {
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

    // ===== CONSULTAR TABLAS =====
    initConsultarTablas() {
        cargarClientes();
    }

    // ===== AGREGAR USUARIO =====
    initAgregarUsuario() {
        const botonAceptar = document.querySelector(".agregar-aceptar");
        if (!botonAceptar) return;

        botonAceptar.addEventListener("click", async () => {
            const inputs = document.querySelectorAll(".agregarUsuarios-inp");
            const usuario = inputs[0].value;
            const password = inputs[1].value;

            console.log("Nuevo usuario:", usuario);
            console.log("Password:", password);

            alert("Usuario agregado correctamente");
            window.location.href = "../html/menu.html";
        });
    }

    // ===== CAMBIAR CONTRASEÑA =====
    initCambiarPassword() {
        const botonAceptar = document.querySelector(".password-aceptar");
        if (!botonAceptar) return;

        botonAceptar.addEventListener("click", async () => {
            const inputs = document.querySelectorAll(".passwordUsuarios-inp");
            const usuario = inputs[0].value;
            const nuevaPassword = inputs[1].value;

            console.log("Usuario:", usuario);
            console.log("Nueva contraseña:", nuevaPassword);

            alert("Contraseña cambiada correctamente");
            window.location.href = "../html/menu.html";
        });
    }

    // ===== ELIMINAR USUARIO =====
    initEliminarUsuario() {
        const botonAceptar = document.querySelector(".eliminar-aceptar");
        if (!botonAceptar) return;

        botonAceptar.addEventListener("click", async () => {
            const usuario = document.querySelector(".eliminarUsuarios-inp").value;

            console.log("Usuario eliminado:", usuario);

            alert("Usuario eliminado correctamente");
            window.location.href = "../html/menu.html";
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

        const formData = new FormData();
        formData.append("file", file);

        const token = localStorage.getItem('token');
        fetch("http://localhost:8080/api/clientes/excel/importar", {
            method: "POST",
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            console.log("Resultado importación:", data);
            alert(`Importación completada: ${data.creados} creados, ${data.actualizados} actualizados`);
        })
        .catch(error => {
            console.error(error);
            alert("Error al importar el archivo");
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    new App();
});