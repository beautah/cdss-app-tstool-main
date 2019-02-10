// TSTool - Main (application startup) class for CDSS version of TSTool.

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

package cdss.app.tstool;

import javax.swing.JApplet;

import DWR.DMI.tstool.TSToolMain;

/**
public class TSTool extends JApplet
Main (application startup) class for CDSS version of TSTool.  This class will start the TSTool GUI
or run the TSCommandProcessor in batch mode with a command file.
*/
public class TSTool extends JApplet
{

/**
Instantiates the application instance as an applet.
*/
public void init()
{	// The init() method is not static so must declare an instance.
    TSToolMain tstool = new TSToolMain();
    tstool.init();
}
    
/**
Start the main application instance.
@param args Command line arguments.
*/
public static void main ( String args[] )
{	TSToolMain.main(args);
}

}
