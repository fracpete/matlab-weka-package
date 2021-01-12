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

import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

    // TODO

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

    // TODO

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

    // TODO

    return result.toArray(new String[0]);
  }

  /**
   * Resets the Loader ready to read a new data set
   *
   * @throws IOException        if something goes wrong
   */
  public void reset() throws IOException {
    m_structure = null;

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
   * the supplied url.
   *
   * @param url 	the source url.
   * @throws IOException        if an error occurs
   */
  public void setSource(URL url) throws IOException {
    m_structure = null;
    setRetrieval(NONE);
    setSource(url.openStream());
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
   * Return the full data set. If the structure hasn't yet been determined
   * by a call to getStructure then method should do so before processing
   * the rest of the data set.
   *
   * @return 			the structure of the data set as an empty 
   * 				set of Instances
   * @throws IOException        if there is no source or parsing fails
   */
  public Instances getDataSet() throws IOException {
    Instance		inst;

    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Cannot mix getting Instances in both incremental and batch modes");

    setRetrieval(BATCH);
    if (m_structure == null)
      getStructure();

    // TODO

    return null;
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
