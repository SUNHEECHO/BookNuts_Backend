package team.nine.booknutsbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.nine.booknutsbackend.domain.Discussion;
import team.nine.booknutsbackend.domain.User;
import team.nine.booknutsbackend.dto.Request.RoomRequest;
import team.nine.booknutsbackend.dto.Response.RoomResponse;
import team.nine.booknutsbackend.exception.Discussion.OpinionValueException;
import team.nine.booknutsbackend.exception.Discussion.StatusValueException;
import team.nine.booknutsbackend.service.DiscussionService;
import team.nine.booknutsbackend.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/discuss")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final UserService userService;

    //토론장 개설
    @PostMapping("/create")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody @Valid RoomRequest room, Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        Discussion saveRoom = discussionService.createRoom(RoomRequest.newRoom(room, user));
        return new ResponseEntity<>(RoomResponse.roomResponse(saveRoom), HttpStatus.CREATED);
    }

    //토론장 참여 가능 여부 (가능할 경우 참여)
    @GetMapping("/join/{roomId}")
    public Boolean joinRoom(@PathVariable Long roomId, @RequestParam int opinion) throws OpinionValueException {
        Boolean canJoin = discussionService.joinRoomCheck(roomId, opinion);

        if (canJoin) {
            discussionService.updateJoin(roomId, opinion); //true 일 경우 참여
            return true;
        } else {
            return false;
        }
    }

    //토론장 나가기
    @GetMapping("/exit/{roomId}")
    public ResponseEntity<Object> exitRoom(@PathVariable Long roomId, @RequestParam int opinion) throws OpinionValueException {
        discussionService.updateExit(roomId, opinion);

        Map<String, String> map = new HashMap<>();
        map.put("result", "나가기 완료");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    //토론장 상태 변경 가능 여부 (인원이 모였는지)
    @GetMapping("/check/{roomId}")
    public Boolean checkStatus(@PathVariable Long roomId) {
        return discussionService.checkStatus(roomId);
    }

    //토론장 상태 변경
    @PutMapping("/update/{roomId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long roomId, @RequestParam int status) throws StatusValueException {
        discussionService.updateStatus(roomId, status);
        Map<String, String> map = new HashMap<>();
        map.put("result", "상태 변경 완료");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
