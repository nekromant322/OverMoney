package com.overmoney.actuator_git_info.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "app.git")
public class InfoGitProperties {

    private String branch;
    private Commit commit;

    @Getter
    @Setter
    public static class Commit {
        private String id;
        private String time;
        private User user;

        @Getter
        @Setter
        public static class User {
            private String name;
            private String email;
        }
    }
}