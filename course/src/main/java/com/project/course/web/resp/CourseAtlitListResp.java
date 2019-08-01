package com.project.course.web.resp;


import com.project.course.web.req.CoursewareAll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseAtlitListResp implements Serializable {

    public String chapterId;

    public int fileCount;

    public List<CoursewareAll> list;

}
