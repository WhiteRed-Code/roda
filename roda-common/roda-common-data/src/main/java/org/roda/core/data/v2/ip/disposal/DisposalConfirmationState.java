/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.data.v2.ip.disposal;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public enum DisposalConfirmationState {
  PENDING, APPROVED, RESTORED, PERMANENTLY_DELETED, EXECUTION_FAILED, SCHEDULED
}
