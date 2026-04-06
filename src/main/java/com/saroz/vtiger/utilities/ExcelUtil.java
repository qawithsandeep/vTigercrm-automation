package com.saroz.vtiger.utilities;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	private Workbook workbook;
	private Sheet sheet;
	private String filePath;

	// Constructor → Excel load automatically
	public ExcelUtil(String path, String sheetName) {
		try {
			this.filePath = path;
			FileInputStream fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet(sheetName);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load Excel file: " + path, e);
		}
	}

	// Get cell data
	public String getCellData(int rowNum, int colNum) {
		try {
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(colNum);

			if (cell == null) return "";

			switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				return String.valueOf(cell.getNumericCellValue());
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			default:
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	// Get row count
	public int getRowCount() {
		return sheet.getLastRowNum();
	}

	// Write data into Excel
	public void setCellData(int rowNum, int colNum, String value) {
		try {
			Row row = sheet.getRow(rowNum);
			if (row == null) row = sheet.createRow(rowNum);

			Cell cell = row.getCell(colNum);
			if (cell == null) cell = row.createCell(colNum);

			cell.setCellValue(value);

			FileOutputStream fos = new FileOutputStream(filePath);
			workbook.write(fos);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to write Excel file", e);
		}
	}

	// Close workbook (important)
	public void close() {
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


