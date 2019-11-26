package com.project.teachplan.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.project.base.common.keyword.DefineCode;
import com.project.base.exception.MyAssert;
import com.project.course.domain.CourseStudy;
import com.project.course.repository.CourseStudyRepository;
import com.project.course.repository.dto.ICourseStudyDto;
import com.project.course.service.OnLineCourseDicService;
import com.project.schoolroll.domain.StudentScore;
import com.project.schoolroll.service.StudentScoreService;
import com.project.schoolroll.service.online.StudentOnLineService;
import com.project.schoolroll.service.online.TbClassService;
import com.project.teachplan.domain.TeachPlan;
import com.project.teachplan.domain.TeachPlanClass;
import com.project.teachplan.domain.TeachPlanCourse;
import com.project.teachplan.domain.verify.TeachPlanClassVerify;
import com.project.teachplan.domain.verify.TeachPlanCourseVerify;
import com.project.teachplan.domain.verify.TeachPlanVerify;
import com.project.teachplan.repository.TeachPlanClassRepository;
import com.project.teachplan.repository.TeachPlanCourseRepository;
import com.project.teachplan.repository.TeachPlanRepository;
import com.project.teachplan.repository.dto.PlanCourseStudyDto;
import com.project.teachplan.repository.dto.TeachPlanDto;
import com.project.teachplan.repository.verify.TeachPlanClassVerifyRepository;
import com.project.teachplan.repository.verify.TeachPlanCourseVerifyRepository;
import com.project.teachplan.repository.verify.TeachPlanVerifyRepository;
import com.project.teachplan.vo.*;
import com.project.user.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.base.common.keyword.Dic.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author zhang
 * @apiNote 在线教学计划
 */
@Service
public class TeachService {
    private final StudentOnLineService studentOnLineService;
    private final TeachPlanCourseService teachPlanCourseService;
    private final TeachPlanRepository teachPlanRepository;
    private final TbClassService tbClassService;
    private final TeachPlanClassRepository teachPlanClassRepository;
    private final TeachPlanCourseRepository teachPlanCourseRepository;
    private final OnLineCourseDicService onLineCourseDicService;
    private final TeacherService teacherService;
    private final PlanFileService planFileService;
    private final CourseStudyRepository courseStudyRepository;
    private final StringRedisTemplate redisTemplate;
    private final TeachPlanVerifyRepository teachPlanVerifyRepository;
    private final TeachPlanClassVerifyRepository teachPlanClassVerifyRepository;
    private final TeachPlanCourseVerifyRepository teachPlanCourseVerifyRepository;
    private final StudentScoreService studentScoreService;

    @Autowired
    public TeachService(StudentOnLineService studentOnLineService, TeachPlanRepository teachPlanRepository,
                        TeachPlanCourseRepository teachPlanCourseRepository, TbClassService tbClassService,
                        TeachPlanClassRepository teachPlanClassRepository, TeacherService teacherService,StudentScoreService studentScoreService,
                        PlanFileService planFileService, TeachPlanCourseService teachPlanCourseService, StringRedisTemplate redisTemplate,
                        OnLineCourseDicService onLineCourseDicService, CourseStudyRepository courseStudyRepository,
                        TeachPlanVerifyRepository teachPlanVerifyRepository, TeachPlanCourseVerifyRepository teachPlanCourseVerifyRepository,
                        TeachPlanClassVerifyRepository teachPlanClassVerifyRepository) {
        this.studentOnLineService = studentOnLineService;
        this.teachPlanRepository = teachPlanRepository;
        this.tbClassService = tbClassService;
        this.teacherService = teacherService;
        this.teachPlanClassRepository = teachPlanClassRepository;
        this.teachPlanCourseService = teachPlanCourseService;
        this.teachPlanCourseRepository = teachPlanCourseRepository;
        this.onLineCourseDicService = onLineCourseDicService;
        this.planFileService = planFileService;
        this.teachPlanVerifyRepository = teachPlanVerifyRepository;
        this.teachPlanCourseVerifyRepository = teachPlanCourseVerifyRepository;
        this.teachPlanClassVerifyRepository = teachPlanClassVerifyRepository;
        this.courseStudyRepository = courseStudyRepository;
        this.redisTemplate = redisTemplate;
        this.studentScoreService = studentScoreService;
    }


