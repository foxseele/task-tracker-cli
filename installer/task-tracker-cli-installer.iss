; Inno Setup script for Task Tracker CLI
[Setup]
AppName=Task Tracker CLI
AppVersion=1.0
DefaultDirName={autopf}\TaskTrackerCLI
DefaultGroupName=Task Tracker CLI
OutputBaseFilename=task-tracker-cli-installer
Compression=lzma
SolidCompression=yes

[Files]
Source: "task-tracker-cli-1.0.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "task-cli.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "jdk-check.bat"; DestDir: "{app}"; Flags: ignoreversion

[Run]
Filename: "{app}\jdk-check.bat"; Description: "Check for JDK 23"; Flags: runhidden shellexec waituntilterminated; StatusMsg: "Checking for JDK 23..."; Check: not JDKCheckPassed

[Registry]
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: expandsz; ValueName: "Path"; ValueData: "{olddata};{app}"; Check: NeedsAddPath('{app}')

[Code]
var
  JDKCheckResult: Integer;

function JDKCheckPassed: Boolean;
begin
  Result := (JDKCheckResult = 0);
end;

function InitializeSetup: Boolean;
begin
  Result := True;
  if not FileExists(ExpandConstant('{tmp}\jdk-check.bat')) then
  begin
    ExtractTemporaryFile('jdk-check.bat');
  end;
  if Exec(ExpandConstant('{tmp}\jdk-check.bat'), '', '', SW_HIDE, ewWaitUntilTerminated, JDKCheckResult) then
  begin
    if JDKCheckResult <> 0 then
    begin
      MsgBox('JDK 23 is required to run Task Tracker CLI. Please install JDK 23 and rerun the installer.', mbCriticalError, MB_OK);
      Result := False;
    end;
  end
  else
  begin
    MsgBox('Failed to check for JDK 23. Please ensure JDK 23 is installed.', mbCriticalError, MB_OK);
    Result := False;
  end;
end;

function NeedsAddPath(Param: string): boolean;
var
  OrigPath: string;
begin
  if not RegQueryStringValue(HKEY_LOCAL_MACHINE,
    'SYSTEM\CurrentControlSet\Control\Session Manager\Environment',
    'Path', OrigPath)
  then begin
    Result := True;
    exit;
  end;
  Result := Pos(';' + UpperCase(Param) + ';', ';' + UpperCase(OrigPath) + ';') = 0;
end;