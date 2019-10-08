package com.project.databank.service.imp;

import com.project.databank.domain.verify.CourseVerifyVo;
import com.project.databank.repository.verify.CourseVerifyVoRepository;
import com.project.databank.service.CourseVerifyVoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.project.base.common.keyword.Dic.TAKE_EFFECT_OPEN;
import static com.project.base.common.keyword.Dic.VERIFY_STATUS_APPLY;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-9-29 14:40
 * @version: 1.0
 * @description:
 */
@Slf4j
@Service
public class CourseVerifyVoServiceImpl implements CourseVerifyVoService {
    private final CourseVerifyVoRepository courseVerifyVoRepository;

    public CourseVerifyVoServiceImpl(CourseVerifyVoRepository courseVerifyVoRepository) {
        this.courseVerifyVoRepository = courseVerifyVoRepository;
    }

    @Override
    public Page<CourseVerifyVo> findAllPage(PageRequest pageRequest) {
        return courseVerifyVoRepository.findAllByIsValidatedEqualsAndVerifyStatus(TAKE_EFFECT_OPEN, VERIFY_STATUS_APPLY, pageRequest);
    }

    @Override
    public Page<CourseVerifyVo> findAllPage(String courseId, PageRequest pageRequest) {
        return courseVerifyVoRepository.findAllByIsValidatedEqualsAndVerifyStatusAndCourseId(TAKE_EFFECT_OPEN, VERIFY_STATUS_APPLY, courseId, pageRequest);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(CourseVerifyVo verifyVo) {
        courseVerifyVoRepository.save(verifyVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CourseVerifyVo verifyVo) {
        courseVerifyVoRepository.save(verifyVo);
    }

    @Async
    @Override
    public void saveAll(List<CourseVerifyVo> verifyVoList) {
        courseVerifyVoRepository.saveAll(verifyVoList);
    }

    @Override
    public Optional<CourseVerifyVo> findById(String id) {
        return courseVerifyVoRepository.findById(id);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void saveUpdateVerify(CourseVerifyRequest request) {
//        Optional<CourseVerifyVo> optionalCourseVerifyVo = courseVerifyVoRepository.findById(request.getId());
//        MyAssert.isFalse(optionalCourseVerifyVo.isPresent(), DefineCode.ERR0014, "不存在要修改的审核信息");
//        CourseVerifyVo verifyVo = optionalCourseVerifyVo.get();
//        //是文件资料信息
//        String type = verifyVo.getCourseType();
//
//        if (StrUtil.isNotBlank(verifyVo.getFileId())
//                && VERIFY_STATUS_AGREE.equals(request.getVerifyStatus())){
//            chapteDataService.verifyData(request, verifyVo.getDatumType());
//        }
//        if (COURSE_DATA.getValue().equals(type)
//                && VERIFY_STATUS_AGREE.equals(request.getVerifyStatus())){
//            //是课程
//
//        }
//        if (CHAPTER_DATE.getValue().equals(type)
//                && VERIFY_STATUS_AGREE.equals(request.getVerifyStatus())){
//            //章节
//        }
//        if (COURSE_IMAGE_DATE.getValue().equals(type)
//                && VERIFY_STATUS_AGREE.equals(request.getVerifyStatus())){
//            //课程图片轮播图
//
//        }
//        //修改数据
//        verifyVo.setUpdateUser(request.getUserId());
//        verifyVo.setVerifyStatus(request.getVerifyStatus());
//        verifyVo.setRemark(request.getRemark());
//        courseVerifyVoRepository.save(verifyVo);
//    }
}