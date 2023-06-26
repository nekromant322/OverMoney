package com.override.dto;

import com.override.dto.constants.StatusMailing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailDTO {
    private StatusMailing statusMail;
    private Long countOfMails;
}
