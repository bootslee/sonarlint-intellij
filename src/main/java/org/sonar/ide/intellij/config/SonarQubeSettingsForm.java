/*
 * SonarQube IntelliJ
 * Copyright (C) 2013 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.ide.intellij.config;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.sonar.ide.intellij.model.SonarQubeServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public final class SonarQubeSettingsForm {

  private JPanel formComponent;
  private JButton addButton;
  private ComboBox serversList;
  private JButton editButton;
  private JButton removeButton;

  private boolean modified = false;

  public SonarQubeSettingsForm() {
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SonarQubeServerDialog dialog = new SonarQubeServerDialog(formComponent);
        dialog.show();
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
          serversList.addItem(dialog.getServer());
          serversList.setSelectedItem(dialog.getServer());
          modified = true;
          refreshButtons();
        }
      }
    });
    serversList.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, value != null ? ((SonarQubeServer) value).getId() : "<no server>", index, isSelected, cellHasFocus);
      }
    });
    editButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SonarQubeServerDialog dialog = new SonarQubeServerDialog(formComponent);
        dialog.setServer((SonarQubeServer) serversList.getSelectedItem());
        dialog.show();
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
          DefaultComboBoxModel model = ((DefaultComboBoxModel) serversList.getModel());
          int selectedIndex = model.getIndexOf(model.getSelectedItem());
          model.removeElementAt(selectedIndex);
          model.insertElementAt(dialog.getServer(), selectedIndex);
          model.setSelectedItem(dialog.getServer());
          modified = true;
          refreshButtons();
        }
      }
    });
    removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (Messages.showYesNoDialog(formComponent, "Are you sure you want to delete this SonarQube server?", "Delete SonarQube server", Messages.getQuestionIcon()) == Messages.YES) {
          serversList.removeItem(serversList.getSelectedItem());
          modified = true;
          refreshButtons();
        }
      }
    });
  }

  private void refreshButtons() {
    if (serversList.getModel().getSize() > 0) {
      editButton.setEnabled(true);
      removeButton.setEnabled(true);
    } else {
      editButton.setEnabled(false);
      removeButton.setEnabled(false);
    }
  }

  public JComponent getFormComponent() {
    return formComponent;
  }

  public boolean isModified() {
    return modified;
  }

  public final java.util.List<SonarQubeServer> getServers() {
    ComboBoxModel model = serversList.getModel();
    java.util.List<SonarQubeServer> result = new ArrayList<SonarQubeServer>(model.getSize());
    for (int i = 0; i < model.getSize(); i++) {
      result.add((SonarQubeServer) model.getElementAt(i));
    }
    return result;
  }

  public final void setServers(java.util.List<SonarQubeServer> servers) {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    for (SonarQubeServer server : servers) {
      model.addElement(server);
    }
    serversList.setModel(model);
    modified = false;
    refreshButtons();
  }

}