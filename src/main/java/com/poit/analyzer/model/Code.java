package com.poit.analyzer.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Code {
    //Тут можно организовать логику самого парсера
    private String code;
    private HashMap<String, Integer> resultOperatorsTable,
            resultOperandsTable;

    public Code() {
    }

    public Code(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private ArrayList<String> createArray() {
        ArrayList<String> regArr = new ArrayList<>();
        File regSrc = new File("regularExpressions.txt");
        try {
            Scanner read = new Scanner(regSrc);
            while (read.hasNext()) {
                regArr.add(read.nextLine().trim());
            }
            return regArr;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            regArr.add("");
            return regArr;
        }
    }

    public HashMap<String, Integer> codeOperatorsAnalyzing() {
        String codeTemp = deleteCommentsAndStringsFromCode(code);
        ArrayList<String> regulars = createArray();
        resultOperatorsTable = new HashMap<>();

        Pattern pattern;
        Matcher matcher;
        String match;
        for (String regExp : regulars) {
            if (!regExp.isEmpty()) {
                pattern = Pattern.compile(regExp);
                matcher = pattern.matcher(codeTemp);
                while (matcher.find()) {
                    // удаляем найденное, чтобы, например, "+=" не реагировало на "+" и "="
                    // для этой же цели в файле с регулярками всё в порядке от длинного к короткому
                    codeTemp = codeTemp.replaceFirst(regExp, "");
                    match = matcher.group();
                    if (match.equals(":")) {
                        match = "? :";
                    } else if (match.equals("when")) {
                        match = "case when";
                    }
                    if (resultOperatorsTable.get(match) == null) {
                        resultOperatorsTable.put(match, 1);
                    } else {
                        resultOperatorsTable.put(match, resultOperatorsTable.get(match) + 1);
                    }
                }
            }
        }
        return resultOperatorsTable;
    }

    public HashMap<String, Integer> codeOperandAnalyzing() {
        resultOperandsTable = new HashMap<>();
        String codeTemp = deleteCommentsAndStringsFromCode(code);
        //поиск инициализаций переменных и внесение их(переменных) в мапу
        Pattern pattern = Pattern.compile("\\b[_a-zA-Z][_\\da-zA-Z]*\\b(?=\\s*=)");
        Matcher matcher = pattern.matcher(codeTemp);
        while (matcher.find()) {
                resultOperandsTable.putIfAbsent(matcher.group(), 0);
        }
        //подсчёт вхождений каждой переменной
        for (String varOperand : resultOperandsTable.keySet()) {
            int count = 0; //так не хотелось много раз вызывать put и get
            pattern = Pattern.compile("\\b" + varOperand + "\\b");
            matcher = pattern.matcher(codeTemp);
            while (matcher.find()) {
                count++;
            }
            resultOperandsTable.put(varOperand, count);
        }
        //подсчёт числовых литералов
        pattern = Pattern.compile("(?<=\\W)\\d+");
        matcher = pattern.matcher(codeTemp);
        while (matcher.find()) {
            if (resultOperandsTable.get(matcher.group()) == null) {
                resultOperandsTable.put(matcher.group(), 1);
            } else {
                resultOperandsTable.put(matcher.group(), resultOperandsTable.get(matcher.group()) + 1);
            }
        }
        return resultOperandsTable;
    }

    public String deleteCommentsAndStringsFromCode(String code) {
        // Очевидно, что то, что в строках и комментариях, считать не стоит
        String codeTemp = code.replaceAll("\".*?[^\\\\](\\\\\\\\)*\"", "");
        codeTemp = codeTemp.replaceAll("(=begin\\s(.*\\r?\\n)*?=end\\s)|(#.*)", "");
        return codeTemp;
    }
}
