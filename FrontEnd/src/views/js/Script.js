class App {

    constructor(){

        this.body = document.body;

        this.init();

    }

    init(){

        if(this.body.classList.contains("index-page")){
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

            const inputs = form.querySelectorAll("input");

            let cliente = {};

            inputs.forEach(input => {

                const label = input.previousElementSibling.textContent;
                cliente[label] = input.value;

            });

            console.log(cliente);

            alert("Cliente guardado correctamente");

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