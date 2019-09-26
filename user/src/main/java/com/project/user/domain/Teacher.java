package com.project.user.domain;

import com.project.mysql.domain.Entitys;
import com.project.user.domain.base.BaseTeacher;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-9-9 10:30
 * @version: 1.0
 * @description: 在线教师信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "teacher", comment = "教师信息")
@Table(name = "teacher", indexes = {
        @Index(columnList = "teacher_id", name = "teacher_id_index"),
        @Index(columnList = "teacher_code", name = "teacher_code_index"),
        @Index(columnList = "phone", name = "phone_index")
})
public class Teacher extends BaseTeacher implements Serializable {

}