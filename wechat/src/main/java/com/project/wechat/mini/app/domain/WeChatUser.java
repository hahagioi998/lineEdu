package com.project.wechat.mini.app.domain;

import cn.hutool.core.util.StrUtil;
import com.project.mysql.domain.Entitys;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

import static com.project.base.common.keyword.Dic.WX_INFO_BINDIND_1;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 19-1-10 16:44
 * @Version: 1.0
 * @Description: 微信登录用户信息
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "wx_user", indexes = {
        @Index(columnList = "wx_id", name = "wx_id_index"),
        @Index(columnList = "open_id", name = "open_id_index", unique = true),
        @Index(columnList = "student_id", name = "student_id_index")
})
@EqualsAndHashCode(callSuper = true)
@org.hibernate.annotations.Table(appliesTo = "wx_user", comment = "微信用户信息")
public class WeChatUser extends Entitys implements Serializable {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "wx_id", columnDefinition = "VARCHAR(32) COMMENT '微信信息id'", unique = true)
    private String wxId;

    @Column(name = "open_id", columnDefinition = "VARCHAR(32) COMMENT '微信openId'", unique = true)
    private String openId;

    @Column(name = "nick_name", columnDefinition = "VARCHAR(32) COMMENT '用户微信昵称'")
    private String nickName;

    @Column(name = "gender", columnDefinition = "CHAR(1) COMMENT '用户性别 0 未知 1男性　2 女性'")
    private String gender;

    @Column(name = "language", columnDefinition = "VARCHAR(32) COMMENT '显示所用的语言 en 英文 zh_CN 简体中文 zh_TW 繁体中文'")
    private String language;

    @Column(name = "city", columnDefinition = "VARCHAR(60) COMMENT '用户所在城市'")
    private String city;

    @Column(name = "province", columnDefinition = "VARCHAR(60) COMMENT '用户所在的省份'")
    private String province;

    @Column(name = "country", columnDefinition = "VARCHAR(32) COMMENT '用户所在的国家'")
    private String country;

    @Column(name = "avatar_url", columnDefinition = "VARCHAR(256) COMMENT '用户的头像URL'")
    private String avatarUrl;

    @Column(name = "student_id", columnDefinition = "VARCHAR(32) COMMENT '学生id'")
    private String studentId;

    @Column(name = "binding", nullable = false, columnDefinition = "CHAR(1) DEFAULT 1 COMMENT '绑定标识 0 绑定 1　未绑定'")
    private String binding = StrUtil.isBlank(this.binding) ? WX_INFO_BINDIND_1 : this.binding ;

    @Column(name = "class_id", columnDefinition = "VARCHAR(32) COMMENT '班级id'")
    private String classId;
    /**
     * 添加角色记录，teacher 0 student 1
     */
    @Column(name = "role_id", nullable = false, columnDefinition = "VARCHAR(32) DEFAULT 1 COMMENT '角色 0 教师 1 学生'")
    private String roleId;
}
