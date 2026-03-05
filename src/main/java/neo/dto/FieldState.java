package neo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;;

@JsonInclude(Include.NON_NULL)
public class FieldState {

    public String value = null;
    public String status = null;
    public String modified = null;

    public FieldState(String value) {
        this.value = value;
    }

    public FieldState(String value, String status) {
        this.value = value;
        this.status = status;
    }

    public FieldState(String value, String status, String modified) {
        this.value = value;
        this.status = status;
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "FieldState [value=" + value + ", status=" + status + ", modified=" + modified + "]";
    }
}