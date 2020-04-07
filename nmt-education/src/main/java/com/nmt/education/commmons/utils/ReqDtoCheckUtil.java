package com.nmt.education.commmons.utils;

import com.nmt.education.commmons.Consts;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ReqDtoCheckUtil {
    public static void reqDtoBaseCheck(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder sb = new StringBuilder();
            errors.stream().forEach(e -> {
                sb.append(e.getDefaultMessage()).append(Consts.分号);
            });
            throw new RuntimeException(sb.toString());
        }
    }
}