    @Transactional(rollbackFor = Exception.class)
    public TeachPlanVerify saveUpdatePlan(TeachPlanVerify teachPlan) {
        teachPlan.setVerifyStatus(VERIFY_STATUS_APPLY);
        if (StrUtil.isBlank(teachPlan.getPlanId())) {
            MyAssert.isTrue(StrUtil.isBlank(teachPlan.getPlanName()), DefineCode.ERR0010, "计划名称不能为空");
            MyAssert.isTrue(StrUtil.isBlank(teachPlan.getStartDate()), DefineCode.ERR0010, "计划开始时间不能为空");
            MyAssert.isTrue(StrUtil.isBlank(teachPlan.getEndDate()), DefineCode.ERR0010, "计划结束时间不能为空");
            MyAssert.isTrue(StrUtil.isBlank(teachPlan.getPlanAdmin()), DefineCode.ERR0010, "负责人不能为空");
            MyAssert.isTrue(teachPlanVerifyRepository.existsByPlanName(teachPlan.getPlanName()), DefineCode.ERR0010, "已经存在同名计划,请修改");
            String planId = IdUtil.fastSimpleUUID();
            teachPlan.setPlanId(planId);
            return teachPlanVerifyRepository.save(teachPlan);
        } else {
            Optional<TeachPlanVerify> optional = teachPlanVerifyRepository.findById(teachPlan.getPlanId());
            MyAssert.isFalse(optional.isPresent(), DefineCode.ERR0010, "要修改的计划不存在");
            TeachPlanVerify t = optional.get();
            //修改计划名称需要判断是否存在同名计划存在不能修改
            if (!t.getPlanName().equals(teachPlan.getPlanName())){
                MyAssert.isTrue(teachPlanVerifyRepository.existsByPlanName(teachPlan.getPlanName()), DefineCode.ERR0010, "已经存在同名计划,请修改");
            }
            BeanUtil.copyProperties(teachPlan, t);
            return teachPlanVerifyRepository.save(t);
        }
    }

    private void saveTeachPlanCourse(String planId, List<TeachPlanCourseVo> courses, String remark, String centerAreaId, String userId) {
        List<TeachPlanCourseVerify> planCourseList = courses.parallelStream().filter(Objects::nonNull)
                .map(t -> createTeachPlanCourse(planId, t, remark, centerAreaId, userId))
                .collect(toList());
        if (!planCourseList.isEmpty()) {
            teachPlanCourseService.saveAllVerify(planCourseList);
        }
    }

    private TeachPlanCourseVerify createTeachPlanCourse(String planId, TeachPlanCourseVo vo, String remark, String centerAreaId, String userId) {
        String teacherName = teacherService.findById(vo.getTeacherId()).getTeacherName();
        return new TeachPlanCourseVerify(planId, vo.getCourseId(), onLineCourseDicService.findId(vo.getCourseId()).getCourseName(),
                vo.getCredit(), vo.getOnLinePercentage(), vo.getLinePercentage(), vo.getTeacherId(), teacherName,
                centerAreaId, remark, userId, VERIFY_STATUS_APPLY);
    }

    private void saveTeachPlanClass(String planId, TeachPlanVerify teachPlan, List<String> classIds, String remark, String centerAreaId, String userId) {
        List<TeachPlanClassVerify> planClassList = classIds.stream().filter(Objects::nonNull)
                .map(c -> new TeachPlanClassVerify(c, planId, tbClassService.findClassByClassId(c).getClassName(), teachPlan.getPlanName(), studentOnLineService.countByClassId(c), centerAreaId, remark, userId, VERIFY_STATUS_APPLY))
                .collect(toList());
        teachPlanClassVerifyRepository.saveAll(planClassList);
        int sumNumber = planClassList.stream().mapToInt(TeachPlanClassVerify::getClassNumber).sum();
        teachPlan.setSumNumber(sumNumber);
    }

