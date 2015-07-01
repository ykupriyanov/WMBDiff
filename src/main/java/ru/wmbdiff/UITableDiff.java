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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.config.proxy.DeployedObject;

public class UITableDiff extends JXTable {
	private static final long serialVersionUID = 4849681190598666301L;
	final static Logger logger = LoggerFactory.getLogger(UITableDiff.class);
	final static String[] tableColumnNames = {
             "Broker",
             "EG Name",
             "Name",
             "Type",
             "Last Modification",
             "Deployment Date",
             "Bar File"};
    
	final static String[][] tableData = {
  		 {"", "", "", "" , "", "", ""},
  		 {"", "", "", "" , "", "", ""}
	 };
	
	public UITableDiff() {
		 super(tableData, tableColumnNames);
		 super.setEditable(false);             
		 super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		 setTableDiffWidth();
		 //super.packAll();
	}

	private void setTableDiffWidth() {
		getColumnModel().getColumn(0).setPreferredWidth(45);
		getColumnModel().getColumn(1).setPreferredWidth(65);
		getColumnModel().getColumn(2).setPreferredWidth(255);
		getColumnModel().getColumn(3).setPreferredWidth(35);
		getColumnModel().getColumn(4).setPreferredWidth(125);
		getColumnModel().getColumn(5).setPreferredWidth(125);
		getColumnModel().getColumn(6).setPreferredWidth(150);
		
	}
    public void setEmpty(){
    	TableModel tm = getModel();
    	addRowEmpty(tm,"A",0);
    	addRowEmpty(tm,"B",1);
    }
	public void update(DiffDeployedObjectResult result){
		try{
			TableModel tm = getModel();
			DeployedObject a, b;
			switch(result.getResult()){
			case DIFF:
			case EQUAL:
				a = result.getAObject();
				b = result.getBObject();
				addRow(tm,"A", 0, a);
				addRow(tm,"B", 1, b);
				break;
			case ONLY_IN_A:
				a = result.getAObject();
				addRow(tm,"A", 0, a);
				addRowEmpty(tm,"B",1);
				break;
			case ONLY_IN_B:
				b = result.getBObject();
				addRowEmpty(tm,"A",0);
				addRow(tm,"B", 1, b);
				break;
			}
			super.packAll();
			super.updateUI();    		
	    } catch (ConfigManagerProxyPropertyNotInitializedException e) {
	    	logger.error("Update tableDiff Error", e);
		} catch (ConfigManagerProxyLoggedException e) {
			logger.error("Update tableDiff Error", e);
		}
	}
       private void addRowEmpty(TableModel tm, String broker, int row){
         tm.setValueAt(broker, row, 0);
    	   tm.setValueAt("",     row, 1);
    	   tm.setValueAt("",     row, 2);
    	   tm.setValueAt("",     row, 3);	  
    	   tm.setValueAt("",     row, 4);
    	   tm.setValueAt("",     row, 5);
    	   tm.setValueAt("",     row, 6);
      }
      private void addRow(TableModel tm, String broker, int row, DeployedObject o) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException{
         tm.setValueAt(broker, row, 0);
 		   tm.setValueAt(o.getExecutionGroup().getName(), row, 1);
 		   tm.setValueAt(o.getName(), row, 2);
 		   tm.setValueAt(o.getFileExtension(), row, 3);	  
 		   tm.setValueAt(timestampToString(o.getModifyTime()), row, 4);
 		   tm.setValueAt(timestampToString(o.getDeployTime()), row, 5);
 		   tm.setValueAt(o.getBARFileName(), row, 6);
      }
      private String timestampToString(Date date) {
		 if( date == null) {
			 return null;
		 } else {
			 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 return dateFormat.format(date);
		 }
	}
}
