package com.skaz.jpa;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skaz.bean.BaseBean;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author jungle
 */
public abstract class AbstractEntity<ID extends Serializable> extends BaseBean implements Serializable {


    public abstract ID getId();

    @Transient
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        AbstractEntity<?> that = (AbstractEntity<?>) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());

    }
}
