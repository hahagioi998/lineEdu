package com.project.databank.domain;

import com.project.mysql.domain.Entitys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Description:　用户评论
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/8 16:41
 */
@Data
@Entity
@Table(name = "user_comment", indexes = {@Index(columnList = "comment_id", name = "comment_id_index"), @Index(columnList = "article_id", name = "article_id_index")})
@EqualsAndHashCode(callSuper = true)
@org.hibernate.annotations.Table(appliesTo = "user_comment", comment = "用户评论")
@AllArgsConstructor
@NoArgsConstructor
public class UserComment extends Entitys implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "comment_id", columnDefinition = "varchar(32) COMMENT '评论id'")
    private String commentId;

    @Column(name = "article_id", columnDefinition = "varchar(32) COMMENT '文章编号'")
    private String articleId;

    @Column(name = "commit_content", columnDefinition = "varchar(255) COMMENT '评论内容'")
    private String commitContent;

    @Column(name = "commit_user", columnDefinition = "varchar(32) COMMENT '评论者'")
    private String commitUser;
}