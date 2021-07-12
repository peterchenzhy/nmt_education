package com.nmt.education.service.authorization;


import com.nmt.education.service.authorization.campus.CampusAuthorizationService;
import com.nmt.education.service.authorization.grade.GradeAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private CampusAuthorizationService campusAuthorizationService;

    @Autowired
    private GradeAuthorizationService gradeAuthorizationService;

    public AuthorizationDto getAuthorization(int userId) {
        AuthorizationDto authorizationDto = new AuthorizationDto();
        authorizationDto.getCampusList().addAll(campusAuthorizationService.getCampusAuthorization(userId));
        authorizationDto.getGradeList().addAll(gradeAuthorizationService.getGradeAuthorization(userId));
        return authorizationDto;
    }


    public AuthorizationDto getAuthorization(AuthorizationCheckDto dto) {
        AuthorizationDto authorizationDto = new AuthorizationDto();
        authorizationDto.getCampusList().addAll(campusAuthorizationService.getCampusAuthorization(dto.getUserId(), dto.getCampus()));
        authorizationDto.getGradeList().addAll(gradeAuthorizationService.getGradeAuthorization(dto.getUserId(), dto.getGrade()));
        return authorizationDto;
    }


}
