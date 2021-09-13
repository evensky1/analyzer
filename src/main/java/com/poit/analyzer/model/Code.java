package com.poit.analyzer.model;

import java.util.HashMap;

public class Code {
    //Тут можно организовать логику самого парсера
    private String code;
    //Была идея использовать хэшмап, но из-за непостояноого кол-ва операндов, пока забил
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
}
