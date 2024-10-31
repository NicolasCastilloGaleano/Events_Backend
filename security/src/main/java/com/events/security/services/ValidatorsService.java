package com.events.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.events.security.models.Permission;
import com.events.security.models.Role;
import com.events.security.models.User;
import com.events.security.repositories.PermissionRepository;
import com.events.security.repositories.UserRepository;

import java.util.logging.Logger;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ValidatorsService {
    private JwtService jwtService;
    private PermissionRepository thePermissionRepository;
    private UserRepository theUserRepository;
    Logger logger = Logger.getLogger(getClass().getName());
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    public ValidatorsService(
            JwtService jwtService,
            PermissionRepository thePermissionRepository,
            UserRepository theUserRepository) {
        this.jwtService = jwtService;
        this.thePermissionRepository = thePermissionRepository;
        this.theUserRepository = theUserRepository;
    }

    public boolean validationRolePermission(HttpServletRequest request, String url, String method) {
        if (url.equals("/roles/656021611ae5d15c7d6d2517") && method.equals("DELETE")) {
            return false;
        }
        User theUser = this.getUser(request);
        if (theUser != null) {
            Role theRole = theUser.getRole();
            url = url.replaceAll("[0-9a-fA-F]{24}", "?");
            url = url.replaceAll("\\d+", "?");
            Permission thePermission = thePermissionRepository.getPermission(url,
                    method).orElse(null);

            if (theRole != null && thePermission != null) {
                for (Permission permission : theRole.getTotalPermissions()) {
                    if (permission.equals(thePermission)) {
                        return true;
                    }
                }
            } else {
                logger.info("no tiene este permiso");
                return false;
            }

        }
        logger.info("el usuario no existe");
        return false;
    }

    public User getUser(final HttpServletRequest request) {
        User theUser = null;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            Boolean isValid = jwtService.validateToken(token);
            if (Boolean.TRUE.equals(isValid)) {
                User theUserFromToken = jwtService.getUserFromToken(token);
                if (theUserFromToken != null) {
                    theUser = this.theUserRepository.findById(theUserFromToken.getId())
                            .orElse(null);
                    theUser.setPassword("");

                }
            }
        }
        return theUser;
    }

}
