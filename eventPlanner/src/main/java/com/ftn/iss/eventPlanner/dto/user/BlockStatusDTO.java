package com.ftn.iss.eventPlanner.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockStatusDTO {
    boolean blocked;
    public BlockStatusDTO(boolean blocked) {this.blocked = blocked;}
}
