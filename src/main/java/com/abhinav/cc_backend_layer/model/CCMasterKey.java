package com.abhinav.cc_backend_layer.model;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class CCMasterKey {
	private String code;
	private String stmtMonthYear;
	private String username;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CCMasterKey that = (CCMasterKey) o;
        return Objects.equals(stmtMonthYear, that.stmtMonthYear) && 
               Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stmtMonthYear, username);
    }

}
