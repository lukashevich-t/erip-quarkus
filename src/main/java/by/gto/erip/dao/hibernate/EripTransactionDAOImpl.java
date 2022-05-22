package by.gto.erip.dao.hibernate;

import by.gto.erip.model.EripTransaction;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class EripTransactionDAOImpl {
    @Inject
    EntityManager em;

    public EripTransaction getTransaction(long id) {
        return em.find(EripTransaction.class, id);
        // EripTransaction eripTransaction = getSession().get(EripTransaction.class, id);
        // //		if (eripTransaction != null && eager) {
        // //			int stub;
        // //			// "ненужный" вызов, чтобы вытянуть зависимости
        // //			stub = eripTransaction.getHistories().size();
        // //		}
        // return eripTransaction;
    }

    // @Override
    // public List<EripTransaction> getTransactions(String personalAccount, boolean onlyRepayable) {
    //     List<EripTransaction> transactions = null;
    //
    //     //		Query q = getSession().createQuery(
    //     //				"FROM EripTransaction as T left join T.request as R where R.personalAccount = :personalAccount" + ((onlyRepayable)
    //     //						? " and T.transactionstateId = " + EripTransactionStatesEnum.TRANSACTION_COMMITED : ""));
    //     //
    //
    //     Query q = getSession().createQuery(
    //         "FROM EripTransaction as T where T.request.personalAccount = :personalAccount" + ((onlyRepayable)
    //             ? " and T.transactionstateId = " + EripTransactionStatesEnum.TRANSACTION_COMMITED : "") +
    //             " order by T.operationDate desc");
    //     q.setParameter("personalAccount", personalAccount);
    //     transactions = q.list();
    //     return transactions;
    // }

    public void saveTransaction(EripTransaction t) {
        em.persist(t);
        // getSession().saveOrUpdate(t);
    }

    //	@Override
    //	public void saveHistory(History h) {
    //		getSession().saveOrUpdate(h);
    //	}

    /*
     * @Override public void modifyTransaction(EripTransaction t, History h) {
     * //Session session = HibernateHelpers.createHibernateSession(); Session
     * session = sessionFactory.openSession(); org.hibernate.Transaction tx =
     * null; try { tx = session.beginTransaction(); t.setLastModified(new
     * Date()); session.saveOrUpdate(t); session.save(h); tx.commit(); } catch
     * (HibernateException e) { if (tx != null) tx.rollback();
     * e.printStackTrace(); throw e; } finally { session.close(); } }
     *
     * @Override public EripTransaction getTransaction(int id, boolean eager) {
     * //Session session = HibernateHelpers.createHibernateSession(); Session
     * session = sessionFactory.openSession(); org.hibernate.Transaction tx =
     * null; EripTransaction eripTransaction = null; try { tx =
     * session.beginTransaction(); eripTransaction =
     * session.get(EripTransaction.class, id); if(eager) { int stub; stub =
     * eripTransaction.getHistories().size(); // "ненужный" вызов, чтобы
     * вытянуть зависимости } tx.commit(); } catch (HibernateException e) { if
     * (tx != null) tx.rollback(); e.printStackTrace(); } finally {
     * session.close(); } return eripTransaction; }
     *
     * @Override public List<EripTransaction> getTransactions(String
     * personalAccount, boolean eager) { //Session session =
     * HibernateHelpers.createHibernateSession(); Session session =
     * sessionFactory.openSession(); org.hibernate.Transaction tx = null;
     * List<EripTransaction> transactions = null; try { tx =
     * session.beginTransaction(); Query q = session.createQuery(
     * "FROM EripTransaction T where T.personalAccount = :personalAccount");
     * q.setParameter("personalAccount", personalAccount); transactions =
     * q.list(); tx.commit(); } catch (HibernateException e) { if (tx != null)
     * tx.rollback(); e.printStackTrace(); } finally { session.close(); } return
     * transactions; }
     *
     * @Override public void saveTransaction(EripTransaction t) { //Session
     * session = HibernateHelpers.createHibernateSession(); Session session =
     * sessionFactory.openSession(); org.hibernate.Transaction tx = null; try {
     * tx = session.beginTransaction(); //session.save(u);
     * session.saveOrUpdate(t); tx.commit(); } catch (HibernateException e) { if
     * (tx != null) tx.rollback(); e.printStackTrace(); } finally {
     * session.close(); } }
     *
     * @Override public void repay(EripTransaction t) { //Session session =
     * HibernateHelpers.createHibernateSession(); Session session =
     * sessionFactory.openSession(); org.hibernate.Transaction tx = null; try {
     * tx = session.beginTransaction(); t = session.get(EripTransaction.class,
     * t.getGaiRequestId()); if(t.isRepayable()) {
     *
     * } tx.commit(); } catch (HibernateException e) { if (tx != null)
     * tx.rollback(); e.printStackTrace(); } finally { session.close(); } }
     */

}
