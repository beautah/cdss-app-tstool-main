package DWR.DMI.tstool;

import java.util.List;

import rti.tscommandprocessor.commands.rccacis.RccAcisDataStore;
import rti.tscommandprocessor.commands.rccacis.RccAcisStationTimeSeriesMetadata;

import RTi.Util.GUI.JWorksheet_AbstractRowTableModel;

/**
This class is a table model for time series header information for TS instances from RCC ACIS web services.
By default the sheet will contain row and column numbers.
The alias can be treated as a hidden column.
This class may eventually be moved to the RTi.TS package.
*/
public class TSTool_RccAcis_TableModel extends JWorksheet_AbstractRowTableModel
{
/**
Data store for data.
*/
private RccAcisDataStore __dataStore = null;

/**
Number of columns in the table model (with the alias).
*/
private int __COLUMNS = 27;

/**
Absolute column indices, for column lookups (includes the alias).
*/
public final int COL_ID_ACIS = 0;
public final int COL_ID_WBAN = 1;
public final int COL_ID_COOP = 2;
public final int COL_ID_FAA = 3;
public final int COL_ID_WMO = 4;
public final int COL_ID_ICAO = 5;
public final int COL_ID_NWSLI = 6;
public final int COL_ID_THREAD_EX = 7;
public final int COL_ID_AWDN = 8;
public final int COL_NAME = 9; // Station name
public final int COL_DATA_SOURCE= 10;
public final int COL_DATA_TYPE_MAJOR = 11;
public final int COL_DATA_TYPE_NAME = 12;
public final int COL_METHOD = 13;
public final int COL_TIME_STEP = 14;
public final int COL_UNITS = 15;
public final int COL_START = 16;
public final int COL_END = 17;
public final int COL_POSTAL = 18;
public final int COL_FIPS_COUNTY = 19;
public final int COL_CLIM_DIV = 20;
public final int COL_NWS_CWA = 21;
public final int COL_HUC = 22;
public final int COL_LONG = 23;
public final int COL_LAT = 24;
public final int COL_ELEV = 25;
public final int COL_DATA_STORE_NAME = 26;

/**
Constructor.  This builds the model for displaying the given time series data.
@param data the Vector of TS that will be displayed in the table (null is allowed).
@throws Exception if an invalid results passed in.
*/
public TSTool_RccAcis_TableModel ( RccAcisDataStore dataStore, List data )
throws Exception
{	if ( data == null ) {
        _rows = 0;
    }
    else {
        _rows = data.size();
    }
    _data = data;
    __dataStore = dataStore;
}

/**
From AbstractTableModel.  Returns the class of the data stored in a given
column.  All values are treated as strings.
@param columnIndex the column for which to return the data class.
*/
public Class getColumnClass (int columnIndex) {
	switch (columnIndex) {
		case COL_ID_ACIS: return String.class;
		case COL_ID_WBAN: return String.class;
		case COL_ID_COOP: return String.class;
		case COL_ID_FAA: return String.class;
		case COL_ID_WMO: return String.class;
		case COL_ID_ICAO: return String.class;
		case COL_ID_NWSLI: return String.class;
		case COL_ID_THREAD_EX: return String.class;
		case COL_ID_AWDN: return String.class;
		case COL_NAME: return String.class;
		case COL_DATA_SOURCE: return String.class;
		case COL_DATA_TYPE_MAJOR: return String.class;
		case COL_DATA_TYPE_NAME: return String.class;
		case COL_METHOD: return String.class;
		case COL_TIME_STEP: return String.class;
		case COL_UNITS: return String.class;
		case COL_START: return String.class;
		case COL_END: return String.class;
		case COL_POSTAL: return String.class;
		case COL_FIPS_COUNTY: return String.class;
		case COL_CLIM_DIV: return String.class;
		case COL_NWS_CWA: return String.class;
		case COL_HUC: return String.class;
	    case COL_LONG: return Double.class;
		case COL_LAT: return Double.class;
		case COL_ELEV: return Double.class;
		case COL_DATA_STORE_NAME: return String.class;
		default: return String.class;
	}
}

/**
From AbstractTableModel.  Returns the number of columns of data.
@return the number of columns of data.
*/
public int getColumnCount() {
	return __COLUMNS;
}

/**
From AbstractTableMode.  Returns the name of the column at the given position.
@return the name of the column at the given position.
*/
public String getColumnName(int columnIndex) {
	switch (columnIndex) {
	    case COL_ID_ACIS: return "ACIS\nID";
	    case COL_ID_WBAN: return "WBAN\nID";
	    case COL_ID_COOP: return "COOP\nID";
	    case COL_ID_FAA: return "FAA\nID";
	    case COL_ID_WMO: return "WMO\nID";
	    case COL_ID_ICAO: return "ICAO\nID";
	    case COL_ID_NWSLI: return "NWSLI\nID";
	    case COL_ID_THREAD_EX: return "ThreadEx\nID";
	    case COL_ID_AWDN: return "AWDN\nID";
		case COL_NAME: return "Name/\nDescription";
		case COL_DATA_SOURCE: return "Data\nSource";
		case COL_DATA_TYPE_MAJOR: return "Data Type\n(Major)";
		case COL_DATA_TYPE_NAME: return "Data Type\n(Name)";
		case COL_METHOD: return "\nMethod";
		case COL_TIME_STEP: return "Time\nStep";
		case COL_UNITS: return "\nUnits";
		case COL_START: return "\nStart";
		case COL_END: return "\nEnd";
	    case COL_POSTAL: return "Postal\nCode";
	    case COL_FIPS_COUNTY: return "FIPS\nCounty";
	    case COL_CLIM_DIV: return "Climate\nDivision";
	    case COL_NWS_CWA: return "NWS\nCWA";
	    case COL_HUC: return "Basin\n(HUC)";
	    case COL_LONG: return "\nLongitude";
	    case COL_LAT: return "\nLatitude";
	    case COL_ELEV: return "Elevation\n(FT)";
		case COL_DATA_STORE_NAME: return "Data Store\nName";
		default: return "";
	}
}

/**
Returns the format to display the specified column.
@param column column for which to return the format.
@return the format (as used by StringUtil.formatString()).
*/
public String getFormat ( int column ) {
	switch (column) {
	    case COL_LONG: return "%.6f";
    	case COL_LAT: return "%.6f";
        case COL_ELEV: return "%.2f";
		default: return "%s";
	}
}

/**
From AbstractTableMode.  Returns the number of rows of data in the table.
*/
public int getRowCount() {
	return _rows;
}

/**
From AbstractTableMode.  Returns the data that should be placed in the JTable at the given row and column.
@param row the row for which to return data.
@param col the absolute column for which to return data.
@return the data that should be placed in the JTable at the given row and column.
*/
public Object getValueAt(int row, int col)
{	// make sure the row numbers are never sorted ...

	if (_sortOrder != null) {
		row = _sortOrder[row];
	}

	RccAcisStationTimeSeriesMetadata ts = (RccAcisStationTimeSeriesMetadata)_data.get(row);
	if ( ts == null ) {
		return "";
	}
	switch (col) {
	    case COL_ID_ACIS: return ts.getUid();
	    case COL_ID_WBAN: return ts.getIDSpecific("WBAN");
	    case COL_ID_COOP: return ts.getIDSpecific("COOP");
	    case COL_ID_FAA: return ts.getIDSpecific("FAA");
	    case COL_ID_WMO: return ts.getIDSpecific("WMO");
	    case COL_ID_ICAO: return ts.getIDSpecific("ICAO");
	    case COL_ID_NWSLI: return ts.getIDSpecific("NWSLI");
	    case COL_ID_THREAD_EX: return ts.getIDSpecific("ThreadEX");
	    case COL_ID_AWDN: return ts.getIDSpecific("AWDN");
		case COL_NAME: return ts.getName();
		case COL_DATA_SOURCE: return ts.getVariable().getSource();
		case COL_DATA_TYPE_MAJOR: return "" + ts.getVariable().getMajor();
		case COL_DATA_TYPE_NAME: return "" + ts.getVariable().getName();
	    case COL_METHOD: return ts.getVariable().getMethod();
		case COL_TIME_STEP: return ts.getVariable().getReportInterval();
		case COL_UNITS: return ts.getVariable().getUnits();
		case COL_START: return ts.getValid_daterange()[0];
		case COL_END: return ts.getValid_daterange()[1];
		case COL_POSTAL: return ts.getPostal();
	    case COL_FIPS_COUNTY: return ts.getCounty();
	    case COL_CLIM_DIV: return "";
	    case COL_NWS_CWA: return "";
	    case COL_HUC: return "";
	    case COL_LONG:
	        double lon = ts.getLl()[0];
	        if ( Double.isNaN(lon)) {
	            return null;
	        }
	        else {
	            return lon;
	        }
	    case COL_LAT:
            double lat = ts.getLl()[1];
            if ( Double.isNaN(lat)) {
                return null;
            }
            else {
                return lat;
            }
	    case COL_ELEV:
            double elev = ts.getElev();
            if ( Double.isNaN(elev)) {
                return null;
            }
            else {
                return elev;
            }
		case COL_DATA_STORE_NAME: return __dataStore.getName();
		default: return "";
	}
}

/**
Returns an array containing the column widths (in number of characters).
@return an integer array containing the widths for each field.
*/
public int[] getColumnWidths() {
	int[] widths = new int[__COLUMNS];
	widths[COL_ID_ACIS] = 4;
	widths[COL_ID_WBAN] = 4;
    widths[COL_ID_COOP] = 4;
    widths[COL_ID_FAA] = 4;
    widths[COL_ID_WMO] = 4;
    widths[COL_ID_ICAO] = 4;
    widths[COL_ID_NWSLI] = 4;
    widths[COL_ID_THREAD_EX] = 6;
    widths[COL_ID_AWDN] = 4;
	widths[COL_NAME] = 20;
	widths[COL_DATA_SOURCE] = 10;
	widths[COL_DATA_TYPE_MAJOR] = 6;
	widths[COL_DATA_TYPE_NAME] = 15;
    widths[COL_METHOD] = 4;
	widths[COL_TIME_STEP] = 4;
	widths[COL_UNITS] = 6;
	widths[COL_START] = 7;
	widths[COL_END] = 7;
	widths[COL_POSTAL] = 4;
    widths[COL_FIPS_COUNTY] = 4;
    widths[COL_CLIM_DIV] = 6;
    widths[COL_NWS_CWA] = 4;
    widths[COL_HUC] = 6;
    widths[COL_LONG] = 6;
    widths[COL_LAT] = 6;
    widths[COL_ELEV] = 6;
	widths[COL_DATA_STORE_NAME] = 10;
	return widths;
}

}