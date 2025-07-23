package com.ftn.iss.eventPlanner.dto.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UpdatedCompanyPhotosDTO {
    List<String> filePaths;
}
