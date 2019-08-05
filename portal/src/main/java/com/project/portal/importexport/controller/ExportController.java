package com.project.portal.importexport.controller;

import com.project.portal.response.WebResult;
import com.project.portal.util.MyExcleUtil;
import com.project.schoolroll.domain.excel.StudentImport;
import com.project.schoolroll.service.ExportService;
import com.project.token.annotation.UserLoginToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/7/9 08:25
 * @Version: 1.0
 * @Description:
 */
@Slf4j
@RestController
@Api(value = "导出接口", tags = {"导出相关数据接口"})
@RequestMapping(path = "/export", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ExportController {

    private final ExportService exportService;

    @Autowired
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @UserLoginToken
    @ApiOperation(value = "导入学生需要信息模版")
    @GetMapping(path = "/exportStudentTemplate")
    public WebResult leadingOutStudentTemplate(HttpServletResponse res, HttpServletRequest req) throws IOException {
        MyExcleUtil.getExcel(res, req, exportService.exportStudentTemplate(),"导入学生数据模版.xlsx");
        return WebResult.okResult();
    }

    @ApiOperation(value = "导出学生信息")
    @GetMapping(path = "/exportStudents")
    public WebResult exportStudents(HttpServletResponse res, HttpServletRequest req) throws IOException {
        List<StudentImport> list = new ArrayList<>();
        MyExcleUtil.getExcel(res, req, list,"导出学生数据.xlsx");
        return WebResult.okResult();
    }
}