package com.alex.restfulweb;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String full_name;
    private String email;
    private String password;
}
