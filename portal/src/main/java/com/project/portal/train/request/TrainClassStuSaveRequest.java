package com.project.portal.train.request;

import com.project.portal.request.BaseReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-7-8 09:58
 * @version: 1.0
 * @description:  培训班级学员
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "培训班级学员添加修改")
public class TrainClassStuSaveRequest extends BaseReq {

    @ApiModelProperty(name = "trainStuId", value = "培训班级学生编号")
    private String trainStuId;

    @ApiModelProperty(name = "trainClassId", value = "培训项目班级编号")
    private String trainClassId;

    @ApiModelProperty(name = "trainClassName", value = "培训班级名称")
    private String trainClassName;

//    @ApiModelProperty(name = "trainProjectId", value = "系统用户编号")
//    private String userId;

    @ApiModelProperty(name = "gender", value = "性别")
    private String gender;

    @ApiModelProperty(name = "stuName", value = "姓名")
    private String stuName;

    @ApiModelProperty(name = "marriage", value = "民族")
    private String marriage;

    @ApiModelProperty(name = "jobTitle", value = "单位职务")
    private String jobTitle;

    @ApiModelProperty(name = "stuIdCard", value = "身份证号")
    private String stuIdCard;

    @ApiModelProperty(name = "stuPhone", value = "联系方式")
    private String stuPhone;
}