package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.LoginCredentialsType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginCredentials {
    private String username;
    private String password;

    public LoginCredentials(String username, String password, LoginCredentialsType type) {
        if (type == LoginCredentialsType.TC3) {
            if (!username.contains("@qsl.com")) {
                username = username + "@qsl.com";
            }
        } else {
            if (username.contains("@qsl.com")) {
                username = username.substring(0, username.indexOf("@qsl.com"));
            }
        }
        this.username = username;

        this.password = password;
    }
}
