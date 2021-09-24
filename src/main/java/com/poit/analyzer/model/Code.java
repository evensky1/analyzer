package com.poit.analyzer.model;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Code {
    //Тут можно организовать логику самого парсера
    private String code;
    private HashMap<String, Integer> resultTable;
    public Code(){}
    public Code(String code){
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public HashMap<String, Integer> codeAnalyzing(){
        Pattern halstedMetrics = Pattern.compile("[a-zA-Z]+"); //тут могла быть нормальная регулярочка
        Matcher halstedOperators = halstedMetrics.matcher(code);
        resultTable = new HashMap<>();
        while(halstedOperators.find()){
            if(resultTable.get(halstedOperators.group()) == null) {
                resultTable.put(halstedOperators.group(), 1);
            } else {
                resultTable.put(halstedOperators.group(), resultTable.get(halstedOperators.group()) + 1);
            }
        }
        return resultTable;
    }
}
