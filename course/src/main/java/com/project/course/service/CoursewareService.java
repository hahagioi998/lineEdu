package com.project.course.service;


import com.project.course.web.req.CoursewareAll;
import com.project.course.web.req.ImpCoursewareAll;

import java.util.List;

public interface CoursewareService {

    /**
     * 保存除图集以外，主要课件文件信息
     *
     * @param obj
     * @return
     */
    public ImpCoursewareAll saveFile(ImpCoursewareAll obj);

    public List<CoursewareAll> saveCourseAtlit(ImpCoursewareAll obj);

    /**
     * 获得对应类型的重要课件信息数量和列表
     *
     * @param chapterId
     * @param importantType
     * @param datumType
     * @return
     */
    public ImpCoursewareAll getImpCourseware(String chapterId, String importantType, String datumType);

    /**
     * 获得图集列表
     *
     * @param chapterId
     * @return
     */
    public List<CoursewareAll> getCourseArlitsList(String chapterId);

    /**
     * 获取图册列表
     *
     * @param arlitId
     * @return
     */
    public List<CoursewareAll> getPhotoList(String arlitId);

    public void removePhotoList(String arlitId);

    public void removeCourseArlitsList(String chapterId);

    public void removeCourseware(String chapterId, String importantType, String datumType);

    public void removeCourseAtlit(String chapterId);
}