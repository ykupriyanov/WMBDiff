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

import java.util.List;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class WMBDiffNoRootTreeTableModel extends AbstractTreeTableModel {
    private final static String[] COLUMN_NAMES = {"Name", "A", "B", "Result Description", "DiffDeployedObject"};
    
    private List<DiffExecutionGroup> diffExecutionGroupList;

    public List<DiffExecutionGroup> getDiffExecutionGroupList() {
		return diffExecutionGroupList;
	}

	public WMBDiffNoRootTreeTableModel( List<DiffExecutionGroup> diffExecutionGroupList) {
        super(new Object());
        this.diffExecutionGroupList =  diffExecutionGroupList;
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    public boolean isLeaf(Object node) {
        return node instanceof DiffDeployedObjectResult;
    }

    public int getChildCount(Object parent) {
        if (parent instanceof DiffExecutionGroup) {
        	DiffExecutionGroup diffEG = (DiffExecutionGroup) parent;
            return diffEG.getDiffResultList().size();
        }
        return  diffExecutionGroupList.size();
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof DiffExecutionGroup) {
        	DiffExecutionGroup diffEG = (DiffExecutionGroup) parent;
            return diffEG.getDiffResultList().get(index);
        }
        return diffExecutionGroupList.get(index);
    }

    public int getIndexOfChild(Object parent, Object child) {
    	DiffExecutionGroup diffEG = (DiffExecutionGroup) parent;
    	DiffDeployedObjectResult diffResult = (DiffDeployedObjectResult) child;
        return diffEG.getDiffResultList().indexOf(diffResult);
    }

    public Object getValueAt(Object node, int column) {
        if (node instanceof DiffExecutionGroup) {
            DiffExecutionGroup diffEG = (DiffExecutionGroup) node;
            switch (column) {
                case 0:
                    return diffEG.getName();
              }
        } else if (node instanceof DiffDeployedObjectResult) {
            DiffDeployedObjectResult diffResult = (DiffDeployedObjectResult) node;
            switch (column) {
                case 0:
                    return diffResult.getName();
                case 1:
                	String columnA="";
                	switch( diffResult.getResult()){
                	case ONLY_IN_A: columnA= "A" ; break;
                	case ONLY_IN_B: columnA=  "" ; break;
                	case EQUAL    : columnA= "="; break;
                	case DIFF     : columnA= "!="; break;
                	};
                	return columnA;
                case 2:
                	String columnB="";
                	switch( diffResult.getResult()){
                	case ONLY_IN_A: columnB= "" ; break;
                	case ONLY_IN_B: columnB=  "B" ; break;
                	case EQUAL    : columnB= "="; break;
                	case DIFF     : columnB= "!="; break;
                	};
                	return columnB;
                case 3:
                    return diffResult.getResultDesc();
                case 4:
                	return diffResult;
            }
        }
        return null;
    }
}
