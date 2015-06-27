/*
 * Copyright 2015 Yaroslav Kupriyanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.wmbdiff;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;



import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSeparator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConnectRemoteBrokersDialog extends JDialog {
	private static final long serialVersionUID = 2263700801618229760L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textAHost;
	private JTextField textAPort;
	private JTextField textAMqMgr;
	private JTextField textBHost;
	private JTextField textBPort;
	private JTextField textBMqMgr;
	private BrokerConnectionParameters aBroker;
	private BrokerConnectionParameters bBroker;
	
	public BrokerConnectionParameters getABroker() {
		return aBroker;
	}

	public void setABroker(BrokerConnectionParameters aBroker) {
		this.aBroker = aBroker;
		textAHost.setText(aBroker.getHost());
		textAPort.setText(Integer.toString(aBroker.getPort()));
		textAMqMgr.setText(aBroker.getMqMgr());
	}

	public BrokerConnectionParameters getBBroker() {
		return bBroker;
	}

	public void setBBroker(BrokerConnectionParameters bBroker) {
		this.bBroker = bBroker;
		textBHost.setText(bBroker.getHost());
		textBPort.setText(Integer.toString(bBroker.getPort()));
		textBMqMgr.setText(bBroker.getMqMgr());
	}

	/**
	 * Create the dialog.
	 */
	public ConnectRemoteBrokersDialog(Frame frame) {
		super(frame, true);
		setBounds(100, 100, 370, 290);
		setTitle("Connect to Remote Brokers");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		{
			JLabel lblBBroker = new JLabel("Broker A");
			contentPanel.add(lblBBroker, "2, 2");
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "4, 2, 3, 1");
		}
		{
			JLabel lblAHost = new JLabel("Host");
			contentPanel.add(lblAHost, "4, 4, left, default");
		}
		{
			textAHost = new JTextField();
			contentPanel.add(textAHost, "6, 4, fill, default");
			textAHost.setColumns(10);
		}
		{
			JLabel lblAPort = new JLabel("Port");
			contentPanel.add(lblAPort, "4, 6, left, default");
		}
		{
			textAPort = new JTextField();
			contentPanel.add(textAPort, "6, 6, fill, default");
			textAPort.setColumns(10);
		}
		{
			JLabel lblAMqMgr = new JLabel("MQ Mgr");
			contentPanel.add(lblAMqMgr, "4, 8, left, default");
		}
		{
			textAMqMgr = new JTextField();
			contentPanel.add(textAMqMgr, "6, 8, fill, default");
			textAMqMgr.setColumns(10);
		}
		{
			JLabel lblBBroker = new JLabel("Broker B");
			contentPanel.add(lblBBroker, "2, 10");
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "4, 10, 3, 1");
		}
		{
			JLabel lblBHost = new JLabel("Host");
			contentPanel.add(lblBHost, "4, 12, left, default");
		}
		{
			textBHost = new JTextField();
			contentPanel.add(textBHost, "6, 12, fill, default");
			textBHost.setColumns(10);
		}
		{
			JLabel lblBPort = new JLabel("Port");
			contentPanel.add(lblBPort, "4, 14, left, default");
		}
		{
			textBPort = new JTextField();
			contentPanel.add(textBPort, "6, 14, fill, default");
			textBPort.setColumns(10);
		}
		{
			JLabel lblBMqMgr = new JLabel("MQ Mgr");
			contentPanel.add(lblBMqMgr, "4, 16, left, default");
		}
		{
			textBMqMgr = new JTextField();
			contentPanel.add(textBMqMgr, "6, 16, fill, default");
			textBMqMgr.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						aBroker = new BrokerConnectionParameters(textAHost.getText(), Integer.parseInt(textAPort.getText()), textAMqMgr.getText());
						bBroker = new BrokerConnectionParameters(textBHost.getText(), Integer.parseInt(textBPort.getText()), textBMqMgr.getText());
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
