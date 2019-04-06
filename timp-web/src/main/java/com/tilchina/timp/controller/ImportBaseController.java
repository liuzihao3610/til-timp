package com.tilchina.timp.controller;

import com.tilchina.auth.Auth;
import com.tilchina.catalyst.result.ApiResult;
import com.tilchina.timp.enums.ClientType;
import com.tilchina.timp.manager.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping(value = {"/s1/imp"}, produces = {"application/json;charset=utf-8"})
@Slf4j
public class ImportBaseController {

    @Autowired
    private ImportBaseManager importBaseManager;

    @Autowired
    private ImportDriverManager importDriverManager;

    @Autowired
    private ImportCarManager importCarManager;

    @Autowired
    private ImportPriceManager importPriceManager;

    @Autowired
    private ImportTransManager importTransManager;

    //批量导入
    @RequestMapping(value = "/upLoad", method = RequestMethod.POST, produces = "multipart/form-data;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> upLoad(@RequestParam("file") MultipartFile file) {
        log.info("File: {}", file.getOriginalFilename());
        File f = null;
        try {
            String fileName = file.getOriginalFilename();
            f = File.createTempFile(UUID.randomUUID().toString(), fileName.substring(fileName.lastIndexOf(".")));
            file.transferTo(f);
            importBaseManager.readFile(f);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        } finally {
            f.deleteOnExit();
        }
    }

    //批量导入
    @RequestMapping(value = "/driver", method = RequestMethod.POST, produces = "multipart/form-data;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> driver(@RequestParam("file") MultipartFile file) {
        log.info("File: {}", file.getOriginalFilename());
        File f = null;
        try {
            String fileName = file.getOriginalFilename();
            f = File.createTempFile(UUID.randomUUID().toString(), fileName.substring(fileName.lastIndexOf(".")));
            file.transferTo(f);
            importDriverManager.readFile(f);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        } finally {
            f.deleteOnExit();
        }
    }

    //批量导入
    @RequestMapping(value = "/car", method = RequestMethod.POST, produces = "multipart/form-data;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> car(@RequestParam("file") MultipartFile file) {
        log.info("File: {}", file.getOriginalFilename());
        File f = null;
        try {
            String fileName = file.getOriginalFilename();
            f = File.createTempFile(UUID.randomUUID().toString(), fileName.substring(fileName.lastIndexOf(".")));
            file.transferTo(f);
            importCarManager.readFile(f);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        } finally {
            f.deleteOnExit();
        }
    }

    //批量导入
    @RequestMapping(value = "/price", method = RequestMethod.POST, produces = "multipart/form-data;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> price(@RequestParam("file") MultipartFile file) {
        log.info("File: {}", file.getOriginalFilename());
        File f = null;
        try {
            String fileName = file.getOriginalFilename();
            f = File.createTempFile(UUID.randomUUID().toString(), fileName.substring(fileName.lastIndexOf(".")));
            file.transferTo(f);
            importPriceManager.readFile(f);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        } finally {
            f.deleteOnExit();
        }
    }

    @RequestMapping(value = "/trans", method = RequestMethod.POST, produces = "multipart/form-data;charset=utf-8")
    @Auth(ClientType.WEB)
    public ApiResult<String> trans(@RequestParam("file") MultipartFile file) {
        log.info("File: {}", file.getOriginalFilename());
        File f = null;
        try {
            String fileName = file.getOriginalFilename();
            f = File.createTempFile(UUID.randomUUID().toString(), fileName.substring(fileName.lastIndexOf(".")));
            file.transferTo(f);
            importTransManager.readFile(f);
            return ApiResult.success();
        } catch (Exception e) {
            log.error("导入失败，param={}", e);
            return ApiResult.failure("9999", e.getMessage());
        } finally {
            f.deleteOnExit();
        }
    }


}
