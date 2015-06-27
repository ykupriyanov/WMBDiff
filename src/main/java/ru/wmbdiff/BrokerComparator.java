
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.wmbdiff.DiffDeployedObjectResult.Result;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.config.proxy.DeployedObject;
import com.ibm.broker.config.proxy.ExecutionGroupProxy;
import com.ibm.broker.config.proxy.MQBrokerConnectionParameters;
import com.ibm.broker.config.proxy.MessageFlowProxy;

public class BrokerComparator {
    final static Logger logger = LoggerFactory.getLogger(WMBDiff.class);
    BrokerProxy aBroker;
    BrokerProxy bBroker;
    MQBrokerConnectionParameters aBrokerCP;
    MQBrokerConnectionParameters bBrokerCP;
    boolean isIgnoreEGCase;//true
    public String tempEGSuffix;// "_TEMP";
   
    public MQBrokerConnectionParameters getABrokerConnectionParameters(){
    	return aBrokerCP;
    }
    public MQBrokerConnectionParameters getBBrokerConnectionParameters(){
    	return bBrokerCP;
    }
   
    public BrokerComparator(BrokerConnectionParameters aBcp, BrokerConnectionParameters bBcp, boolean isIgnoreEGCase, String tempEGSuffix) {
    	this.aBrokerCP = new MQBrokerConnectionParameters(aBcp.getHost(), aBcp.getPort(), aBcp.getMqMgr());
        this.bBrokerCP = new MQBrokerConnectionParameters(bBcp.getHost(), bBcp.getPort(), bBcp.getMqMgr());       
        this.isIgnoreEGCase = isIgnoreEGCase;
        this.tempEGSuffix = tempEGSuffix.toUpperCase();
        this.aBroker = connect(aBrokerCP);
        this.bBroker = connect(bBrokerCP);
    }

    private BrokerProxy connect(MQBrokerConnectionParameters bcp) {
    	 BrokerProxy b = null;
    	 try {
    		 logger.info("Connecting to Remote Broker");
             b = BrokerProxy.getInstance(bcp);         
             boolean brokerIsResponding = b.hasBeenPopulatedByBroker(true);          
             if (brokerIsResponding) {
            	 logger.info("Successfully connected to the broker");
             } else {
            	 logger.error("No response from Broker");
                 b.disconnect();
                 b = null;
             }
         } catch (ConfigManagerProxyLoggedException e) {
        	 logger.error("Connect failed:", e);
         }
         return b;
    }
     
