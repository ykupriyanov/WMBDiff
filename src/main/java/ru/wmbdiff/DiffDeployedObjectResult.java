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
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.config.proxy.DeployedObject;
public class DiffDeployedObjectResult {
	public enum Result {
	    ONLY_IN_A, ONLY_IN_B, EQUAL, DIFF
	}
	  private Result result;
	  public Result getResult() {
		return result;
	}
	private String name;
	  private String egName;
	  public DiffDeployedObjectResult(String egName, String name, String resultDesc, Result result,
			DeployedObject aObject, DeployedObject bObject) throws ConfigManagerProxyPropertyNotInitializedException, ConfigManagerProxyLoggedException {
		super();
		this.egName = egName;
		this.name = name;
		this.resultDesc = resultDesc;
		this.result = result;
		this.aObject = aObject;
		this.bObject = bObject;	
	  }
	public String getName() {
		return name;
	}

	public String getResultDesc() {
		return resultDesc;
	}
	

	public DeployedObject getAObject() {
		return aObject;
	}

	public DeployedObject getBObject() {
		return bObject;
	}

	public String getEgName() {
		return egName;
	}

	private String resultDesc;
	private DeployedObject aObject;
	private DeployedObject bObject;
}
