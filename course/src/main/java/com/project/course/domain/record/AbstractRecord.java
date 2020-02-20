package com.project.course.domain.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.course.domain.pk.CourseRecordPk;
import com.project.mysql.domain.Entitys;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-8-5 10:40
 * @version: 1.0
 * @description:
 */
@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@IdClass(CourseRecordPk.class)
abstract class AbstractRecord extends Entitys {

    @EmbeddedId
    @JsonIgnore
    private CourseRecordPk courseRecordPk;

    private String studentId;

    private String courseId;

    private String chapterId;

    @Column(name = "course_name", columnDefinition = "VARCHAR(32) COMMENT '课程名称'")
    private String courseName;

    @Column(name = "sum_time", columnDefinition = "BIGINT(20) DEFAULT 0 COMMENT '上课总时间(秒)'", nullable = false)
    private Integer sumTime = this.sumTime == null ? 0 : this.sumTime;

    @Column(name = "grade", columnDefinition = "VARCHAR(32) COMMENT '评分'")
    private String grade;
}