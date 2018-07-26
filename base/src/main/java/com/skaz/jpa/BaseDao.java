package com.skaz.jpa;

import lombok.NoArgsConstructor;

/**
 * @author jungle
 */
@NoArgsConstructor
public class BaseDao<E extends AbstractEntity<Long>> extends AbstractDao<E, Long> {


}
