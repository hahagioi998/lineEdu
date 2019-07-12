package com.project.portal.schoolroll.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.project.base.common.keyword.DefineCode;
import com.project.base.exception.MyAssert;
import com.project.base.util.UpdateUtil;
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
import org.springframework.web.bind.annotation.*;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-7-4 09:36
 * @version: 1.0
 * @description: 学生成员信息
 */
@RestController
@Api(value = "学生家庭成员信息管理", tags = {"学生家庭成员管理"})
@RequestMapping(path = "/family", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FamilyController {


    private final FamilyService familyService;
    private final FamilyRepository familyRepository;
    private FamilyController(FamilyService familyService, FamilyRepository familyRepository){
        this.familyService = familyService;
        this.familyRepository = familyRepository;
    }

    @PostMapping("/saveOrUpdate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "familyId", value = "家庭成员id", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "stuId", value = "学生id", dataType = "string", paramType = "form", example = "添加不能为空"),
            @ApiImplicitParam(name = "name", value = "姓名", dataType = "string", paramType = "form", example = "添加不能为空"),
            @ApiImplicitParam(name = "familyRelationship", value = "家庭关系", dataType = "string", paramType = "form", example = "添加不能为空"),
            @ApiImplicitParam(name = "phone", value = "电话", dataType = "string", paramType = "form", example = "添加不能为空"),
            @ApiImplicitParam(name = "isGuardian", value = "是都是监护人", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "cardType", value = "身份证件类型", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "IDCard", value = "身份证号码", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "birthDate", value = "出生日期", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "healthCondition", value = "健康状态", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "companyOrganization", value = "工作单位", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "politicalStatus", value = "政治面貌", dataType = "string", paramType = "form"),
    })
    public WebResult saveOrUpdate(@RequestBody FamilySaveUpdateRequest request){
        if (StrUtil.isNotBlank(request.getFamilyId())){
            //是修改
            familyRepository.findById(request.getFamilyId()).ifPresent(family -> {
                UpdateUtil.copyNullProperties(request, family);
                familyRepository.save(family);
            });
        }else {
            //添加
            MyAssert.isNull(request.getStuId(), DefineCode.ERR0010, "学生id不为空");
            MyAssert.isNull(request.getFamilyRelationship(), DefineCode.ERR0010, "家庭成员关系不为空");
            MyAssert.isNull(request.getName(), DefineCode.ERR0010, "家庭成员名称不为空");
            MyAssert.isNull(request.getPhone(), DefineCode.ERR0010, "电话号码不为空");
            Family family = new Family();
            UpdateUtil.copyNullProperties(request, family);
            family.setFamilyId(IdUtil.fastSimpleUUID());
            familyRepository.save(family);
        }
        return WebResult.okResult();
    }

    @ApiOperation(value = "查询基本家庭成员信息")
    @ApiImplicitParam(name = "stuId", value = "学生id", dataType = "string", required = true, paramType = "query")
    @GetMapping("/findFamilyDtoList")
    public WebResult findFamilyDtoList(@RequestBody String stuId){
        MyAssert.isNull(stuId, DefineCode.ERR0010, "学生id不为空");
        return WebResult.okResult(familyService.findFamilyDtoList(JSONObject.parseObject(stuId).getString("stuId")));
    }

    @ApiOperation(value = "查询全部有效家庭成员信息")
    @ApiImplicitParam(name = "stuId", value = "学生id", dataType = "string", required = true, paramType = "query")
    @GetMapping("/findFamilies")
    public WebResult findFamilies(@RequestBody String stuId){
        MyAssert.isNull(stuId, DefineCode.ERR0010, "学生id不为空");
        return WebResult.okResult(familyService.findFamilies(JSONObject.parseObject(stuId).getString("stuId")));
    }

    @ApiOperation(value = "移除家庭成员信息")
    @ApiImplicitParam(name = "familyId", value = "家庭成员id", dataType = "string", required = true, paramType = "form")
    @PostMapping("/removeFamilyById")
    public WebResult removeFamilyById(@RequestBody String familyId){
        MyAssert.isNull(familyId, DefineCode.ERR0010, "家庭成员id不为空");
        familyService.removeFamilyById(JSONObject.parseObject(familyId).getString("familyId"));
        return WebResult.okResult();
    }
}