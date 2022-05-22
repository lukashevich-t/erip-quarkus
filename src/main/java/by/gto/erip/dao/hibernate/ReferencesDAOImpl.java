package by.gto.erip.dao.hibernate;

import by.gto.erip.model.hib.GaiRegCache;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class ReferencesDAOImpl {
    @Inject
    EntityManager em;

    @SuppressWarnings(value = {"rawtypes"})
    public String acqureRandomCertNumber() {
        final List resultList = em.createQuery("select rc from GaiRegCache rc")
            .setMaxResults(1).
            setFirstResult((int) (Math.random() * 100))
            .getResultList();
        if (resultList.size() == 0) {
            return null;
        } else {
            return ((GaiRegCache) resultList.get(0)).getCertNum();
        }
    }

    // public GaiRegCache getRegCacheByRegCert(String certNum) {
    //     Query q = getSession().createQuery("FROM GaiRegCache g where g.certNum = :certNum");
    //     q.setParameter("certNum", StringUtils.upperCase(certNum));
    //     q.setMaxResults(1);
    //     return (GaiRegCache) q.uniqueResult();
    // }
    //
    // public String acqureRandomCertNumber() {
    //     GaiRegCache grc;
    //     Query q = getSession().createQuery("from GaiRegCache");
    //     q.setMaxResults(1);
    //     q.setFirstResult((int) (Math.random() * 100));
    //     grc = (GaiRegCache) q.uniqueResult();
    //     if (grc == null) {
    //         return null;
    //     } else {
    //         return grc.getCertNum();
    //     }
    // }
}
