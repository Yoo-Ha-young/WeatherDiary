package zerobase.weather.controller;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;
@RestController
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    // get은 조회, post는 저장할때 많이 쓴다 RequestBody안에 내용을 넣기에 좋기 때문
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text){
        // 요청을 보낼때 넣어주는 파라미터 : @RequestParam 이걸 써주면
        // 로컬 날짜가 /create/diary/202308001 처럼 날짜가 파라미터로 넣어진다.
        // @DatetimeFormat으로 날짜 형식을 지정해준다.
        // 바디에 데이터를 넣어 전송, 바디에는 일기 값을 넣는다.
        diaryService.createDiary(date, text);
    }

    // 일기를 날짜에 따라 조회한다. 조회할 때 많이 쓰이는 get
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date){
        return diaryService.readDiary(date);
    }

    // 날짜 범위 별 일기들을 조회
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return diaryService.readDiaries(startDate, endDate);
    }
    // 다이어리 수정
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text){
        diaryService.updateDiary(date,text);
    }

    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date){
        diaryService.deleteDiary(date);
    }
//
//    @Operation(summary = "test hello", description = "hello api example")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK !!"),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
//            @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
//    })
//    @GetMapping("/hello")
//    public ResponseEntity<String> hello(@Parameter(description = "이름", required = true, example = "Park") @RequestParam String name) {
//        return ResponseEntity.ok("hello " + name);
//    }

}


