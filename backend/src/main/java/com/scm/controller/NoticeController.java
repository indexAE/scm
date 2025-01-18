package com.scm.controller;

import com.scm.entity.Notice;
import com.scm.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping
    public ResponseEntity<?> createNotice(@RequestBody Notice notice) {
        try {
            // 验证必要字段
            if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "标题不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (notice.getScope() == null || notice.getScope().trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "接收范围不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            noticeService.createNotice(notice);
            Map<String, String> response = new HashMap<>();
            response.put("message", "发布成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        try {
            notice.setId(id);
            noticeService.updateNotice(notice);
            Map<String, String> response = new HashMap<>();
            response.put("message", "更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoticeById(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getNoticeById(id);
            return ResponseEntity.ok(notice);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getNoticeList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Map<String, Object> result = noticeService.getNoticeList(title, startTime, endTime, page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 