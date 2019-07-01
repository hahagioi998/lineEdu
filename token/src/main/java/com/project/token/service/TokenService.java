package com.project.token.service;

import com.auth0.jwt.JWTVerifier;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 2019/2/20 21:59
 * @Version: 1.0
 * @Description:
 */
public interface TokenService {
    /**
     * 用微信openId生成一个一天有效期的token
     * @param userId
     * @return
     */
    String createToken(String userId);

    /**
     * 获取JWT验证
     * @param openId
     * @return
     */
    JWTVerifier verifier(String openId);

    /**
     * 根据用户请求token 信息获取请求的用户信息
     * @param request
     * @return
     */
    String getUserId(String token);

    /**
     * 查询对应学生id信息
     * @param request
     * @return
     */
    String getStudentId(String token);

    /**
     * 查找老师id
     * @param request
     * @return
     */
    String getTeacherId(String token);

    /**
     * 通过token 获取学生班级id信息
     * @param request
     * @return
     */
    String getClassId(String token);

    /**
     * 保存token 到redis
     * @param token
     * @param users
     */
//    void saveRedis(String token, SysUsers users);

    /**
     * 移除 redis 保存的 token 数据信息
     * @param userId
     */
    void removeToken(String userId);
}