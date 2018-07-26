package com.skaz.jpa.util;

import com.skaz.utils.Casts;
import com.skaz.utils.Lists;
import com.skaz.utils.Reflections;
import com.skaz.utils.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.persistence.EnumType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 用于sql条件查询转换的工具类
 *
 * @author jungle
 */
public class Querys {

    /**
     * 将map的查询条件解析为Specification
     *
     * @param entityClass
     * @param filter
     * @param like
     * @param <E>
     * @return
     */
    public static <E> Specification<E> parseMapQuery(Class<E> entityClass, Map<String, Object> filter, Map<String, Object> like) {
        //返回一个实现Specification的匿名类的对象
        return (Specification<E>) (root, query, builder) -> {
            List<Predicate> predicates = Lists.newArrayList();
            //判定非like的条件
            if (filter != null && !filter.isEmpty()) {
                Iterator<Map.Entry<String, Object>> it = filter.entrySet().iterator();
                while (it.hasNext()) {
                    Entry entry = it.next();
                    String key = (String) entry.getKey();
                    Object value = convertStringToFieldType(entityClass, key, entry.getValue());
                    boolean isValueNull = value == null || Strings.isNullOrEmpty(value);
                    Path expression;
                    if (Strings.indexOfAny(key, ">=", "<=", ">", "<") > 0) {
                        if (isValueNull) {
                            continue;
                        }
                        if (key.indexOf(">=") > 0) {
                            expression = root.get(key.replace(">=", ""));
                            predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) value));
                            continue;
                        }
                        if (key.indexOf("<=") > 0) {
                            expression = root.get(key.replace("<=", ""));
                            predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) value));
                            continue;
                        }
                        if (key.indexOf(">") > 0) {
                            expression = root.get(key.replace(">", ""));
                            predicates.add(builder.greaterThan(expression, (Comparable) value));
                            continue;
                        }
                        if (key.indexOf("<") > 0) {
                            expression = root.get(key.replace("<", ""));
                            predicates.add(builder.lessThan(expression, (Comparable) value));
                            continue;
                        }


                    } else if (key.indexOf("!") > 0) {
                        expression = root.get(Strings.remove(key.trim(), "!", "="));
                        if (isValueNull) {
                            predicates.add(builder.isNotNull(expression));
                        } else {
                            predicates.add(builder.notEqual(expression, value));
                        }
                        continue;

                    } else {
                        expression = root.get(key);
                        if (isValueNull) {
                            predicates.add(builder.isNull(expression));
                        } else {
                            predicates.add(builder.equal(expression, value));
                        }
                        continue;
                    }
                }
            }
            //判定like条件
            if (like != null && !like.isEmpty()) {
                Iterator it = like.entrySet().iterator();
                while (it.hasNext()) {
                    Entry entry = (Entry) it.next();
                    String key = (String) entry.getKey();
                    String value = (String) convertStringToFieldType(entityClass, key, entry.getValue());
                    if (value == null) {
                        continue;
                    } else {
                        if (Strings.isNotBlank(value)) {
                            Path expression = root.get(key);
                            if (Strings.isContains(value, "%")) {
                                predicates.add(builder.like(expression, value));
                            } else {
                                predicates.add(builder.like(expression, "%" + value + "%"));
                            }

                        }
                    }
                }
            }
            //联合所有条件
            if (predicates.size() > 0) {
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }

            return builder.conjunction();
        };
    }

    /**
     * 根据实体类字段的类型转换类型
     *
     * @param entityClass
     * @param key
     * @param value
     * @param <E>
     * @return
     */
    private static <E> Object convertStringToFieldType(Class<E> entityClass, String key, Object value) {
        if (value instanceof String) {
            String valueString = String.valueOf(value).trim();
            Class<?> field_type = Reflections.getField(entityClass, key).getType();
            value = Casts.to(valueString, field_type);
        }
        return value;
    }

    /**
     * 将排序的map转换为Sort类型
     * @param sort
     * @return
     */
    public static Sort parseMapSort(Map<String, Object> sort) {
        org.springframework.data.domain.Sort sorter = org.springframework.data.domain.Sort.unsorted();
        List<org.springframework.data.domain.Sort> sorters = Lists.newArrayList();
        if (sort != null && !sort.isEmpty()) {
            for (Entry<String, ?> entry : sort.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                if (Strings.isNotNullOrEmpty(value)) {
                    sorters.add(new org.springframework.data.domain.Sort(Sort.Direction.ASC, key));
                } else {
                    if (value.trim().toLowerCase().equals("asc")) {
                        sorters.add(new org.springframework.data.domain.Sort(Sort.Direction.ASC, key));
                    } else {
                        sorters.add(new org.springframework.data.domain.Sort(Sort.Direction.DESC, key));
                    }
                }
            }
            for (int i = 0; i < sorters.size(); i++) {
                sorter = sorter.and(sorters.get(i));
            }
        }
        return sorter;
    }

}
