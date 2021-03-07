package com.nmt.education.controller;

import com.github.pagehelper.PageInfo;
import com.nmt.education.commmons.utils.ReqDtoCheckUtil;
import com.nmt.education.pojo.dto.req.CourseReqDto;
import com.nmt.education.pojo.dto.req.CourseSearchDto;
import com.nmt.education.pojo.po.CoursePo;
import com.nmt.education.pojo.vo.CourseDetailVo;
import com.nmt.education.pojo.vo.CourseVo;
import com.nmt.education.service.course.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nmt.education.commmons.Consts.LOGIN_USER_HEAD;
import static com.nmt.education.commmons.Consts.ROLE_ID_HEAD;

/**
 * CourseController
 *
 * @author PeterChen
 * @summary StudentController
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @Description StudentController
 * @since 2020-03-22 22:32
 */
@RestController
@Api(tags = "课程")
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation(value = "manager", notes = "课程编辑提交按钮")
    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public Boolean courseManager(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                 @RequestBody @Validated CourseReqDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return courseService.courseManager(logInUser, dto);
    }


    @ApiOperation(value = "search", notes = "课程查询")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public PageInfo<CourseVo> search(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                     @RequestBody @Validated CourseSearchDto dto, BindingResult bindingResult) {
        ReqDtoCheckUtil.reqDtoBaseCheck(bindingResult);
        return courseService.search(logInUser, dto);
    }

    @ApiOperation(value = "/search/fuzzy", notes = "根据课程编号或者课程名称，课程模糊查询")
    @RequestMapping(value = "/search/fuzzy", method = RequestMethod.GET)
    public List<CoursePo> searchFuzzy(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId,
                                      @RequestParam String name) {
        return courseService.searchFuzzy(logInUser, name);
    }

    @ApiOperation(value = "detail", notes = "课程明细")
    @RequestMapping(value = "/detail/{courseId}", method = RequestMethod.POST)
    public CourseDetailVo detail(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long courseId) {
        return courseService.detail(logInUser, courseId);
    }

    @ApiOperation(value = "finish", notes = "结课")
    @RequestMapping(value = "/finish/{courseId}", method = RequestMethod.GET)
    public void finish(@RequestHeader(LOGIN_USER_HEAD) Integer logInUser, @RequestHeader(ROLE_ID_HEAD) String roleId
            , @PathVariable Long courseId) {
        courseService.finish(logInUser, courseId);
    }

    @SneakyThrows
    public static void main(String[] args) {
        String str2 = "123";
        String str = Base64Utils.encodeToString(str2.getBytes());
        System.out.println(str);
         //---------
        String md51 = DigestUtils.md5Hex(DigestUtils.md5Hex(str));
        System.out.println(md51);
        System.out.println(DigestUtils.md5Hex(DigestUtils.md5Hex(md51)));
        System.out.println(md51.length());

//        System.out.println( DigestUtils.md5Hex(str));

//
//        String t = "123";
//        System.out.println(DigestUtils.md5Hex(t));


    }
}
