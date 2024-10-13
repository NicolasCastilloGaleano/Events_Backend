package com.events.security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.events.security.models.Permission;
import com.events.security.models.Role;
import com.events.security.repositories.PermissionRepository;
import com.events.security.repositories.RoleRepository;
import com.events.security.services.JSONResponsesService;

import java.util.Arrays;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("roles")

public class RolesController {
    private final RoleRepository theRoleRepository;
    private final PermissionRepository thePermissionRepository;
    private final JSONResponsesService jsonResponsesService;

    @Autowired
    public RolesController(RoleRepository theRoleRepository,
            PermissionRepository thePermissionRepository, JSONResponsesService jsonResponsesService) {
        this.theRoleRepository = theRoleRepository;
        this.thePermissionRepository = thePermissionRepository;
        this.jsonResponsesService = jsonResponsesService;
    }

    @GetMapping("")
    public ResponseEntity<?> index() {
        try {
            if (this.theRoleRepository.findAll() != null && !this.theRoleRepository.findAll().isEmpty()) {
                List<Role> roles = this.theRoleRepository.findAll();
                this.jsonResponsesService.setData(roles);
                this.jsonResponsesService.setMessage("Listado de roles encontrado con exito");
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
            this.jsonResponsesService.setMessage("Error al buscar roles");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public ResponseEntity<?> store(@RequestBody Role newRole) {
        try {
            Role theActualRole = this.theRoleRepository.getRole(newRole.getName()).orElse(null);
            if (theActualRole != null) {
                this.jsonResponsesService.setMessage("Ya existe un rol con este nombre");
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.theRoleRepository.save(newRole);
                this.jsonResponsesService.setData(newRole);
                this.jsonResponsesService.setMessage("Rol agregado con éxito");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error al intentar crear el rol");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        try {
            Role theRole = this.theRoleRepository
                    .findById(id)
                    .orElse(null);
            if (theRole != null) {
                this.jsonResponsesService.setMessage("Rol encontrado con exito");
                this.jsonResponsesService.setData(theRole);
                return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("No se encontro al rol");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error en la busqueda del rol");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> udpate(@PathVariable String id, @RequestBody Role theNewRole) {
        try {
            Role theRole = this.theRoleRepository.findById(id).orElse(null);
            if (theRole != null) {
                if (!theRole.getName().equals(theNewRole.getName())
                        && this.theRoleRepository.getRole(theNewRole.getName()).orElse(null) != null) {
                    this.jsonResponsesService.setMessage("Ya existe un rol con este nombre");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(this.jsonResponsesService.getFinalJSON());
                } else {
                    theRole.setName(theNewRole.getName());
                    theRole.setDescription(theNewRole.getDescription());
                    theRole.setStatus(theNewRole.getStatus());
                    this.theRoleRepository.save(theRole);
                    this.jsonResponsesService.setMessage("Rol actualizado");
                    this.jsonResponsesService.setData(theRole);
                    return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());

                }
            } else {
                this.jsonResponsesService.setMessage("No se encontro al rol a actualizar");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());

            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error en la actualizacion del rol");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public ResponseEntity<?> destroy(@PathVariable String id) {
        try {
            Role theRole = this.theRoleRepository.findById(id).orElse(null);
            if (theRole != null) {
                this.theRoleRepository.delete(theRole);
                this.jsonResponsesService.setData(theRole);
                this.jsonResponsesService.setMessage("Se elimino correctamente el rol");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(this.jsonResponsesService.getFinalJSON());
            } else {
                this.jsonResponsesService.setMessage("No se encontro al rol a eliminar");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error en la eliminacion del perfil");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @PutMapping("role/{role_id}/permission/{permission_id}")
    public ResponseEntity<String> addPermissions(@PathVariable String role_id, @PathVariable String permission_id) {
        try {
            Role theActualRole = this.theRoleRepository.findById(role_id).orElse(null);
            Permission theActualPermission = this.thePermissionRepository.findById(permission_id).orElse(null);
            if (theActualRole != null && theActualPermission != null) {
                if (theActualRole.getTotalPermissions() != null && Arrays.stream(theActualRole.getTotalPermissions())
                        .anyMatch(existingPermission -> existingPermission.getId()
                                .equals(theActualPermission.getId()))) {
                    this.jsonResponsesService.setMessage("Este permiso ya existe en este rol.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(this.jsonResponsesService.getFinalJSON());
                } else {
                    theActualRole.addPermission(theActualPermission);
                    this.theRoleRepository.save(theActualRole);
                    this.jsonResponsesService.setData(theActualRole);
                    this.jsonResponsesService.setMessage("Permiso agregado con éxito");
                    return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());
                }
            } else {
                if (theActualRole == null) {
                    this.jsonResponsesService.setMessage("Rol no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                } else {
                    this.jsonResponsesService.setMessage("Permiso no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                }
            }

        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error al añadir el permiso al rol");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("role/{role_id}/permission/{permission_id}")
    public ResponseEntity<?> removeRolePermission(@PathVariable String role_id, @PathVariable String permission_id) {
        try {
            Role theActualRole = this.theRoleRepository.findById(role_id).orElse(null);
            Permission theActualPermission = thePermissionRepository.findById(permission_id).orElse(null);
            if (theActualRole != null && theActualPermission != null) {
                theActualRole.removePermission(theActualPermission);
                this.theRoleRepository.save(theActualRole);
                this.jsonResponsesService.setMessage("Permiso removido con éxito");
                this.jsonResponsesService.setData(theActualRole);
                return ResponseEntity.status(HttpStatus.OK).body(this.jsonResponsesService.getFinalJSON());
            } else {
                if (theActualRole == null) {
                    this.jsonResponsesService.setMessage("Rol no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                } else {
                    this.jsonResponsesService.setMessage("Permiso no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.jsonResponsesService.getFinalJSON());
                }
            }
        } catch (Exception e) {
            this.jsonResponsesService.setData(null);
            this.jsonResponsesService.setError(e.toString());
            this.jsonResponsesService.setMessage("Error al remover el permiso al rol");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(this.jsonResponsesService.getFinalJSON());
        }

    }
}