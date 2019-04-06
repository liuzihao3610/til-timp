package com.tilchina.timp;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class TestClass {
	public static void main(String[] args) {

		String excelPath = "C:\\Users\\YuSong Xue\\Desktop\\TEST.xlsx";

		try {
			Workbook workbook = new XSSFWorkbook(new FileInputStream(excelPath));
			Sheet sheet = workbook.getSheetAt(0);
			int rowCount = sheet.getLastRowNum();

			for (int i = 0; i <= rowCount; i++) {
				Cell cell = sheet.getRow(i).getCell(0);
				cell.setCellType(CellType.STRING);
				System.out.println(cell);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
