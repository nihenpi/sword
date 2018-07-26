package com.skaz.jpa;

import com.skaz.jpa.support.DefaultQueryHints;
import com.skaz.jpa.support.QueryHints;
import com.skaz.utils.Reflections;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * @author jungle
 */
@Repository
@Transactional(readOnly = true)
public class SimpleJpaDao<E, ID> implements JpaRepository<E, ID>, JpaSpecificationExecutor<E> {


    protected Class<E> entityClass;
    protected JpaEntityInformation<E, ?> entityInformation;
    protected EntityManager entityManager;
    protected PersistenceProvider provider;
    @Nullable
    protected CrudMethodMetadata metadata;


    public void init() {
        this.entityClass = Reflections.getClassGenricType(getClass());
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(this.entityClass, entityManager);
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
    }


    @Override
    @Transactional
    public <S extends E> S save(S entity) {
        if (entityInformation.isNew(entity)) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    public Optional findById(Object o) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Object o) {
        return false;
    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public List findAll(Sort sort) {
        return null;
    }

    @Override
    public Page findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List findAllById(Iterable iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Object o) {

    }

    @Override
    @Transactional
    public void delete(E entity) {
        Assert.notNull(entity, "The entity must not be null!");
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }

    @Override
    public void deleteAll(Iterable iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void flush() {

    }

    @Override
    public void deleteInBatch(Iterable iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public E getOne(Object o) {
        return null;
    }

    @Override
    public List findAll(Example example, Sort sort) {
        return null;
    }

    @Override
    public List findAll(Example example) {
        return null;
    }

    @Override
    public E saveAndFlush(Object o) {
        return null;
    }

    @Override
    public List saveAll(Iterable iterable) {
        return null;
    }

    @Override
    public Optional findOne(Specification specification) {
        return Optional.empty();
    }

    /**
     * 动态查询 不分页
     *
     * @param specification
     * @return
     */
    @Override
    public List findAll(Specification specification) {
        return null;
    }


    /**
     * 动态查询 分页
     *
     * @param specification
     * @param pageable
     * @return
     */
    @Override
    public Page findAll(Specification specification, Pageable pageable) {
        TypedQuery<E> query = getQuery(specification, pageable);
        return isUnpaged(pageable) ? new PageImpl<>(query.getResultList()) : readPage(query, getDomainClass(), pageable, specification);
    }

    protected TypedQuery<E> getQuery(Specification specification, Pageable pageable) {
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return getQuery(specification, getDomainClass(), sort);
    }

    protected <S extends E> TypedQuery<S> getQuery(Specification specification, Class<S> domainClass, Sort sort) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<S> query = builder.createQuery(domainClass);

        Root<S> root = applySpecificationToCriteria(specification, domainClass, query);
        query.select(root);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(entityManager.createQuery(query));
    }

    private <S extends E> TypedQuery<S> applyRepositoryMethodMetadata(TypedQuery<S> query) {
        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        TypedQuery<S> toReturn = type == null ? query : query.setLockMode(type);

        applyQueryHints(toReturn);

        return toReturn;
    }

    private void applyQueryHints(Query query) {
        for (Map.Entry<String, Object> hint : getQueryHints().withFetchGraphs(entityManager)) {
            query.setHint(hint.getKey(), hint.getValue());
        }
    }

    protected QueryHints getQueryHints() {
        return metadata == null ? QueryHints.NoHints.INSTANCE : DefaultQueryHints.of(entityInformation, metadata);
    }

    @Override
    public List findAll(Specification specification, Sort sort) {
            return getQuery(specification,sort).getResultList();
    }

    @Override
    public long count(Specification specification) {
        return 0;
    }

    @Override
    public Optional findOne(Example example) {
        return Optional.empty();
    }

    @Override
    public Page findAll(Example example, Pageable pageable) {
        return null;
    }

    @Override
    public long count(Example example) {
        return 0;
    }

    @Override
    public boolean exists(Example example) {
        return false;
    }

    protected static boolean isUnpaged(Pageable pageable) {
        return pageable.isUnpaged();
    }

    protected <S extends E> Page<S> readPage(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable, @Nullable Specification<S> spec) {

        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable, () -> executeCountQuery(getCountQuery(spec, domainClass)));
    }

    private static Long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query, "TypedQuery must not be null!");

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    protected <S extends E> TypedQuery<Long> getCountQuery(@Nullable Specification<S> spec, Class<S> domainClass) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        query.orderBy(Collections.<Order>emptyList());

        return entityManager.createQuery(query);
    }

    private <S, U extends E> Root<U> applySpecificationToCriteria(@Nullable Specification<U> spec, Class<U> domainClass, CriteriaQuery<S> query) {

        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");

        Root<U> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    protected Class<E> getDomainClass() {
        return entityInformation.getJavaType();
    }

    protected TypedQuery<E> getQuery(@Nullable Specification<E> spec, Sort sort) {
        return getQuery(spec, getDomainClass(), sort);
    }
}
