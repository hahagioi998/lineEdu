package com.project.train.repository;

import com.project.train.domain.TrainClass;
import com.project.train.domain.TrainClassStu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 培训项目班级学生管理
 */
@Repository("trainClassStuRepository")
public interface TrainClassStuRepository extends JpaRepository<TrainClassStu, String>, JpaSpecificationExecutor<TrainClassStu> {

    //获得计划下的班级成员信息
    public Page<TrainClassStu> findByPjPlanIdOrderByCreateTime(String planId, Pageable pageable);

    //获得班级下的班级成员信息
    public Page<TrainClassStu> findByTrainClassId(String classId, Pageable pageable);
}