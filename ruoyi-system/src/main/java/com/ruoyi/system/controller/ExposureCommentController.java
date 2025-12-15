package com.ruoyi.system.controller;

import com.ruoyi.system.service.IExposureCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/exposure/comment")
public class ExposureCommentController {

    @Autowired
    private IExposureCommentService commentService;

    @PostMapping("/add")
    public Map<String, Object> addSegment(@RequestParam Long exposureLogId,
                                          @RequestParam int segmentIndex,
                                          @RequestParam String content) {
        int r = commentService.addCommentSegment(exposureLogId, segmentIndex, content);
        Map<String, Object> res = new HashMap<>();
        res.put("rows", r);
        return res;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam Long exposureLogId) {
        List<Map<String, Object>> rows = commentService.getComments(exposureLogId);
        Map<String, Object> res = new HashMap<>();
        res.put("rows", rows);
        return res;
    }
}
