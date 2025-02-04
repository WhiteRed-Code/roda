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
package org.roda.wui.client.browse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.v2.notifications.Notification;
import org.roda.core.data.v2.notifications.Notifications;
import org.roda.core.data.v2.validation.ValidationException;
import org.roda.core.data.v2.validation.ValidationIssue;
import org.roda.core.plugins.PluginHelper;
import org.roda.wui.client.browse.bundle.DescriptiveMetadataEditBundle;
import org.roda.wui.client.browse.bundle.SupportedMetadataTypeBundle;
import org.roda.wui.client.common.NoAsyncCallback;
import org.roda.wui.client.common.TitlePanel;
import org.roda.wui.client.common.UserLogin;
import org.roda.wui.client.common.dialogs.Dialogs;
import org.roda.wui.client.common.utils.AsyncCallbackUtils;
import org.roda.wui.client.common.utils.FormUtilities;
import org.roda.wui.client.common.utils.HtmlSnippetUtils;
import org.roda.wui.client.common.utils.JavascriptUtils;
import org.roda.wui.client.common.utils.PermissionClientUtils;
import org.roda.wui.common.client.HistoryResolver;
import org.roda.wui.common.client.tools.HistoryUtils;
import org.roda.wui.common.client.tools.ListUtils;
import org.roda.wui.common.client.widgets.Toast;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;
import org.roda.wui.server.browse.BrowserServiceImpl;

/**
 * @author Luis Faria
 *
 */
public class EditDescriptiveMetadata extends Composite {

  public static final HistoryResolver RESOLVER = new HistoryResolver() {

    @Override
    public void resolve(List<String> historyTokens, final AsyncCallback<Widget> callback) {
      if (historyTokens.size() == 2 || historyTokens.size() == 3) {
        final String aipId = historyTokens.get(0);
        final String representationId = historyTokens.size() == 3 ? historyTokens.get(1) : null;
        final String descriptiveMetadataId = new HTML(historyTokens.get(historyTokens.size() - 1)).getText();

        BrowserService.Util.getInstance().requestAIPLock(aipId, new NoAsyncCallback<Boolean>() {
          @Override
          public void onSuccess(Boolean result) {
            if (Boolean.TRUE.equals(result)) {
              BrowserService.Util.getInstance().retrieveDescriptiveMetadataEditBundle(aipId, representationId,
                descriptiveMetadataId, LocaleInfo.getCurrentLocale().getLocaleName(),
                new AsyncCallback<DescriptiveMetadataEditBundle>() {

                  @Override
                  public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                  }

                  @Override
                  public void onSuccess(DescriptiveMetadataEditBundle bundle) {
                    callback.onSuccess(new EditDescriptiveMetadata(aipId, representationId, bundle));
                  }
                });
            } else {
              HistoryUtils.newHistory(BrowseTop.RESOLVER, aipId);
              Toast.showInfo(messages.editDescMetadataLockedTitle(), messages.editDescMetadataLockedText());
            }
          }
        });
      } else {
        HistoryUtils.newHistory(BrowseTop.RESOLVER);
        callback.onSuccess(null);
      }
    }

    @Override
    public void isCurrentUserPermitted(AsyncCallback<Boolean> callback) {
      // TODO check for edit metadata permission
      UserLogin.getInstance().checkRoles(new HistoryResolver[] {BrowseTop.RESOLVER}, false, callback);
    }

    @Override
    public List<String> getHistoryPath() {
      return ListUtils.concat(BrowseTop.RESOLVER.getHistoryPath(), getHistoryToken());
    }

