package com.shoppingmall.web;

import com.shoppingmall.config.auth.attribute.SessionMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final HttpSession session;

    @GetMapping("/member/loginForm")
    public String loginForm(Model model, Authentication authentication) {
        SessionMember sessionMember = (SessionMember) session.getAttribute("LOGIN_SESSION_USER");
        if (sessionMember != null) {
            model.addAttribute("name", sessionMember.getName());
            model.addAttribute("email", sessionMember.getEmail());
        }
        return "member/loginForm";
    }

    @GetMapping("/member/joinForm")
    public String joinForm(Model model) {
        return "member/joinForm";
    }

    @GetMapping("/member/access-denied")
    public String accessDenied(Model model) {
        return "member/accessDenied";
    }
}
