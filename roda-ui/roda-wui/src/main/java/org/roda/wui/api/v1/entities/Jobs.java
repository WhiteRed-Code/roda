package org.roda.wui.api.v1.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "jobs")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Jobs {
  private List<Job> jobs;

  public Jobs() {
    super();
    jobs = new ArrayList<Job>();
  }

  public Jobs(List<Job> jobs) {
    super();
    this.jobs = jobs;
  }

  @JsonProperty(value = "jobs")
  @XmlElement(name = "job")
  public List<Job> getJobs() {
    return jobs;
  }

  public void setJobs(List<Job> jobs) {
    this.jobs = jobs;
  }

}
