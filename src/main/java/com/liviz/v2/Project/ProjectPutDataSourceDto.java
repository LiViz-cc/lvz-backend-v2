package com.liviz.v2.Project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectPutDataSourceDto {

    @NotNull
    List<String> dataSourceIds;
}
