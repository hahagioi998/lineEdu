package com.project.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.project.base.common.keyword.DefineCode;
import com.project.base.exception.MyAssert;
import com.project.user.domain.Teacher;
import com.project.user.repository.SysUsersRepository;
import com.project.user.repository.TeacherRepository;
import com.project.user.service.TeacherService;
import com.project.user.service.UserService;
import com.project.user.web.vo.RegisterTeacherVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.project.base.common.keyword.Dic.TAKE_EFFECT_CLOSE;
import static com.project.base.common.keyword.Dic.TAKE_EFFECT_OPEN;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-9-9 10:55
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final SysUsersRepository sysUsersRepository;
    private final UserService userService;

    public TeacherServiceImpl(TeacherRepository teacherRepository,
                              UserService userService,
                              SysUsersRepository sysUsersRepository) {
        this.teacherRepository = teacherRepository;
        this.sysUsersRepository = sysUsersRepository;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Teacher save(Teacher teacher) {
        teacher.setTeacherId(IdUtil.simpleUUID());
        // 添加到用户保存用户
        RegisterTeacherVo vo = new RegisterTeacherVo();
        BeanUtil.copyProperties(teacher, vo);
        userService.registerTeacher(vo);
        return teacherRepository.save(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Teacher update(Teacher teacher) {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacher.getTeacherId());
        if (optionalTeacher.isPresent()) {
            Teacher t = optionalTeacher.get();
            BeanUtil.copyProperties(teacher, t);
            return teacherRepository.save(teacher);
        }
        MyAssert.isNull(null, DefineCode.ERR0014, "没有要修改的数据");
        return null;
    }

    @Override
    public Page<Teacher> findAllPage(PageRequest pageRequest) {
        return teacherRepository.findAllByIsValidatedEqualsOrderByCreateTimeDesc(TAKE_EFFECT_OPEN, pageRequest);
    }

    @Override
    public List<Teacher> findAll() {
        return teacherRepository.findAllByIsValidatedEquals(TAKE_EFFECT_OPEN);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByTeacherCode(String teacherCode) {
        teacherRepository.deleteTeacherByTeacherCode(teacherCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByTeacherId(String teacherId) {
        teacherRepository.findById(teacherId).ifPresent(t -> {
                    t.setIsValidated(TAKE_EFFECT_CLOSE);
                    teacherRepository.save(t);
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByTeacherId(String teacherId) {
        teacherRepository.deleteById(teacherId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFile(String teacherId, String fileUrl){
        teacherRepository.findById(teacherId).ifPresent(t -> {
            t.setFileUrl(fileUrl);
            teacherRepository.save(t);
        });
    }
}