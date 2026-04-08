class App {

    constructor(){

        this.body = document.body;

        this.init();

    }

    init(){

        if(this.body.classList.contains("index-page")){
            this.initLogin();
        }

        if(this.body.classList.contains("password-page")){
            this.initCambiarPassword();
        }

        if(this.body.classList.contains("eliminar-page")){
            this.initEliminarUsuario();
        }

        if(this.body.classList.contains("agregarUsuario-page")){
            this.initAgregarUsuario();
        }


        if(this.body.classList.contains("menu-page")){
            this.initMenuPrincipal();
        }

        if(this.body.classList.contains("agregarCliente-page")){
            this.initAgregarCliente();
        }

        if(this.body.classList.contains("consultarTablas-page")){
            this.initConsultarTablas();
        }

    }

    /* ===== MENU PRINCIPAL ===== */

    /* Logout */

    function logout(){
        localStorage.clear(); // o token/session si usáis backend
        window.location.href = "login.html";
    }

    /* Se termina el logout */


    initMenuPrincipal(){

        const resetButton = document.getElementById("resetButton");
        const fileInput = document.getElementById("excelFile");
        const importButton = document.getElementById("importButton");

        if(resetButton){
            resetButton.addEventListener("click", () => this.resetFiscalYear());
        }

        if(importButton){
            importButton.addEventListener("click", () => fileInput.click());
        }

        if(fileInput){
            fileInput.addEventListener("change", (e) => this.handleFile(e));
        }

    }

    /* ===== AGREGAR CLIENTE ===== */

initAgregarCliente(){

    const form = document.querySelector(".cliente-form");

    if(!form) return;

    form.addEventListener("submit", (e) => {

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
            facturado: document.getElementById("facturado").value,
            recogidaDatos: document.getElementById("recogidaDatos").value,
            excelDatosElaboracion: document.getElementById("excelDatosElaboracion").value,
            borrador: document.getElementById("borrador").value,
            presentada: document.getElementById("presentada").value,
            cobrado: document.getElementById("cobrado").value,
            tipoCliente: document.getElementById("tipoCliente").value,
            estadoCliente: document.getElementById("estadoCliente").value,
            casilla505Actual: document.getElementById("casilla505Actual").value
        };

        console.log("Cliente a enviar:", cliente);

        fetch("http://localhost:8080/clientes", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(cliente)
        })
        .then(response => {
            if(!response.ok){
                throw new Error("Error al guardar cliente");
            }
            return response.json();
        })
        .then(data => {
            console.log("Respuesta:", data);
            alert("Cliente guardado correctamente");
            window.location.href = "../html/campaniaActual.html";
        })
        .catch(error => {
            console.error(error);
            alert("Error al guardar cliente");
        });

    });

}

    /* ===== CONSULTAR TABLAS ===== */

    initConsultarTablas(){

        const botonBuscar = document.querySelector(".consultas-aceptar");

        if(!botonBuscar) return;

        botonBuscar.addEventListener("click", () => {

            alert("Función de búsqueda en desarrollo");

        });

    }



    /* ===== USUARIOS ===== */
    /* Agregar Usuario */
    initAgregarUsuario(){

        const botonAceptar = document.querySelector(".agregar-aceptar");

        if(!botonAceptar) return;

        botonAceptar.addEventListener("click", () => {

            const inputs = document.querySelectorAll(".agregarUsuarios-inp");

            const usuario = inputs[0].value;
            const password = inputs[1].value;

            console.log("Nuevo usuario:", usuario);
            console.log("Password:", password);

            alert("Usuario agregado correctamente");

            window.location.href = "../html/menu.html";

        });

    }




    /* Cambiar Contraseña */
    initCambiarPassword(){

        const botonAceptar = document.querySelector(".password-aceptar");

        if(!botonAceptar) return;

        botonAceptar.addEventListener("click", () => {

            const inputs = document.querySelectorAll(".passwordUsuarios-inp");

            const usuario = inputs[0].value;
            const nuevaPassword = inputs[1].value;

            console.log("Usuario:", usuario);
            console.log("Nueva contraseña:", nuevaPassword);

            alert("Contraseña cambiada correctamente");

            window.location.href = "../html/menu.html";

        });

    }


    /* Eliminar usuario */
    initEliminarUsuario(){

        const botonAceptar = document.querySelector(".eliminar-aceptar");

        if(!botonAceptar) return;

        botonAceptar.addEventListener("click", () => {

            const usuario = document.querySelector(".eliminarUsuarios-inp").value;

            console.log("Usuario eliminado:", usuario);

            alert("Usuario eliminado correctamente");

            window.location.href = "../html/menu.html";

        });

    }





    /* ===== FUNCIONES ===== */

    resetFiscalYear(){

        const confirmacion = confirm("¿Seguro que desea resetear el año fiscal?");

        if(confirmacion){
            alert("Año fiscal reseteado");
        }

    }

    handleFile(event){

        const file = event.target.files[0];

        if(file){
            alert(`Archivo seleccionado: ${file.name}`);
        }

    }

}

document.addEventListener("DOMContentLoaded", () => {

    new App();

});