    @Override
    public String getHistoryToken() {
      return "edit_metadata";
    }
  };

  interface MyUiBinder extends UiBinder<Widget, EditDescriptiveMetadata> {
  }

  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  private final String aipId;
  private final String representationId;
  private DescriptiveMetadataEditBundle bundle;
  private SupportedMetadataTypeBundle supportedBundle;
  private boolean inXML = false;
  private TextArea metadataXML;
  private String metadataTextFromForm = null;

  private boolean aipLocked;

  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  @UiField
  TextBox id;

  @UiField
  ListBox type;

  @UiField
  Label formSimilarDanger;

  @UiField
  FocusPanel showXml;

  @UiField
  HTML showXmlIconXML;

  @UiField
  HTML showXmlIconForm;

  @UiField
  FlowPanel formOrXML;

  @UiField
  Button buttonApply;

  @UiField
  Button buttonRemove;

  @UiField
  Button buttonCancel;

  @UiField
  HTML errors;

  @UiField
  TitlePanel title;

  /**
   * Create a new panel to edit a descriptive metadata
   *
   * @param aipId
   * @param representationId
   *          the user to edit
   */
  public EditDescriptiveMetadata(final String aipId, final String representationId,
    final DescriptiveMetadataEditBundle bundleParam) {
    this.aipId = aipId;
    this.representationId = representationId;
    this.bundle = bundleParam;
    aipLocked = true;

    // Create new Set of MetadataValues so we can keep the original
    Set<MetadataValue> newValues = null;
    if (bundle.getValues() != null) {
      newValues = new TreeSet<>();
      for (MetadataValue mv : bundle.getValues()) {
        newValues.add(mv.copy());
      }
    }

    supportedBundle = new SupportedMetadataTypeBundle(bundle.getId(), bundle.getType(), bundle.getVersion(),
      bundle.getId(), bundle.getRawTemplate(), newValues);

    initWidget(uiBinder.createAndBindUi(this));

    CreateDescriptiveMetadata.initTitle(aipId, title);

    metadataXML = new TextArea();
    metadataXML.addStyleName("form-textbox metadata-edit-area metadata-form-textbox");
    metadataXML.setTitle("Metadata edit area");

    id.setText(bundle.getId());
    id.setEnabled(false);

    type.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent changeEvent) {
        setInXML(false);
        String typeString = null;
        String version = null;
        String value = type.getSelectedValue();
        if (value.contains(RodaConstants.METADATA_VERSION_SEPARATOR)) {
          typeString = value.substring(0, value.lastIndexOf(RodaConstants.METADATA_VERSION_SEPARATOR));
          version = value.substring(value.lastIndexOf(RodaConstants.METADATA_VERSION_SEPARATOR) + 1, value.length());
        }

        if (typeString == null) {
          typeString = value;
        }

        BrowserService.Util.getInstance().retrieveDescriptiveMetadataEditBundle(aipId, representationId, bundle.getId(),
          typeString, version, LocaleInfo.getCurrentLocale().getLocaleName(),
          new AsyncCallback<DescriptiveMetadataEditBundle>() {
            @Override
            public void onFailure(Throwable caught) {
              AsyncCallbackUtils.defaultFailureTreatment(caught);
            }

            @Override
            public void onSuccess(DescriptiveMetadataEditBundle editBundle) {
              bundle = editBundle;
              // Create new Set of MetadataValues so we can keep the original
              Set<MetadataValue> newValues = null;
              if (bundle.getValues() != null) {
                newValues = new TreeSet<>();
                for (MetadataValue mv : bundle.getValues()) {
                  newValues.add(mv.copy());
                }
              }

              supportedBundle = new SupportedMetadataTypeBundle(bundle.getId(), bundle.getType(), bundle.getVersion(),
                bundle.getId(), bundle.getRawTemplate(), newValues);

              updateFormOrXML();
            }
          });
      }
    });

    BrowserService.Util.getInstance().retrieveSupportedMetadata(aipId, representationId,
      LocaleInfo.getCurrentLocale().getLocaleName(), new AsyncCallback<List<SupportedMetadataTypeBundle>>() {

        @Override
        public void onFailure(Throwable caught) {
          AsyncCallbackUtils.defaultFailureTreatment(caught);
        }

        @Override
        public void onSuccess(List<SupportedMetadataTypeBundle> metadataTypes) {
          // TODO sort by alphabetic order of value
          int selected = -1;
          int index = 0;
          Map<String, Integer> types = new HashMap<>();
          for (SupportedMetadataTypeBundle b : metadataTypes) {
            if (b.getVersion() != null) {
              type.addItem(b.getLabel(), b.getType() + RodaConstants.METADATA_VERSION_SEPARATOR + b.getVersion());
            } else {
              type.addItem(b.getLabel(), b.getType());
            }

            String lowerCaseType = bundle.getType() != null ? bundle.getType().toLowerCase() : null;
            if (b.getType().equalsIgnoreCase(lowerCaseType)) {
              String lowerCaseVersion = bundle.getVersion() != null ? bundle.getVersion().toLowerCase() : null;
              if (b.getVersion() != null && lowerCaseVersion != null) {
                if (lowerCaseVersion != null && b.getVersion().equalsIgnoreCase(lowerCaseVersion)) {
                  selected = index;
                }
              } else if (b.getVersion() == null && lowerCaseVersion == null) {
                selected = index;
              }
            }

            types.put(b.getType(), index);
            index++;
          }

          updateFormOrXML();

          if (selected >= 0) {
            type.addItem(messages.otherItem(), "");
            type.setSelectedIndex(selected);
          } else if ("".equals(bundle.getType())) {
            type.addItem(messages.otherItem(), "");
            type.setSelectedIndex(type.getItemCount() - 1);
          } else {
            if (!types.keySet().contains(bundle.getType())) {
              type.addItem(messages.otherItem() + "(" + bundle.getType() + ")", bundle.getType());
              type.addItem(messages.otherItem(), "");
              type.setSelectedIndex(type.getItemCount() - 2);
            } else {
              type.addItem(messages.otherItem(), "");
              type.setSelectedIndex(types.get(bundle.getType()));
            }
          }
        }
      });

    PermissionClientUtils.bindPermission(buttonRemove, bundle.getPermissions(),
      RodaConstants.PERMISSION_METHOD_DELETE_DESCRIPTIVE_METADATA_FILE);

    Element firstElement = showXml.getElement().getFirstChildElement();
    if ("input".equalsIgnoreCase(firstElement.getTagName())) {
      firstElement.setAttribute("title", "browse input");
    }
  }

  @Override
  protected void onDetach() {
    if (aipLocked) {
      BrowserService.Util.getInstance().releaseAIPLock(this.aipId, new NoAsyncCallback<Void>() {
        @Override
        public void onSuccess(Void result) {
          aipLocked = false;
        }
      });
    }
    super.onDetach();
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    JavascriptUtils.stickSidebar();
  }

  private void createForm(SupportedMetadataTypeBundle bundle) {
    formOrXML.clear();
    metadataXML.setText(this.bundle.getXml());
    FormUtilities.create(formOrXML, bundle.getValues(), true);
  }

  public void setInXML(boolean inXML) {
    this.inXML = inXML;
    showXmlIconXML.setVisible(!inXML);
    showXmlIconForm.setVisible(inXML);
  }

  @UiHandler("showXml")
  void buttonShowXmlHandler(ClickEvent e) {
    setInXML(!inXML);
    updateFormOrXML();
  }

  private void updateFormOrXML() {
    if (bundle != null && bundle.getValues() != null && !bundle.getValues().isEmpty()) {
      if (!bundle.isSimilar()) {
        formSimilarDanger.setVisible(true);
      } else {
        formSimilarDanger.setVisible(false);
      }
      showXml.setVisible(true);

      if (inXML) {
        updateMetadataXML();
      } else {
        // if the user changed the metadata text
        if (metadataTextFromForm != null && !metadataXML.getText().equals(metadataTextFromForm)) {
          Dialogs.showConfirmDialog(messages.confirmChangeToFormTitle(), messages.confirmChangeToFormMessage(),
            messages.dialogCancel(), messages.dialogYes(), new AsyncCallback<Boolean>() {
              @Override
              public void onFailure(Throwable caught) {
                AsyncCallbackUtils.defaultFailureTreatment(caught);
              }

              @Override
              public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                  formOrXML.clear();
                  createForm(supportedBundle);
                } else {
                  setInXML(!inXML);
                }
              }
            });
        } else {
          formOrXML.clear();
          createForm(supportedBundle);
        }
      }
    } else {
      setInXML(true);
      formSimilarDanger.setVisible(false);
      formOrXML.clear();
      if (bundle != null) {
        metadataXML.setText(bundle.getXml());
      } else {
        metadataXML.setText("");
      }
      formOrXML.add(metadataXML);
      showXml.setVisible(false);
      metadataTextFromForm = null;
    }
  }

  private boolean hasModifiedForm() {
    HashMap<String, MetadataValue> formMap = new HashMap<>();
    for (MetadataValue mv : supportedBundle.getValues()) {
      formMap.put(mv.getId(), mv);
    }

    HashMap<String, MetadataValue> bundleMap = new HashMap<>();
    for (MetadataValue mv : bundle.getValues()) {
      bundleMap.put(mv.getId(), mv);
    }

    for (Entry<String, MetadataValue> entry : formMap.entrySet()) {
      String key = entry.getKey();
      MetadataValue mvForm = entry.getValue();
      String formValue = mvForm != null ? mvForm.get("value") : "";
      MetadataValue mvBundle = bundleMap.get(key);
      String bundleValue = mvBundle != null ? bundleMap.get(key).get("value") : "";

      if ((formValue != null && !formValue.equals(bundleValue)) || (formValue == null && bundleValue != null)) {
        return true;
      }
    }
    return false;
  }

  private void updateMetadataXML() {
    if (hasModifiedForm()) {
      // Apply the form values to the template (server)
      BrowserService.Util.getInstance().retrieveDescriptiveMetadataPreview(supportedBundle,
        new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
            AsyncCallbackUtils.defaultFailureTreatment(caught);
          }

          @Override
          public void onSuccess(String preview) {
            formOrXML.clear();
            metadataXML.setText(preview);
            formOrXML.add(metadataXML);
            metadataTextFromForm = preview;
          }
        });
    } else {
      formOrXML.clear();
      metadataXML.setText(bundle.getXml());
      formOrXML.add(metadataXML);
      metadataTextFromForm = bundle.getXml();
    }
  }

  @UiHandler("buttonApply")
  void buttonApplyHandler(ClickEvent e) {
    String xmlText = metadataXML.getText();
    if (inXML) {
      updateMetadataOnServer(xmlText);
    } else {
      // Get the resulting XML using the data from the form
      BrowserService.Util.getInstance().retrieveDescriptiveMetadataPreview(supportedBundle,
        new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
            AsyncCallbackUtils.defaultFailureTreatment(caught);
          }

          @Override
          public void onSuccess(String preview) {
            updateMetadataOnServer(preview);
          }
        });
    }
  }

  private void updateMetadataOnServer(String content) {
    String typeText = type.getSelectedValue();
    String version = null;

    if (typeText.contains(RodaConstants.METADATA_VERSION_SEPARATOR)) {
      version = typeText.substring(typeText.lastIndexOf(RodaConstants.METADATA_VERSION_SEPARATOR) + 1);
      typeText = typeText.substring(0, typeText.lastIndexOf(RodaConstants.METADATA_VERSION_SEPARATOR));
    }

    DescriptiveMetadataEditBundle updatedBundle = new DescriptiveMetadataEditBundle(bundle.getId(), typeText, version,
      content, bundle.getPermissions());

    Dialogs.showConfirmDialog(messages.updateMetadataFileTitle(), messages.updateMetadataFileLabel(),
      messages.cancelButton(), messages.confirmButton(), new NoAsyncCallback<Boolean>() {

        public void onSuccess(Boolean confirm) {
          if (confirm) {
            BrowserService.Util.getInstance().updateDescriptiveMetadataFile(aipId, representationId, updatedBundle,
              new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                  if (caught instanceof ValidationException) {
                    ValidationException e = (ValidationException) caught;
                    updateErrors(e);
                  } else {
                    AsyncCallbackUtils.defaultFailureTreatment(caught);
                  }
                }

                @Override
                public void onSuccess(Void result) {
                  errors.setText("");
                  errors.setVisible(false);
                  Toast.showInfo(messages.dialogSuccess(), messages.metadataFileSaved());
                  back();
                }
              });
          }
        }
      });

  }

  protected void updateErrors(ValidationException e) {
    SafeHtmlBuilder b = new SafeHtmlBuilder();
    for (ValidationIssue issue : e.getReport().getIssues()) {
      b.append(SafeHtmlUtils.fromSafeConstant("<span class='error'>"));
      b.append(messages.metadataParseError(issue.getLineNumber(), issue.getColumnNumber(), issue.getMessage()));
      b.append(SafeHtmlUtils.fromSafeConstant("</span>"));
    }

    errors.setHTML(b.toSafeHtml());
    errors.setVisible(true);
  }

  @UiHandler("buttonRemove")
  void buttonRemoveHandler(ClickEvent e) {
    Dialogs.showConfirmDialog(messages.removeMetadataFileTitle(), messages.removeMetadataFileLabel(),
      messages.cancelButton(), messages.confirmButton(), new NoAsyncCallback<Boolean>() {

        public void onSuccess(Boolean confirm) {
          if (confirm) {
            BrowserService.Util.getInstance().deleteDescriptiveMetadataFile(aipId, representationId, bundle.getId(),
              new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                  AsyncCallbackUtils.defaultFailureTreatment(caught);
                }

                @Override
                public void onSuccess(Void result) {
                  Toast.showInfo(messages.dialogSuccess(), messages.metadataFileRemoved());
                  back();
                }
              });
          }
        }

      });
  }

  @UiHandler("buttonCancel")
  void buttonCancelHandler(ClickEvent e) {
    back();
  }

  private void back() {
    if (representationId == null) {
      HistoryUtils.openBrowse(aipId);
    } else {
      HistoryUtils.openBrowse(aipId, representationId);
    }
  }
}
