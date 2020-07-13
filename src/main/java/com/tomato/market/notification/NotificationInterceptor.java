package com.tomato.market.notification;

import com.tomato.market.account.UserAccount;
import com.tomato.market.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {  //인터셉터 적용하기 위해선 WebConfig에 WebMvc 설정해야함

    private final NotificationRepository notificationRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();   //인증 정보가 있는 요청에 대한 응답에만 실행해야하므로 ContextHolder의 Context에서 Authentication 가져옴
        if (modelAndView != null && !isRedirectView(modelAndView) && authentication != null && authentication.getPrincipal() instanceof UserAccount) {  //ModelAndView를 쓰는경우만 모델에 넣어줌, 리다이렉트뷰가 아니어야함, authentication이 있어야하고 타입이 UserAccount여야 함
            Account account = ((UserAccount)authentication.getPrincipal()).getAccount();   //principal을 꺼내서 UserAccount 타입으로 캐스팅한 뒤 Account(현재 유저정보)를 꺼낸다.
            long count = notificationRepository.countByAccountAndChecked(account, false);  //account에서 읽지 않은(false) 알림의 갯수 가져옴
            modelAndView.addObject("hasNotification", count > 0);  // hasNotification 이라는 모델의 이름으로 true(count>0)를 넣어줌
            modelAndView.addObject("numberOfNotChecked", count);
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}
