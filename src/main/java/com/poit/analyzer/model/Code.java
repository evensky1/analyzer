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
    private HashMap<String, Integer> resultTable;

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
        ArrayList<String> regArr= new ArrayList<>();
        File regSrc = new File("regularExpressions.txt");
        try {
            Scanner read = new Scanner(regSrc);
            while (read.hasNext()){
                regArr.add(read.nextLine());
            }
            return regArr;
        } catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
            regArr.add("");
            return regArr;
        }
    }

    public HashMap<String, Integer> codeAnalyzing() {
        //вроде работает правильно, но беда с регулярками
        ArrayList<String> regulars = createArray();
        resultTable = new HashMap<>();
        for (String regExp : regulars) {
            Pattern halstedMetrics = Pattern.compile(regExp);
            Matcher halstedOperators = halstedMetrics.matcher(code);
            while (halstedOperators.find()) {
                if(resultTable.get(halstedOperators.group()) == null) {
                    resultTable.put(halstedOperators.group(), 1);
                } else {
                    resultTable.put(halstedOperators.group(), resultTable.get(halstedOperators.group()) + 1);
                }
            }
        }
        return resultTable;
    }
    /*
    private HashMap<String, Integer> createMap() {
        resultTable = new HashMap<>();
        File regSrc = new File("regularExpressions.txt");
        try {
            Scanner read = new Scanner(regSrc);
            while (read.hasNext()){
                resultTable.put(read.nextLine(), 0);
            }
            return resultTable;
        } catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
            resultTable.put("0", 0);
            return resultTable;
        }
    }
    public HashMap<String, Integer> codeAnalyzing() {
        resultTable = createMap();
        for (String regExp : resultTable.keySet()) {
            Pattern halstedMetrics = Pattern.compile(regExp);
            Matcher halstedOperators = halstedMetrics.matcher(code);
            while (halstedOperators.find()) {
                if(resultTable.get(halstedOperators.group()) != null) {
                    resultTable.put(halstedOperators.group(), resultTable.get(halstedOperators.group()) + 1);
                }
            }
        }
        return resultTable;
    }
     */
}