   public String getEGNameNoSuffix(String egName) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException{
	    egName = getEGName(egName);
    	if(tempEGSuffix != null && egName.endsWith(tempEGSuffix)) 
    		egName = egName.substring(0, egName.length() - tempEGSuffix.length());
       	return egName;
    }
   private String getEGNameNoSuffix(DeployedObject o) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException{
	   return getEGNameNoSuffix(getEGName(o));
   }
   private String getEGName(String egName){
   	if(isIgnoreEGCase) egName = egName.toUpperCase();
   	return egName;
   }
   private String getEGName(DeployedObject o) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException{
	    return getEGName(o.getExecutionGroup().getName()); 	
    }
    private List<DeployedObject> getSortedList(BrokerProxy b) throws ConfigManagerProxyPropertyNotInitializedException {
    	List<DeployedObject> depObjList = new ArrayList<DeployedObject>();
    	 //Get EG
        Enumeration<ExecutionGroupProxy> egList = b.getExecutionGroups(null);
         while (egList.hasMoreElements()) {
            ExecutionGroupProxy curEG = egList.nextElement();
            //add only running message flow           
            Enumeration<DeployedObject> deployedObjects = curEG.getDeployedObjects(null);
            while (deployedObjects.hasMoreElements()) {
            	DeployedObject curObject = deployedObjects.nextElement();
            	if(curObject instanceof MessageFlowProxy) {
            		MessageFlowProxy curMsgFlow = (MessageFlowProxy) curObject;
            		if(curMsgFlow.isRunning()) depObjList.add(curMsgFlow);
            	} else {
            		depObjList.add(curObject);
            	}             
            }
    
            Collections.sort(depObjList,  new Comparator<DeployedObject>() {
                public int compare(DeployedObject o1, DeployedObject o2) {
                	    int result = 0;
                	    try {
                	    	result = compareDeployedObjectByName(o1, o2, false);
                	 	} catch (ConfigManagerProxyPropertyNotInitializedException e) {
                	 		logger.error("compare", e);							
						} catch (ConfigManagerProxyLoggedException e) {
							logger.error("compare", e);
						}
                	    return result;
                }
            });
            
         	}
          
          return depObjList;
        }
    private boolean compareBARFileName(String aBARFileName, String bBARFileName){
    	if(aBARFileName == null || aBARFileName.equals("") ||
    	   bBARFileName == null || bBARFileName.equals("")) {
    		return false;
    	} else {
    		File aFile = new File(aBARFileName);
    		File bFile = new File(bBARFileName);
    		return aFile.getName().equals(bFile.getName());
    	}
    }
    public List<DiffDeployedObjectResult> compare() throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException{
    	 List<DeployedObject>  al = getSortedList(aBroker);
    	 List<DeployedObject>  bl = getSortedList(bBroker);
    	 //Если задан суффикс эквивалентных групп, то при наличии нескольких объектов, оставляем, тот что в группе с суффиксом
    	 if(!tempEGSuffix.equals("")){
    	    al = removeDuplicate(al);
    	    bl = removeDuplicate(bl);
    	 }
    	 DeployedObject[] a = al.toArray(new DeployedObject[al.size()]);
    	 DeployedObject[] b = bl.toArray(new DeployedObject[bl.size()]);
    	 int lenA = a.length;
    	 int lenB = b.length;
    	 int aIdx = 0, bIdx = 0;
    	 int len = lenA + lenB;
    	 List<DiffDeployedObjectResult> result = new ArrayList<DiffDeployedObjectResult>();
    	 for (int i = 0; i < len; i++) {
    		if (bIdx < lenB && aIdx < lenA) {
    				int cmp = compareDeployedObjectByName(a[aIdx],b[bIdx], true);
    				if (cmp == 0){
    					//нашли и в A и в B
    					String egName = a[aIdx].getExecutionGroup().getName();					
    					String name = a[aIdx].getFullName();
    					String resultDesc = "";
    					Result diffResult = DiffDeployedObjectResult.Result.EQUAL;
    					
    					if(! compareBARFileName(a[aIdx].getBARFileName(), b[bIdx].getBARFileName())) {
    						resultDesc += "; " + "Different BAR file name";
    						diffResult = Result.DIFF;
    					}
    					if(! a[aIdx].getModifyTime().equals(b[bIdx].getModifyTime())){
    						resultDesc += "; " + "Different Modifty Time";
    						diffResult = Result.DIFF;
    					}
    					if(!resultDesc.equals("")) resultDesc = resultDesc.substring(2, resultDesc.length());
    					result.add(new DiffDeployedObjectResult(egName, name, resultDesc, diffResult   , a[aIdx], b[bIdx]));
    					aIdx++;
    					bIdx++;
    					i++;
    				} else if(cmp > 0 ){
    					//нашли только в B
    					result.add(new DiffDeployedObjectResult(b[bIdx].getExecutionGroup().getName(), b[bIdx].getFullName(), "Missing in A", Result.ONLY_IN_B, null,  b[bIdx]));
    					bIdx++;
    				} else {
    					// нашли только в A
    					result.add(new DiffDeployedObjectResult(a[aIdx].getExecutionGroup().getName(), a[aIdx].getFullName(), "Missing in B", Result.ONLY_IN_A, a[aIdx],  null));
    					aIdx++;
    				}
    		} else if (bIdx < lenB) {
    			//остались только в B
				result.add(new DiffDeployedObjectResult(b[bIdx].getExecutionGroup().getName(), b[bIdx].getFullName(), "Missing in A", Result.ONLY_IN_B, null,  b[bIdx]));
				bIdx++;

    		} else {
    			//остались только в A
    			result.add(new DiffDeployedObjectResult(a[aIdx].getExecutionGroup().getName(), a[aIdx].getFullName(), "Missing in B", Result.ONLY_IN_A, a[aIdx],  null));
				aIdx++;
    		}
    	}
    	return result;
    }
  
    /*Если задан суффикс эквивалентных групп, то 
     * при наличии нескольких объектов отличных от MsgFlow
     * оставляем, тот что в группе с суффиксом т.е. находится дальше по списку
     */
    private List<DeployedObject> removeDuplicate(List<DeployedObject> dObjList) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException {
    	ListIterator<DeployedObject> iter = dObjList.listIterator(dObjList.size());
    	DeployedObject curr = null;
    	logger.debug("removeDuplicate");
    	while (iter.hasPrevious()) {
    		DeployedObject prev = iter.previous();
    	    if( curr == null){
    	    	curr = prev;
    	    	continue;
    	    }
    	   	if(!prev.getFileExtension().equals("cmf") &&
    	    	prev.getFullName().equals(curr.getFullName()) &&
    	        getEGNameNoSuffix(prev).equals(getEGNameNoSuffix(curr))){
    	    	iter.remove();	 	
    	      	logger.debug("Prev " + prev.getFullName());
        	    logger.debug("Curr " + curr.getFullName());
        	    logger.debug("Prev " + getEGNameNoSuffix(prev));
        	    logger.debug("Curr " + getEGNameNoSuffix(curr));
    	    	continue;
    	    }	
    	    curr = prev;
    	}
		return dObjList;
	}

    private int compareDeployedObjectByName(DeployedObject o1, DeployedObject o2, boolean isIgnoreEGSuffix) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException{
    	int result = 0;
    	String o1EGName = getEGName(o1); //с учетом нужного регистра
        String o2EGName = getEGName(o2);
        String o1EGNameNoSuffix = getEGNameNoSuffix(o1);
        String o2EGNameNoSuffix = getEGNameNoSuffix(o2);
    	result = o1EGNameNoSuffix.compareTo(o2EGNameNoSuffix);
    	if (result == 0) {
    		//внутри "группы" без учета суффикса упорядочиваем по имени
    		result = o1.getFullName().compareTo(o2.getFullName());
    		if(result == 0 && !isIgnoreEGSuffix){
    			//Если объекты равны по имени без учета суффикса EG(_TEMP), то раньше идет тот который в основной группе
    			result =  o1EGName.compareTo(o2EGName);
    		}
    	}
    	return result;
    }
  	public String getTempEGSuffix() {
		return tempEGSuffix;
	}

        
}
