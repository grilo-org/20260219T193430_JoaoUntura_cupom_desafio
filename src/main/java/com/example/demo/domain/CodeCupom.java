package com.example.demo.domain;

import com.example.demo.domain.exceptions.InvalidCodeException;

public class CodeCupom {

    private String code;

    public CodeCupom(String rawCode){
        this.code = this.normalizeCode(rawCode);
    }

    private String normalizeCode(String rawCode){

        if(rawCode == null){
            throw new InvalidCodeException("Code não pode ser vazio");
        }

        String cleaned = this.filterToAlphanumeric(rawCode);

        this.validateLenght(cleaned);

        return cleaned;

    }

    private String filterToAlphanumeric(String rawCode){
        return rawCode.replaceAll("[^a-zA-Z0-9]", "");
    }

    private void validateLenght(String code) {
        if (code.length() != 6) {
            throw new InvalidCodeException(
                    "Code deve ter exatemente 6 caracteres alphanumericos"
            );
        }
    }
    public String getValue() {
        return code;
    }

}
