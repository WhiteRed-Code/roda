/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.client.browse;

import java.io.Serializable;

public class SupportedMetadataTypeBundle implements Serializable {

  private static final long serialVersionUID = 1L;

  private String type;
  private String version;
  private String label;
  private String template;

  public SupportedMetadataTypeBundle() {
    super();
  }

  public SupportedMetadataTypeBundle(String type, String version, String label, String template) {
    super();
    this.type = type;
    this.version = version;
    this.label = label;
    this.template = template;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

}
