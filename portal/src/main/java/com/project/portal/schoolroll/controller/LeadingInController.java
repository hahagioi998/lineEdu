package com.project.portal.schoolroll.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.project.base.common.keyword.DefineCode;
import com.project.base.exception.MyAssert;
import com.project.portal.response.WebResult;
import com.project.schoolroll.service.LeadingInService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/7/9 08:19
 * @Version: 1.0
 * @Description: 导入相关数据 leading-in
 */
@RestController
@Slf4j
@Api(value = "导入接口", tags = {"导入相关数据接口"})
@RequestMapping(path = "/leadingIn", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LeadingInController {
    private final LeadingInService leadingInService;

    @Autowired
    public LeadingInController(LeadingInService leadingInService) {
        this.leadingInService = leadingInService;
    }

    @PostMapping(path = "/students")
    public WebResult leadingInStudents(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()){
            MyAssert.isNull(null, DefineCode.ERR0010, "导入的文件不存在,请重新选择");
        }
        try {

            String name = file.getOriginalFilename();
//            Resource resource = file.getResource();

//            Long size = file.getSize();

            String type = FileUtil.extName(file.getOriginalFilename());

//            String contentType = file.getContentType();
//            String fileName = file.getOriginalFilename();
//            InputStream inputStream = file.getInputStream();
//            String type = FileTypeUtil.getType(file.getInputStream());
//            file.getName();

            if (StrUtil.isNotBlank(type) && "xlsx".equals(type)){
                leadingInService.studentsSave(file.getInputStream());
                return WebResult.okResult();
            }
        } catch (IOException e) {
            log.error("students in IOException, file : [{}],  message : [{}]", file, e.getMessage());
            e.printStackTrace();
        }
        return WebResult.failException("只支持Excel2007以上版本");
    }
}