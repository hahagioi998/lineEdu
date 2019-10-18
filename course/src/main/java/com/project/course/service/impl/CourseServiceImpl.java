package com.project.course.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.project.base.common.keyword.DefineCode;
import com.project.base.exception.MyAssert;
import com.project.base.util.UpdateUtil;
import com.project.course.domain.Course;
import com.project.course.domain.CourseImages;
import com.project.course.repository.CourseRepository;
import com.project.course.repository.CourseStudyRepository;
import com.project.course.repository.dto.ICourseDto;
import com.project.course.repository.dto.ICourseListDto;
import com.project.course.repository.dto.ICourseStudyDto;
import com.project.course.service.CourseService;
import com.project.course.web.req.CourseImagesReq;
import com.project.course.web.resp.CourseListResp;
import com.project.course.web.vo.CourseTeacherVo;
import com.project.course.web.vo.CourseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.base.common.keyword.Dic.*;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 18-11-21 15:59
 * @Version: 1.0
 * @Description:　课程管理
 */
@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    /**
     * 课程基本信息
     */
    @Resource
    private CourseRepository courseRepository;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

//    @Resource
//    private CourseVerifyRepository courseVerifyRepository;

//    @Resource
//    private CourseVerifyVoService courseVerifyVoService;

    /**
     * 课程轮播图
     */
    @Resource
    private CourseImagesServiceImpl courseImagesServiceImpl;

    @Resource
    private CourseStudyRepository courseStudyRepository;

    /**
     * 保存课程基本信息
     *
     * @param course 课程基本信息
     *               //     * @param teachers 集体备课教师信息
     * @return 课程编号和集体备课资源编号
     */
    @Override
    @Transactional(rollbackForClassName = "Exception")
    public String saveUpdate(Course course) {
        //1、保存课程基本信息
//        com.project.databank.domain.verify.CourseVerifyVo verifyVo = new com.project.databank.domain.verify.CourseVerifyVo();
//        verifyVo.setCourseType(COURSE_DATA.getValue());
//        verifyVo.setTeacherId(course.getCreateUser());
//        verifyVo.setTeacherName(teacherName);
//        verifyVo.setCenterName(centerName);
        if (StrUtil.isBlank(course.getCourseId())) {
            course.setCourseId(IdUtil.fastSimpleUUID());
//            course.setUpdateUser(course.getCreateUser());
//            BeanUtil.copyProperties(course, verifyVo);
//            verifyVo.setSubmitType("添加课程");
//            courseVerifyVoService.save(verifyVo);
            return courseRepository.save(course).getCourseId();
        } else {
            courseRepository.findById(course.getCourseId()).ifPresent(c -> {
                UpdateUtil.copyProperties(course, c);
                c.setUpdateUser(course.getCreateUser());

//                BeanUtil.copyProperties(course, verifyVo);
//                verifyVo.setSubmitType("修改课程");
//                courseVerifyVoService.save(verifyVo);

                courseRepository.save(c);
            });
        }
        return course.getCourseId();
    }

    /**
     * 获得所有可用课程列表
     *
     * @param page
     * @return
     */
    @Override
    public List<ICourseListDto> findAll(PageRequest page) {
        return courseRepository.findByIsValidated(TAKE_EFFECT_OPEN, page)
                .getContent();
    }

    @Override
    public Course findByCourseId(String courseId) {
        return courseRepository.findByCourseId(courseId);
    }

