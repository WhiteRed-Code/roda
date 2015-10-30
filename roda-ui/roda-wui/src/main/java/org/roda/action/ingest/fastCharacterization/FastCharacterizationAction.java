/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.action.ingest.fastCharacterization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.roda.action.orchestrate.Plugin;
import org.roda.action.orchestrate.PluginException;
import org.roda.core.common.InvalidParameterException;
import org.roda.core.data.PluginParameter;
import org.roda.core.data.Report;
import org.roda.index.IndexService;
import org.roda.model.AIP;
import org.roda.model.ModelService;
import org.roda.storage.StorageService;

public class FastCharacterizationAction implements Plugin<AIP> {
  private final Logger logger = Logger.getLogger(getClass());

  @Override
  public void init() throws PluginException {
  }

  @Override
  public void shutdown() {
    // do nothing
  }

  @Override
  public String getName() {
    return "Fast characterization action";
  }

  @Override
  public String getDescription() {
    return "Update the premis files with the format identification";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public List<PluginParameter> getParameters() {
    return new ArrayList<>();
  }

  @Override
  public Map<String, String> getParameterValues() {
    return new HashMap<>();
  }

  @Override
  public void setParameterValues(Map<String, String> parameters) throws InvalidParameterException {
    // no params
  }

  @Override
  public Report execute(IndexService index, ModelService model, StorageService storage, List<AIP> list)
    throws PluginException {
    // try {
    // Path rodaConfigPath = RodaCoreFactory.getConfigPath();
    // Path signaturePath = rodaConfigPath
    // .resolve(RodaCoreFactory.getRodaConfigurationAsString("tools", "droid",
    // "signatureFile"));
    // DroidUtils droidUtils = DroidUtils.getInstance(signaturePath);
    // Path temp = Files.createTempDirectory("temp");
    // for (AIP aip : list) {
    // logger.debug("Processing AIP " + aip.getId());
    // try {
    // for (String representationID : aip.getRepresentationIds()) {
    // logger.debug("Processing representation " + representationID + " of AIP "
    // + aip.getId());
    // Representation representation = model.retrieveRepresentation(aip.getId(),
    // representationID);
    // for (String fileID : representation.getFileIds()) {
    // logger.debug(
    // "Processing file " + fileID + " of representation " + representationID +
    // " from AIP " + aip.getId());
    // String fileName = fileID + ".premis.xml";
    // File file = model.retrieveFile(aip.getId(), representationID, fileID);
    // Binary binary = storage.getBinary(file.getStoragePath());
    // Path pathFile = Paths.get(temp.toString(),
    // file.getStoragePath().getName());
    // Files.copy(binary.getContent().createInputStream(), pathFile,
    // StandardCopyOption.REPLACE_EXISTING);
    // FileFormat format = droidUtils.execute(pathFile);
    //
    // RepresentationFilePreservationObject premisObject =
    // PremisUtils.getPremisFile(storage, aip.getId(),
    // representationID, fileName);
    // premisObject = PremisUtils.addFormatToPremis(premisObject, format);
    //
    // Path premis = Files.createTempFile(file.getId(), ".premis.xml");
    // PremisFileObjectHelper helper = new PremisFileObjectHelper(premisObject);
    // helper.saveToFile(premis.toFile());
    // model.createPreservationMetadata(aip.getId(), representationID, fileName,
    // (Binary) FSUtils.convertPathToResource(premis.getParent(), premis));
    // premis.toFile().delete();
    // }
    // }
    // } catch (ModelServiceException mse) {
    // logger.error("Error processing AIP " + aip.getId() + ": " +
    // mse.getMessage(), mse);
    // } catch (StorageServiceException sse) {
    // logger.error("Error processing AIP " + aip.getId() + ": " +
    // sse.getMessage(), sse);
    // } catch (DroidException de) {
    // logger.error("Error processing AIP " + aip.getId() + ": " +
    // de.getMessage(), de);
    // } catch (PremisMetadataException pme) {
    // logger.error("Error processing AIP " + aip.getId() + ": " +
    // pme.getMessage(), pme);
    // }
    // }
    // } catch (IOException ioe) {
    // logger.error("Error executing FastCharacterizationAction: " +
    // ioe.getMessage(), ioe);
    // } catch (DroidException de) {
    // logger.error("Error executing FastCharacterizationAction: " +
    // de.getMessage(), de);
    // }
    return null;
  }

  @Override
  public Report beforeExecute(IndexService index, ModelService model, StorageService storage) throws PluginException {

    return null;
  }

  @Override
  public Report afterExecute(IndexService index, ModelService model, StorageService storage) throws PluginException {

    return null;
  }

}
