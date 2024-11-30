package com.events.security.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.events.security.models.UserProfile;
import com.events.security.repositories.UserProfileRepository;
import com.events.security.services.JSONResponsesService;

@CrossOrigin
@RestController
@RequestMapping("/profiles")
public class UserProfileController {
    private final UserProfileRepository theUserprofileRepository;
    private final JSONResponsesService jsonResponsesService;

    @Autowired
    public UserProfileController(
            UserProfileRepository theUserprofileRepository,
            JSONResponsesService jsonResponsesService) {
        this.theUserprofileRepository = theUserprofileRepository;
        this.jsonResponsesService = jsonResponsesService;
    }

    @GetMapping("")
    public ResponseEntity<?> index() {
        try {
            if (this.theUserprofileRepository.findAll() != null
                    && !this.theUserprofileRepository.findAll().isEmpty()) {
                List<UserProfile> userProfiles = this.theUserprofileRepository.findAll();
                this.jsonResponsesService.setData(userProfiles);
                this.jsonResponsesService.setMessage("listado de Perfiles encontrado correctamente");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("No hay perfiles registrados");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor al buscar perfiles");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ResponseEntity<?> store(@RequestBody UserProfile userProfile) {
        try {
            this.theUserprofileRepository.save(userProfile);
            this.jsonResponsesService.setMessage("Perfil agregado con éxito");
            this.jsonResponsesService.setData(userProfile);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.jsonResponsesService.getFinalJSON());

        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor al intentar crear el perfil");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        try {
            UserProfile theUserProfile = this.theUserprofileRepository
                    .findById(id)
                    .orElse(null);
            if (theUserProfile != null) {
                this.jsonResponsesService.setData(theUserProfile);
                this.jsonResponsesService.setMessage("Perfil encontrado con exito");
                return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("No se encontro el perfil");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor en la busqueda del perfil");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UserProfile theNewProfile) {
        try {
            UserProfile theProfile = this.theUserprofileRepository.findById(id).orElse(null);
            if (theProfile != null) {
                theProfile.setName(theNewProfile.getName());
                theProfile.setProfilePhoto(theNewProfile.getProfilePhoto());
                this.theUserprofileRepository.save(theProfile);
                this.jsonResponsesService.setData(theProfile);
                this.jsonResponsesService.setMessage("Perfil actualizado");
                return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());

            } else {
                this.jsonResponsesService.setMessage("No se encontro al perfil a actualizar");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor en la actualizacion del perfil");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public ResponseEntity<?> destroy(@PathVariable String id) {
        try {
            UserProfile theProfile = this.theUserprofileRepository.findById(id).orElse(null);
            if (theProfile != null) {
                this.theUserprofileRepository.delete(theProfile);
                this.jsonResponsesService.setData(theProfile);
                this.jsonResponsesService.setMessage("Se elimino correctamente al perfil");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("No se encontro al perfil a eliminar");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error del servidor en la eliminacion del perfil");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }
}