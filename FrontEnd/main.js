const { app, BrowserWindow } = require('electron');
const path = require('path');
const { spawn } = require('child_process');

let backendProcess = null;
let mainWindow = null;
let loadingWindow = null;

function startBackend() {
    const isPackaged = app.isPackaged;
    const basePath = isPackaged
        ? path.join(process.resourcesPath)
        : __dirname;

    const javaPath = path.join(basePath, 'jre', 'bin', 'java.exe');
    const jarPath = path.join(basePath, 'backend.jar');

    backendProcess = spawn(javaPath, ['-jar', jarPath]);

    backendProcess.stdout.on('data', (data) => {
        console.log(`Backend: ${data}`);
        if (data.toString().includes('Started AppgestoriarrApplication')) {
            showMainWindow();
        }
    });

    backendProcess.stderr.on('data', (data) => {
        console.error(`Backend error: ${data}`);
        if (data.toString().includes('Started AppgestoriarrApplication')) {
            showMainWindow();
        }
    });
}

function createLoadingWindow() {
    loadingWindow = new BrowserWindow({
        width: 400,
        height: 300,
        frame: false,
        resizable: false,
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true
        }
    });

    loadingWindow.loadURL(`data:text/html;charset=utf-8,
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body {
                    font-family: 'Segoe UI', sans-serif;
                    background: #1c1c2e;
                    color: white;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    gap: 20px;
                }
                h2 { font-size: 20px; color: #a0a8f0; }
                p  { font-size: 14px; color: #888; }
                .spinner {
                    width: 48px;
                    height: 48px;
                    border: 5px solid #333;
                    border-top-color: #4a6cf7;
                    border-radius: 50%;
                    animation: spin 0.9s linear infinite;
                }
                @keyframes spin { to { transform: rotate(360deg); } }
            </style>
        </head>
        <body>
            <div class="spinner"></div>
            <h2>Iniciando aplicación...</h2>
            <p>Arrancando el servidor, por favor espera</p>
        </body>
        </html>
    `);
}

function showMainWindow() {
    if (mainWindow) return; // evitar que se abra dos veces

    mainWindow = new BrowserWindow({
        width: 1280,
        height: 800,
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true
        }
    });

    mainWindow.loadFile(path.join(__dirname, 'src/views/html/index.html'));

    mainWindow.once('ready-to-show', () => {
        if (loadingWindow) {
            loadingWindow.close();
            loadingWindow = null;
        }
        mainWindow.show();
    });
}

app.whenReady().then(() => {
    createLoadingWindow();
    startBackend();

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) createLoadingWindow();
    });
});

app.on('window-all-closed', () => {
    if (backendProcess) backendProcess.kill();
    if (process.platform !== 'darwin') app.quit();
});