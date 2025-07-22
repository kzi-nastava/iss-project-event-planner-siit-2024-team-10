package com.ftn.iss.eventPlanner.dto.company;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompanyPhotosDTO {
    List<String> filePaths;
}
