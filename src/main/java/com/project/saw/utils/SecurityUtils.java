package com.project.saw.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public interface SecurityUtils {
    static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
