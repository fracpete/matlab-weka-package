/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * MatlabMatLoader.java
 * Copyright (C) 2021 FracPete
 *
 */

package weka.core.converters;

import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.AbstractCharBase;
import us.hebi.matlab.mat.types.Cell;
import us.hebi.matlab.mat.types.Char;
import us.hebi.matlab.mat.types.MatFile;
import us.hebi.matlab.mat.types.Matrix;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Reads binary Matlab .mat files.
 *
 * @author FracPete (fracpete at gmail dot com)
 * @see Loader
 */
public class MatlabMatLoader
  extends AbstractFileLoader
  implements BatchConverter, OptionHandler {

  /** for serialization */
  private static final long serialVersionUID = 3764533621135196582L;

  /** the default file extension */
  public final static String FILE_EXTENSION = ".mat";

  /** the default entry name. */
  public final static String DEFAULT_ENTRY_NAME = "";

  /** the name of the entry to retrieve. */
  protected String m_EntryName = DEFAULT_ENTRY_NAME;

  /** the default max number of values for nominal attributes. */
  public final static int DEFAULT_MAX_NOMINAL_VALUES = 25;

  /** the maximum number of values for nominal attributes. */
  protected int m_MaxNominalValues = DEFAULT_MAX_NOMINAL_VALUES;

  /** the loaded data. */
  protected Instances m_Data;

  /**
   * Returns a string describing this Loader
   *
   * @return 		a description of the Loader suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Reads binary Matlab .mat files.";
  }

  /**
   * Sets the entry to retrieve; first if empty.
   *
   * @param value	the name
   */
  public void setEntryName(String value) {
    m_EntryName = value;
  }

  /**
   * Returns the entry name to retrieve; first if empty.
   *
   * @return		the name
   */
  public String getEntryName() {
    return m_EntryName;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String entryNameTipText() {
    return "The entry name to retrieve; first if empty.";
  }

  /**
   * Sets the maximum number of values for nominal attributes.
   *
   * @param value	the maximum; -1 for always nominal, 0 for always string
   */
  public void setMaxNominalValues(int value) {
    if (value < -1)
      value = -1;
    m_MaxNominalValues = value;
  }

  /**
   * Returns the maximum number of values for nominal attributes.
   *
   * @return		the maximum; -1 for always nominal, 0 for always string
   */
  public int getMaxNominalValues() {
    return m_MaxNominalValues;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String maxNominalValuesTipText() {
    return "The maximum number of values for nominal attributes, beyond that "
      + "it is considered a string attribute; use -1 to always convert to "
      + "nominal and 0 to always convert to string.";
  }

  /**
   * Get the file extension used for libsvm files
   *
   * @return 		the file extension
   */
  public String getFileExtension() {
    return FILE_EXTENSION;
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  public String[] getFileExtensions() {
    return new String[]{FILE_EXTENSION};
  }

  /**
   * Returns a description of the file type.
   *
   * @return 		a short file description
   */
  public String getFileDescription() {
    return "Matlab .mat files";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();

    result.addElement(new Option("\tThe entry name to retrieve; first if empty\n"
      + "\t(default: " + DEFAULT_ENTRY_NAME + ")",
      "entry-name", 1, "-entry-name <name>"));

    result.addElement(new Option("\tThe maximum number of distinct values a NOMINAL attribute\n"
      + "\tcan have; beyond that it is considered a STRING attribute.\n"
      + "\tUse -1 to always convert to NOMINAL, 0 to always convert to STRING.\n"
      + "\t(default: " + DEFAULT_MAX_NOMINAL_VALUES + ")",
      "max-nominal-values", 1, "-max-nominal-values <int>"));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmp;

    tmp = Utils.getOption("entry-name", options);
    if (!tmp.isEmpty())
      setEntryName(tmp);
    else
      setEntryName(DEFAULT_ENTRY_NAME);

    tmp = Utils.getOption("max-nominal-values", options);
    if (!tmp.isEmpty())
      setMaxNominalValues(Integer.parseInt(tmp));
    else
      setMaxNominalValues(DEFAULT_MAX_NOMINAL_VALUES);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the Apriori object.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add("-entry-name");
    result.add(getEntryName());

    result.add("-max-nominal-values");
    result.add("" + getMaxNominalValues());

    return result.toArray(new String[0]);
  }

  /**
   * Resets the Loader ready to read a new data set
   *
   * @throws IOException        if something goes wrong
   */
  public void reset() throws IOException {
    m_structure = null;
    m_Data      = null;

    setRetrieval(NONE);

    if (m_File != null)
      setFile(new File(m_File));
  }

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied File object.
   *
   * @param file 		the source file.
   * @throws IOException        if an error occurs
   */
  public void setSource(File file) throws IOException {
    m_structure = null;
    m_Data      = null;

    setRetrieval(NONE);

    if (file == null)
      throw new IOException("Source file object is null!");

    try {
      if (file.getName().endsWith(FILE_EXTENSION_COMPRESSED))
	setSource(new GZIPInputStream(new FileInputStream(file)));
      else
	setSource(new FileInputStream(file));
    }
    catch (FileNotFoundException ex) {
      throw new IOException("File not found");
    }

    m_sourceFile = file;
    m_File       = file.getAbsolutePath();
  }

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied InputStream.
   *
   * @param in 			the source InputStream.
   * @throws IOException        if initialization of reader fails.
   */
  public void setSource(InputStream in) throws IOException {
    m_File = (new File(System.getProperty("user.dir"))).getAbsolutePath();
  }

  /**
   * Determines and returns (if possible) the structure (internally the 
   * header) of the data set as an empty set of instances.
   * If not yet read, also reads the full dataset into m_Data.
   *
   * @return 			the structure of the data set as an empty set 
   * 				of Instances
   * @throws IOException        if an error occurs
   */
  public Instances getStructure() throws IOException {
    if (m_structure != null)
      return new Instances(m_structure, 0);

    try {
      return new Instances(getDataSet(), 0);
    }
    catch (IOException ioe) {
      // just re-throw it
      throw ioe;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @param array	the array to convert
   * @throws IOException if something goes wrong with the conversion
   * @return the converted data
   */
  protected Instances convert(us.hebi.matlab.mat.types.Array array) throws IOException {
    Matrix			matrix;
    Char 			matChar;
    Cell 			matCell;
    int				i;
    int				n;
    Object[][]			data;
    boolean[]			numeric;
    ArrayList<Attribute>	atts;
    double[]			values;
    String			cell;
    Map<Integer,Set<String>> 	unique;
    List<String>		labels;

    if (array.getNumDimensions() > 2)
      throw new IllegalStateException("Cannot handle arrays with more than two dimensions, received: " + array.getNumDimensions());
    matrix = null;
    if (array instanceof Matrix)
      matrix = (Matrix) array;
    matCell = null;
    if (array instanceof Cell)
      matCell = (Cell) array;
    matChar = null;
    if (array instanceof Char)
      matChar = (Char) array;
    if ((matrix == null) && (matCell == null) && (matChar == null))
      throw new IllegalStateException("Unhandled array type: " + array.getClass().getName());

    // convert matrix
    data = new Object[array.getNumRows()][array.getNumCols()];
    numeric = new boolean[array.getNumCols()];
    for (i = 0; i < numeric.length; i++)
      numeric[i] = true;
    for (n = 0; n < array.getNumRows(); n++) {
      for (i = 0; i < array.getNumCols(); i++) {
        if (matrix != null) {
	  data[n][i] = matrix.getDouble(n, i);
	}
        else if (matCell != null) {
          if (matCell.get(n, i) instanceof AbstractCharBase) {
	    data[n][i] = ((AbstractCharBase) matCell.get(n, i)).asCharSequence().toString();
	    numeric[i] = false;
	  }
          else {
            cell = matCell.get(n, i).toString();
            try {
              data[n][i] = Double.parseDouble(cell);
	    }
	    catch (Exception e) {
              data[n][i] = cell;
	      numeric[i] = false;
	    }
	  }
	}
        else if (matChar != null) {
	  data[n][i] = matChar.getChar(n, i);
          numeric[i] = false;
	}
      }
    }

    // determine unique values
    unique = new HashMap<Integer, Set<String>>();
    for (i = 0; i < array.getNumCols(); i++) {
      if (!numeric[i]) {
	for (n = 0; n < array.getNumRows(); n++) {
	  if (!unique.containsKey(i))
	    unique.put(i, new HashSet<String>());
	  unique.get(i).add((String) data[n][i]);
	}
      }
    }

    // create dataset
    // 1. header
    atts = new ArrayList<Attribute>();
    for (i = 0; i < numeric.length; i++) {
      if (numeric[i]) {
	atts.add(new Attribute("col-" + (i + 1)));
      }
      else {
        // nominal
        if ((m_MaxNominalValues == -1) || (unique.get(i).size() <= m_MaxNominalValues)) {
          labels = new ArrayList<String>(unique.get(i));
          Collections.sort(labels);
	  atts.add(new Attribute("col-" + (i + 1), labels));
	}
        // string
        else {
	  atts.add(new Attribute("col-" + (i + 1), (List<String>) null));
	}
      }
    }
    m_Data = new Instances(retrieveFile().getName(), atts, array.getNumRows());

    // 2. data
    for (n = 0; n < array.getNumRows(); n++) {
      values = new double[m_Data.numAttributes()];
      for (i = 0; i < array.getNumCols(); i++) {
        if (m_Data.attribute(i).isNumeric())
          values[i] = (Double) data[n][i];
        else if (m_Data.attribute(i).isNominal())
          values[i] = m_Data.attribute(i).indexOfValue((String) data[n][i]);
        else
          values[i] = m_Data.attribute(i).addStringValue((String) data[n][i]);
      }
      m_Data.add(new DenseInstance(1.0, values));
    }

    return m_Data;
  }

  /**
   * Return the full data set. If the structure hasn't yet been determined
   * by a call to getStructure then method should do so before processing
   * the rest of the data set.
   *
   * @return 			the structure of the data set as an empty 
   * 				set of Instances
   * @throws IOException        if there is no source or parsing fails
   */
  public Instances getDataSet() throws IOException {
    Mat5File				mat5;
    us.hebi.matlab.mat.types.Array	array;
    int					i;

    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes");

    setRetrieval(BATCH);

    mat5  = Mat5.readFromFile(retrieveFile());
    array = null;
    i     = 0;
    System.err.println("Entries in: " + retrieveFile());
    for (MatFile.Entry entry: mat5.getEntries()) {
      System.err.println((i+1) + ": " + entry.getName());
      if (entry.getName().equals(m_EntryName) || (m_EntryName.isEmpty() && (array == null)))
        array = entry.getValue();
      i++;
    }
    if (array == null)
      throw new IOException("Failed to load array with name: " + m_EntryName);

    m_Data = convert(array);
    m_structure = new Instances(m_Data, 0);

    return m_Data;
  }

  /**
   * CommonCSVLoader is unable to process a data set incrementally.
   *
   * @param structure		ignored
   * @return 			never returns without throwing an exception
   * @throws IOException        always. CommonCSVLoader is unable to process a
   * 				data set incrementally.
   */
  public Instance getNextInstance(Instances structure) throws IOException {
    throw new IOException("Incremental loading not supported!");
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1 $");
  }

  /**
   * Main method.
   *
   * @param args 	should contain the name of an input file.
   */
  public static void main(String[] args) {
    runFileLoader(new MatlabMatLoader(), args);
  }
}
