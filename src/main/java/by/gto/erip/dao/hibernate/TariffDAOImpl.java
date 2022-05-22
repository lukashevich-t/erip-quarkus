package by.gto.erip.dao.hibernate;

import by.gto.erip.model.Service;
import by.gto.erip.model.Tariff;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class TariffDAOImpl {
    @Inject
    EntityManager em;

    // @Cacheable(value = "svcCache")
    public Service getServiceCacheable(int serviceId) {
        return em.find(Service.class, serviceId);
        // Session s = getSession();
        // return s.get(Service.class, serviceId);
    }

    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public List<Tariff> getAllTariffs() {
        final List r = em.createNamedQuery(QueryNames.getAllTariffs).getResultList();
        return (List<Tariff>) r;
        // Session session = getSession();
        // Query q = session.createQuery(
        //         "from Tariff T order by T.startDate DESC, T.main DESC");
        // return (List<Tariff>) q.list();
    }
}
