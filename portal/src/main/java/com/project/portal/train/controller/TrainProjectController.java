package com.project.portal.train.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.project.base.common.keyword.DefineCode;
import com.project.base.exception.MyAssert;
import com.project.portal.response.WebResult;
import com.project.portal.schoolroll.request.FamilySaveUpdateRequest;
import com.project.schoolroll.domain.Family;
import com.project.schoolroll.repository.FamilyRepository;
import com.project.schoolroll.service.FamilyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-7-4 09:36
 * @version: 1.0
 * @description: 培训项目列表管理
 */
@RestController
@Api(value = "培训项目列表管理", tags = {"培训项目列表管理"})
@RequestMapping(path = "/train/project", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TrainProjectController {


    private final FamilyService familyService;
    private final FamilyRepository familyRepository;
    private TrainProjectController(FamilyService familyService, FamilyRepository familyRepository){
        this.familyService = familyService;
        this.familyRepository = familyRepository;
    }

    @PostMapping("/saveOrUpdate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "trainProjectId", value = "项目id", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "trainProjectName", value = "项目名称", dataType = "string", paramType = "form", example = "添加不能为空"),
            @ApiImplicitParam(name = "trainAreaId", value = "项目领域", dataType = "string", paramType = "form", example = "添加不能为空"),
            @ApiImplicitParam(name = "centerId", value = "培训中心", dataType = "string", paramType = "form", example = "添加不能为空")
    })
    public WebResult saveOrUpdate(@RequestBody FamilySaveUpdateRequest request){
        return WebResult.okResult();
    }

}