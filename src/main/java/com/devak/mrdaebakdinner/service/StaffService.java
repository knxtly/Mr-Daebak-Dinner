package com.devak.mrdaebakdinner.service;

import com.devak.mrdaebakdinner.dto.StaffSessionDTO;
import com.devak.mrdaebakdinner.exception.IncorrectPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffService {
    public StaffSessionDTO login(String inputPassword) {
        if (!inputPassword.equals("staff")) {
            throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
        }

        StaffSessionDTO staffSessionDTO = new StaffSessionDTO();
        staffSessionDTO.setPosition("chef");
        return staffSessionDTO;
    }
}
