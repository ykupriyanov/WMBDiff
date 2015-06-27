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

public class DiffExecutionGroup {
	private List<DiffDeployedObjectResult> diffResultList;
	private String name;
	public String getName() {
		return name;
	}
	public DiffExecutionGroup(List<DiffDeployedObjectResult> diffResultList)  {
		super();
		this.diffResultList = diffResultList;
		this.name = this.diffResultList.get(0).getEgName();
	}
	public List<DiffDeployedObjectResult> getDiffResultList() {
		return diffResultList;
	}
	public void setDiffResultList(List<DiffDeployedObjectResult> diffResultList) {
		this.diffResultList = diffResultList;
	}
}
