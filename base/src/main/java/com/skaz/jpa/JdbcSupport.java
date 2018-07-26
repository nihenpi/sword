package com.skaz.jpa;

import com.skaz.bean.Page;
import com.skaz.jpa.util.Sqls;
import com.skaz.utils.Lists;
import com.skaz.utils.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author jungle
 */
@Component
public class JdbcSupport {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public <E> E get(String sql, Class<E> entityClass) {
        List<E> es = this.jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(entityClass));
        return es.size() > 0 ? es.get(0) : null;
    }

    public <E> E get(String sql, Class<E> entityClass, Map<String, Object> params) {
        List<E> es = this.namedParameterJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(entityClass));
        return es.size() > 0 ? es.get(0) : null;
    }

    public <E> List<E> list(String sql, Class<E> entityClass, Map<String, Object> params) {
        return this.namedParameterJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(entityClass));
    }

    public <E> List<E> list(String sql, Class<E> entityClass) {
        return this.jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(entityClass));
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int no, int size) {
        return this.page(sql, entityClass, no, size, Maps.newHashMap());
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int no, int size, Map<String, Object> params) {
        boolean hasCount = size > 0;
        if (no < 1) {
            no = 1;
        }
        if (size <= 0) {
            throw new IllegalArgumentException("参数错误:pageSize不能为0");
        }
        int pageNo = no - 1;
        int pageSize = Math.abs(size);
        long total;
        List<E> entities = Lists.newArrayList();
        if (hasCount) {
            total = this.count(sql, params);
            if (total > 0) {
                entities = this.list(Sqls.buildPageSql(sql, pageNo, pageSize, hasCount), entityClass, params);
            }
        } else {
            // 多查一条数据以判断是否有下一页
            int expectedSize = pageSize + 1;
            entities = this.list(Sqls.buildPageSql(sql, pageNo, expectedSize, hasCount), entityClass, params);
            int contentSize = entities.size();
            if (contentSize == expectedSize) {
                // 去掉多查询的一条结果
                entities.remove(entities.size() - 1);
            }
            // 若未查询到结果,则总数为0;
            total = contentSize > 0 ? (pageNo * pageSize) + contentSize : 0;
        }
        // 可传递正负数,以让Page对象区分是否为分页查询.
        return new Page<>(entities, no, size, total);
    }

    public long count(String sql, Map<String, Object> params) {
        return this.namedParameterJdbcTemplate.queryForObject(Sqls.buildCountSql(sql), params, long.class);
    }
}
