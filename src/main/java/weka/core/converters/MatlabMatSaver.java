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
 * MatlabMatSaver.java
 * Copyright (C) 2019-2020 FracPete
 *
 */

package weka.core.converters;

import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.Cell;
import us.hebi.matlab.mat.types.Sinks;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Writes binary Matlab .mat files.
 *
 * @author FracPete (fracpete at gmail dot com)
 * @see Saver
 */
public class MatlabMatSaver
  extends AbstractFileSaver
  implements BatchConverter {

  /** for serialization */
  private static final long serialVersionUID = -7226404765213522043L;

  /** the default entry name (header). */
  public final static String DEFAULT_ENTRY_NAME_HEADER = "header";

  /** the name of the entry to store the header under. */
  protected String m_EntryNameHeader = DEFAULT_ENTRY_NAME_HEADER;

  /** the default entry name (data). */
  public final static String DEFAULT_ENTRY_NAME_DATA = "data";

  /** the name of the entry to store the data under. */
  protected String m_EntryNameData = DEFAULT_ENTRY_NAME_DATA;

  /**
   * Constructor
   */
  public MatlabMatSaver() {
    resetOptions();
  }

  /**
   * Returns a string describing this Saver
   *
   * @return a description of the Saver suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return "Writes binary Matlab .mat files.";
  }

  /**
   * Sets the entry to use for the header.
   *
   * @param value	the name
   */
  public void setEntryNameHeader(String value) {
    m_EntryNameHeader = value;
  }

  /**
   * Returns the entry name to use for the header.
   *
   * @return		the name
   */
  public String getEntryNameHeader() {
    return m_EntryNameHeader;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String entryNameHeaderTipText() {
    return "The entry name to use for the header.";
  }

  /**
   * Sets the entry to use for the data.
   *
   * @param value	the name
   */
  public void setEntryNameData(String value) {
    m_EntryNameData = value;
  }

  /**
   * Returns the entry name to use the data.
   *
   * @return		the name
   */
  public String getEntryNameData() {
    return m_EntryNameData;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String entryNameDataTipText() {
    return "The entry name to use for the data.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();
    Enumeration enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    result.addElement(new Option("\tThe entry name to use for the header\n"
      + "\t(default: " + DEFAULT_ENTRY_NAME_HEADER + ")",
      "entry-name-header", 1, "-entry-name-header <name>"));

    result.addElement(new Option("\tThe entry name to use for the data\n"
      + "\t(default: " + DEFAULT_ENTRY_NAME_DATA + ")",
      "entry-name-data", 1, "-entry-name-data <name>"));

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
    String tmp;

    tmp = Utils.getOption("entry-name-header", options);
    if (!tmp.isEmpty())
      setEntryNameHeader(tmp);
    else
      setEntryNameHeader(DEFAULT_ENTRY_NAME_DATA);

    tmp = Utils.getOption("entry-name-data", options);
    if (!tmp.isEmpty())
      setEntryNameData(tmp);
    else
      setEntryNameData(DEFAULT_ENTRY_NAME_DATA);

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the object.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> result;

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

    result.add("-entry-name-header");
    result.add(getEntryNameHeader());

    result.add("-entry-name-data");
    result.add(getEntryNameData());

    return result.toArray(new String[0]);
  }

  /**
   * Returns a description of the file type.
   *
   * @return a short file description
   */
  @Override
  public String getFileDescription() {
    return new MatlabMatLoader().getFileDescription();
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    return new MatlabMatLoader().getFileExtensions();
  }

  /**
   * Resets the Saver
   */
  @Override
  public void resetOptions() {
    super.resetOptions();
    setFileExtension(MatlabMatLoader.FILE_EXTENSION);
  }

  /**
   * Returns the Capabilities of this saver.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enableAllClasses();
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.enable(Capability.NO_CLASS);

    return result;
  }

  /** Sets the writer to null. */
  public void resetWriter() {
    super.resetWriter();
  }

  /**
   * Writes the header to the file.
   *
   * @param mat5	the file to write to
   * @param data 	the data
   */
  protected void writeHeader(Mat5File mat5, Instances data) {
    Cell	cell;
    int		i;

    cell = Mat5.newCell(2, data.numAttributes());
    for (i = 0; i < data.numAttributes(); i++) {
      cell.set(0, i, Mat5.newString(data.attribute(i).name()));
      cell.set(1, i, Mat5.newString(Attribute.typeToStringShort(data.attribute(i).type())));
    }

    mat5.addArray(m_EntryNameHeader, cell);
  }

  /**
   * Writes the data to the file.
   *
   * @param mat5	the file to write to
   * @param data 	the data
   */
  protected void writeData(Mat5File mat5, Instances data) {
    Cell	cell;
    int		i;
    int		n;
    Instance	inst;

    cell = Mat5.newCell(data.numInstances(), data.numAttributes());
    for (n = 0; n < data.numInstances(); n++) {
      inst = data.instance(n);
      for (i = 0; i < data.numAttributes(); i++) {
        switch (data.attribute(i).type()) {
	  case Attribute.NUMERIC:
	    cell.set(n, i, Mat5.newScalar(inst.value(i)));
	    break;
	  default:
	    cell.set(n, i, Mat5.newString(inst.stringValue(i)));
	    break;
	}
      }
    }

    mat5.addArray(m_EntryNameHeader, cell);
  }

  /**
   * Writes a Batch of instances
   *
   * @throws IOException throws IOException if saving in batch mode is not
   *           possible
   */
  @Override
  public void writeBatch() throws IOException {
    Instances 		data;
    Mat5File		mat5;

    if (getInstances() == null)
      throw new IOException("No instances to save");

    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Batch and incremental saving cannot be mixed.");

    setRetrieval(BATCH);
    setWriteMode(WRITE);

    mat5 = Mat5.newMatFile();
    data = getInstances();
    writeHeader(mat5, data);
    writeData(mat5, data);

    mat5.writeTo(Sinks.newStreamingFile(retrieveFile()));

    setWriteMode(WAIT);
    resetWriter();
    setWriteMode(CANCEL);
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1 $");
  }

  /**
   * Main method.
   *
   * @param args should contain the options of a Saver.
   */
  public static void main(String[] args) {
    runFileSaver(new MatlabMatSaver(), args);
  }
}
