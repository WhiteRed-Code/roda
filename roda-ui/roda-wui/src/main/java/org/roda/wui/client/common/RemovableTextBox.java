package org.roda.wui.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RemovableTextBox extends Composite implements HasHandlers {
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  interface MyUiBinder extends UiBinder<Widget, RemovableTextBox> {
  }

  @UiField
  TextBox item;

  @UiField
  Button removeDynamicTextBoxButton;

  public RemovableTextBox() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public RemovableTextBox(String content) {
    initWidget(uiBinder.createAndBindUi(this));
    if (content != null) {
      item.setText(content);
    }
  }

  public String getTextBoxValue() {
    return item.getText();
  }

  public void addRemoveClickHandler(ClickHandler clickHandler) {
    removeDynamicTextBoxButton.addClickHandler(clickHandler);
  }

  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }
}