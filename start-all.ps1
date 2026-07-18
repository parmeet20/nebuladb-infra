# =============================================================================
# start-all.ps1 — Nebula DB: Start all Spring Boot microservices on Windows
# =============================================================================
$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$LogDir = Join-Path $ScriptDir "logs"
if (!(Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir | Out-Null
}
$EurekaWait = 20
$ServiceWait = 10
$GatewayWait = 15
function Log-Message($Message) {
    Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $Message" -ForegroundColor Cyan
}
function Wait-For-Http($Name, $Url, $MaxAttempts = 30) {
    Log-Message "Waiting for $Name to be ready at $Url ..."
    for ($attempt = 1; $attempt -le $MaxAttempts; $attempt++) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Log-Message "$Name is UP"
                return
            }
        } catch {
            # Ignore and retry
        }
        Log-Message "  [$attempt/$MaxAttempts] $Name not ready yet, retrying in 3s..."
        Start-Sleep -Seconds 3
    }
    Log-Message "ERROR: $Name did not start within expected time. Check log in logs directory."
    exit 1
}
function Start-Service($Name, $JarPath, $HealthUrl) {
    Log-Message "Starting $Name ..."

    if (-not (Test-Path $JarPath)) {
        Log-Message "ERROR: JAR not found: $JarPath"
        exit 1
    }

    $StdoutFile = Join-Path $LogDir "${Name}.log"
    $StderrFile = Join-Path $LogDir "${Name}.err"
    $PidFile    = Join-Path $LogDir "${Name}.pid"

    # Remove stale logs from previous run
    Remove-Item $StdoutFile -ErrorAction SilentlyContinue
    Remove-Item $StderrFile -ErrorAction SilentlyContinue

    $process = Start-Process java `
        -ArgumentList @("-jar", "`"$JarPath`"", "--spring.output.ansi.enabled=NEVER") `
        -RedirectStandardOutput $StdoutFile `
        -RedirectStandardError  $StderrFile `
        -PassThru -WindowStyle Hidden -WorkingDirectory $ScriptDir

    $process.Id | Out-File -FilePath $PidFile -Encoding utf8 -NoNewline
    Log-Message "$Name started with PID $($process.Id) - logs: $StdoutFile"

    if ($HealthUrl) {
        Wait-For-Http $Name $HealthUrl
    }
}

# ---- Stop any existing running services -----------------------------------
Log-Message "===== Stopping any existing services ====="
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name cmd  -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like '*java*' } | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3
Log-Message "Existing services stopped."

# ---- Build modules first ---------------------------------------------------
Log-Message "===== Building all modules ====="
Set-Location $ScriptDir
mvn clean package -DskipTests -q
Log-Message "Build complete"
Log-Message ""
Log-Message "===== [1/5] Starting Eureka Server ====="
$EurekaJar = Join-Path $ScriptDir "cloud\eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar"
Start-Service "eureka-server" $EurekaJar "http://localhost:8761/actuator/health"
Log-Message "Waiting $EurekaWait seconds for Eureka to stabilise..."
Start-Sleep -Seconds $EurekaWait
Log-Message ""
Log-Message "===== [2/5] Starting nebula-auth ====="
$AuthJar = Join-Path $ScriptDir "service-infra\nebula-auth\target\nebula-auth-service-0.0.1-SNAPSHOT.jar"
Start-Service "nebula-auth" $AuthJar "http://localhost:8002/actuator/health"
Start-Sleep -Seconds $ServiceWait
Log-Message ""
Log-Message "===== [3/5] Starting project-service ====="
$ProjectJar = Join-Path $ScriptDir "service-infra\project-service\target\project-service-0.0.1-SNAPSHOT.jar"
Start-Service "project-service" $ProjectJar "http://localhost:8000/actuator/health"
Start-Sleep -Seconds $ServiceWait
Log-Message ""
Log-Message "===== [4/5] Starting docker-infra-service ====="
$DockerJar = Join-Path $ScriptDir "service-infra\docker-infra-service\target\docker-infra-service-0.0.1-SNAPSHOT.jar"
Start-Service "docker-infra-service" $DockerJar "http://localhost:8001/actuator/health"
Start-Sleep -Seconds $ServiceWait
Log-Message ""
Log-Message "===== [5/5] Starting api-gateway ====="
Log-Message "Waiting $GatewayWait seconds for all services to register with Eureka before starting gateway..."
Start-Sleep -Seconds $GatewayWait
$GatewayJar = Join-Path $ScriptDir "cloud\api-gateway\target\api-gateway-0.0.1-SNAPSHOT.jar"
Start-Service "api-gateway" $GatewayJar "http://localhost:8888/actuator/health"
Log-Message ""
Log-Message "============================================"
Log-Message "  All services are running!"
Log-Message "  API Gateway : http://localhost:8888"
Log-Message "  Eureka UI   : http://localhost:8761"
Log-Message "  Auth Service: http://localhost:8002"
Log-Message "  Project Svc : http://localhost:8000"
Log-Message "  Docker Infra: http://localhost:8001"
Log-Message "============================================"
Log-Message ""
Log-Message "To stop all services:"
Log-Message "Stop-Process -Name java -Force"
Log-Message ""
