package com.poit.analyzer.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Code {
    private String code;
    private HashMap<String, Integer> resultOperatorsTable,
            resultOperandsTable;

    public Code() {
    }

    public int getOperandsCount() {
        int operandsCount = 0;
        if (resultOperandsTable != null) {
            for (String varOperand : resultOperandsTable.keySet()) {
                operandsCount += 1;
            }
        }
        return operandsCount;
    }

    public int getOperandsSum() {
        int operandsSum = 0;
        if (resultOperandsTable != null) {
            for (int currentCount : resultOperandsTable.values()) {
                operandsSum += currentCount;
            }
        }
        return operandsSum;
    }

    public int getOperatorsSum() {
        int operatorsSum = 0;
        if (resultOperatorsTable != null) {
            for (int currentCount : resultOperatorsTable.values()) {
                operatorsSum += currentCount;
            }
        }
        return operatorsSum;
    }

    public int getOperatorsCount() {
        int operatorsCount = 0;
        if (resultOperatorsTable != null) {
            for (String varOperator : resultOperatorsTable.keySet()) {
                operatorsCount += 1;
            }
        }
        return operatorsCount;
    }

    public double getVol() {
        return (getOperandsSum() + getOperatorsSum()) *
                (Math.log(getOperandsCount() + getOperatorsCount()) / Math.log(2));
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

    public String createOutString() {
        int operandsSum = getOperatorsCount() + getOperandsCount();
        int operatorsSum = getOperatorsSum() + getOperandsSum();
        return "Словарь программы h = " + getOperatorsCount() + " + " + getOperandsCount() + " = " + operandsSum +
                "<br>Длина программы N = " + getOperatorsSum() + " + " + getOperandsSum() + " = " + operatorsSum +
                "<br>Объём программы V = " + getVol();
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

    private void findFunctions(String codeTemp) {
        Pattern pattern = Pattern.compile("(?<=\\bdef\\s)[a-zA-Z_]\\w*");
        Matcher matcher = pattern.matcher(codeTemp);

        while (matcher.find()) {
            resultOperatorsTable.putIfAbsent(matcher.group(), 0);
        }
        //подсчёт вхождений каждой переменной
        for (String varOperand : resultOperatorsTable.keySet()) {
            int count = 0; //так не хотелось много раз вызывать put и get
            pattern = Pattern.compile("\\b" + varOperand + "\\b");
            matcher = pattern.matcher(codeTemp);
            while (matcher.find()) {
                count++;
            }
            resultOperatorsTable.put(varOperand, count);
        }
    }

    public HashMap<String, Integer> codeOperatorsAnalyzing() {
        String codeTemp = deleteCommentsAndStringsFromCode(code);
        ArrayList<String> regulars = createArray();
        resultOperatorsTable = new HashMap<>();

        findFunctions(codeTemp);

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
        String codeTemp = code.replaceAll("(=begin\\s(.*\\r?\\n)*?=end\\s)|(#.*)", "");
        Pattern pattern = Pattern.compile("(\".*?[^\\\\](\\\\\\\\)*\")|('.*?[^\\\\](\\\\\\\\)*')");
        Matcher matcher = pattern.matcher(codeTemp);
        while(matcher.find()){
            if (resultOperandsTable.get(matcher.group()) == null) {
                resultOperandsTable.put(matcher.group(), 1);
            } else {
                resultOperandsTable.put(matcher.group(), resultOperandsTable.get(matcher.group()) + 1);
            }
        }
        codeTemp = codeTemp.replaceAll("\".*?[^\\\\](\\\\\\\\)*\"", "");
        //поиск инициализаций переменных и внесение их(переменных) в мапу
        String[] patterns = {"\\b[_a-zA-Z][_\\da-zA-Z]*\\b(?=\\s*=)", "(?<!\\.)(?<=\\W)\\d+",
                                "(?<=\\W)\\d+(?=(\\.\\d+))", "true|false"};
        for(String regExp: patterns){
            pattern = Pattern.compile(regExp);
            matcher = pattern.matcher(codeTemp);
            while(matcher.find()){
                if (resultOperandsTable.get(matcher.group()) == null) {
                        resultOperandsTable.put(matcher.group(), 1);
                } else {
                    resultOperandsTable.put(matcher.group(), resultOperandsTable.get(matcher.group()) + 1);
                }
            }
        }
        return resultOperandsTable;
    }
    public String deleteCommentsAndStringsFromCode(String code) {
        String codeTemp = code.replaceAll("\".*?[^\\\\](\\\\\\\\)*\"", "");
        codeTemp = codeTemp.replaceAll("(=begin\\s(.*\\r?\\n)*?=end\\s)|(#.*)", "");
        return codeTemp;
    }
}
