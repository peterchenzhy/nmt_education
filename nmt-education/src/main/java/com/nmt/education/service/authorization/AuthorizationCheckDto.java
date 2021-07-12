package com.nmt.education.service.authorization;

import lombok.Data;

@Data
public class AuthorizationCheckDto {
    private int userId;
    private Integer campus;
    private Integer grade;
}
