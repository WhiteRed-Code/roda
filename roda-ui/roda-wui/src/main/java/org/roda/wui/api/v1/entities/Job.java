package org.roda.wui.api.v1.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;

@XmlRootElement(name = "job")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Job implements Serializable {
  private static final long serialVersionUID = 615993757726175203L;

  // work identifier
  private String id;
  // work start date
  private Date start;
  // work end date
  private Date end;
  // 0-100 scale completion status
  private int completionStatus;

  // plugin full class (e.g. org.roda.core.plugins.plugins.base.FixityPlugin)
  private String plugin;
  // plugin parameters
  private Map<String, String> pluginParameters;

  // resource type (e.g. bagit, e-ark sip, etc.)
  private String resourceType;

  // type of method that orchestrator should execute (e.g.
  // runPluginOnTransferredResources, runPluginOnAIPs, etc.)
  private String orchestratorMethod;
  // list of object ids to act upon
  private List<String> objectIds;
  // object full class (e.g. org.roda.core.model.AIP, etc.)
  private String objectType;

  public Job() {
    super();
    start = new Date();
    end = null;
    completionStatus = 0;
  }

  public Job(String id) {
    this();
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  @XmlElement(nillable = true)
  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public int getCompletionStatus() {
    return completionStatus;
  }

  public void setCompletionStatus(int completionStatus) {
    this.completionStatus = completionStatus;
  }

  public String getPlugin() {
    return plugin;
  }

  public void setPlugin(String plugin) {
    this.plugin = plugin;
  }

  @XmlElement(nillable = true)
  public Map<String, String> getPluginParameters() {
    return pluginParameters;
  }

  public void setPluginParameters(Map<String, String> pluginParameters) {
    this.pluginParameters = pluginParameters;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public String getOrchestratorMethod() {
    return orchestratorMethod;
  }

  public void setOrchestratorMethod(String orchestratorMethod) {
    this.orchestratorMethod = orchestratorMethod;
  }

  @XmlElement(nillable = true)
  public List<String> getObjectIds() {
    return objectIds;
  }

  public void setObjectIds(List<String> objectIds) {
    this.objectIds = objectIds;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  @Override
  public String toString() {
    return "Job [id=" + id + ", start=" + start + ", end=" + end + ", completionStatus=" + completionStatus
      + ", plugin=" + plugin + ", pluginParameters=" + pluginParameters + ", resourceType=" + resourceType
      + ", orchestratorMethod=" + orchestratorMethod + ", objectIds=" + objectIds + ", objectType=" + objectType + "]";
  }

}
