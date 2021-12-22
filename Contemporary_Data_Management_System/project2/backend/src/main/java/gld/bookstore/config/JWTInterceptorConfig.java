package gld.bookstore.config;

import gld.bookstore.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JWTInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/register",
                        "/auth/unregister",
                        "/auth/login",
                        "/auth/password",
                        "/buyer/payment",
                        "/buyer/add_funds"
                );
    }
}
