package com.example.finalprj.web.user.controller;

import com.example.finalprj.db.domain.Entry;
import com.example.finalprj.db.domain.User;
import com.example.finalprj.db.service.EntryService;
import com.example.finalprj.db.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final UserService userService;
    private final EntryService entryService;

    @GetMapping("/usage")
    public String usage(@AuthenticationPrincipal User user, Model model) {
        long playgroundId = user.getPlayground().getId();

        List<Long> userIds = entryService.findAllUserIdByPlaygroundIdAndStatusEqual(playgroundId, 2); // 이용중인 사람들 목록
        List<User> users = new ArrayList<>();
        for (Long id : userIds) {
            users.add(userService.findById(id).orElse(null));
        }

        model.addAttribute("users", users);
        model.addAttribute("site", "usage");
        model.addAttribute("url", "manager");
        return "usage";
    }

    @PutMapping("/usage")
    public String usage(@AuthenticationPrincipal User user, long userId) {
        long playgroundId = user.getPlayground().getId();
        entryService.exitUser(userId, playgroundId);

        return "redirect:/manager/usage";
    }

    /////////////////////예약////////////////////////////////////

    @GetMapping("/reservation")
    public String reservation(@AuthenticationPrincipal User user, Model model) {
        long playgroundId = user.getPlayground().getId();

        List<Long> userIds = entryService.findAllUserIdByPlaygroundIdAndStatusEqual(playgroundId, 1);
        List<User> users = new ArrayList<>();
        for (Long id : userIds) {
            users.add(userService.findById(id).orElse(null));
        }
        model.addAttribute("users", users);
        model.addAttribute("site", "reservation");
        model.addAttribute("url", "manager");

        return "reservation";
    }

    @PutMapping("/reservation")
    public String reservation1(@AuthenticationPrincipal User user, long userId) {
        long playgroundId = user.getPlayground().getId();
        entryService.entryUser(userId, playgroundId);

        return "redirect:/manager/reservation";
    }

    @DeleteMapping("/reservation")
    public String reservation2(@AuthenticationPrincipal User user, long userId) {
        long playgroundId = user.getPlayground().getId();

        entryService.deleteUserIdStatusEquals(userId, playgroundId, 1); // status가 1인 userid 찾아서 지우기

        return "redirect:/manager/reservation";
    }

    //////////////////////////////////////////////////////////

    @GetMapping("/list")
    public String list(@AuthenticationPrincipal User user, Model model) {

        long playgroundId = user.getPlayground().getId();

        List<Entry> entries = entryService.findAllByPlaygroundIdAndStatusEqual(playgroundId,3);  // 놀이터 1에 출입한 기록 있는 user_id 시산 순서로 불러오기
        List<User> users = new ArrayList<>();

        entries.forEach(e -> {
            long tempId = e.getUserId();
            User tempUser = userService.findById(tempId).orElse(null);
            users.add(tempUser);
        });

        model.addAttribute("users", users);
        model.addAttribute("entries", entries);
        model.addAttribute("site", "list");
        model.addAttribute("url", "manager");
        model.addAttribute("myId", playgroundId);
        return "list";
    }

}
