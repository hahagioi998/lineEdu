package com.project.course.domain.record;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-8-5 10:25
 * @version: 1.0
 * @description:
 */
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@org.hibernate.annotations.Table(appliesTo = "course_records", comment = "学生上课课程记录")
@Table(name = "course_records", indexes = {
        @Index(columnList = "record_id", name = "record_id_index"),
        @Index(columnList = "student_id", name = "student_id_index"),
        @Index(columnList = "course_id", name = "course_id_index")
})
@EqualsAndHashCode(callSuper = true)
public class CourseRecords extends AbstractRecord implements Serializable {

}