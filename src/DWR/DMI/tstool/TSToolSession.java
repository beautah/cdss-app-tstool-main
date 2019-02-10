// TSToolSession - Class to maintain TSTool session information such as the history of command files opened.

/* NoticeStart

TSTool
TSTool is a part of Colorado's Decision Support Systems (CDSS)
Copyright (C) 1994-2019 Colorado Department of Natural Resources

TSTool is free software:  you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TSTool is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TSTool.  If not, see <https://www.gnu.org/licenses/>.

NoticeEnd */

package DWR.DMI.tstool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import RTi.Util.IO.IOUtil;
import RTi.Util.IO.PropList;
import RTi.Util.Message.Message;

/**
Class to maintain TSTool session information such as the history of command files opened.
Class to maintain TSTool session information such as the history of command files opened.
A singleton instance should be retrieve using the getInstance() method.
*/
public class TSToolSession
{

/**
Global value that indicates if the command file history is being written.
Need to handle because if the file is being modified at the same time exceptions will be thrown.
*/
private boolean historyBeingWritten = false;

/**
Global value that indicates if the UI state file is being written.
Need to handle because if the file is being modified at the same time exceptions will be thrown.
*/
private boolean uiStateBeingWritten = false;

/**
 * List of properties for the UI state, such as last selections in wizards, choices, etc.
 */
private PropList uiStateProps = new PropList("ui-state");

/**
 * Private singleton instance.
 * Instance is created in getInstance().
 */
private static TSToolSession instance = null;

/**
 * Major software version, used for folder below .tstool/.
 */
private int majorVersion = 0; // 0 will be an obvious error if a folder is created

/**
Private constructor for the session instance.
@param majorVersion the major version of TSTool, necessary because user files are organized by TSTool version.
*/
private TSToolSession ( int majorVersion )
{	// Read UI state properties so they are available for interaction.
	// They will be written when TSTool closes, and at other intermediate points, as appropriate,
	// by calling writeUIState().
	this.majorVersion = majorVersion;
	readUIState();
}

/**
Create a new system configuration file in user files.
This is used when transitioning from TSTool earlier than 11.09.00 to version later.
@return true if the file was created, false for all other cases.
*/
public boolean createConfigFile ( )
{
	if ( getUserFolder().equals("/") ) {
		// Don't allow files to be created under root on Linux
		return false;
	}

	// Create the configuration folder if necessary
	File f = new File(getConfigFile());
	File folder = f.getParentFile();
	if ( !folder.exists() ) {
		if ( !folder.mkdirs() ) {
			// Unable to make folder
			return false;
		}
	}
	try {
		String nl = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder ( "# TSTool configuration file containing user settings, shared between TSTool versions" + nl );
		sb.append("# This file indicates which datastore software features should be enabled." + nl );
		sb.append("# Disabling datastore types that are not used can improve TSTool performance and simplifies the user interface." + nl );
		sb.append("# Refer to the TSTool.cfg file under the software installation folder for global configuration properties." + nl );
		sb.append("# User settings in this file will override the installation settings." + nl );
		sb.append(nl);
		// Include a line for HydroBase since it often needs to be disabled on computers where HydroBase is not used
		sb.append("HydroBaseEnabled = true" + nl );
		IOUtil.writeFile ( f.getPath(), sb.toString() );
		return true;
	}
	catch ( Exception e ) {
		return false;
	}
}

/**
Create the datastores folder if necessary.
@return true if datastores folder exists and is writable, false otherwise.
*/
public boolean createDatastoresFolder () {
	String datastoreFolder = getDatastoresFolder();
	// Do not allow datastore folder to be created under Linux root but allow TSTool to run
	if ( datastoreFolder.equals("/") ) {
		return false;
	}
	File f = new File(datastoreFolder);
	if ( !f.exists() ) {
		try {
			f.mkdirs();
		}
		catch ( SecurityException e ) {
			return false;
		}
	}
	else {
		// Make sure it is writable
		if ( !f.canWrite() ) {
			return false;
		}
	}
	return true;
}

/**
Create the "logs" folder if necessary.
@return true if "logs" folder exists and is writable, false otherwise.
*/
public boolean createLogsFolder () {
	String logsFolder = getLogsFolder();
	// Do not allow log file to be created under Linux root but allow TSTool to run
	if ( logsFolder.equals("/") ) {
		return false;
	}
	File f = new File(logsFolder);
	if ( !f.exists() ) {
		try {
			f.mkdirs();
		}
		catch ( SecurityException e ) {
			return false;
		}
	}
	else {
		// Make sure it is writable
		if ( !f.canWrite() ) {
			return false;
		}
	}
	return true;
}

/**
Create the "plugins" folder if necessary.
@return true if "plugins" folder exists and is writable, false otherwise.
*/
public boolean createPluginsFolder () {
	String pluginsFolder = getPluginsFolder();
	// Do not allow log file to be created under Linux root but allow TSTool to run
	if ( pluginsFolder.equals("/") ) {
		return false;
	}
	File f = new File(pluginsFolder);
	if ( !f.exists() ) {
		try {
			f.mkdirs();
		}
		catch ( SecurityException e ) {
			return false;
		}
	}
	else {
		// Make sure it is writable
		if ( !f.canWrite() ) {
			return false;
		}
	}
	return true;
}

/**
Create the system folder if necessary.
@return true if system folder exists and is writable, false otherwise.
*/
public boolean createSystemFolder () {
	String systemFolder = getSystemFolder();
	// Do not allow system folder to be created under Linux root but allow TSTool to run
	if ( systemFolder.equals("/") ) {
		return false;
	}
	File f = new File(systemFolder);
	if ( !f.exists() ) {
		try {
			f.mkdirs();
		}
		catch ( SecurityException e ) {
			return false;
		}
	}
	else {
		// Make sure it is writable
		if ( !f.canWrite() ) {
			return false;
		}
	}
	return true;
}

/**
Return the value of the requested property from the user's TSTool configuration file.
This reads the configuration file each time to ensure synchronization.
@param propName property name
@return the value of the property or null if file or property is not found
*/
public String getConfigPropValue ( String propName )
{
	String configFile = getConfigFile();
	File f = new File(configFile);
	if ( !f.exists() || !f.canRead() ) {
		return null;
	}
	PropList props = new PropList("TSToolUserConfig");
	props.setPersistentName(configFile);
	try {
		props.readPersistent();
		return props.getValue(propName);
	}
	catch ( Exception e ) {
		return null;
	}
}

/**
Return the name of the user's TSTool configuration file.
*/
public String getConfigFile ()
{
	String logFile = getSystemFolder() + File.separator + "TSTool.cfg";
	//Message.printStatus(1,"","Config file is \"" + logFolder + "\"");
	return logFile;
}

/**
Return the name of the datastore configuration folder.
@return the "datastores" folder path (no trailing /).
*/
public String getDatastoresFolder ()
{
	// 12.06.00 and earlier (not under version folder and singular)...
	//String datastoreFolder = getUserFolder() + File.separator + "datastore";
	// 12.07.00 and later (under version folder and plural, which seems more appropriate)
	String datastoresFolder = getMajorVersionFolder() + File.separator + "datastores";
	//Message.printStatus(1,"","Datastores folder is \"" + datastoreFolder + "\"");
	return datastoresFolder;
}

/**
 * Return the the File for the graph template file.
 * The user's file location for templates is prepended to the specific file.
 * @param tspFilename a *.tsp file, without leading path, one of the items from getGraphTemplateFileList().
 */
public File getGraphTemplateFile ( String tspFilename ) {
	return new File(getUserFolder() + File.separator + "template-graph" + File.separator + tspFilename );
}

/**
Return the list of graph templates.
*/
public List<File> getGraphTemplateFileList ()
{
	String graphTemplateFolder = getUserFolder() + File.separator + "template-graph";
	return IOUtil.getFilesMatchingPattern(graphTemplateFolder, "tsp", false);
}

/**
Return the name of the TSTool history file.
*/
public String getHistoryFile ()
{
	String historyFile = System.getProperty("user.home") + File.separator + ".tstool" + File.separator + "command-file-history.txt";
	//Message.printStatus(1,"","History file \"" + historyFile + "\"");
	return historyFile;
}

/**
 * Return the singleton instance of the TSToolSession.
 * This version must be called after the overloaded version that specifies the major version.
 * Otherwise, 0 is set as the major version.
 */
public static TSToolSession getInstance() {
	if ( instance == null ) {
		instance = new TSToolSession( 0 );
	}
	// Else instance is non-null and will be returned
	instance.initializeUserFiles(instance.getMajorVersion()); // Won't do anything if already initialized
	return instance;
}

/**
 * Return the singleton instance of the TSToolSession.
 * @param majorVersion the major version of TSTool, necessary because user files are organized by TSTool version.
 */
public static TSToolSession getInstance( int majorVersion ) {
	if ( instance == null ) {
		instance = new TSToolSession( majorVersion );
	}
	// Else instance is non-null and will be returned
	instance.initializeUserFiles(instance.getMajorVersion()); // Won't do anything if already initialized
	return instance;
}

/**
Return the name of the log file for the user.
*/
public String getLogFile ()
{
	String logFile = getLogsFolder() + File.separator + "TSTool_" + System.getProperty("user.name") + ".log";
	//Message.printStatus(1,"","Log folder is \"" + logFolder + "\"");
	return logFile;
}

/**
Return the name of the log file folder.
@return the "logs" folder path (no trailing /).
*/
public String getLogsFolder ()
{
	// 12.06.00 and earlier (not under version folder and singular)...
	//String logFolder = getUserFolder() + File.separator + "log";
	// 12.07.00 and later (under version folder and plural, which seems more appropriate)
	String logsFolder = getMajorVersionFolder() + File.separator + "logs";
	//Message.printStatus(1,"","Log folder is \"" + logFolder + "\"");
	return logsFolder;
}

/**
 * Return the major software version, used for top-level user files.
 * @return the software major version, used for top-level user files
 */
public int getMajorVersion () {
	return this.majorVersion;
}

/**
Return the folder to the major version:
<ul>
<li>	Windows:  C:\Users\UserName\.tstool\12</li>
<li>	Linux: /home/UserName/.tstool/12</li>
</ul>
*/
public String getMajorVersionFolder ()
{
	String majorVersionFolder = getUserFolder() + File.separator + getMajorVersion();
	//Message.printStatus(1,"","Major version folder is \"" + majorVersionFolder + "\"");
	return majorVersionFolder;
}

/**
Return the name of the plugins configuration folder.
@return the "plugins" folder path (no trailing /).
*/
public String getPluginsFolder ()
{
	// 12.06.00 and earlier was split into plugin-command and plugin-datastore
	// 12.07.00 and later (under version folder and plural, which seems more appropriate)
	String pluginsFolder = getMajorVersionFolder() + File.separator + "plugins";
	//Message.printStatus(1,"","Plugins folder is \"" + pluginsFolder + "\"");
	return pluginsFolder;
}

/**
Return the name of the system folder.
@return the "system" folder path (no trailing /).
*/
public String getSystemFolder ()
{

	// 12.06.00 and earlier (not under version folder)...
	//String systemFolder = getUserFolder() + File.separator + "system";
	// 12.07.00 and later (under version folder)
	String systemFolder = getMajorVersionFolder() + File.separator + "system";
	//Message.printStatus(1,"","System folder is \"" + systemFolder + "\"");
	return systemFolder;
}

/**
Return the name of the TSTool UI state file.
*/
public String getUIStateFile ()
{
	String uiStateFile = System.getProperty("user.home") + File.separator + ".tstool" + File.separator + "ui-state.txt";
	//Message.printStatus(1,"","UI state file \"" + uiStateFile + "\"");
	return uiStateFile;
}

/**
 * Return a UI state property, as a string.
 * @param propertyName name of property being requested.
 */
public String getUIStateProperty ( String propertyName ) {
	return this.uiStateProps.getValue(propertyName);
}

/**
Return the name of the TSTool user folder for the operating system, for example:
<ul>
<li>	Windows:  C:\Users\UserName\.tstool</li>
<li>	Linux: /home/UserName/.tstool</li>
</ul>
*/
public String getUserFolder ()
{
	String userFolder = System.getProperty("user.home") + File.separator + ".tstool";
	//Message.printStatus(1,"","User folder is \"" + userFolder + "\"");
	return userFolder;
}

/**
 * Initialize user files.
 * This method should be called at application startup to make sure that user files are created.
 * TSTool 12.06.00 and earlier used the following folder structure, using Windows as example:
 * 
 * <pre>
 * C:/Users/user/
 *   .tstool/
 *      datastore/
 *        *.cfg
 *      log/
 *        TSTool_user.log
 *      plugin-command/
 *        SomeCommand/
 *          bin/
 *            SomeCommand-Version.jar
 *          doc/
 *            SomeCommand.html
 *      plugin-datastore/
 *        SomeDatastore/
 *          bin/
 *            SomeDataStore.jar
 * </pre>
 * 
 * The above has proven to be problematic for a number of reasons including
 * 1) strict folder structure is prone to errors (flexible drop-in for jar files is better),
 * 2) TSTool version evolution is prone to breaking
 * 
 * Therefore the following alternative is now being implemented in TSTool 12.07.00:
 * 
 * <pre>
 * C:/Users/user/
 *   .tstool/
 *      12/
 *        datastores/
 *          somedatastore/
 *            somedatastore.cfg
 *        logs/
 *          TSTool-user.log
 *        plugins/
 *          someplugin/
 *            someplugin.jar
 *            supporting files
 *      13/
 *        ...
 *      14/
 *        ...
 * </pre>
 * 
 * Conventions will be used to manage files but users will be able to organize as they prefer.
 * The jar files can contain datastores and commands in the same jar file so as to
 * minimize duplicate deployment of code.
 * The use of a version folder is a compromise: users will need to use migration tools
 * to import previous version datastore configurations, etc., but the version folder
 * allows different major versions of TSTool to remain functional if major design changes occur.
 * @param majorVersion the major TSTool version, a parameter to allow calling multiple times if necessary
 * @return true if the files were initialized, false for all other cases.
 */
public boolean initializeUserFiles ( int version ) {
	String routine = getClass().getSimpleName() + ".initializeUserFiles";
	String userFolder = getUserFolder();
	if ( userFolder.equals("/") ) {
		// Don't allow files to be created under root on Linux
		Message.printWarning(3, routine, "Unable to create user files in root folder - need to run as normal user.");
		return false;
	}
	// Create the version folder if it does not exist
	String versionFolder = userFolder + File.separator + version;
	File f = new File(versionFolder);
	if ( !f.exists() ) {
		try {
			f.mkdirs();
		}
		catch ( SecurityException e ) {
			Message.printWarning(3, routine, "Could not create TSTool user files version folder \"" + versionFolder + "\" (" + e + ").");
			return false;
		}
	}
	else {
		// Make sure it is writeable
		if ( !f.canWrite() ) {
			Message.printWarning(3, routine, "TSTool user files version folder \"" + versionFolder + "\" is not writeable.");
			return false;
		}
	}
	// Create main folders under the version folder
	createDatastoresFolder();
	createLogsFolder();
	createPluginsFolder();
	createSystemFolder();
	return true;
}

/**
Push a new command file onto the history.  This reads the history, updates it, and writes it.
This is done because if multiple TSTool sessions are running they, will share the history.
@param commandFile full path to command file that has been opened
*/
public void pushHistory ( String commandFile )
{
	// Read the history file from the .tstool-history file
	List<String> history = readHistory();
	// Add in the first position so it will show up first in the File...Open... menu
	history.add(0, commandFile);
	// Process from the back so that old duplicates are removed and recent access is always at the top of the list
	// TODO SAM 2014-12-17 use a TSTool configuration file property to set cap
	int max = 100;
	String old;
	for ( int i = history.size() - 1; i >= 1; i-- ) {
		old = history.get(i);
		if ( i >= max ) {
			// Trim the history to the maximum
			history.remove(i);
		}
		else if ( old.equals(commandFile) || old.equals("") || old.startsWith("#")) {
			// Ignore comments, blank lines and duplicate to most recent access
			history.remove(i--);
		}
	}
	//Message.printStatus(2,"", "History length is " + history.size());
	// Write the updated history
	writeHistory(history);
}

/**
Read the history of command files that have been opened.
@return list of command files recently opened, newest first
*/
public List<String> readHistory()
{	//String routine = getClass().getSimpleName() + ".readHistory";
	try {
		List<String> history = IOUtil.fileToStringList(getHistoryFile());
		// Remove comment lines
		for ( int i = (history.size() - 1); i >= 0; i-- ) {
			String f = history.get(i);
			if ( f.startsWith("#") ) {
				history.remove(i);
			}
		}
		return history;
	}
	catch ( Exception e ) {
		// For now just swallow exception - may be because the history folder does not exist
		//Message.printWarning(3,routine,e);
		return new ArrayList<String>();
	}
}

/**
Read the UI state.  The UI state is saved as simple property=value text file in
the .tstool/ui-state.txt file.
Properties are saved in the uiStateProps PropList internally.
*/
public void readUIState()
{	//String routine = getClass().getSimpleName() + ".readUIState";
	try {
		this.uiStateProps = new PropList("ui-state");
		this.uiStateProps.setPersistentName(getUIStateFile());
		this.uiStateProps.readPersistent();
	}
	catch ( Exception e ) {
		// For now just swallow exception - may be because the UI state file has not
		// been created the first time.
		//Message.printWarning(3,routine,e);
	}
}

/**
 * Set a UI state property.
 * @propertyName name of the state property.
 * @propertyValue value of the property as a string.
 */
public void setUIStateProperty ( String propertyName, String propertyValue ) {
	this.uiStateProps.set(propertyName,propertyValue);
}

/**
Write the history of commands files that have been opened.
*/
private void writeHistory ( List<String> history )
{
	String nl = System.getProperty("line.separator");
	StringBuilder sb = new StringBuilder ( "# TSTool command file history, most recent at top, shared between TSTool instances" );
	
	if ( getUserFolder().equals("/") ) {
		// Don't allow files to be created under root on Linux
		return;
	}

	long ms = System.currentTimeMillis();
	while ( this.historyBeingWritten ) {
		// Need to wait until another operation finishes writing
		// But don't wait longer than a couple of seconds before moving on
		if ( (System.currentTimeMillis() - ms) > 2000 ) {
			break;
		}
	}
	// Now can continue
	try {
	
		for ( String s : history ) {
			sb.append(nl + s);
		}
		// Create the history folder if necessary
		File f = new File(getHistoryFile());
		File folder = f.getParentFile();
		if ( !folder.exists() ) {
			if ( !folder.mkdirs() ) {
				// Unable to make folder
				return;
			}
		}
		try {
			//Message.printStatus(1, "", "Writing history: " + sb );
			IOUtil.writeFile ( f.getPath(), sb.toString() );
		}
		catch ( Exception e ) {
			// Absorb exception for now
		}
	}
	finally {
		// Make sure to do the following so don't lock up
		this.historyBeingWritten = false;
	}
}

/**
Write the UI state properties.
*/
public void writeUIState ()
{
	if ( getUserFolder().equals("/") ) {
		// Don't allow files to be created under root on Linux
		return;
	}

	long ms = System.currentTimeMillis();
	while ( this.uiStateBeingWritten ) {
		// Need to wait until another operation finishes writing
		// But don't wait longer than a couple of seconds before moving on
		if ( (System.currentTimeMillis() - ms) > 2000 ) {
			break;
		}
	}
	// Now can continue
	this.uiStateBeingWritten = true;
	try {
		try {
			//Message.printStatus(1, "", "Writing UI state" );
			this.uiStateProps.writePersistent();
		}
		catch ( Exception e ) {
			// Absorb exception for now
		}
	}
	finally {
		// Make sure to do the following so don't lock up
		this.uiStateBeingWritten = false;
	}
}

}
