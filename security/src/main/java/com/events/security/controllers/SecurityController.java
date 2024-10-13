package com.events.security.controllers;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.events.security.models.Permission;
import com.events.security.models.Role;
import com.events.security.models.User;
import com.events.security.repositories.RoleRepository;
import com.events.security.repositories.UserRepository;
import com.events.security.services.EncryptionService;
import com.events.security.services.JSONResponsesService;
import com.events.security.services.JwtService;
import com.events.security.services.ValidatorsService;

@CrossOrigin
@RestController
@RequestMapping("/security")
public class SecurityController {
    private final UserRepository theUserRepository;
    private final RoleRepository theRoleRepository;
    private final JwtService jwtService;
    private final EncryptionService encryptionService;
    private final ValidatorsService validatorService;
    private final JSONResponsesService jsonResponsesService;

    @Autowired
    public SecurityController(
            UserRepository theUserRepository,
            RoleRepository theRoleRepository,
            JwtService jwtService,
            EncryptionService encryptionService,
            ValidatorsService validatorService,
            JSONResponsesService jsonResponsesService) {
        this.theUserRepository = theUserRepository;
        this.theRoleRepository = theRoleRepository;
        this.jwtService = jwtService;
        this.encryptionService = encryptionService;
        this.validatorService = validatorService;
        this.jsonResponsesService = jsonResponsesService;
    }

    // Método login
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User theUser) {
        try {
            String token = "";
            User actualUser = this.theUserRepository.getUserByEmail(theUser.getEmail()).orElse(null);
            if (actualUser != null
                    && actualUser.getPassword().equals(encryptionService.convertirSHA256(theUser.getPassword()))) {
                // Generar token
                token = jwtService.generateToken(actualUser);
                this.jsonResponsesService.setMessage("Inicio de Sesion Correcto");
                this.jsonResponsesService.setData(token);
                return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("Inicio de sesion incorrecto");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setMessage("Error al intentar iniciar sesion");
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@RequestBody User newUser) {
        try {
            User theActualUser = this.theUserRepository.getUserByEmail(newUser.getEmail()).orElse(null);
            if (theActualUser != null) {
                this.jsonResponsesService.setMessage("Ya existe un usuario con este correo");
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                newUser.setPassword(encryptionService.convertirSHA256(newUser.getPassword()));
                Role defaultRole = this.theRoleRepository.getRole("default").orElse(null);
                newUser.setRole(defaultRole);
                this.theUserRepository.save(newUser);
                this.jsonResponsesService.setMessage("Usuario creado con exito");
                this.jsonResponsesService.setData(newUser);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setMessage("Error al intentar crear el usuario");
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    // Método logout
    // Método reset pass
    @GetMapping("get-user")
    public ResponseEntity<?> getUser(final HttpServletRequest request) {
        try {
            User theUser = this.validatorService.getUser(request);
            if (theUser != null) {
                this.jsonResponsesService.setMessage("Token valido");
                this.jsonResponsesService.setData(theUser);
                return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("Token no valido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setMessage("Error al validar el token");
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @PostMapping("permissions-validation")
    public ResponseEntity<?> permissionsValidation(final HttpServletRequest request,
            @RequestBody Permission thePermission) {
        try {
            boolean success = this.validatorService.validationRolePermission(request, thePermission.getRoute(),
                    thePermission.getMethod());
            if (success) {
                this.jsonResponsesService.setMessage("permiso valido");
                return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("permiso no valido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setMessage("Error al validar el permiso");
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }
}
