package com.tilchina.timp.util;
/*
 * @author XueYuSong
 * @date 2018-06-04 13:20
 */

import com.tilchina.timp.annotation.EnumField;
import com.tilchina.timp.annotation.ExportField;
import com.tilchina.timp.annotation.ImportField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil {

	/**
	 * 根据EXCEL文件后缀返回对应的Workbook对象
	 * @param excelPath
	 * @return Workbook
	 * @throws Exception
	 */
	public static Workbook getWorkbook(String excelPath) throws Exception {
		File file = new File(excelPath);
		if (StringUtils.isBlank(excelPath) || !file.isFile()) {
			throw new RuntimeException("请检查上传文件路径是否有效或文件是否存在。");
		}

		Workbook workbook = null;
		if (excelPath.endsWith(".xls")) {
			workbook = new HSSFWorkbook(new FileInputStream(excelPath));
		} else if (excelPath.endsWith(".xlsx")) {
			workbook = new XSSFWorkbook(new FileInputStream(excelPath));
		} else {
			throw new RuntimeException("请检查上传文件后缀名是否为.xls或.xlsx结尾的有效EXCEL文件。");
		}

		if (workbook.getNumberOfSheets() <= 0) {
			throw new RuntimeException("EXCEL文件中未找到任何有效工作表, 请检查后重试。。");
		}

		return workbook;
	}

	/**
	 * 校验EXCEL文件中的数据是否符合标准
	 * @param workbook
	 * @param nullableMap ("test", true)意为EXCEL中标题为test的列的数据可以为空
	 * @return
	 */
	public static String validateWorkbook(Workbook workbook, Map<String, Boolean> nullableMap) {
		Sheet sheet = workbook.getSheetAt(0);

		Integer rowCount = sheet.getLastRowNum();
		Integer colCount = getLastColNum(workbook);

		if (rowCount <= 0 || colCount <= 0) {
			throw new RuntimeException("请检查EXCEL文件中是否包含有效数据。");
		}

		Map<Integer, String> titleMap = new HashMap<>();
		for (Integer i = 0; i < colCount; i++) {
			Cell cell = sheet.getRow(0).getCell(i);
			if (Objects.nonNull(cell)) {
				cell.setCellType(CellType.STRING);
			}
			if (Objects.isNull(cell) || StringUtils.isBlank(cell.getStringCellValue().trim())) {
				throw new RuntimeException("请检查EXCEL文件是否为当前页面提供的下载模板。");
			}
			if (nullableMap.containsKey(cell.getStringCellValue().trim())) {
				titleMap.put(i, cell.getStringCellValue().trim());
			}
		}

		StringBuilder errorMsg = new StringBuilder();
		for (Integer i = 1; i <= rowCount; i++) {
			Cell[] cells = new Cell[colCount];
			for (Integer j = 0; j < colCount; j++) {
				cells[j] = cells[j] = sheet.getRow(i).getCell(j);
				if (Objects.nonNull(cells[j])) {
					cells[j].setCellType(CellType.STRING);
				}

				if (Objects.nonNull(titleMap.get(j))) {
					if (!nullableMap.get(titleMap.get(j))) {
						if (Objects.isNull(cells[j]) || StringUtils.isBlank(cells[j].getStringCellValue().trim())) {
							errorMsg.append(String.format("[%c%d] %s不能为空/r/n", (char) ('A' + j), i + 1, titleMap.get(j)));
						}
					}
				}
			}
		}

		return errorMsg.toString();
	}

	/**
	 * 根据传入的对象动态读取EXCEL文件
	 * @param workbook
	 * @param clazz 对象类型
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static<T> List<T> getModelsFromWorkbook(Workbook workbook, Class<T> clazz) throws Exception {

		List<T> models = new ArrayList<>();
		Sheet sheet = workbook.getSheetAt(0);

		Integer rowCount = sheet.getLastRowNum();
		Integer colCount = getLastColNum(workbook);

		if (rowCount <= 0 || colCount <= 0) {
			throw new RuntimeException("请检查EXCEL文件中是否包含有效数据。");
		}

		Map<Integer, String> titleMap = new HashMap<>();
		for (Integer i = 0; i < colCount; i++) {
			Cell cell = sheet.getRow(0).getCell(i);
			if (Objects.nonNull(cell)) {
				cell.setCellType(CellType.STRING);
			}
			if (Objects.isNull(cell) || StringUtils.isBlank(cell.getStringCellValue().trim())) {
				throw new RuntimeException("请检查EXCEL文件是否为当前页面提供的下载模板。");
			}
			titleMap.put(i, cell.getStringCellValue().trim());
		}

		Field[] fields = clazz.getDeclaredFields();
		for (Integer i = 1; i <= rowCount; i++) {
			Cell[] cells = new Cell[colCount];
			T newInstance = clazz.newInstance();

			for (Integer j = 0; j < colCount; j++) {
				cells[j] = cells[j] = sheet.getRow(i).getCell(j);
				if (Objects.nonNull(cells[j])) {
					cells[j].setCellType(CellType.STRING);
				}

				for (Field field : fields) {
					Annotation[] annotations = field.getAnnotations();
					for (Annotation annotation : annotations) {
						if (annotation instanceof EnumField) {
							EnumField enumField = (EnumField) annotation;
							field.setAccessible(true);

							if (titleMap.get(j).equals(enumField.title())) {
								Class<?> aClass = Class.forName(enumField.value());

								Method getIndexByName = aClass.getDeclaredMethod("getIndexByName", String.class);
								if (Objects.isNull(cells[j]) || StringUtils.isBlank(cells[j].getStringCellValue().trim())) {
									field.set(newInstance, 0);
								} else {
									int index = (int) getIndexByName.invoke(null, cells[j].getStringCellValue());
									field.set(newInstance, index);
								}
							}
						}
						if (annotation instanceof ImportField) {
							ImportField importField = (ImportField) annotation;
							field.setAccessible(true);

							if (titleMap.get(j).equals(importField.title())) {
								String typeName = field.getGenericType().getTypeName();

								if (Objects.isNull(cells[j])) {
									field.set(newInstance, null);
									continue;
								}

								// TODO 各类型转换未测试，可能发生异常
								switch (typeName) {
									case "java.lang.Long":
										field.set(newInstance, NumberUtils.toLong(cells[j].getStringCellValue().trim()));
										break;
									case "java.lang.Float":
										field.set(newInstance, NumberUtils.toFloat(cells[j].getStringCellValue().trim()));
										break;
									case "java.lang.Double":
										field.set(newInstance, NumberUtils.toDouble(cells[j].getStringCellValue().trim()));
										break;
									case "java.lang.String":
										field.set(newInstance, cells[j].getStringCellValue().trim());
										break;
									case "java.lang.Integer":
										field.set(newInstance, NumberUtils.toInt(cells[j].getStringCellValue().trim()));
										break;
									case "java.math.BigDecimal":
										field.set(newInstance, new BigDecimal(cells[j].getStringCellValue().trim()));
										break;
									case "java.util.Date":
										field.set(newInstance, new SimpleDateFormat("yyyy-MM-dd").parse(cells[j].getStringCellValue().trim()));
										break;
								}
							}
						}
					}
				}
			}
			models.add(newInstance);
		}

		return models;
	}

	/**
	 * 根据传入的对象动态创建EXCEL文件
	 * @param objects
	 * @param <T>
	 * @return Workbook对象
	 * @throws Exception
	 */
	public static<T> Workbook getWorkbookFromModels(List<T> objects) throws Exception {
		Workbook workbook = new XSSFWorkbook();

		if (CollectionUtils.isNotEmpty(objects)) {
			Sheet sheet = workbook.createSheet();

			int i = 0;
			T t = objects.get(0);
			Map<String, Integer> positionMap = new HashMap<>();

			Field[] fields = t.getClass().getDeclaredFields();
			for (Field field : fields) {
				Annotation[] annotations = field.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation instanceof ExportField || annotation instanceof EnumField) {
						String title = "";
						// 当注解为ExportField时
						if (annotation instanceof ExportField) {
							ExportField exportField = (ExportField) annotation;
							title = exportField.title();
							positionMap.put(title, i);
						}

						// 当注解为EnumField时
						if (annotation instanceof EnumField) {
							EnumField enumField = (EnumField) annotation;
							title = enumField.title();
							positionMap.put(title, i);
						}

						Cell cell;
						if (Objects.isNull(sheet.getRow(0))) {
							cell = sheet.createRow(0).createCell(i);
						} else {
							cell = sheet.getRow(0).createCell(i);
						}

						cell.setCellValue(title);
						i++;
					}
				}
			}

			for (int index = 0; index < objects.size(); index++) {
				Field[] declaredFields = objects.get(index).getClass().getDeclaredFields();
				for (Field declaredField : declaredFields) {
					Annotation[] annotations = declaredField.getAnnotations();
					for (Annotation annotation : annotations) {
						if (annotation instanceof ExportField || annotation instanceof EnumField) {
							declaredField.setAccessible(true);


							String title = "";
							if (annotation instanceof ExportField) {
								ExportField exportField = (ExportField) annotation;
								title = exportField.title();
							}

							if (annotation instanceof EnumField) {
								EnumField enumField = (EnumField) annotation;
								title = enumField.title();
							}

							Integer rowNum = index + 1;
							Integer colNum = positionMap.get(title);

							if (Objects.nonNull(declaredField.get(objects.get(index)))) {
								Cell cell;
								if (Objects.isNull(sheet.getRow(rowNum))) {
									cell = sheet.createRow(rowNum).createCell(colNum);
								} else {
									cell = sheet.getRow(rowNum).createCell(colNum);
								}

								if (annotation instanceof ExportField) {
									// 将日期类型的字段处理为yyyy-MM-dddd的格式
									if (declaredField.getType().isAssignableFrom(Date.class)) {
										String dateStr = DateFormatUtils.format((Date) declaredField.get(objects.get(index)), "yyyy-MM-dd");
										cell.setCellValue(dateStr);
									} else {
										cell.setCellValue(declaredField.get(objects.get(index)).toString());
									}
								}
								if (annotation instanceof EnumField) {
									EnumField enumField = (EnumField) annotation;
									Class<?> aClass = Class.forName(enumField.value());
									Method getIndexByName = aClass.getDeclaredMethod("getNameByIndex", int.class);

									if (Objects.isNull(objects.get(index)) || Objects.isNull(declaredField.get(objects.get(index))) || StringUtils.isBlank(declaredField.get(objects.get(index)).toString())) {
										cell.setCellValue(declaredField.get(objects.get(index)).toString());
									} else {
										String name = (String) getIndexByName.invoke(null, NumberUtils.toInt(declaredField.get(objects.get(index)).toString()));
										cell.setCellValue(name);
									}
								}
							}
						}
					}
				}
			}
		}
		return workbook;
	}

	/**
	 * 获取EXCEL文件的总行数
	 * @param workbook
	 * @return
	 */
	public static Integer getLastRowNum(Workbook workbook) {
		Sheet sheet = workbook.getSheetAt(0);

		int i = 0;
		while (true) {
			Cell cell;
			try {
				cell = sheet.getRow(i).getCell(0);
				cell.setCellType(CellType.STRING);
			} catch (Exception e) {
				return i;
			}

			if (Objects.isNull(cell) || StringUtils.isBlank(cell.getStringCellValue().trim())) {
				return i;
			}
			i++;
		}
	}

	/**
	 * 获取EXCEL文件的总列数
	 * @param workbook
	 * @return
	 */
	public static Integer getLastColNum(Workbook workbook) {
		Sheet sheet = workbook.getSheetAt(0);

		int i = 0;
		while (true) {
			Cell cell;
			try {
				cell = sheet.getRow(0).getCell(i);
				cell.setCellType(CellType.STRING);
			} catch (Exception e) {
				return i;
			}

			if (Objects.isNull(cell) || StringUtils.isBlank(cell.getStringCellValue().trim())) {
				return i;
			}
			i++;
		}
	}

	/**
	 * 通过response向浏览器返回workbook对象
	 * @param response HttpServletResponse对象
	 * @param workbook workbook对象
	 * @param fileName 文件名称
	 * @throws Exception
	 */
	public static void returnToBrowser(HttpServletResponse response, Workbook workbook, String fileName) throws Exception {
		fileName = URLEncoder.encode(fileName, "UTF-8");
		try (OutputStream outputStream = response.getOutputStream()) {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", String.format("attachment;filename=%s.xlsx", fileName));
			workbook.write(outputStream);
		}
	}

	public static int getLastRowNumber(Workbook workbook) {

		Sheet sheet = workbook.getSheetAt(0);

		int i = 0;
		while (true) {
			Cell cell;
			try {
				cell = sheet.getRow(i).getCell(0);
				cell.setCellType(CellType.STRING);
			} catch (Exception e) {
				return i - 1;
			}

			if (cell == null || StringUtils.isBlank(cell.getStringCellValue())) {
				return i - 1;
			}
			i++;
		}
	}
}
