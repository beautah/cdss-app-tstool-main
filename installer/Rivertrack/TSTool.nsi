#############################################################
# AUTHOR: Kurt Tometich
# DATE: Aug 26, 2006
#
# BRIEF:
#  This NSIS script creates an executable
#  install file:"TsTool_Rivertrak_version_setup.exe"
#  This .exe install file will setup TsTool
#  programs and allow the user to choose
#  from certain components to install
#
# COMPONENTS:
#  BaseComponents - base jar files in bin
#  Documentation  - pdf's and text files for this release
#  TsTool - installs TsTool specific files (jars, bats, etc)
#  Start Menu Shortcuts - installs shortcuts to run TsTool
#                          and uninstall TsTool
#  DesktopShortcut - should be self explanatory
#
##############################################################

# Define Vars
!define DISPLAYNAME "TSTool"
Name "${DISPLAYNAME}"
!define VERSION 7.00.00
!define NAMEVERSION $(^Name)-${VERSION}
!define REGKEY "Software\RTi\${NAMEVERSION}"
!define COMPANY RTi
!define URL http://www.riverside.com
!define EXTERNALS_DIR "externals"
!define JRE_VERSION "142"
!define INSTALL_IS_CDSS "false"
!define INST_BUILD_DIR "dist\install-rti"
# uncomment to skip some sections to allow installer compilation to run faster
#!define TEST "true"
# change working directory to product root to make paths more sane!
!cd "..\..\"

SetCompressor lzma
BrandingText "Riverside Technology, inc."

# Included files
!include "UMUI.nsh"
!include "Registry.nsh"
!include "TextReplace.nsh"

# Global Variables
Var StartMenuGroup
Var myInstDir
Var choseTSTool
Var choseDocs
Var choseJRE
#Var numInstComponents

# Installer attributes
OutFile "dist\TSTool_CDSS_${VERSION}_Setup.exe"
InstallDir "C:\Program Files\RTi"
InstallDirRegKey HKLM "${REGKEY}" Path

# MUI defines
!define MUI_ICON "externals\Rivertrak\graphics\RTi.ico"
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_REGISTRY_KEY "${REGKEY}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULT_FOLDER RTi
!define MUI_UNICON "externals\Rivertrak\graphics\RTi.ico"
!define MUI_UNFINISHPAGE_NOAUTOCLOSE
!define MUI_ABORTWARNING

# MUI Overrides for Text
!define MUI_PAGE_HEADER_SUBTEXT "This wizard will guide you through the installation of $(^Name)"
!define MUI_WELCOMEPAGE_TEXT "It is recommended that you close all other RiverTrak® System applications before starting this Setup. This will make it possible to update relevant RiverTrak® System files without any conflicts.  Please close all RiverTrak® System specific applications, files and folders and click Next to continue."
!define MUI_COMPONENTSPAGE_TEXT_TOP "Select the components to install by checking the corresponding boxes.  Click Next to continue."
!define MUI_COMPONENTSPAGE_TEXT_DESCRIPTION_INFO "Position the mouse over a component to view its description."
!define MUI_DIRECTORYPAGE_TEXT_TOP "Setup will install TSTool in the following folder.  It is recommended that the main RTi folder be specified.  To install in a different folder, click Browse and select another folder.  Click Next to continue."
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "RTi"

### Use custom button text
MiscButtonText "Back" "Next" "Cancel" "Done"

### Pages ###
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "installer\Rivertrack\License.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
!insertmacro MUI_PAGE_INSTFILES  
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
#Page custom SetCustom

# Installer language
!insertmacro MUI_LANGUAGE English

ReserveFile "externals\Rivertrak\installer\server_name.ini"
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

!addincludedir ..\rtibuild\externals\NSIS_Common
!include PathManipulation.nsh
!include RegisterExtension.nsh
!include Util.nsh
!ifndef TEST
    !include JRE.nsh
!endif
!addincludedir externals\Rivertrak\installer
# @todo - revisit whether BaseComponents needed
#!include BaseComponents.nsh
!include server_name.nsh


##################################################################
# SECTION: -setInstallVariables
# 
# initializes some global variables
#   myInstDir - users chosen install directory
#   choseTSTool, choseDocs 
#             - Used for dependencies in later sections
#             0 = user chose not to install TSTool,
#             1 = user chose to install TSTool
#   StartMenuGroup - sets the StartMenu Folder Name
#  
# BRIEF:
#  The minus sign at the beginning of the section name
#  is used to make this a hidden section to the user
#  this means they cannot choose to not run it
###################################################################
Section -setInstallVariables
    
    strcpy $myInstDir "$INSTDIR"
    strcpy $choseTSTool "0"
    strcpy $choseDocs "0"
    strcpy $choseJRE "0"
    strcpy $StartMenuGroup "RTi"
    SetShellVarContext all
    
SectionEnd

