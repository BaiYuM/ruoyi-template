package com.ruoyi.system.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExposureParseService
{
    public static class ParseResult {
        public List<String> parsed = new ArrayList<>();
        public List<String> errors = new ArrayList<>();
    }

    public ParseResult parseFile(MultipartFile file) throws Exception {
        ParseResult result = new ParseResult();
        String filename = file.getOriginalFilename();
        try (InputStream in = file.getInputStream()) {
            if (filename != null && filename.toLowerCase().endsWith(".csv")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                    String line;
                    int lineNo = 0;
                    while ((line = reader.readLine()) != null) {
                        lineNo++;
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        String[] parts = line.split(",");
                        String value = parts.length > 0 ? parts[0].trim() : line;
                        if (value.isEmpty()) result.errors.add("第" + lineNo + "行为空");
                        else result.parsed.add(value);
                    }
                }
            } else {
                try (Workbook workbook = WorkbookFactory.create(in)) {
                    Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
                    if (sheet != null) {
                        int rowNo = 0;
                        DataFormatter formatter = new DataFormatter();
                        for (Row row : sheet) {
                            rowNo++;
                            Cell cell = row.getCell(0);
                            if (cell == null) continue;
                            String v = formatter.formatCellValue(cell).trim();
                            if (v == null || v.isEmpty()) result.errors.add("第" + rowNo + "行为空");
                            else result.parsed.add(v);
                        }
                    }
                }
            }
        }
        return result;
    }
}
