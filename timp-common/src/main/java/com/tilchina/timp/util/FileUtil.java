package com.tilchina.timp.util;

import com.tilchina.catalyst.spring.PropertiesUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class FileUtil {

	public static String uploadFile(MultipartFile file, String... carVin) throws Exception {

		String path = PropertiesUtils.getString("timp.image.path");
		path = FileUtil.combine(path, Arrays.stream(carVin).findFirst().get());
		FileUtil.create(path);

		String filePath = FileUtil.combine(path, FileUtil.generateUniqueFileName(file.getOriginalFilename()));
		file.transferTo(new File(filePath));
		return filePath;
	}

	public static String uploadImage(MultipartFile file, String imageType) throws Exception {
		String path = PropertiesUtils.getString(imageType);
		FileUtil.create(path);
		String filePath = FileUtil.combine(path, FileUtil.generateUniqueFileName(file.getOriginalFilename()));
		file.transferTo(new File(filePath));
		return convertToUrlAddress(filePath);
	}

	public static String uploadExcel(MultipartFile file) throws IOException {
		String path = PropertiesUtils.getString("timp.excel.path");
		FileUtil.create(path);
		String filePath = FileUtil.combine(path, FileUtil.generateUniqueFileName(file.getOriginalFilename()));
		file.transferTo(new File(filePath));
		return filePath;
	}

	public static String uploadApk(MultipartFile file) throws Exception {
		String path = PropertiesUtils.getString("timp.app.path");
		FileUtil.create(path);
		String filePath = FileUtil.combine(path, FileUtil.generateUniqueFileName(file.getOriginalFilename()));
		file.transferTo(new File(filePath));
		return filePath;
	}

	public static String combine(String pathA, String pathB) {

		// File path = new File(pathA, pathB);
		// return path.getPath();

		String path = String.format("%s/%s", pathA, pathB);
		return path;
	}

	public static void create(String path) {

		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public static String generateUniqueFileName(String fileName) {

		UUID uuid = UUID.randomUUID();
		String extension = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
		return String.format("%s%s", uuid.toString(), extension);
	}

	public static String convertToUrlAddress(String fileName) {
		fileName = String.format("http://10.8.12.123%s", fileName.substring(5, fileName.length()));
		return fileName;
	}
}