########################################################
# SECTION: -Main
# 
# BRIEF:
#  used to write the Reg Key for the components
########################################################
Section -Main
    WriteRegStr HKLM "${REGKEY}\Components" Main 1
SectionEnd


##################################################
# SECTION: TSTool
#
# BRIEF: 
#  Installs the TSTool specific files.
#  These may change each release so
#  the files included may need to be 
#  updated.  Before each file is placed
#  on the users machine it is checked
#  to make sure we don't overwrite
#  a newer or current version.  Each
#  file is checked with a Utility function
#  located in Util.nsh:CompareFileModificationDates  
#
##################################################
Section "TSTool" TSTool

    # set choseTSTool variable to true since it was chosen
    strcpy $choseTSTool "1"
    
    # copy important bat/jar files specific to this product
    SetOverwrite ifnewer
    SetOutPath $INSTDIR

    !ifndef TEST
        File /r "${INST_BUILD_DIR}\bin"
        File /r "${INST_BUILD_DIR}\system"
    !endif
    
    # Insert the -home Directory into the .bat file
    # according to the user's install location
    ${textreplace::ReplaceInFile} "$INSTDIR\bin\TSTool.bat" "$INSTDIR\bin\TSTool.bat" "SET HOMED=\CDSS" "SET HOMED=$INSTDIR" "" $0
    ${textreplace::ReplaceInFile} "$INSTDIR\bin\TSTool.bat" "$INSTDIR\bin\TSTool.bat" "SET JREHOMED=%HOMED%\jre_142" "SET JREHOMED=..\..\jre_142" "" $0

    # Write some registry keys for TSTool
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    WriteRegStr HKLM "${REGKEY}" StartMenuGroup $StartMenuGroup
    SetOverwrite off
    WriteUninstaller $INSTDIR\Uninstall_${NAMEVERSION}.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" DisplayName "${NAMEVERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" DisplayIcon $INSTDIR\Uninstall_${NAMEVERSION}.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" UninstallString $INSTDIR\Uninstall_${NAMEVERSION}.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}" NoRepair 1

    ${registerExtension} "$INSTDIR\bin\TSTool.exe" ".TSTool" "TSTool Commands File"
   
SectionEnd


#########################################
# SECTION: Documentation
#
# BRIEF:
#  Installs current documentation   
#  for TSTool to doc\TSTool
#
# The /o stands for optional.  This
# allows the component page to uncheck
# this box by default.   
#########################################
Section "Documentation" Docs
   
    # set boolean choseDocs since documentation was selected
    strcpy $choseDocs "1"
    
    # copy documentation
    SetOutPath $INSTDIR\doc
    SetOverwrite on

    # @Todo Where are the docs?
    #File /r /x *svn* ..\..\doc\TSTool\Rivertrak\*

SectionEnd

##############################################
# SECTION: Start Menu Shortcuts
#
# BRIEF: 
#  This section creates the start -> apps
#  shortcuts as RTi -> TSTool -> uninstall
#                             -> run TSTool
#  
##############################################
Section "Start Menu" StartMenu

    # make sure user chose to install TSTool
    strcmp $choseTSTool "0" 0 +2
      Goto skipMenu
    
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
    # Shortcut added for launch of java program
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    SetOutPath $INSTDIR\bin
    CreateShortCut "$SMPROGRAMS\$StartMenuGroup\${NAMEVERSION}.lnk" "$INSTDIR\bin\$(^Name).exe"
    
    # Shortcut for uninstall of program
    SetOutPath $SMPROGRAMS\$StartMenuGroup\Uninstall
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall\${NAMEVERSION}.lnk" $INSTDIR\Uninstall_${NAMEVERSION}.exe
    
    skipMenu:
    
    # make sure user chose to install docs
    strcmp $choseDocs "0" 0 +2
      Goto Done
      
    # Shortcut for TSTool documentation
    SetOutPath $SMPROGRAMS\$StartMenuGroup\Documentation
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Documentation\${NAMEVERSION}.lnk" $INSTDIR\doc\$(^Name)\UserManual\$(^Name).pdf
      
    !insertmacro MUI_STARTMENU_WRITE_END  
      
    Done:
    
SectionEnd


############################################
# SECTION: DesktopShortcut
#
# BRIEF:
#  should be pretty obvious what this does.
#  If its not, you shouldn't be reading this
#  
############################################
Section /o "Desktop Shortcut" DesktopShortcut

    # make sure user chose to install TSTool
    strcmp $choseTSTool "0" 0 +2
      Goto skipShortcut
   
    # Installs shortcut on desktop
    SetOutPath $INSTDIR\bin
    CreateShortCut "$DESKTOP\${NAMEVERSION}.lnk" "$INSTDIR\bin\TSTool.exe"

    skipShortcut:

SectionEnd


