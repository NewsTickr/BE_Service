package com.newstickr.newstickr.user.dto;

import com.newstickr.newstickr.user.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String role;
    private String name;
    private String username;
    private String id;
}