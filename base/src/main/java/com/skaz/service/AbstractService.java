package com.skaz.service;

import com.skaz.bean.Page;
import com.skaz.jpa.AbstractDao;
import com.skaz.jpa.AbstractEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * @author jungle
 */
public class AbstractService<E extends AbstractEntity, ID extends Serializable> {

    @Autowired
    protected AbstractDao<E, ID> dao;

    @Transactional
    public <S extends E> S create(S entity) {
        return dao.create(entity);
    }

    public Page<E> page(int no, int size) {
        return dao.page(no, size);
    }

    public List<E> list() {
        return dao.list();
    }

    public E get (ID id){
        return dao.get(id);
    }

}