    @Transactional(rollbackFor = Exception.class)
    public TeachPlanVerify saveUpdatePlanClass(String planId, List<String> classIds, String remark, String centerAreaId, String userId) {
        Optional<TeachPlanVerify> teachPlanOptional = teachPlanVerifyRepository.findById(planId);
        MyAssert.isFalse(teachPlanOptional.isPresent(), DefineCode.ERR0010, "不存在对应的计划编号");
        TeachPlanVerify teachPlan = teachPlanOptional.get();
        teachPlanClassVerifyRepository.deleteAllByPlanId(planId);
        saveTeachPlanClass(planId, teachPlan, classIds, remark, centerAreaId, userId);
        if (!classIds.isEmpty()) {
            teachPlan.setClassNumber(classIds.size());
        }
        teachPlan.setVerifyStatus(VERIFY_STATUS_APPLY);
        return teachPlanVerifyRepository.save(teachPlan);
    }

    @Transactional(rollbackFor = Exception.class)
    public TeachPlanVerify saveUpdatePlanCourse(String planId, List<TeachPlanCourseVo> courses, String remark, String centerAreaId, String userId) {
        Optional<TeachPlanVerify> teachPlanOptional = teachPlanVerifyRepository.findById(planId);
        MyAssert.isFalse(teachPlanOptional.isPresent(), DefineCode.ERR0010, "不存在对应的计划编号");
        TeachPlanVerify teachPlan = teachPlanOptional.get();
        teachPlanCourseVerifyRepository.deleteAllByPlanId(planId);
        saveTeachPlanCourse(planId, courses, remark, centerAreaId, userId);
        if (!courses.isEmpty()) {
            teachPlan.setCourseNumber(courses.size());
        }
        teachPlan.setVerifyStatus(VERIFY_STATUS_APPLY);
        return teachPlanVerifyRepository.save(teachPlan);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeByPlanId(String planId) {
        teachPlanClassRepository.updateIsValidatedByPlanId(TAKE_EFFECT_CLOSE, planId);
        teachPlanRepository.updateIsValidatedByPlanId(TAKE_EFFECT_CLOSE, planId);
        teachPlanClassRepository.updateIsValidatedByPlanId(TAKE_EFFECT_CLOSE, planId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByPlanId(String planId) {
        Optional<TeachPlanVerify> optional = teachPlanVerifyRepository.findById(planId);
        MyAssert.isFalse(optional.isPresent(), DefineCode.ERR0010, "不存在要删除的计划信息");
        TeachPlanVerify teachPlanVerify = optional.get();
        MyAssert.isTrue(DateUtil.parseDate(teachPlanVerify.getEndDate()).isAfterOrEquals(new Date()), DefineCode.ERR0010, "正在进行的计划不能删除");
        teachPlanCourseRepository.deleteAllByPlanId(planId);
        teachPlanClassRepository.deleteAllByPlanId(planId);
        teachPlanRepository.deleteAllByPlanId(planId);
        teachPlanVerifyRepository.deleteById(planId);
        teachPlanCourseVerifyRepository.deleteAllByPlanId(planId);
        teachPlanClassVerifyRepository.deleteAllByPlanId(planId);
    }

    public List<TeachPlanClass> findAllClassByPlanId(String planId) {
        return teachPlanClassRepository.findAllByIsValidatedEqualsAndPlanIdOrderByCreateTimeDesc(TAKE_EFFECT_OPEN, planId);
    }

    public List<TeachPlanClassVerify> findAllClassVerifyByPlanId(String planId) {
        return teachPlanClassVerifyRepository.findAllByPlanId(planId);
    }

    public Page<TeachPlanDto> findAllPageByPlanIdAndVerifyStatus(String planId, String verifyStatus, Pageable pageable) {
        if (StrUtil.isNotBlank(planId) && StrUtil.isNotBlank(verifyStatus)) {
            return teachPlanVerifyRepository.findAllPageByPlanIdAndVerifyStatusDto(planId, verifyStatus, pageable);
        } else if (StrUtil.isNotBlank(planId) && StrUtil.isBlank(verifyStatus)) {
            return teachPlanVerifyRepository.findAllPageByPlanIdDto(planId, pageable);
        } else if (StrUtil.isNotBlank(verifyStatus) && StrUtil.isBlank(planId)) {
            return teachPlanVerifyRepository.findAllPageByVerifyStatusDto(verifyStatus, pageable);
        } else {
            return teachPlanVerifyRepository.findAllPageDto(pageable);
        }
    }


    public Page<TeachPlanDto> findAllPageDtoByCenterAreaId(String centerAreaId, String verifyStatus, Pageable pageable) {
        if (StrUtil.isBlank(verifyStatus)) {
            return teachPlanVerifyRepository.findAllPageByCenterAreaIdDto(centerAreaId, pageable);
        } else {
            return teachPlanVerifyRepository.findAllPageByVerifyStatusAndCenterAreaIdDto(verifyStatus, centerAreaId, pageable);
        }
    }

    public Page<TeachPlanDto> findAllPageDtoByCenterAreaIdAndPlanId(String centerAreaId, String planId, String verifyStatus, Pageable pageable) {
        if (StrUtil.isBlank(verifyStatus)) {
            return teachPlanVerifyRepository.findAllPageByCenterAreaIdAndPlanIdDto(centerAreaId, planId, pageable);
        } else {
            return teachPlanVerifyRepository.findAllPageByPlanIdAndCenterAreaIdDto(planId, verifyStatus, centerAreaId, pageable);
        }
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    void updateClassByPlanId(String planId, String status, String userId) {
        List<TeachPlanClass> list = teachPlanClassRepository.findAllByPlanId(planId).stream().peek(c -> {
            c.setIsValidated(status);
            c.setUpdateUser(userId);
        }).collect(Collectors.toList());
        teachPlanClassRepository.saveAll(list);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    void updateCourseByPlanId(String planId, String status, String userId) {
        List<TeachPlanCourse> list = teachPlanCourseRepository.findAllByPlanId(planId).stream().peek(c -> {
            c.setIsValidated(status);
            c.setUpdateUser(userId);
        }).collect(Collectors.toList());
        teachPlanCourseRepository.saveAll(list);
    }

    public void updateStatus(String planId, String userId) {
        Optional<TeachPlan> optionalTeachPlan = teachPlanRepository.findById(planId);
        MyAssert.isFalse(optionalTeachPlan.isPresent(), DefineCode.ERR0014, "不存在对应的计划信息");
        optionalTeachPlan.ifPresent(t -> {
            String status = t.getIsValidated();
            if (TAKE_EFFECT_CLOSE.equals(status)) {
                t.setIsValidated(TAKE_EFFECT_OPEN);
                // 修改班级计划状态
                updateClassByPlanId(planId, TAKE_EFFECT_OPEN, userId);
                //修改课程计划状态
                updateCourseByPlanId(planId, TAKE_EFFECT_OPEN, userId);
                //修改计划文件状态
                planFileService.updateStatus(planId, TAKE_EFFECT_OPEN, userId);
                //修改审核的计划信息
                setVerifyStatus(planId, TAKE_EFFECT_OPEN, userId);
            } else {
                t.setIsValidated(TAKE_EFFECT_CLOSE);
                updateClassByPlanId(planId, TAKE_EFFECT_CLOSE, userId);
                updateCourseByPlanId(planId, TAKE_EFFECT_CLOSE, userId);
                planFileService.updateStatus(planId, TAKE_EFFECT_CLOSE, userId);
                //修改审核的计划信息
                setVerifyStatus(planId, TAKE_EFFECT_CLOSE, userId);
            }
            t.setUpdateUser(userId);
            teachPlanRepository.save(t);
        });
    }

    private void setVerifyStatus(String planId, String status, String userId) {
        //修改教学计划
        updateTeachPlanVerify(planId, status, userId);
        //修改教学计划班级
        updateClassVerifyByPlanId(planId, status, userId);
        //修改教学计划课程
        updateVerfyCourseByPlanId(planId, status, userId);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    void updateTeachPlanVerify(String planId, String status, String userId) {
        teachPlanVerifyRepository.findById(planId).ifPresent(t -> {
            t.setIsValidated(status);
            t.setUpdateUser(userId);
            teachPlanVerifyRepository.save(t);
        });
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    void updateVerfyCourseByPlanId(String planId, String status, String userId) {
        List<TeachPlanCourseVerify> list = teachPlanCourseVerifyRepository.findAllByPlanId(planId).stream().peek(c -> {
            c.setIsValidated(status);
            c.setUpdateUser(userId);
        }).collect(Collectors.toList());
        if (!list.isEmpty()) {
            teachPlanCourseVerifyRepository.saveAll(list);
        }
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    void updateClassVerifyByPlanId(String planId, String status, String userId) {
        List<TeachPlanClassVerify> list = teachPlanClassVerifyRepository.findAllByPlanId(planId).stream().peek(c -> {
            c.setIsValidated(status);
            c.setUpdateUser(userId);
        }).collect(Collectors.toList());
        if (!list.isEmpty()) {
            teachPlanClassVerifyRepository.saveAll(list);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void verifyTeachPlan(String planId, String verifyStatus, String remark, String userId) {
        //修改对应的计划信息
        updateVerifyTeachPlan(planId, verifyStatus, remark, userId);
        //修改对应的班级信息
        updateVerifyPlanClass(planId, verifyStatus, remark, userId);
        //修改对应的课程信息
        teachPlanCourseService.updateVerifyPlanCourse(planId, verifyStatus, remark, userId);
    }


    private void updateVerifyPlanClass(String planId, String verifyStatus, String remark, String userId) {
        // 审核通过 删除原来的班级
        if (VERIFY_STATUS_AGREE.equals(verifyStatus)) {
            teachPlanClassRepository.deleteAllByPlanId(planId);
        }
        List<TeachPlanClass> teachPlanClassList = new ArrayList<>();
        List<TeachPlanClassVerify> teachPlanClassVerifyList = teachPlanClassVerifyRepository.findAllByPlanId(planId).stream().filter(Objects::nonNull)
                .peek(t -> {
                    t.setRemark(remark);
                    t.setVerifyStatus(verifyStatus);
                    t.setUpdateUser(userId);
                    if (VERIFY_STATUS_AGREE.equals(verifyStatus)) {
                        TeachPlanClass p = new TeachPlanClass();
                        BeanUtil.copyProperties(t, p);
                        p.setUpdateUser(userId);
                        teachPlanClassList.add(p);
                    }
                }).collect(Collectors.toList());
        if (!teachPlanClassVerifyList.isEmpty()) {
            teachPlanClassVerifyRepository.saveAll(teachPlanClassVerifyList);
        }
        if (!teachPlanClassList.isEmpty()) {
            teachPlanClassRepository.saveAll(teachPlanClassList);
        }
    }

    /**
     * 修改计划信息
     */
    private void updateVerifyTeachPlan(String planId, String verifyStatus, String remark, String userId) {
        Optional<TeachPlanVerify> optional = teachPlanVerifyRepository.findById(planId);
        MyAssert.isFalse(optional.isPresent(), DefineCode.ERR0014, "不存在对应的计划信息");
        TeachPlanVerify t = optional.get();
        t.setUpdateUser(userId);
        t.setVerifyStatus(verifyStatus);
        if (StrUtil.isNotBlank(remark)) {
            t.setRemark(remark);
        }
        //审核通过
        if (VERIFY_STATUS_AGREE.equals(verifyStatus)) {
            TeachPlan p = new TeachPlan();
            BeanUtil.copyProperties(t, p);
            teachPlanRepository.save(p);
        }
        teachPlanVerifyRepository.save(t);
    }

    @SuppressWarnings(value = "all")
    public Page<TeachCourseVo> findAllPageDtoByPlanId(String planId, String key, Pageable pageable) {
        //查询redis缓存
        if (redisTemplate.hasKey(key)) {
            JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForValue().get(key));
            return new PageImpl(jsonObject.getJSONArray("content").toJavaList(TeachCourseVo.class), pageable, jsonObject.getLong("totalElements"));
        }
        //设置redis缓存
        Page<TeachCourseVo> page = findAllPageByPlanId(planId, pageable);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(page), Duration.ofMinutes(1));
        return page;
    }

    @SuppressWarnings(value = "all")
    public Page<CourseScoreVo> findScoreAllPageDtoByPlanId(String planId, String key, Pageable pageable) {
        //查询redis缓存
        if (redisTemplate.hasKey(key)) {
            JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForValue().get(key));
            return new PageImpl(jsonObject.getJSONArray("content").toJavaList(TeachCourseVo.class), pageable, jsonObject.getLong("totalElements"));
        }
        //设置redis缓存
        Page<CourseScoreVo> page = findScoreAllPageByPlanId(planId, pageable);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(page), Duration.ofMinutes(1));
        return page;
    }

    private Page<CourseScoreVo> findScoreAllPageByPlanId(String planId, Pageable pageable) {
        Page<PlanCourseStudyDto> page = teachPlanRepository.findAllPageDtoByPlanId(planId, pageable);
        List<CourseScoreVo> list = page.getContent()
                .stream()
                .map(d -> new CourseScoreVo(d.getStudentId(), d.getStudentName(), d.getStuPhone(), d.getCenterAreaId(),
                        d.getCenterName(), d.getPlanId(), d.getPlanName(), d.getStartDate(), d.getEndDate(),
                        toListScore(d.getStudentId(), d.getCourse())))
                .collect(toList());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private Page<TeachCourseVo> findAllPageByPlanId(String planId, Pageable pageable) {
        Page<PlanCourseStudyDto> page = teachPlanRepository.findAllPageDtoByPlanId(planId, pageable);
        List<TeachCourseVo> list = page.getContent()
                .stream()
                .map(d -> new TeachCourseVo(d.getStudentId(), d.getStudentName(), d.getStuPhone(), d.getCenterAreaId(),
                        d.getCenterName(), d.getPlanId(), d.getPlanName(), d.getStartDate(), d.getEndDate(),
                        toListStudy(d.getStudentId(), d.getCourse())))
                .collect(toList());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    private List<StudyVo> toListStudy(String studentId, String course) {
        List<StudyVo> list = CollUtil.newArrayList();
        Arrays.asList(StrUtil.split(course, ","))
                .forEach(s -> {
                    String[] strings = StrUtil.split(s, "&");
                    courseStudyRepository.findStudyDto(studentId, strings[1], strings[2])
                            .ifPresent(d -> list.add(findStudyVo(strings, d)));
                });
        return list;
    }

    private StudyVo findStudyVo(String[] strings, ICourseStudyDto d){
        return new StudyVo(d.getCourseId(), strings[0], d.getOnLineTime(), d.getOnLineTimeSum(), d.getAnswerSum(), d.getCorrectSum());
    }

    private List<ScoreVo> toListScore(String studentId, String course) {
        List<ScoreVo> list = CollUtil.newArrayList();
        Arrays.asList(StrUtil.split(course, ","))
                .forEach(s -> {
                    String[] strings = StrUtil.split(s, "&");
                    courseStudyRepository.findStudyDto(studentId, strings[1], strings[2])
                            .ifPresent(d -> {
                                //线上成绩 (学习时长/课程总时长) * 视频占比 + (习题回答正确数量/总习题数量) * 练习占比
                                BigDecimal videoScore = new BigDecimal("0");
                                BigDecimal jobsScore = new BigDecimal("0");
                                if (0 != d.getOnLineTimeSum()){
                                    videoScore = NumberUtil.mul(NumberUtil.div(d.getOnLineTime(), d.getOnLineTimeSum(), 2), Double.valueOf(d.getVideoPercentage()) / 100);
                                }
                                if (0 != d.getAnswerSum()){
                                    jobsScore = NumberUtil.mul(NumberUtil.div(d.getCorrectSum(), d.getCorrectSum(), 2), Double.valueOf(d.getVideoPercentage()) / 100);
                                }
                                list.add(new ScoreVo(d.getCourseId(), strings[0], NumberUtil.add(videoScore, jobsScore).toPlainString()));
                            });
                });
        return list;
    }

    public void taskPlanStatus(){
        List<TeachPlan> list = teachPlanRepository.findAllByStatusAndEndDateBefore(PLAN_STATUS_ONGOING, DateUtil.today()).stream()
                .filter(Objects::nonNull).peek(t -> t.setStatus(PLAN_STATUS_SUCCESS)).collect(toList());
        if (!list.isEmpty()){
            teachPlanRepository.saveAll(list);
        }
    }

    public void taskOnLineCourseScore() {
        List<TeachPlan> planList = teachPlanRepository.findAllByIsValidatedEqualsAndStatusAndCountStatus(TAKE_EFFECT_OPEN, PLAN_STATUS_SUCCESS, PLAN_COUNT_STATUS_ONGOING);
        Set<String> planIds = planList.stream().filter(Objects::nonNull).map(TeachPlan::getPlanId).collect(toSet());
        planIds.forEach(p -> teachPlanCourseRepository.findAllPlanCourseDtoByPlanId(p)
                .forEach(c -> {
                    List<StudentScore> list = findAllStudentScore(p, c.getCourseId(), c.getOnLinePercentage(), c.getLinePercentage(), c.getVideoPercentage(), c.getJobsPercentage());
                    if (!list.isEmpty()) {
                        studentScoreService.saveAll(list);
                    }
                }));
        if (!planList.isEmpty()){
            teachPlanRepository.saveAll(planList.stream().peek(t -> t.setCountStatus(PLAN_COUNT_STATUS_SUCCESS)).collect(toList()));
        }
    }

    private List<StudentScore> findAllStudentScore(String planeId, String courseId, int onLinePercentage, int linePercentage, String videoPercentage, String jobsPercentage){
        return teachPlanClassRepository.findAllStudentIdByPlanId(planeId).stream()
                .filter(Objects::nonNull)
                .map(s -> findSetStudentScore(s, courseId, onLinePercentage, linePercentage, videoPercentage, jobsPercentage))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private StudentScore findSetStudentScore(String studentId, String courseId, int onLinePercentage, int linePercentage, String videoPercentage, String jobsPercentage){
        Optional<CourseStudy> optional = courseStudyRepository.findAllByCourseIdAndStudentId(courseId, studentId);
        if (optional.isPresent()) {
            CourseStudy c = optional.get();
            StudentScore studentScore = studentScoreService.findByStudentIdAndCourseId(studentId, courseId);
            studentScore.setCourseId(courseId);
            studentScore.setStudentId(studentId);
            studentScore.setUpdateUser(c.getStudentId());
            studentScore.setCreateUser(c.getStudentId());
            studentScore.setCenterAreaId(c.getCenterAreaId());
            int onLineTime = c.getOnLineTime();
            int onLineTimeSum = c.getOnLineTimeSum();
            //观看视频成绩 (观看视频时长/视频总时长) * 观看视频占比
            double videoScore = NumberUtil.mul(NumberUtil.mul(NumberUtil.div(onLineTime, onLineTimeSum, 2), 100F), Double.valueOf(videoPercentage) / 100);
            //平时作业成绩 (回答正确题目数量/总题目数量) * 平时作业占比
            BigDecimal jobScore = NumberUtil.mul(NumberUtil.mul(NumberUtil.div(c.getCorrectSum(), c.getAnswerSum(), 2), 100F), Double.valueOf(jobsPercentage) / 100);
            //线上成绩 = (观看视频时长/视频总时长) * 观看视频占比 + (回答正确题目数量/总题目数量) * 平时作业占比
            BigDecimal onLineScore = NumberUtil.add(videoScore, jobScore);
            //计算课程成绩 线上成绩部分 = 线上成绩 * 线上成绩占比
            BigDecimal courseScore = NumberUtil.mul(onLineScore, NumberUtil.div(onLinePercentage, 100, 2));
            studentScore.setOnLineScore(onLineScore.toPlainString());
            studentScore.setCourseScore(courseScore.floatValue());
            //线下成绩占比 %
            studentScore.setLinePercentage(linePercentage);
            //线上成绩占比 %
            studentScore.setOnLinePercentage(onLinePercentage);
            return studentScore;
        }
        return null;
    }

    public List<TeachPlanVerify> findAllPlan() {
        return teachPlanVerifyRepository.findAllByIsValidatedEqualsAndVerifyStatus(TAKE_EFFECT_OPEN, VERIFY_STATUS_AGREE);
    }

    public List<TeachPlanVerify> findAllPlanByCenterId(String centerId) {
        return teachPlanVerifyRepository.findAllByIsValidatedEqualsAndVerifyStatusAndCenterAreaId(TAKE_EFFECT_OPEN, VERIFY_STATUS_AGREE, centerId);
    }
}