###########################################
# SECTION: Uninstall
#
# BRIEF: Deletes files and RegKeys
###########################################
Section "Uninstall"

    SetShellVarContext all

    # Get the StartMenuFolder
    !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup

    # delete registry and StartMenu stuff
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${NAMEVERSION}"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall\${NAMEVERSION}.lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Documentation\${NAMEVERSION}.lnk"
    RmDir /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Documentation"
    RmDir /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\${NAMEVERSION}.lnk"
    Delete /REBOOTOK "$INSTDIR\${NAMEVERSION}.lnk"
    Delete /REBOOTOK "$DESKTOP\${NAMEVERSION}.lnk"
    Delete /REBOOTOK $INSTDIR\Uninstall_${NAMEVERSION}.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /REBOOTOK $SMPROGRAMS\RTi
    
    # Remove files from install directory
    Delete /REBOOTOK $INSTDIR\bin\TSTool.bat
    Delete /REBOOTOK $INSTDIR\bin\TSTool_142.jar
    RmDir /r /REBOOTOK $INSTDIR\doc\TSTool
    RmDir /REBOOTOK $INSTDIR\doc
    Delete /REBOOTOK $INSTDIR\system\TSTool.cfg
    Delete /REBOOTOK $INSTDIR\TSTool_README.txt
    Delete /REBOOTOK $INSTDIR\bin\TSTool.exe
    Delete /REBOOTOK $INSTDIR\bin\TSTool.ini
    DeleteRegValue HKLM "${REGKEY}\Components" Main

SectionEnd


### Section Descriptions ###
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${Docs} "Enabling this component will install TSTool documentation into the Rivertrak\doc\TSTool folder"
  !insertmacro MUI_DESCRIPTION_TEXT ${TSTool} "Enabling this component will install TSTool under the main folder"
  !insertmacro MUI_DESCRIPTION_TEXT ${StartMenu} "Enabling this component will install start menu folders"
  !insertmacro MUI_DESCRIPTION_TEXT ${DesktopShortcut} "Enabling this component will install a desktop shortcut to run the TSTool application"
  !insertmacro MUI_DESCRIPTION_TEXT ${BaseComponents} "Enabling this component will install the RiverTrak® System base components"
  !insertmacro MUI_DESCRIPTION_TEXT ${JRE} "Enabling this component will install the Java runtime environment"
!insertmacro MUI_FUNCTION_DESCRIPTION_END


################################################
# FUNCTION: .onInstSuccess
#
# BRIEF: NSIS default function
#  When installation is successful and the
#  user clicks the close button, this function
#  is called.  It prompts the user to execute
#  the program and view the readme
################################################
Function .onInstSuccess

    SetOutPath $INSTDIR\bin
    
    # if 0 then user didn't choose to install TSTool
    strcmp $choseTSTool "0" 0 +2
      Goto skipThis
      
    ### delete these comments to include a readme
    #MessageBox MB_YESNO "Would you like to view the README?" IDYES yes IDNO no
    #yes:
    #  Exec 'notepad.exe $INSTDIR\TSTool_README.txt'
    #  Goto next2
    #no:
    #  DetailPrint "Skipping README"
    #next2:
    
    MessageBox MB_YESNO "Would you like to run the program?" IDYES true IDNO false
    true:
      Exec '"$INSTDIR\bin\TSTool.exe"'
      Goto next
    false:
      DetailPrint "User chose to not start application"
    next:
       
                
    skipThis:
    
FunctionEnd


########################################
# FUNCTION: .onInit
#
# BRIEF: NSIS default function
#  executes on Init of Outfile created
#
########################################
Function .onInit
    
    
    InitPluginsDir
    !insertmacro MUI_INSTALLOPTIONS_EXTRACT_AS "externals\Rivertrak\installer\server_name.ini" "server_name.ini"
    
    # check user privileges and abort if not admin
    ClearErrors
    UserInfo::GetName
    IfErrors Win9x
    Pop $0
    UserInfo::GetAccountType
    Pop $1
    StrCmp $1 "Admin" 0 +3
        #MessageBox MB_OK 'User "$0" is in the Administrators group'
        Goto done
    StrCmp $1 "Power" 0 +3
        #MessageBox MB_OK 'User "$0" is in the Power Users group'
        Goto InsufficientRights
    StrCmp $1 "User" 0 +3
        #MessageBox MB_OK 'User "$0" is just a regular user'
        Goto InsufficientRights
    StrCmp $1 "Guest" 0 +3
        #MessageBox MB_OK 'User "$0" is a guest'
        Goto InsufficientRights
    MessageBox MB_OK "Unknown error"
    Goto done

    Win9x:
        # This one means you don't need to care about admin or
        # not admin because Windows 9x doesn't either
        MessageBox MB_OK "Error! This DLL can't run under Windows 9x!"
        Abort
        
    InsufficientRights:
        MessageBox MB_OK "You must log on using an account with administrator$\nprivileges to install this application."
        Abort
        
    done:
    
    
FunctionEnd

