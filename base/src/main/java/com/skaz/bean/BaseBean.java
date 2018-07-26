package com.skaz.bean;

import com.skaz.utils.Jsons;
import com.skaz.utils.Maps;
import org.apache.commons.lang3.builder.*;

import java.io.Serializable;
import java.util.Map;

/**
 * @author jungle
 */
public abstract class BaseBean implements Serializable {

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public boolean equals(Object o, String... excludeFields) {
        return EqualsBuilder.reflectionEquals(this, o, excludeFields);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public int compareTo(Object o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }

    public int compareTo(Object o, String... excludeFields) {
        return CompareToBuilder.reflectionCompare(this, o, excludeFields);
    }

    public String toJSONString() {
        return Jsons.toJSONString(this);
    }

    public Map<String, Object> copyPropertiesToMap() {
        return Maps.newHashMap(this);
    }


}

