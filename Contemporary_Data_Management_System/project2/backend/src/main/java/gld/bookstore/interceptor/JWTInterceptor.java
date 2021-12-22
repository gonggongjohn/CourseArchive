package gld.bookstore.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.utils.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=request.getHeader("token");
        Message message=new Message();
        try{
            JWTUtils.verify(token);
            message.setMessage("ok");
            return true;
        }catch (Exception e){
            message.setMessage("token错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(message));
            return false;
        }
    }
}
