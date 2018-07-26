package com.skaz.jpa;

import com.skaz.Constants;
import com.skaz.bean.Page;
import com.skaz.jpa.util.Querys;
import com.skaz.utils.Reflections;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author jungle
 */
@NoRepositoryBean
@Data
@Transactional(readOnly = true)
public class AbstractDao<E extends AbstractEntity, ID extends Serializable> extends SimpleJpaDao<E, ID> {

    protected static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";


    @Autowired
    protected JdbcSupport jdbcSupport;

    /**
     * 注入之后调用
     */
    @Override
    @PostConstruct
    public void init() {
        super.init();
        this.entityClass = Reflections.getClassGenricType(getClass());
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(this.entityClass, entityManager);
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
    }

    /**
     * 注入entityManager
     *
     * @param entityManager
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public <E> E getBySql(String sql, Class<E> entityClass) {
        return this.jdbcSupport.get(sql, entityClass);
    }

    public <E> E getBySql(String sql, Class<E> entityClass, Map<String, Object> filter) {
        return this.jdbcSupport.get(sql, entityClass, filter);
    }

    public E getBySql(String sql, Map<String, Object> params) {
        return this.jdbcSupport.get(sql, this.entityClass, params);
    }

    @Transactional
    public <S extends E> S create(S entity) {
        super.entityManager.persist(entity);
        return entity;
    }

    public E get(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        Class<E> domainType = getDomainClass();
        if (metadata == null) {
            return super.entityManager.find(domainType, id);
        }
        LockModeType type = metadata.getLockModeType();
        Map<String, Object> hints = getQueryHints().withFetchGraphs(entityManager).asMap();
        return type == null ? entityManager.find(domainType, id, hints) : entityManager.find(domainType, id, type, hints);
    }

    @Override
    @Transactional
    public <S extends E> S save(S entity) {
        return super.save(entity);
    }

    @Transactional
    public <S extends E> E update(S entity) {
        return this.entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void delete(E entity) {
        super.delete(entity);
    }

    public List<E> listBySql(String sql) {
        return this.jdbcSupport.list(sql, this.entityClass);
    }

    public List<E> listBySql(String sql, Map<String, Object> params) {
        return this.jdbcSupport.list(sql, this.entityClass, params);
    }

    public List<E> list() {
        return this.list(null, null, null);
    }

    public List<E> list(Map<String, Object> filter, Map<String, Object> like, Map<String, Object> sort) {
        return super.findAll(Querys.parseMapQuery(this.entityClass, filter, like), Querys.parseMapSort(sort));
    }

    public Page<E> pageBySql(String sql, int no, int size) {
        return this.jdbcSupport.page(sql, this.entityClass, no, size);
    }

    public Page<E> pageBySql(String sql, int no, int size, Map<String, Object> params) {
        return this.jdbcSupport.page(sql, this.entityClass, no, size, params);
    }

    public Page<E> page(int no, int size) {
        return this.page(null, no, size);
    }

    public Page<E> page(Map<String, Object> filter, int no, int size) {
        return this.page(filter, null, null, no, size);
    }

    public Page<E> page(Map<String, Object> filter, Map<String, Object> like, Map<String, Object> sort, int no, int size) {
        org.springframework.data.domain.Page<E> page;
        if (no < 1) {
            no = 1;
        }
        if (size == 0) {
            size = Constants.PAGE_SIZE;
        }
        int pageNo = no - 1;
        int pageSize = Math.abs(size);
        if (size > 0) {
            page = super.findAll(Querys.parseMapQuery(this.entityClass, filter, like), PageRequest.of(pageNo, pageSize, Querys.parseMapSort(sort)));
        } else {
            page = this.findAllWithoutCount(Querys.parseMapQuery(this.entityClass, filter, like), PageRequest.of(pageNo, pageSize, Querys.parseMapSort(sort)));
        }
        return new Page<>(page.getContent(), no, size, page.getTotalElements());
    }

    protected org.springframework.data.domain.Page<E> findAllWithoutCount(Specification<E> eSpecification, PageRequest pageable) {
        TypedQuery<E> query = getQuery(eSpecification, pageable);
        return isUnpaged(pageable) ? new PageImpl<E>(query.getResultList()) : readPageWithoutCount(query, getDomainClass(), pageable, eSpecification);
    }

    protected <S extends E> org.springframework.data.domain.Page<S> readPageWithoutCount(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable, @Nullable Specification<S> spec) {
        int pageNo = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        query.setFirstResult((int) pageable.getOffset());
        int expectedSize = pageable.getPageSize() + 1;
        query.setMaxResults(expectedSize);
        List<S> content = query.getResultList();
        int contentSize = content.size();
        if (contentSize == expectedSize) {
            content.remove(content.size() - 1);
        }
        long total = contentSize > 0 ? pageNo * pageSize + contentSize : 0;
        return new PageImpl<>(content, PageRequest.of(pageNo, pageSize), total);
    }

}
