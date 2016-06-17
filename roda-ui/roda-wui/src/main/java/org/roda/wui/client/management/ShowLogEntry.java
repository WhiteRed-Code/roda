/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
/**
 *
 */
package org.roda.wui.client.management;

import java.util.List;

import org.roda.core.data.v2.log.LogEntry;
import org.roda.core.data.v2.log.LogEntryParameter;
import org.roda.wui.client.common.UserLogin;
import org.roda.wui.client.common.utils.StringUtils;
import org.roda.wui.common.client.HistoryResolver;
import org.roda.wui.common.client.tools.Tools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @author Luis Faria
 *
 */
public class ShowLogEntry extends Composite {

  public static final HistoryResolver RESOLVER = new HistoryResolver() {

    @Override
    public void resolve(List<String> historyTokens, final AsyncCallback<Widget> callback) {
      if (historyTokens.size() == 1) {
        String logEntryId = historyTokens.get(0);
        UserManagementService.Util.getInstance().retrieveLogEntry(logEntryId, new AsyncCallback<LogEntry>() {

          @Override
          public void onFailure(Throwable caught) {
            callback.onFailure(caught);
          }

          @Override
          public void onSuccess(LogEntry result) {
            ShowLogEntry logEntryPanel = new ShowLogEntry(result);
            callback.onSuccess(logEntryPanel);
          }
        });
      } else {
        Tools.newHistory(UserLog.RESOLVER);
        callback.onSuccess(null);
      }
    }

    @Override
    public void isCurrentUserPermitted(AsyncCallback<Boolean> callback) {
      UserLogin.getInstance().checkRoles(new HistoryResolver[] {MemberManagement.RESOLVER}, false, callback);
    }

    public List<String> getHistoryPath() {
      return Tools.concat(UserLog.RESOLVER.getHistoryPath(), getHistoryToken());
    }

    public String getHistoryToken() {
      return "logentry";
    }
  };

  interface MyUiBinder extends UiBinder<Widget, ShowLogEntry> {
  }

  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  private static ClientMessages messages = GWT.create(ClientMessages.class);

  @UiField
  Label logIdLabel, logIdValue;

  @UiField
  Label logComponentLabel,logComponentValue;

  @UiField
  Label logMethodLabel,logMethodValue;

  @UiField
  Label logAddressLabel,logAddressValue;

  @UiField
  Label logDatetimeLabel,logDatetimeValue;

  @UiField
  Label logRelatedObjectLabel, logRelatedObjectValue;

  @UiField
  Label logUsernameLabel,logUsernameValue;

  @UiField
  Label logParametersLabel;
  
  @UiField
  FlowPanel logParametersValue;

  @UiField
  Button buttonCancel;

  /**
   * Create a new panel to view a log entry
   *
   */
  public ShowLogEntry(LogEntry logEntry) {
    initWidget(uiBinder.createAndBindUi(this));

    logIdValue.setText(logEntry.getId());
    logIdLabel.setVisible(StringUtils.isNotBlank(logEntry.getId()));
    logIdValue.setVisible(StringUtils.isNotBlank(logEntry.getId()));
    
    logComponentValue.setText(logEntry.getActionComponent());
    logComponentLabel.setVisible(StringUtils.isNotBlank(logEntry.getActionComponent()));
    logComponentValue.setVisible(StringUtils.isNotBlank(logEntry.getActionComponent()));

    logMethodValue.setText(logEntry.getActionMethod());
    logMethodLabel.setVisible(StringUtils.isNotBlank(logEntry.getActionMethod()));
    logMethodValue.setVisible(StringUtils.isNotBlank(logEntry.getActionMethod()));
    
    logAddressValue.setText(logEntry.getAddress());
    logAddressLabel.setVisible(StringUtils.isNotBlank(logEntry.getAddress()));
    logAddressValue.setVisible(StringUtils.isNotBlank(logEntry.getAddress()));

    logDatetimeValue.setText(DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL).format(logEntry.getDatetime()));
    logDatetimeLabel.setVisible(logEntry.getDatetime()!=null);
    logDatetimeValue.setVisible(logEntry.getDatetime()!=null);

    logRelatedObjectValue.setText(logEntry.getRelatedObjectID());
    logRelatedObjectLabel.setVisible(StringUtils.isNotBlank(logEntry.getRelatedObjectID()));
    logRelatedObjectValue.setVisible(StringUtils.isNotBlank(logEntry.getRelatedObjectID()));


    logUsernameValue.setText(logEntry.getUsername());
    logUsernameLabel.setVisible(StringUtils.isNotBlank(logEntry.getUsername()));
    logUsernameValue.setVisible(StringUtils.isNotBlank(logEntry.getUsername()));

    List<LogEntryParameter> parameters = logEntry.getParameters();

    if (parameters != null && parameters.size()>0) {
      for (LogEntryParameter par : parameters) {
        HTML parPanel = new HTML();
        parPanel.setHTML(messages.logParameter(par.getName(), par.getValue()));
        logParametersValue.add(parPanel);
      }
      logParametersLabel.setVisible(true);
      logParametersValue.setVisible(true);
    }else{
      logParametersLabel.setVisible(false);
      logParametersValue.setVisible(false);
    }

  }

  @UiHandler("buttonCancel")
  void handleButtonCancel(ClickEvent e) {
    cancel();
  }

  private void cancel() {
    Tools.newHistory(UserLog.RESOLVER);
  }

}
