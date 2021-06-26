package com.isagron.security.services.validators;

import com.isagron.security.domain.dtos.RoleDto;
import com.isagron.security.domain.repositories.AuthorityRepository;
import com.isagron.security.domain.repositories.RoleRepository;
import com.isagron.security.exceptions.AppErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CreateRoleValidator implements OperationValidator {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthorityRepository authorityRepository;
    @Override
    public AppOperation getOperation() {
        return AppOperation.CREATE_ROLE;
    }

    @Override
    public void validate(Object target, Errors errors) {
        RoleDto role = (RoleDto) target;
        if (roleRepository.existsByName(role.getName())){
            errors.rejectValue(RoleDto.Fields.name,
                    AppErrorCode.ROLE_ALREADY_EXIST.code(),
                    String.format(AppErrorCode.ROLE_ALREADY_EXIST.defaultMessageFormat(), role.getName()));
        }

        AtomicInteger index = new AtomicInteger(0);
        role.getAuthorities().forEach(authName -> {
            if (!this.authorityRepository.existsByName(authName))
            {
                errors.rejectValue(RoleDto.Fields.authorities + "[" + index.getAndIncrement() + "]",
                        AppErrorCode.AUTHORITY_NOT_EXIST.code(),
                        String.format(AppErrorCode.AUTHORITY_NOT_EXIST.defaultMessageFormat(), authName));
            }
        });

    }
}
