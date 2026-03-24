package neo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import neo.enums.Status;;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder(value = { "old", "upd", "sts" })
public class TagState {

    public String old = null;
    public String upd = null;
    public String sts = null;

    public TagState() {
        
    }

    public TagState(String old, String upd, String sts) {
        this.old = old;
        this.upd = upd;
        this.sts = sts;
    }

    public TagState(String old, Status sts) {
        this.old = old;
        this.sts = sts.toString();
    }

    public TagState(String old, String upd) {
        this.old = old;
        this.upd = upd;
    }

    public TagState(String old) {
        this.old = old;
    }

    @Override
    public String toString() {
        return "TagState [old=" + old + ", upd=" + upd + ", sts=" + sts + "]";
    }
}