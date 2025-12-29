package webtech.online.course.services;

import jakarta.transaction.Transactional;
import webtech.online.course.models.Role;

public interface RoleService {
    public Role findOrCreateRole(String name);
}
