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
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.config.proxy.DeployedObject;
public class ExportIntoExcel {
final static Logger logger = LoggerFactory.getLogger(ExportIntoExcel.class);
public ExportIntoExcel() {
}
private String timestampToString(Date date) {
	 if(date == null){
		 return null;
	 } else {
		 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 return dateFormat.format(date);
	 }
}
private void createRow(int row, Sheet sheet, DeployedObject dObj, String result, String resultDesc){
	Row rowData = sheet.createRow(row);
	try {
		rowData.createCell(0).setCellValue(result);
		rowData.createCell(1).setCellValue(dObj.getExecutionGroup().getParent().getName());
		rowData.createCell(2).setCellValue(dObj.getExecutionGroup().getName());
		rowData.createCell(3).setCellValue(dObj.getName());
		rowData.createCell(4).setCellValue(dObj.getFileExtension());
		rowData.createCell(5).setCellValue(timestampToString(dObj.getModifyTime()));
		rowData.createCell(6).setCellValue(timestampToString(dObj.getDeployTime()));
		rowData.createCell(7).setCellValue(dObj.getBARFileName());
		rowData.createCell(8).setCellValue(resultDesc);
	} catch (ConfigManagerProxyPropertyNotInitializedException e) {
		logger.error("createRow", e);
	} catch (ConfigManagerProxyLoggedException e) {
		logger.error("createRow", e);
	}
}
public void export(File file, WMBDiffNoRootTreeTableModel model) {
	logger.info("export begin");
	Workbook workbook = new  HSSFWorkbook(); 
    Sheet sheet = workbook.createSheet("WMBDiff");
    int rowNum = 0;
    //Create Header
    CellStyle style;
    Font headerFont = workbook.createFont();
    headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
    headerFont.setColor(IndexedColors.WHITE.getIndex());
    headerFont.setFontHeightInPoints((short) 10);
    style = workbook.createCellStyle();
    style.setBorderRight(CellStyle.BORDER_THIN);
    style.setRightBorderColor(IndexedColors.WHITE.getIndex());
    style.setBorderBottom(CellStyle.BORDER_THIN);
    style.setBottomBorderColor(IndexedColors.WHITE.getIndex());
    style.setBorderLeft(CellStyle.BORDER_THIN);
    style.setLeftBorderColor(IndexedColors.WHITE.getIndex());
    style.setBorderTop(CellStyle.BORDER_THIN);
    style.setTopBorderColor(IndexedColors.WHITE.getIndex());
    style.setAlignment(CellStyle.ALIGN_CENTER);
    style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    style.setFont(headerFont);
    
    Row row = sheet.createRow(rowNum++);
    Cell cell;
    cell = row.createCell(0);
    cell.setCellValue("Result");
    cell.setCellStyle(style);
    cell = row.createCell(1);
    cell.setCellValue("Broker");
    cell.setCellStyle(style);
    cell = row.createCell(2);
    cell.setCellValue("Execution Group");
    cell.setCellStyle(style);
    cell = row.createCell(3);
    cell.setCellValue("Name");
    cell.setCellStyle(style);
    cell = row.createCell(4);
    cell.setCellValue("Type");
    cell.setCellStyle(style);
    cell = row.createCell(5);
    cell.setCellValue("Last Modificaton");
    cell.setCellStyle(style);
    cell = row.createCell(6);
    cell.setCellValue("Deployment Date");
    cell.setCellStyle(style);
    cell = row.createCell(7);
    cell.setCellValue("Bar File");
    cell.setCellStyle(style);
    cell = row.createCell(8);
    cell.setCellValue("Result Description");
    cell.setCellStyle(style);
    sheet.createFreezePane(0,1);

    List<DiffExecutionGroup> dEG = model.getDiffExecutionGroupList() ;
    ListIterator<DiffExecutionGroup> litr = dEG.listIterator();
	while(litr.hasNext()) {
		DiffExecutionGroup element = litr.next();
		element.getDiffResultList();
		ListIterator<DiffDeployedObjectResult> litr2 = element.getDiffResultList().listIterator();
		while( litr2.hasNext()){
			DiffDeployedObjectResult res = litr2.next();
			switch(res.getResult()){
				case ONLY_IN_A: createRow(rowNum++, sheet,res.getAObject(), "A", res.getResultDesc()); break;
				case ONLY_IN_B: createRow(rowNum++, sheet,res.getBObject(), "B", res.getResultDesc()); break;
				case EQUAL    : createRow(rowNum++, sheet,res.getAObject(), "=", res.getResultDesc());
								createRow(rowNum++, sheet,res.getBObject(), "=", res.getResultDesc());
								sheet.groupRow(rowNum-2, rowNum-2);
								break;
				case DIFF     : createRow(rowNum++, sheet,res.getAObject(), "!=", res.getResultDesc());
								createRow(rowNum++, sheet,res.getBObject(), "!=", res.getResultDesc());
								sheet.groupRow(rowNum-2, rowNum-2);
								break;
			};
    	
		};	
	};
    //Adjust column width to fit the contents
	for(int i=0; i<9;i++) sheet.autoSizeColumn(i);
	//set Filter
	sheet.setAutoFilter(new CellRangeAddress(0, rowNum-1, 0, 8));
    try {
    	  FileOutputStream out = new FileOutputStream(file);
		  workbook.write(out);
		  workbook.close();
		  out.close();
	} catch (Exception e) {
		logger.error("export", e);
	}
	logger.info("export end");
}
}

