package com.project.train.domain;

import com.project.mysql.domain.Entitys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 培训计划资料填写情况
 */
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@org.hibernate.annotations.Table(appliesTo = "traint_plan_finish", comment = "培训计划资料填写情况")
@Table(name = "traint_plan_finish")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class TrainPlanFinish extends Entitys implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pj_plan_id", columnDefinition = "tinyint COMMENT '培训项目计划编号'")
    private int pjPlanId;

    @Column(name = "is_course", columnDefinition = "tinyint COMMENT '课程是否添加'")
    private int isCourse;

    @Column(name = "is_class", columnDefinition = "tinyint COMMENT '班级是否添加'")
    private int isClass;

    @Column(name = "is_student", columnDefinition = "tinyint COMMENT '班级成员是否添加'")
    private int isStudent;

    @Column(name = "is_file", columnDefinition = "tinyint COMMENT '签名表是否添加'")
    private int isFile;

    @Column(name = "is_all", columnDefinition = "tinyint COMMENT '是否全部添加'")
    private int isAll;


}
