/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins.plugins.risks;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.roda.core.data.common.RodaConstants.PreservationEventType;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.InvalidParameterException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.File;
import org.roda.core.data.v2.ip.Representation;
import org.roda.core.data.v2.jobs.PluginType;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.index.IndexService;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.AbstractPlugin;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.PluginException;
import org.roda.core.plugins.plugins.PluginHelper;
import org.roda.core.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiskIncidenceRemoverPlugin<T extends Serializable> extends AbstractPlugin<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RiskIncidenceRemoverPlugin.class);
  private static String riskId;
  private static String OTHER_METADATA_TYPE = "RiskIncidence";

  @Override
  public void init() throws PluginException {
    // do nothing
  }

  @Override
  public void shutdown() {
    // do nothing
  }

  @Override
  public String getName() {
    return "Removes old risk incidences";
  }

  @Override
  public String getDescription() {
    return "Removes all risk incidences with removed risks";
  }

  @Override
  public String getVersionImpl() {
    return "1.0";
  }

  @Override
  public void setParameterValues(Map<String, String> parameters) throws InvalidParameterException {
    super.setParameterValues(parameters);

    if (parameters.containsKey("riskId")) {
      riskId = parameters.get("riskId");
    }
  }

  @Override
  public Report execute(IndexService index, ModelService model, StorageService storage, List<T> list)
    throws PluginException {

    LOGGER.debug("Removing old risk incidences");
    Report pluginReport = PluginHelper.createPluginReport(this);

    try {
      for (T element : list) {
        if (element instanceof AIP) {
          AIP aip = (AIP) element;
          model.deleteRiskIncidence(riskId, aip.getId(), null, null, null, OTHER_METADATA_TYPE);
        } else if (element instanceof Representation) {
          Representation representation = (Representation) element;
          model.deleteRiskIncidence(riskId, representation.getAipId(), representation.getId(), null, null,
            OTHER_METADATA_TYPE);
        } else if (element instanceof File) {
          File file = (File) element;
          model.deleteRiskIncidence(riskId, file.getAipId(), file.getRepresentationId(), file.getPath(), file.getId(),
            OTHER_METADATA_TYPE);
        }
      }
    } catch (GenericException | NotFoundException | AuthorizationDeniedException | RequestNotValidException e) {
      LOGGER.error("Could not delete risk incidence");
    }

    LOGGER.debug("Done removing old risk incidences");
    return pluginReport;
  }

  @Override
  public Report beforeAllExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report beforeBlockExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report afterBlockExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report afterAllExecute(IndexService index, ModelService model, StorageService storage) throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Plugin<T> cloneMe() {
    return new RiskIncidenceRemoverPlugin<T>();
  }

  @Override
  public PluginType getType() {
    return PluginType.MISC;
  }

  @Override
  public boolean areParameterValuesValid() {
    return true;
  }

  // TODO FIX
  @Override
  public PreservationEventType getPreservationEventType() {
    return null;
  }

  @Override
  public String getPreservationEventDescription() {
    return "XXXXXXXXXX";
  }

  @Override
  public String getPreservationEventSuccessMessage() {
    return "XXXXXXXXXXXXXXXXXXXXXXXX";
  }

  @Override
  public String getPreservationEventFailureMessage() {
    return "XXXXXXXXXXXXXXXXXXXXXXXXXX";
  }
}
