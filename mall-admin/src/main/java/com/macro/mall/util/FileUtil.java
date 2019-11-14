package com.macro.mall.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件相关工具类
 */
public class FileUtil {

    /**
     * excel文件后缀
     */
    public static final String EXCEL_SUFFIX = ".xlsx";


    /**
     * 根据map列表生成excel
     *
     * @param maps
     */
    public static void writeExcelByMaps(HttpServletResponse response, List<HashMap<String, Object>> maps) throws IOException {
        Workbook wb = new XSSFWorkbook();
        String sheetName = "导入失败";
        Sheet sheet = wb.createSheet(sheetName);
        AtomicInteger rowNum = new AtomicInteger();
        if (maps.size() > 0) {
            HashMap<String, Object> stringObjectHashMap = maps.get(0);
            HashMap<Integer, Object> columnMap = new HashMap<>();
            AtomicInteger atomicInteger = new AtomicInteger();
            for (Map.Entry<String, Object> entry : stringObjectHashMap.entrySet()) {
                int i = atomicInteger.getAndIncrement();
                columnMap.put(i, entry.getKey());
            }
            //写第一行
            Row row = sheet.createRow(rowNum.getAndIncrement());
            columnMap.entrySet().forEach(integerObjectEntry -> {
                Integer columnNum = integerObjectEntry.getKey();
                Cell cell = row.createCell(columnNum);
                String value = String.valueOf(integerObjectEntry.getValue());
                cell.setCellValue(value);
            });
            //写数据行
            maps.forEach(map -> {
                Row sheetRow = sheet.createRow(rowNum.getAndIncrement());
                columnMap.entrySet().forEach(integerObjectEntry -> {
                    Integer columnNum = integerObjectEntry.getKey();
                    String value = String.valueOf(integerObjectEntry.getValue());
                    Cell cell = sheetRow.createCell(columnNum);
                    cell.setCellValue(String.valueOf(map.get(value)));
                });

            });
        }

        //冻结窗格
        wb.getSheet(sheetName).createFreezePane(0, 1, 0, 1);
        //浏览器下载excel
        buildExcelDocument(wb, response);
        //生成excel文件
        buildExcelFile("test" + EXCEL_SUFFIX, wb);

    }

    /**
     * 浏览器下载excel
     *
     * @param wb
     * @param response
     */
    private static void buildExcelDocument(Workbook wb, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        //response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        response.flushBuffer();
        wb.write(response.getOutputStream());
    }

    /**
     * 生成excel文件
     *
     * @param path 生成excel路径
     * @param wb
     */
    private static void buildExcelFile(String path, Workbook wb) {

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            wb.write(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
