; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Cafe Compiler"
#define MyAppPublisher "Cafe"
#define MyAppURL "https://www.cafe-lang.tech/"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId=Cafe
AppName={#MyAppName}
AppVersion=${applicationVersion}
AppVerName={#MyAppName} ${applicationVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
AppCopyright=Copyright (C) 2021 Cafe Authors

DefaultDirName={pf}\\cafe
DisableDirPage=yes
DefaultGroupName=Cafe
DisableProgramGroupPage=yes
ChangesEnvironment=yes

LicenseFile=..\\..\\innosetup\\LICENSE.txt

; Uncomment the following line to run in non administrative install mode (install for current user only.)
;PrivilegesRequired=lowest

SourceDir=..\\install\\cafe
OutputDir=..\\..\\innosetup
OutputBaseFilename=cafe-${applicationVersion}

; SetupIconFile=..\\..\\innosetup\\logo.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Tasks]
Name: modifypath; Description: Add Cafe to PATH environment variable (recommended)

[Registry]
Root: HKLM; Subkey: "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment"; \
    ValueType: expandsz; ValueName: "Path"; ValueData: "{olddata};{pf}\\cafe\\bin"; \
    Check: ModifyPathKeyCheck(ExpandConstant('{pf}\\cafe\\bin'));

[Code]
var ModifyPathKey: Boolean;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  if CurPageID = wpSelectTasks then
  begin
    ModifyPathKey := False;

    if IsTaskSelected('modifypath') then
    begin
        ModifyPathKey := True
    end;
  end;
  Result := True;
end;

function NeedsAddPath(Param: string): boolean;
var
  OrigPath: string;
begin
  if not RegQueryStringValue(HKEY_LOCAL_MACHINE,
    'SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment',
    'Path', OrigPath)
  then begin
    Result := True;
    exit;
  end;
  { look for the path with leading and trailing semicolon }
  { Pos() returns 0 if not found }
  Result := Pos(';' + Param + ';', ';' + OrigPath + ';') = 0;
end;

function ModifyPathKeyCheck(Path: string): Boolean;
begin
  Result := True;
  Log(Path);
  if(ModifyPathKey) then
  begin
    Result := NeedsAddPath(Path);
  end;
end;
