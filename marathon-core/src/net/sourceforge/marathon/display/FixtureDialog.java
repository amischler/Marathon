/*******************************************************************************
 *  
 *  Copyright (C) 2010 Jalian Systems Private Ltd.
 *  Copyright (C) 2010 Contributors to Marathon OSS Project
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 * 
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Project website: http://www.marathontesting.com
 *  Help: Marathon help forum @ http://groups.google.com/group/marathon-testing
 * 
 *******************************************************************************/
package net.sourceforge.marathon.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.marathon.Constants;
import net.sourceforge.marathon.mpf.ApplicationPanel;
import net.sourceforge.marathon.util.EscapeDialog;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class FixtureDialog extends EscapeDialog {
    private static final long serialVersionUID = 1L;
    private boolean ok;
    private JButton okButton;

    JTextArea descriptionField;
    ApplicationPanel applicationPanel ;
    
    private static final ImageIcon OK_ICON = new ImageIcon(FixtureDialog.class.getResource("icons/enabled/ok.gif"));;
    private static final ImageIcon CANCEL_ICON = new ImageIcon(FixtureDialog.class.getResource("icons/enabled/cancel.gif"));
    private JTextField nameField;
    private final List<String> fixtures;

    public FixtureDialog(JFrame parent, String[] fixtures) {
        super(parent, "Create New Fixture", true);
        this.fixtures = Arrays.asList(fixtures);
        initialize();
    }

    private void initialize() {
        nameField = new JTextField();
        descriptionField = new JTextArea(3, 20);
        JScrollPane descriptionPane = new JScrollPane(descriptionField, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        applicationPanel = new ApplicationPanel(this, ApplicationPanel.NODIALOGBORDER);
        setProperties();
        JPanel buildOKCancelBar = createButtonBar();

        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("fill:pref:n, 3dlu, pref:grow, 3dlu", ""));
        builder.setDefaultDialogBorder();

        builder.append("&Name", nameField);
        builder.appendRow("top:pref:n");
        builder.append("&Description", descriptionPane);
        builder.nextLine();
        builder.appendRow("fill:pref:grow");
        builder.append(applicationPanel.getPanel(), 3);

        builder.nextLine();
        builder.append(buildOKCancelBar, 3);

        getContentPane().add(builder.getPanel());

        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createButtonBar() {
        okButton = new JButton("OK", OK_ICON);
        okButton.setEnabled(true);
        ok = false;

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    ok = true;
                    dispose();
                }
            }
        });
        JButton cancelButton = new JButton("Cancel", CANCEL_ICON);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        getRootPane().setDefaultButton(okButton);
        setCloseButton(cancelButton);
        return ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
    }

    protected boolean validateInputs() {
        if (!isValidFixture()) {
            JOptionPane.showMessageDialog(this, "Invalid name for fixture", "Fixture Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return applicationPanel.isValidInput();
    }

    private boolean isValidFixture() {
        return nameField.getText().length() > 0 && !exists(nameField.getText());
    }

    private boolean exists(String fixtureName) {
        return fixtures.contains(fixtureName);
    }

    private void setProperties() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(System.getProperty(Constants.PROP_PROJECT_DIR),
                    Constants.PROJECT_FILE));
            Properties properties = new Properties();
            properties.load(fileInputStream);
            properties.setProperty(Constants.PROP_PROJECT_DIR, System.getProperty(Constants.PROP_PROJECT_DIR));
            applicationPanel.setProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedLauncher() {
        return applicationPanel.getClassName();

    }

    public boolean isOk() {
        return ok;
    }

    public Properties getProperties() {
        Properties props = new Properties();
        props.setProperty(Constants.PROP_PROJECT_DIR, System.getProperty(Constants.PROP_PROJECT_DIR));
        props.setProperty(Constants.FIXTURE_DESCRIPTION, descriptionField.getText());
        applicationPanel.getProperties(props);
        return props;
    }

    public String getFixtureName() {
        return nameField.getText();
    }
}