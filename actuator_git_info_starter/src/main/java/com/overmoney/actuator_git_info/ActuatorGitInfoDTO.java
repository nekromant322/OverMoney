package com.overmoney.actuator_git_info;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActuatorGitInfoDTO {

    public String username;
    public String branch;
    public String commit;
    public String commitTime;
    public LocalDateTime applicationStartUpTime;
}