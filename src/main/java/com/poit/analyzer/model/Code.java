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
        ArrayList<String> regArr= new ArrayList<>();
        File regSrc = new File("regularExpressions.txt");
        try {
            Scanner read = new Scanner(regSrc);
            while (read.hasNext()){
                regArr.add(read.nextLine().trim());
            }
            return regArr;
        } catch(FileNotFoundException e) {
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
            if(!regExp.isEmpty()) {
                pattern = Pattern.compile(regExp);
                matcher = pattern.matcher(codeTemp);
                while (matcher.find()) {
                    // удаляем найденное, чтобы, например, "+=" не реагировало на "+" и "="
                    // для этой же цели в файле с регулярками всё в порядке от длинного к короткому
                    codeTemp = codeTemp.replaceFirst(regExp, "");
                    match = matcher.group().trim().replaceAll("[()]", "");
                    if (match.equals(":")) {
                        match = "? :";
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
    public HashMap<String, Integer> codeOperandAnalyzing(){
        resultOperandsTable = new HashMap<>();
        String codeTemp = deleteCommentsAndStringsFromCode(code);

        Pattern pattern = Pattern.compile("\\b[_a-zA-Z][_\\da-zA-Z]*\\b(?=\\s*=)");
        Matcher matcher = pattern.matcher(codeTemp);

        while (matcher.find()){
            if(resultOperandsTable.get(matcher.group()) == null){
                resultOperandsTable.put(matcher.group(), 1);
            } else {
                resultOperandsTable.put(matcher.group(), resultOperandsTable.get(matcher.group()) + 1);
            }
        }
        return resultOperandsTable;
    }
    public String deleteCommentsAndStringsFromCode(String code){
        // Очевидно, что то, что в строках и комментариях, считать не стоит
        String codeTemp = code.replaceAll("\".*?[^\\\\](\\\\\\\\)*\"", "");
        codeTemp = codeTemp.replaceAll("(=begin\\s(.*\\r?\\n)*?=end\\s)|(#.*)", "");
        //System.out.println("******************************** NEW CODE *******************************\n" + codeTemp);
        return codeTemp;
    }
}
