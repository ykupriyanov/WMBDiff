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
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdesktop.swingx.JXTreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;

public class WMBDiff {
	final static Logger logger = LoggerFactory.getLogger(WMBDiff.class);
	private JFrame frame;
	private JTextArea textAreaDiffMain;
	private UITableDiff tableDiff;
	private JToggleButton btnShowOnlyInAButton, btnShowOnlyInBButton,
					      btnShowOnlyEqualButton, btnShowOnlyDiffButton;
	
	private JXTreeTable treeTable;
	final JFileChooser fcExport = new JFileChooser();
	final ExportIntoExcel exp = new ExportIntoExcel();
	private ModelCreator mc;
	private ConnectRemoteBrokersDialog connectDialog;
	private final String configFile = "config.xml";
	private Properties config = new Properties();
  
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WMBDiff window = new WMBDiff();
					window.frame.setVisible(true);
				} catch (Exception e) {
					logger.error("main", e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws ConfigManagerProxyPropertyNotInitializedException 
	 * @throws ConfigManagerProxyLoggedException 
	 */
	public WMBDiff() throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException {
		loadConfig();
		initialize();
	}
	public void connectToRemoteBrokers(){
		saveConfig();
		try {
			mc = new ModelCreator(connectDialog.getABroker(), connectDialog.getBBroker(), config.getProperty("TEMP_EG_SUFFIX"));
			mc.setModelFilter(btnShowOnlyInAButton.isSelected(),
					btnShowOnlyInBButton.isSelected(),
					btnShowOnlyEqualButton.isSelected(),
					btnShowOnlyDiffButton.isSelected());
			updateTreeTable();
			updateAreaDiffMain();
			tableDiff.setEmpty();
		} catch (ConfigManagerProxyPropertyNotInitializedException e) {
			logger.error("Connect to broker Error", e);
		} catch (ConfigManagerProxyLoggedException e) {
			logger.error("Connect to broker Error", e);
		}
	}

	private void saveConfig(){
		File file = new File(configFile);
	try {
		OutputStream outputStream = new FileOutputStream(file);
		connectDialog.getABroker();
		connectDialog.getABroker().getHost();
		config.setProperty("AHost", connectDialog.getABroker().getHost());
		config.setProperty("APort", Integer.toString(connectDialog.getABroker().getPort()));
		config.setProperty("AMqMgr", connectDialog.getABroker().getMqMgr());
		config.setProperty("BHost", connectDialog.getBBroker().getHost());
		config.setProperty("BPort", Integer.toString(connectDialog.getBBroker().getPort()));
		config.setProperty("BMqMgr", connectDialog.getBBroker().getMqMgr());
		config.storeToXML(outputStream, "Remote Brokers");	  
	    outputStream.close();
	} catch (FileNotFoundException ex) {
	    // file does not exist
		logger.error("saveConfig", ex);
	} catch (IOException ex) {
	    // I/O error
		logger.error("saveConfig", ex);
	}
		
	}
	private void loadConfig(){
		File file = new File(configFile);
	try {
		InputStream inputStream = new FileInputStream(file);
        config.loadFromXML(inputStream);
	    inputStream.close();
	} catch (FileNotFoundException ex) {
	    // file does not exist
		logger.error("loadConfig", ex);
	} catch (IOException ex) {
	    // I/O error
		logger.error("loadConfig", ex);
	}
	}
	private void setTreeTableWidth() {
		treeTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		treeTable.getColumnModel().getColumn(1).setPreferredWidth(10);
		treeTable.getColumnModel().getColumn(2).setPreferredWidth(10);
		treeTable.getColumnModel().getColumn(3).setPreferredWidth(200);
		//for(int i=0; i<4;i++) logger.info("TreeTable Column="+ Integer.valueOf(i).toString()+ " Width=" + Integer.valueOf(treeTable.getColumnModel().getColumn(i).getWidth()).toString());  
	}
	private void updateTreeTable(){
		  try {
			treeTable.setTreeTableModel(mc.getModel());
		} catch (ConfigManagerProxyPropertyNotInitializedException e) {
			logger.error("Update Tree Table Error", e);
		} catch (ConfigManagerProxyLoggedException e) {
			logger.error("Update Tree Table Error", e);
		}
	      treeTable.removeColumn(treeTable.getColumnModel().getColumn(4));
	      treeTable.expandAll();
	      //treeTable.packAll();
	      setTreeTableWidth();
	      treeTable.updateUI();
	}

	private void updateAreaDiffMain(){
		 BrokerConnectionParameters aBroker =  connectDialog.getABroker();
	     BrokerConnectionParameters bBroker =  connectDialog.getBBroker();
	     textAreaDiffMain.setText(null);
	     textAreaDiffMain.append("A: MQ Mgr = " + aBroker.getMqMgr() + " Host = " + aBroker.getHost() +"(" + Integer.toString(aBroker.getPort()) + ")\n");
	     textAreaDiffMain.append("B: MQ Mgr = " + bBroker.getMqMgr() + " Host = " + bBroker.getHost() +"(" + Integer.toString(bBroker.getPort()) + ")\n");
	}
	/**
	 * Initialize the contents of the frame.
	 * @throws ConfigManagerProxyPropertyNotInitializedException 
	 * @throws ConfigManagerProxyLoggedException 
	 */
	private void initialize() throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException {
		frame = new JFrame();
		ImageIcon img = new ImageIcon("./WMBDiff.jpg");
		frame.setIconImage(img.getImage());
		frame.setTitle("WMBDiff");
		frame.setBounds(100, 100, 1500, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectDialog = new ConnectRemoteBrokersDialog(frame);
	    connectDialog.setABroker(new BrokerConnectionParameters(config.getProperty("AHost"),Integer.parseInt(config.getProperty("APort")), config.getProperty("AMqMgr")));
	    connectDialog.setBBroker(new BrokerConnectionParameters(config.getProperty("BHost"),Integer.parseInt(config.getProperty("BPort")), config.getProperty("BMqMgr")));
		FormLayout layout = new FormLayout("pref, 3dlu, pref:grow, 3dlu", // columns
			    "pref, 3dlu, pref, 3dlu, fill:pref:grow, 3dlu");      // rows);
		/*
				"pref, 3dlu, pref, 3dlu",
				"p, 3dlu, p, 3dlu, p, 3dlu");*/
		frame.getContentPane().setLayout(new BorderLayout());
		//layout.setColumnGroups(new int[][]{{1,3}});
		//JPanel panel = new FormDebugPanel();
		JPanel panel = new JPanel();
		PanelBuilder builder = new PanelBuilder(layout, panel);
		builder.border(Borders.DIALOG);//setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		JToolBar toolBar = new JToolBar();
		builder.add(toolBar, cc.xy(1, 1));
		frame.getContentPane().add(panel);
		JButton btnConnect = new JButton("Connect");
		btnConnect.setToolTipText("Connect to Remote Brokers");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 // parent, isModal
				connectDialog.setVisible(true);
				connectToRemoteBrokers();
			}
		});
		toolBar.add(btnConnect);
		JButton btnExport = new JButton("Export");
		btnExport.setToolTipText("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				    FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS files", "xls");
				    fcExport.setFileFilter(filter);
				    fcExport.setDialogTitle("Specify a file to export");
				    fcExport.setCurrentDirectory(new File("."));
				    int returnVal = fcExport.showSaveDialog(frame);
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fcExport.getSelectedFile();
			            exp.export(file, (WMBDiffNoRootTreeTableModel) treeTable.getTreeTableModel());
			            logger.debug("Export to file: " + file.getName());
			        } else {
			            logger.debug("Export command cancelled by user");
			        }
			}
		});
		toolBar.add(btnExport);
		
		
		btnShowOnlyInAButton = new JToggleButton("  >  ");
		btnShowOnlyInAButton.setToolTipText("Show Objects only in A");
		btnShowOnlyInAButton.setSelected(true);
		btnShowOnlyInAButton.addItemListener(new ItemListener() {
			   public void itemStateChanged(ItemEvent ev) {
				      if(ev.getStateChange()==ItemEvent.SELECTED){
				    	  mc.getModelFilter().setOnlyInA(true);
				      } else if(ev.getStateChange()==ItemEvent.DESELECTED){
				    	  mc.getModelFilter().setOnlyInA(false);
				      }
				     updateTreeTable();
				   }
				});		
		toolBar.add(btnShowOnlyInAButton);
		
		btnShowOnlyInBButton = new JToggleButton("  <  ");
		btnShowOnlyInBButton.setToolTipText("Show Objects only in B");
		btnShowOnlyInBButton.setSelected(true);
		btnShowOnlyInBButton.addItemListener(new ItemListener() {
			   public void itemStateChanged(ItemEvent ev) {
				      if(ev.getStateChange()==ItemEvent.SELECTED){
				    	  mc.getModelFilter().setOnlyInB(true);
				      } else if(ev.getStateChange()==ItemEvent.DESELECTED){
				    	  mc.getModelFilter().setOnlyInB(false);
				      }
				      updateTreeTable();
				   }
				});
		toolBar.add(btnShowOnlyInBButton);
		
		btnShowOnlyEqualButton= new JToggleButton("  =  ");
		btnShowOnlyEqualButton.setToolTipText("Show Objects only equal");
		btnShowOnlyEqualButton.setSelected(true);
		btnShowOnlyEqualButton.addItemListener(new ItemListener() {
			   public void itemStateChanged(ItemEvent ev) {
				      if(ev.getStateChange()==ItemEvent.SELECTED){
				    	  mc.getModelFilter().setEqual(true);
				      } else if(ev.getStateChange()==ItemEvent.DESELECTED){
				    	  mc.getModelFilter().setEqual(false);
				      }
				      updateTreeTable();
				   }
				});		
		toolBar.add(btnShowOnlyEqualButton);
		
		btnShowOnlyDiffButton = new JToggleButton("  !=  ");
		btnShowOnlyDiffButton.setToolTipText("Show Objects only different");
		btnShowOnlyDiffButton.setSelected(true);
		btnShowOnlyDiffButton.addItemListener(new ItemListener() {
			   public void itemStateChanged(ItemEvent ev) {
				      if(ev.getStateChange()==ItemEvent.SELECTED){
				    	  mc.getModelFilter().setDiff(true);
				      } else if(ev.getStateChange()==ItemEvent.DESELECTED){
				    	  mc.getModelFilter().setDiff(false);
				      }
				      updateTreeTable();
				   }
				});	
		toolBar.add(btnShowOnlyDiffButton);
		
		//BEGIN TREE TABLE
		WMBDiffNoRootTreeTableModel noRootTreeTableModel =  new WMBDiffNoRootTreeTableModel(new ArrayList<DiffExecutionGroup>());
        treeTable = new JXTreeTable(noRootTreeTableModel);
        treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        treeTable.setRootVisible(false);  // hide the root
        treeTable.removeColumn(treeTable.getColumnModel().getColumn(4));
        treeTable.expandAll();
        //treeTable.packAll();
        setTreeTableWidth();
        treeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
               Object o =  treeTable.getModel().getValueAt(treeTable.getSelectedRow(), 4);
               if(o != null && o instanceof DiffDeployedObjectResult) {
            	   DiffDeployedObjectResult result = (DiffDeployedObjectResult) o;
            	   tableDiff.update(result);
               };
            }});
      JScrollPane scrollPane_2 = new JScrollPane();
      scrollPane_2.setViewportView(treeTable);
      builder.add(scrollPane_2, cc.xywh(1, 3, 1, 3));
      //END
        
      //BEGIN TEXT AREA AND TABLE DIFF
      textAreaDiffMain = new JTextArea();
      textAreaDiffMain.append("A: \n");
      textAreaDiffMain.append("B: \n");
      textAreaDiffMain.setBackground(frame.getBackground());
      textAreaDiffMain.setEditable(false);
      builder.add(textAreaDiffMain, cc.xy(3, 3));
      //ADD TABLE DIFF
      tableDiff = new UITableDiff();
      JScrollPane scrollPane = new JScrollPane(tableDiff, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      builder.add(scrollPane, cc.xy(3, 5));
      //END TEXT AREA AND TABLE DIFF
	
	}
}
