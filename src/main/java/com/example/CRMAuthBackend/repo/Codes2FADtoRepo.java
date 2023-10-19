package com.example.CRMAuthBackend.repo;

import com.example.CRMAuthBackend.dto.auth.Codes2FADto;
import org.springframework.data.repository.CrudRepository;

public interface Codes2FADtoRepo extends CrudRepository<Codes2FADto, Long> {
    Codes2FADto findByEmailAndCode(String email, String code);
}
