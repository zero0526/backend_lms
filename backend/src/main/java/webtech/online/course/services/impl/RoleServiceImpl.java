package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webtech.online.course.models.Role;
import webtech.online.course.repositories.RoleRepository;
import webtech.online.course.services.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository= roleRepository;
    }

    @Transactional
    @Override
    public Role findOrCreateRole(String name) {
        return roleRepository.findByName(name)
                .orElseGet(()->{
                    Role role= new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }
}
