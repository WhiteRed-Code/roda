/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.common;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.data.v2.jobs.Report.PluginState;
import org.testng.AssertJUnit;

public class ReportAssertUtils {
  public static void assertReports(List<Report> reports) {
    assertReports(reports, null, null);
  }

  public static void assertReports(List<Report> reports, List<String> outcomeObjectIds) {
    assertReports(reports, outcomeObjectIds, null);
  }

  public static void assertReports(List<Report> reports, List<String> outcomeObjectIds, List<String> sourceObjectIds) {
    if (outcomeObjectIds != null) {
      AssertJUnit.assertEquals(outcomeObjectIds.size(), reports.size());
    } else if (sourceObjectIds != null) {
      AssertJUnit.assertEquals(sourceObjectIds.size(), reports.size());
    }

    for (Report report : reports) {
      if (!PluginState.SUCCESS.equals(report.getPluginState())) {
        AssertJUnit.fail("Report failure: " + report);
      }

      if (outcomeObjectIds != null && StringUtils.isNotBlank(report.getOutcomeObjectId())) {
        MatcherAssert.assertThat(report.getOutcomeObjectId(), Matchers.isIn(outcomeObjectIds));
      }

      if (sourceObjectIds != null && StringUtils.isNotBlank(report.getSourceObjectId())) {
        MatcherAssert.assertThat(report.getSourceObjectId(), Matchers.isIn(sourceObjectIds));
      }

      // assert sub-reports
      if (report.getReports().size() > 0) {
        assertReports(report.getReports(), outcomeObjectIds, sourceObjectIds);
      }
    }
  }
}
