<# 
  ASFJ_start.ps1
  Start ASFJ fat JAR with necessary VM-args
#>

# Script Directory
# This allows the script to be run from any directory
# and still find the JAR in the same directory as the script.
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# JAR path
$Jar = Join-Path $ScriptDir 'ASFJ-1.0.0.jar'

if (-Not (Test-Path $Jar)) {
    Write-Error "Error: file $Jar not found."
    exit 1
}

# VM's options
$VmOpts = @(
    '-DLOG_LEVEL=INFO'
    '-Dfirewallgui.application.guice.stage=production'
)

# Construct the Java command with VM options and JAR file
$javaArgs = $VmOpts + @('-jar', "`"$Jar`"")

# Start the Java application
& java @javaArgs
