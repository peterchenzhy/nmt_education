package com.nmt.education.service.authorization;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AuthorizationDto {

    List<Integer> campusList = new ArrayList<>() ;
    List<Integer> gradeList  = new ArrayList<>();
}
