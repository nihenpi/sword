package com.skaz.jpa.support;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 直接copy的大神的
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @since 2.0
 */
public interface QueryHints extends Iterable<Entry<String, Object>> {

    QueryHints withFetchGraphs(EntityManager em);

    Map<String, Object> asMap();

    enum NoHints implements QueryHints {

        INSTANCE;

        @Override
        public Map<String, Object> asMap() {
            return Collections.emptyMap();
        }

        @Override
        public Iterator<Entry<String, Object>> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public QueryHints withFetchGraphs(EntityManager em) {
            return this;
        }
    }
}
