package by.gto.erip.dao.hibernate;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;

@ApplicationScoped
@NamedQuery(name = QueryNames.deleteOldRegisteredPayments, query = "delete from RegisteredPayment rp where rp.date < :date")
@NamedQuery(name = QueryNames.getAllTariffs, query = "select T from Tariff T order by T.startDate DESC, T.main DESC")
public class CommonDAOImpl {
    // @Inject
    // protected SessionFactory sessionFactory;

    @Inject
    EntityManager em;

    // protected Session getSession() {
    //     Session s = sessionFactory.getCurrentSession();
    //     return s;
    // }

    public <T> T get(Class<T> entityType, Serializable id) {
        return em.find(entityType, id);
        // Session s = getSession();
        // return s.get(entityType, id);
    }

    public void delete(Object entity) {
        em.remove(entity);
        // Session s = getSession();
        // s.delete(entity);
    }

    public void saveOrUpdate(Object entity) {
        em.merge(entity);
        // getSession().saveOrUpdate(entity);
    }

    // @SuppressWarnings("unchecked")
    // public <T> List<T> getEntities(Class<T> entityType, int maxResults) {
    //     List<T> entities = null;
    //     Session session = getSession();
    //     // Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
    //     //     .getActualTypeArguments()[0];
    //     String n = entityType.getCanonicalName();
    //     long t1 = System.currentTimeMillis();
    //     Query q = session.createQuery("from " + n);
    //     q.setMaxResults(maxResults);
    //     entities = q.list();
    //     long total = System.currentTimeMillis() - t1;
    //     return entities;
    // }
    //
    // @SuppressWarnings("unchecked")
    // public <T> List<T> getEntities(Class<T> entityType) {
    //     List<T> entities = null;
    //     Session session = getSession();
    //     // Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
    //     //     .getActualTypeArguments()[0];
    //     String n = entityType.getCanonicalName();
    //     Query q = session.createQuery("from " + n);
    //     entities = q.list();
    //     return entities;
    // }
    //
    // public List<Object[]> customQuery(String query, Map params) {
    //     Query q = getSession().createQuery(query);
    //     Iterator it = params.entrySet().iterator();
    //     while (it.hasNext()) {
    //         Map.Entry pair = (Map.Entry) it.next();
    //         q.setParameter((String) pair.getKey(), pair.getValue());
    //     }
    //     List l = q.list();
    //     return l;
    // }
    //
    // public List<Object[]> customQueryTransactional(String query, Map params) {
    //     return customQuery(query, params);
    // }
    //
    // public void updateQueryTransactional(String query, Map<String, Object> params) {
    //     org.hibernate.query.Query q = getSession().createQuery(query);
    //     for (Map.Entry<String, Object> pair : params.entrySet()) {
    //         q.setParameter(pair.getKey(), pair.getValue());
    //     }
    //     q.executeUpdate();
    // }
}
