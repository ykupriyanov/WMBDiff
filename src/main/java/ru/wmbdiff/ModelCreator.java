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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.config.proxy.MQBrokerConnectionParameters;

public class ModelCreator {
public class ModelFilter {
	private boolean onlyInA = true, onlyInB = true, equal = true, diff = true;
	public boolean isOnlyInA() {
		return onlyInA;
	}
	public void setOnlyInA(boolean onlyInA) {
		this.onlyInA = onlyInA;
	}
	public boolean isOnlyInB() {
		return onlyInB;
	}
	public void setOnlyInB(boolean onlyInB) {
		this.onlyInB = onlyInB;
	}
	public boolean isEqual() {
		return equal;
	}
	public void setEqual(boolean equal) {
		this.equal = equal;
	}
	public boolean isDiff() {
		return diff;
	}
	public void setDiff(boolean diff) {
		this.diff = diff;
	}
	public ModelFilter(){
		this(true, true, true, true);
	}
	public ModelFilter(boolean onlyInA, boolean onlyInB, boolean equal, boolean diff){
		this.setModelFilter(onlyInA, onlyInB, equal, diff);
	}
	public void setModelFilter(boolean onlyInA, boolean onlyInB, boolean equal, boolean diff){
		this.onlyInA = onlyInA;
		this.onlyInB = onlyInB;
		this.equal = equal;
		this.diff = diff;
	}
	boolean isFilter(DiffDeployedObjectResult d){
		switch(d.getResult()){
		case DIFF: return diff;
		case EQUAL: return equal;
		case ONLY_IN_A: return onlyInA;
		case ONLY_IN_B: return onlyInB;
		}
		return false;
	}
	}
private ModelFilter modelFilter = new ModelFilter();

public ModelFilter getModelFilter() {
	return modelFilter;
}
public void setModelFilter(boolean onlyInA, boolean onlyInB, boolean equal, boolean diff) {
	this.modelFilter.setModelFilter(onlyInA, onlyInB, equal, diff);
}
BrokerComparator bc;
public MQBrokerConnectionParameters getABrokerConnectionParameters(){
	return bc.getABrokerConnectionParameters();
}
public MQBrokerConnectionParameters getBBrokerConnectionParameters(){
	return bc.getBBrokerConnectionParameters();
}

static final boolean IS_IGNORE_EG_CASE = true;
private List<DiffDeployedObjectResult> diffResultList;
	public ModelCreator(BrokerConnectionParameters aBcp, BrokerConnectionParameters bBcp, String tempEgSuffix) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException {
		this.bc = new BrokerComparator(aBcp, bBcp,IS_IGNORE_EG_CASE, tempEgSuffix);
		this.diffResultList  = bc.compare();
		
}
public WMBDiffNoRootTreeTableModel getModel() throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException {
	 ListIterator<DiffDeployedObjectResult> litr = diffResultList.listIterator();
	 String prevEGName = null;
	 List<DiffExecutionGroup> diffExecutionGroupsList = new ArrayList<DiffExecutionGroup>();
	 List<DiffDeployedObjectResult> list = new ArrayList<DiffDeployedObjectResult>();
	 while(litr.hasNext()) {
    	 DiffDeployedObjectResult element = litr.next();
    	 if (!modelFilter.isFilter(element)) continue;
    	 String elementEGName = bc.getEGNameNoSuffix(element.getEgName());
    	 if(prevEGName != null && !(prevEGName.equals(elementEGName))){
    		 diffExecutionGroupsList.add(new DiffExecutionGroup(list));
    		 list = new ArrayList<DiffDeployedObjectResult>();
    		 prevEGName = elementEGName;
    	 } 
    	 if (prevEGName == null) prevEGName = elementEGName;
    	list.add(element);
	 }
	 if(!list.isEmpty()){
		 diffExecutionGroupsList.add(new DiffExecutionGroup(list));
	 }
	 return new  WMBDiffNoRootTreeTableModel( diffExecutionGroupsList);
}
}
