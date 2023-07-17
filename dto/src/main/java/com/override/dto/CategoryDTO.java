package com.override.dto;

import com.override.dto.constants.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class
CategoryDTO implements Comparable<CategoryDTO> {
    private Long id;
    private String name;
    private Type type;
    private List<KeywordIdDTO> keywords;

    @Override
    public int compareTo(CategoryDTO o) {
        return name.compareTo(o.getName());
    }
}
