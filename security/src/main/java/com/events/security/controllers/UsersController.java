package com.events.security.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.events.security.models.Role;
import com.events.security.models.User;
import com.events.security.models.UserProfile;
import com.events.security.repositories.RoleRepository;
import com.events.security.repositories.UserProfileRepository;
import com.events.security.repositories.UserRepository;
import com.events.security.services.EncryptionService;
import com.events.security.services.JSONResponsesService;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserRepository theUserRepository;
    private final RoleRepository theRoleRepository;
    private final UserProfileRepository theUserProfileRepository;
    private final EncryptionService encryptionService;
    private final JSONResponsesService jsonResponsesService;
    private static final String NOT_FOUND_USER = "No se encontro al usuario";

    @Autowired
    public UsersController(
            UserRepository theUserRepository,
            RoleRepository theRoleRepository,
            UserProfileRepository theUserProfileRepository,
            EncryptionService encryptionService,
            JSONResponsesService jsonResponsesService) {
        this.theUserRepository = theUserRepository;
        this.theRoleRepository = theRoleRepository;
        this.theUserProfileRepository = theUserProfileRepository;
        this.encryptionService = encryptionService;
        this.jsonResponsesService = jsonResponsesService;
    }

    @GetMapping("")
    public ResponseEntity<?> index() {
        try {
            List<User> users = this.theUserRepository.findAllProjectedBy();
            if (!users.isEmpty()) {
                this.jsonResponsesService.setData(users);
                this.jsonResponsesService.setMessage("Lista de Usuarios encontrada correctamente");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("No hay usuarios registrados");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor al buscar usuarios");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        try {
            User theUser = this.theUserRepository
                    .findById(id)
                    .orElse(null);
            if (theUser != null) {
                this.jsonResponsesService.setData(theUser);
                this.jsonResponsesService.setMessage("usuario encontrado con exito");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage(NOT_FOUND_USER);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor en la busqueda del usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @GetMapping("{id}/no_role")
    public ResponseEntity<?> showWithoutRole(@PathVariable String id) {
        try {
            User theUser = this.theUserRepository
                    .findById_noRole(id)
                    .orElse(null);
            if (theUser != null) {
                this.jsonResponsesService.setData(theUser);
                this.jsonResponsesService.setMessage("usuario encontrado con exito");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage(NOT_FOUND_USER);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor en la busqueda del usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> udpate(@PathVariable String id, @RequestBody User theNewUser) {
        try {
            User theActualUser = this.theUserRepository.findById(id).orElse(null);
            if (theActualUser != null) {
                if (!theActualUser.getEmail().equals(theNewUser.getEmail())
                        && this.theUserRepository.getUserByEmail(theNewUser.getEmail()).orElse(null) != null) {
                    this.jsonResponsesService.setMessage("Este correo ya le pertenece a otro usuario");
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(this.jsonResponsesService.getFinalJSON());

                } else {
                    theActualUser.setEmail(theNewUser.getEmail());
                    theActualUser.setPassword(encryptionService.convertirSHA256(theNewUser.getPassword()));
                    this.theUserRepository.save(theActualUser);
                    this.jsonResponsesService.setData(theActualUser);
                    this.jsonResponsesService.setMessage("Usuario actualizado");
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(this.jsonResponsesService.getFinalJSON());
                }
            } else {
                this.jsonResponsesService.setMessage("No se encontro al usuario a actualizar");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());

            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor en la actualizacion del usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> destroy(@PathVariable String id) {
        try {
            User theActualUser = this.theUserRepository.findById(id).orElse(null);
            if (theActualUser != null) {
                this.theUserRepository.delete(theActualUser);
                this.jsonResponsesService.setData(theActualUser);
                this.jsonResponsesService.setMessage("Se elimino correctamente al usuario");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("No se encontro al usuario a eliminar");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor en la eliminacion del usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }

    }

    @PutMapping("user/{user_id}/role/{role_id}")
    public ResponseEntity<?> matchUserRole(@PathVariable String user_id,
            @PathVariable String role_id) {
        try {
            User theActualUser = this.theUserRepository.findById(user_id)
                    .orElse(null);
            Role theActualRole = this.theRoleRepository.findById(role_id)
                    .orElse(null);
            if (theActualUser != null && theActualRole != null) {
                theActualUser.setRole(theActualRole);
                this.theUserRepository.save(theActualUser);
                this.jsonResponsesService.setData(theActualUser);
                this.jsonResponsesService.setMessage("Se añadio correctamente el rol al usuario");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                if (theActualUser == null) {
                    this.jsonResponsesService.setMessage(NOT_FOUND_USER);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                } else {
                    this.jsonResponsesService.setMessage("No se encontro el rol");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                }
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor al añadir el rol al usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @PutMapping("user/{user_id}/role")
    public ResponseEntity<?> unMatchUserRole(@PathVariable String user_id) {
        try {
            User theActualUser = this.theUserRepository.findById(user_id)
                    .orElse(null);
            if (theActualUser != null) {
                theActualUser.setRole(null);
                this.theUserRepository.save(theActualUser);
                this.jsonResponsesService.setData(theActualUser);
                this.jsonResponsesService.setMessage("Se removio correctamente el rol al usuario");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage(NOT_FOUND_USER);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor al remover el rol al usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @PutMapping("user/{user_id}/user_profile/{user_profile_id}")
    public ResponseEntity<?> matchUserProfile(@PathVariable String user_id,
            @PathVariable String user_profile_id) {
        try {
            User theActualUser = this.theUserRepository.findById(user_id)
                    .orElse(null);
            UserProfile theUserProfile = this.theUserProfileRepository.findById(user_profile_id)
                    .orElse(null);
            if (theActualUser != null && theUserProfile != null) {
                theActualUser.setUserProfile(theUserProfile);
                this.theUserRepository.save(theActualUser);
                this.jsonResponsesService.setData(theActualUser);
                this.jsonResponsesService.setMessage("Se añadio correctamente el perfil al usuario");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                if (theActualUser == null) {
                    this.jsonResponsesService.setMessage(NOT_FOUND_USER);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                } else {
                    this.jsonResponsesService.setMessage("No se encontro el perfil");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                }
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor al añadir el perfil al usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @PutMapping("user/{user_id}/user_profile")
    public ResponseEntity<?> unMatchUserProfile(@PathVariable String user_id) {
        try {
            User theActualUser = this.theUserRepository.findById(user_id)
                    .orElse(null);
            if (theActualUser != null) {
                theActualUser.setUserProfile(null);
                this.theUserRepository.save(theActualUser);
                this.jsonResponsesService.setData(theActualUser);
                this.jsonResponsesService.setMessage("Se removio correctamente el perfil al usuario");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage(NOT_FOUND_USER);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor al remover el perfil al usuario");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

}