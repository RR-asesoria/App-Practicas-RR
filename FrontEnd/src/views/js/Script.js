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
        const response = await fetchConToken('http://localhost:8080/api/clientes/obtenerTodos');
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

    // Función reutilizable para el login
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

    // ← NUEVO: disparar login al pulsar Enter en cualquiera de los dos inputs
    document.querySelector(".login-usuario")?.addEventListener("keydown", (e) => {
        if (e.key === "Enter") handleLogin();
    });
    document.querySelector(".login-password")?.addEventListener("keydown", (e) => {
        if (e.key === "Enter") handleLogin();
    });

    // Toggle contraseña (sin cambios)
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

    // ===== CONSULTAR TABLAS =====
    initConsultarTablas() {
        cargarClientes();
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