//    @Override
//    public Course findCourseVerifyById(String courseId){
//        Optional<Course> courseVerifyOptional = courseRepository.findById(courseId);
//        MyAssert.isFalse(courseVerifyOptional.isPresent(), DefineCode.ERR0010, "不存在对应的课程信息");
//        return courseVerifyOptional.get();
//    }

    /**
     * 分页查询我的课程科目
     *
     * @param page
     * @return
     */
    @Override
    public List<ICourseListDto> findMyCourse(String userId, PageRequest page) {
        return courseRepository.findByCreateUserAndIsValidatedOrderByCreateTimeDesc(userId, TAKE_EFFECT_OPEN, page)
                .getContent();
    }

    /**
     * 根据课程编号，获得课程基本信息
     *
     * @param id
     * @return
     */
    @Override
    public Course getById(String id) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        MyAssert.isFalse(optionalCourse.isPresent(), DefineCode.ERR0010, "编号对应的课程信息不存在");
        return optionalCourse.get();
    }

    /**
     * 学生端查询我的课程信息
     *
     * @param classId
     * @return
     */
    @Override
    @Cacheable(value = "myCourseList", key = "#classId", sync = true, unless = "#result eq null")
    public List<CourseListResp> myCourseList(String classId) {
        List<CourseListResp> listRespList = CollUtil.newArrayList();
        courseRepository.findByIsValidatedEqualsAndCourseIdInOrderByCreateTime(classId)
                .stream()
                .filter(Objects::nonNull)
                .forEach(iCourseChapterListDto -> {
                    listRespList.add(CourseListResp.builder()
                            .courseId(iCourseChapterListDto.getCourseId())
                            .courseName(iCourseChapterListDto.getCourseName())
                            .alias(iCourseChapterListDto.getAlias())
                            .topPicSrc(iCourseChapterListDto.getTopPicSrc())
                            .courseDescribe(iCourseChapterListDto.getCourseDescribe())
                            .joinChapterId(iCourseChapterListDto.getChapterId())
                            .joinChapterName(iCourseChapterListDto.getChapterName())
                            .teacherId(iCourseChapterListDto.getTeacherId())
                            .teacherName(iCourseChapterListDto.getTeacherName())
                            .build());
                });
        return listRespList;
    }

    /**
     * 根据课程ID，获得课程基本信息和集体备课共享编号
     *
     * @param courseId
     * @return
     */
    @Override
    public Map<String, Object> getCourseById(String courseId) {
        Map<String, Object> result = new HashMap<>(2);
        courseRepository.findById(courseId).ifPresent(course -> result.put("course", course));
        return result;
    }

    @Override
    @Transactional(rollbackForClassName = "Exception")
    public void deleteIsValidById(String courseId) {
        courseRepository.findById(courseId)
                .ifPresent(course -> {
                    course.setIsValidated(TAKE_EFFECT_CLOSE);
                    courseRepository.save(course);
                });
    }

    @Override
    @Transactional(rollbackForClassName = "Exception")
    public void saveCourseImages(CourseImagesReq courseImagesReq) {
        courseImagesServiceImpl.saveCourseImages(courseImagesReq.getCourseId(), courseImagesReq.getImages(),
                courseImagesReq.getCreateUser(), courseImagesReq.getCenterAreaId());
    }


    @Override
    @Transactional(rollbackForClassName = "Exception")
    public void deleteById(String courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    @Transactional(rollbackForClassName = "Exception")
    public void delete(Course course) {
        courseRepository.delete(course);
    }

    /**
     * 查询封面图片信息
     *
     * @param courseId
     * @return
     */
    @Override
    public List<CourseImages> findImagesByCourseId(String courseId, String verifyStatus) {
        return courseImagesServiceImpl.findImagesByCourseId(courseId, verifyStatus);
    }

    @Override
    public List<ICourseStudyDto> findCourseStudyList(String studentId, Integer studyStatus) {
        return courseStudyRepository.findByIsValidatedEqualsAndStudentId(studentId, studyStatus);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteImagesByCourseId(String courseId) {
        return courseImagesServiceImpl.deleteImagesByCourseId(courseId);
    }

    @Override
    public List<CourseVo> findByCourseNumberAndTeacherId(List<CourseTeacherVo> courseIds, String classId) {
        List<CourseVo> vos = new ArrayList<>();
        for (CourseTeacherVo v : courseIds){
            List<ICourseDto> list = courseRepository.findAllByCourseNumberAndCreateUserOrderByCreateTimeDescDto(v.getCourseId(), v.getTeacherId());
            if (!list.isEmpty()){
                ICourseDto iCourseDto = list.get(0);
                vos.add(new CourseVo(iCourseDto.getCourseId(), iCourseDto.getCourseName(), iCourseDto.getCourseNumber(), iCourseDto.getAlias(),
                        iCourseDto.getTopPicSrc(), iCourseDto.getCourseDescribe(), iCourseDto.getLearningTime(), iCourseDto.getVideoPercentage(),
                        iCourseDto.getJobsPercentage(), iCourseDto.getCreateUser(), iCourseDto.getCreateUserName()));
            }
        }
        stringRedisTemplate.opsForValue().set(TEACH_PLAN_CLASS_COURSEVO.concat(classId), JSONUtil.toJsonStr(vos), Duration.ofSeconds(10));
        return vos;
    }
    @Override
    public List<CourseVo> findCourseVoByClassId(String classId){
        String key = TEACH_PLAN_CLASS_COURSEVO.concat(classId);
        if (stringRedisTemplate.hasKey(key)){
            return JSONUtil.toList(JSONUtil.parseArray(stringRedisTemplate.opsForValue().get(key)), CourseVo.class);
        }
        return null;
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void verifyCourse(CourseVerifyVo verifyVo) {
//        Optional<CourseVerify> optional = courseVerifyRepository.findById(verifyVo.getCourseId());
//        MyAssert.isFalse(optional.isPresent(), DefineCode.ERR0014, "不存在对应的课程信息");
//        CourseVerify courseVerify = optional.get();
//        courseVerify.setRemark(verifyVo.getRemark());
//        courseVerify.setVerifyStatus(verifyVo.getVerifyStatus());
//        courseVerify.setUpdateUser(verifyVo.getUserId());
//        if (VERIFY_STATUS_AGREE.equals(verifyVo.getVerifyStatus())) {
//            Course course = new Course();
//            BeanUtil.copyProperties(courseVerify, course);
//            courseRepository.save(course);
//        }
//        courseVerifyRepository.save(courseVerify);
//    }
}