package com.tilchina.auth.manager;

import java.io.File;
import java.util.Map;

public interface RailtransOrderManager {

    String importText(File file) throws Exception;

    void importExcel(File file);

    File exportExcel();

    void getLatestCabinStatus();

    void getSpecifiedCabinStatus(Map<String, String> params);

    String getHtmlFromCrscsc(String cabinNumber);
}
