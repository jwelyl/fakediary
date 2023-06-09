package com.a101.fakediary.diary.controller;

import com.a101.fakediary.alarm.dto.AlarmRequestDto;
import com.a101.fakediary.alarm.dto.AlarmResponseDto;
import com.a101.fakediary.alarm.service.AlarmService;
import com.a101.fakediary.chatgptdiary.dto.result.DiaryResultDto;
import com.a101.fakediary.diary.dto.DiaryFilterDto;
import com.a101.fakediary.diary.dto.DiaryRequestDto;
import com.a101.fakediary.diary.dto.DiaryResponseDto;
import com.a101.fakediary.diary.dto.request.DiaryInformation;
import com.a101.fakediary.diary.service.DiaryService;
import com.a101.fakediary.mattermost.MatterMostSender;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@ApiOperation(value = "DiaryController")
@RequestMapping("/diary")
@RequiredArgsConstructor
@Slf4j
public class DiaryController {
    private final DiaryService diaryService;
    private final AlarmService alarmService;
    private final MatterMostSender matterMostSender;

    /**
     * 
     * @param information : 생성할 일기 정보
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<?> saveDiaryWithDiaryInformation(@RequestBody DiaryInformation information) {
        log.info("saveDiaryWithDiaryInformation");
        log.info("memberId = " + information.getMemberId());
        log.info("cardIdList = " + information.getCardIdList());
        log.info("genreList = " + information.getGenreList());

        try {
            Long memberId = information.getMemberId();
            List<Long> cardIdList = information.getCardIdList();
            List<String> genreList = information.getGenreList();

            log.info("memberId = " + memberId);
            log.info("cardIdList = " + cardIdList);
            log.info("genreList = " + genreList);

            DiaryResponseDto diaryResponseDto = diaryService.createDiary(memberId, cardIdList, genreList);
            String alarmTitle = "따끈따끈한 일기의 순간입니다";
            String alarmBody = "내가 선택한 가짜다이어리가 완성되었어요";
            alarmService.saveAlarm(new AlarmRequestDto(diaryResponseDto.getMemberId(), diaryResponseDto.getDiaryId(), alarmTitle, alarmBody, "MANUAL"));
            alarmService.sendNotificationByToken(new AlarmResponseDto(diaryResponseDto.getMemberId(), alarmTitle, alarmBody));

            return new ResponseEntity<>(diaryResponseDto, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            matterMostSender.sendMessage(e, "all");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param diaryId : 정보 가져올 일기 pk
     * @return : DiaryResponseDto
     */
    @ApiOperation(value = "일기 상세 조회")
    @GetMapping("/detail/{diaryId}")
    public ResponseEntity<?> detailDiary(@PathVariable Long diaryId) {
        try {
            DiaryResponseDto diary = diaryService.detailDiary(diaryId);
            return new ResponseEntity<DiaryResponseDto>(diary, HttpStatus.OK);
        } catch (NullPointerException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "일기 전체 조회")
    @GetMapping("/all/{memberId}")
    public ResponseEntity<?> allDiary(@PathVariable Long memberId) {
        try {
            List<DiaryResponseDto> diary = diaryService.allDiary(memberId);
            return new ResponseEntity<List<DiaryResponseDto>>(diary, HttpStatus.OK);
        } catch (NullPointerException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "일기 필터 조회, id: 선택한 사람 id, memberId: 조회한 사람 id, genre: 장르")
    @PostMapping("/filter")
    public ResponseEntity<?> filterDiary(@RequestBody DiaryFilterDto filter) {
        try {
            List<DiaryResponseDto> diary = diaryService.filterDiary(filter);
            return new ResponseEntity<List<DiaryResponseDto>>(diary, HttpStatus.OK);
        } catch (NullPointerException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "일기 삭제(status상 삭제)")
    @DeleteMapping("/{diaryId}")
        public ResponseEntity<?> deleteStatusDiary(@PathVariable Long diaryId){
            try{
                diaryService.deleteStatusDiary(diaryId);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

    @ApiOperation(value = "일기 삭제(DB에서 삭제. 진짜 DB에서 삭제되니 status삭제로 사용 권장)")
    @DeleteMapping("/delete/{diaryId}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long diaryId) {
        try {
            diaryService.deleteDiary(diaryId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * for developing : 모든 일기 랜덤 교환 사용 내역 초기화
     * @return
     */
    @PutMapping("/reset")
    public ResponseEntity<?> setAllDiariesRandomExchangedUnused() {
        try {
            diaryService.setAllDiariesRandomExchangedUnused();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create/old")
    public ResponseEntity<?> saveDiaryWithDiaryInformationOld(@RequestBody DiaryInformation information) {
        log.info("saveDiaryWithDiaryInformation");
        log.info("memberId = " + information.getMemberId());
        log.info("cardIdList = " + information.getCardIdList());
        log.info("genreList = " + information.getGenreList());

        try {
            Long memberId = information.getMemberId();
            List<Long> cardIdList = information.getCardIdList();
            List<String> genreList = information.getGenreList();

            log.info("memberId = " + memberId);
            log.info("cardIdList = " + cardIdList);
            log.info("genreList = " + genreList);

            DiaryResponseDto diaryResponseDto = diaryService.createDiaryOld(memberId, cardIdList, genreList);
            String alarmTitle = "따끈따끈한 일기의 순간입니다";
            String alarmBody = "내가 선택한 가짜다이어리가 완성되었어요";
            alarmService.saveAlarm(new AlarmRequestDto(diaryResponseDto.getMemberId(), diaryResponseDto.getDiaryId(), alarmTitle, alarmBody, "MANUAL"));
            alarmService.sendNotificationByToken(new AlarmResponseDto(diaryResponseDto.getMemberId(), alarmTitle, alarmBody));

            return new ResponseEntity<>(diaryResponseDto, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            matterMostSender.sendMessage(e, "all");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}