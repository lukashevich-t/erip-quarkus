package by.gto.erip.service;

import by.gto.ais.model.gai.GaiAnswerEntryV1;
import by.gto.erip.dao.hibernate.CommonDAOImpl;
import by.gto.erip.dao.hibernate.EripTransactionDAOImpl;
import by.gto.erip.dao.hibernate.QueryNames;
import by.gto.erip.dao.hibernate.ReferencesDAOImpl;
import by.gto.erip.dao.hibernate.TariffDAOImpl;
import by.gto.erip.exceptions.AccountFormatException;
import by.gto.erip.exceptions.GenericEripException;
import by.gto.erip.helpers.GaiHelpers;
import by.gto.erip.helpers.Settings;
import by.gto.erip.helpers.StringHelpers;
import by.gto.erip.model.DeliveryQueue;
import by.gto.erip.model.EripActionTypesEnum;
import by.gto.erip.model.EripOfflineMsg210;
import by.gto.erip.model.EripRequest;
import by.gto.erip.model.EripServiceFlagsEnum;
import by.gto.erip.model.EripTransaction;
import by.gto.erip.model.EripTransactionStatesEnum;
import by.gto.erip.model.History;
import by.gto.erip.model.OrderInfo;
import by.gto.erip.model.PersonalAccountFormatsEnum;
import by.gto.erip.model.RegisteredPayment;
import by.gto.erip.model.Tariff;
import by.gto.erip.model.bft.EripXmlRequest;
import by.gto.erip.model.tariff.TariffSimple;
import by.gto.erip.model.tariff.TariffsHolder;
import by.gto.erip.pools.Pools;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
// @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EripServiceImpl {

    private static final TariffsHolder tariffsHolder = new TariffsHolder();

    private static final Logger logger = Logger.getLogger(EripServiceImpl.class.getName());

    private static final Pattern patternPersonalName = Pattern.compile("^\\s*[A-ZА-Яa-zа-яЁёўЎіІ -]{5,}\\s*$");
    //    private static Pattern patternPersonalName = Pattern.compile("^\\s*([A-ZА-Яa-zа-яЁёўЎіІ]{2,})((\\s+|-)[A-ZА-Яa-zа-яЁёўЎіІ]{2,})+\\s*$");

    @Inject
    EripTransactionDAOImpl transactionDAO;
    @Inject
    CommonDAOImpl commonDAO;
    @Inject
    TariffDAOImpl tariffDAO;
    @Inject
    ReferencesDAOImpl referencesDAO;

    @Inject
    EntityManager em;

    //    @Transactional(propagation = Propagation.REQUIRED)
    //    public void saveTransaction(EripTransaction t) {
    //        t.setLastModified(new Date());
    //        transactionDAO.saveTransaction(t);
    //    }

    public void saveEntity(Object persistent) {
        commonDAO.saveOrUpdate(persistent);
    }

    //    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    //    public <T> T getEntity(Class<T> entityType, Serializable id) {
    //        return commonDAO.get(entityType, id);
    //    }

    //    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    //    public <T> List<T> getEntities(Class<T> entityType, int maxResults) {
    //        return commonDAO.getEntities(entityType, maxResults);
    //    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public by.gto.erip.model.Service getServiceCacheable(int serviceId) {
        return tariffDAO.getServiceCacheable(serviceId);
    }

    public void modifyTransaction(EripTransaction eripTransaction, EripRequest eripRequest, int actionTypeId) {
        // logger.info(String.format(
        //     "modifyTransaction: eripTransaction: %s; eripRequest:%s; history: %s",
        //     eripTransaction, eripRequest, history));

        Date d = new Date();
        History history = new History(actionTypeId, d);
        history.setDate(d);

        eripTransaction.setRequest(eripRequest);
        eripTransaction.setLastModified(d);
        eripTransaction.addHistory(history);
        if (eripTransaction.getOperationDate() == null) {
            logger.error("modifyTransaction: before saving: eripTransaction.getOperationDate() == null txId = " + eripTransaction.getId());
        }
        transactionDAO.saveTransaction(eripTransaction);
        if (eripTransaction.getOperationDate() == null) {
            logger.error("modifyTransaction: after transactionDAO.saveTransaction(eripTransaction): eripTransaction.getOperationDate() == null txId = " + eripTransaction.getId());
        }
        // transactionDAO.saveHistory(history);
        if (eripTransaction.getOperationDate() == null) {
            logger.error("modifyTransaction: after transactionDAO.saveHistory(history): eripTransaction.getOperationDate() == null txId = " + eripTransaction.getId());
        }
    }

    //    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    //    public EripTransaction getTransaction(long id) {
    //        return transactionDAO.getTransaction(id);
    //    }
    //
    //    public List<EripTransaction> getTransactions(String personalAccount, boolean onlyRepayable) {
    //        return transactionDAO.getTransactions(personalAccount, onlyRepayable);
    //    }

    //    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    //    public List<EripTransaction> getTransactionsForAccount(String personalAccount, boolean onlyRepayable) {
    //        return transactionDAO.getTransactions(personalAccount, onlyRepayable);
    //    }

    //    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    //    public void saveEntities(int count, Object... entities) {
    //        int b = Math.min(count, entities.length);
    //        for (int i = 0; i < b; i++) {
    //            commonDAO.saveOrUpdate(entities[i]);
    //        }
    //    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public String acquireRandomCertNumber() {
        return referencesDAO.acqureRandomCertNumber();
    }

    //    @Deprecated
    //    private GaiRegCache getRegistrationInfo(String personalAccount) {
    //        GaiRegCache result = null;
    //        R regInfo = null;
    //        // String normalizedPersonalAccount;
    //        // try {
    //        //     normalizedPersonalAccount = StringHelpers.normalizeCertNumber(personalAccount, 1);
    //        // } catch (AccountFormatException e) {
    //        //     logger.warn("getRegistrationInfo: неверный формат техпаспорта: " + personalAccount);
    //        //     return null;
    //        // }
    //        // сначала пробуем забрать информацию из ГАИ
    //        if (Settings.isGaiServiceEnabled()) {
    //            regInfo = GaiHelpers.getRegistrationInfoFromGAI(
    //                    personalAccount, Settings.getGaiServiceUrl(),
    //                    Settings.getGaiServiceEncodedAuth(), Settings.getGaiRetries());
    //            if (regInfo != null) {
    //                result = GaiHelpers.constructRegCacheFromRegInfo(regInfo);
    //            }
    //        }
    //        if (regInfo != null) {
    //            // что-то пришло из ГАИ - запишем это в наш кэш
    //            commonDAO.saveOrUpdate(result);
    //        } else {
    //            // в ГАИ нет инфы по этой регистрации или их веб-сервис недоступен - ищем в своем кэше:
    //            result = referencesDAO.getRegCacheByRegCert(personalAccount);
    //        }
    //        return result;
    //    }


    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Exception.class})
    public String createResponse(EripXmlRequest request) throws Exception {
        DecimalFormat numberFormatter1 = null;
        var writer = new StringWriter(1024);
        try {
            XMLOutputFactory xof = XMLOutputFactory.newDefaultFactory();
            XMLStreamWriter xsw = xof.createXMLStreamWriter(writer);
            EripTransaction eripTransaction = null;
            EripRequest eripRequest = null;

            int actionTypeId;
            byte transactionState = -1;

            Integer serviceNoObj = request.getServiceNo();
            if (serviceNoObj == null) {
                throw new GenericEripException("В запросе не указан номер услуги");
            }
            int serviceNo = serviceNoObj;

            if (request.getRequestType().equals("ServiceInfo")) {
                eripRequest = commonDAO.get(EripRequest.class, request.getRequestId());
            } else {
                eripTransaction = commonDAO.get(EripTransaction.class, request.getTransactionId());
                if (null != eripTransaction) {
                    transactionState = eripTransaction.getTransactionstateId();
                    eripRequest = eripTransaction.getRequest();
                    if (eripTransaction.getOperationDate() == null) {
                        logger.error("eripTransaction.getOperationDate is null after reading from db id=" + eripTransaction.getId());
                    }
                }
            }
            if (eripRequest != null && eripRequest.getOperationDate() == null) {
                logger.warn(eripRequest + " operationDate is null");
            }

            String paRaw = StringUtils.replacePattern(request.getPersonalAccount(), "\\s+", "");
            numberFormatter1 = Pools.decimalFormat_Pool.borrowObject();
            switch (request.getRequestType()) {
                case "ServiceInfo":
                    if (null == eripRequest) {
                        String message;
                        by.gto.erip.model.Service service = getServiceCacheable(serviceNo);
                        if (service.getStartDate().getTime() > System.currentTimeMillis()) {
                            throw new GenericEripException("Оплата услуги невозможна.");
                        }
                        String paForStoring;
                        switch (service.getPersonalAccountFormat()) {
                            case PersonalAccountFormatsEnum.CERTIFICATE_NUMBER:
                                paForStoring = StringHelpers.normalizeCertNumberForErip(paRaw);
                                message = findAndDescribeVehicleV1(paRaw);
                                eripRequest = new EripRequest(paForStoring);
                                TariffSimple t = tariffsHolder.getMainTariff(serviceNo, request.getDateTime());
                                if (t != null) {
                                    eripRequest.setTariff(t.getTariff());
                                    message = message + "\nТариф: " + t.getFormatted() + " руб.";
                                } else {
                                    eripRequest.setTariff(BigDecimal.ZERO);
                                }
                                break;

                            case PersonalAccountFormatsEnum.ORDER_NUMBER:
                                long paOrderNumber = NumberUtils.toLong(paRaw, Long.MIN_VALUE);
                                if (Long.MIN_VALUE == paOrderNumber) {
                                    throw new AccountFormatException("Неверно указан номер заказа: " + paRaw + ".\nТребуется ввести число, полученное на gto.by/pay/");
                                }
                                RegisteredPayment rp = commonDAO.get(RegisteredPayment.class, paOrderNumber);
                                if (rp == null) {
                                    throw new GenericEripException("Номер заказа " + paRaw + ".\nИнформация для оплаты не найдена.\nНомер заказа действителен "
                                        + Settings.getRegisteredPaymentKeepDays() + " дней.");
                                }
                                if (rp.getServiceId() != serviceNo) {
                                    throw new GenericEripException("Номер заказа " + paRaw + " не соответствует выбранной услуге "
                                        + service.getName()
                                    );
                                }

                                paForStoring = paRaw;
                                message = "Заказ № " + paForStoring;
                                // if ((service.getServiceFlags() & EripServiceFlagsEnum.AGREEMENT) != 0) {
                                //     message = String.format("%s к договору № %s от %3$td.%3$tm.%3$tY", message, rp.getAgrNum(), rp.getAgrDate());
                                // }

                                if ((service.getServiceFlags() & EripServiceFlagsEnum.TARIFF_FIXED) != 0) {
                                    tariffsHolder.checkFixedTariffSum(request.getDateTime(), rp.getAmount(), numberFormatter1, serviceNo);
                                }
                                eripRequest = new EripRequest(paForStoring);
                                eripRequest.setTariff(rp.getAmount());
                                if (rp.getLegalType() == 0) {
                                    // техпаспорт вводят только физлица
                                    message = message + "\n" + findAndDescribeVehicleV1(rp.getCertNumber());
                                } else {
                                    message = message + "\nУНП: " + rp.getUnp();
                                }
                                message = message + "\nСумма: " + numberFormatter1.format(rp.getAmount()) + " руб.";
                                break;
                            default:
                                throw new GenericEripException("Неизвестный формат лицевого счета. Пожалуйста, сообщите разработчикам.");
                        }

                        eripRequest.setId(request.getRequestId());
                        eripRequest.setServiceFlags(service.getServiceFlags());
                        eripRequest.setService(service);
                        eripRequest.setCurrency(request.getCurrency());
                        eripRequest.setOperationDate(request.getDateTime());
                        eripRequest.setLastModified(new Date());
                        eripRequest.setPersonalAccount(paForStoring);
                        if ((eripRequest.getServiceFlags() & EripServiceFlagsEnum.FULL_NAME_REQUIRED) != 0) {
                            message = message + "\nПотребуется ввести ФИО. Допускаются русские, белорусские, английские буквы, дефис";
                        }
                        eripRequest.setMessage(message);

                        commonDAO.saveOrUpdate(eripRequest);
                    }

                    // Tariff t = DbConnection.getTariff(serviceNo,
                    // request.getDateTime());
                    xsw.writeStartDocument("windows-1251", "1.0");
                    xsw.writeStartElement("ServiceProvider_Response");
                    xsw.writeStartElement("ServiceInfo");
                    xsw.writeStartElement("Amount");
                    xsw.writeAttribute("Editable", ((eripRequest.getServiceFlags() & EripServiceFlagsEnum.TARIFF_EDITABLE) != 0) ? "Y" : "N");
                    xsw.writeStartElement("Debt");

                    xsw.writeCharacters(numberFormatter1.format(eripRequest.getTariff()));
                    xsw.writeEndElement(); // Debt
                    xsw.writeEndElement(); // Amount

                    if ((eripRequest.getServiceFlags() & EripServiceFlagsEnum.FULL_NAME_REQUIRED) != 0) {
                        xsw.writeStartElement("Name");
                        xsw.writeAttribute("Editable", "Y");
                        xsw.writeEmptyElement("Surname");
                        xsw.writeEmptyElement("FirstName");
                        xsw.writeEmptyElement("Patronymic");
                        xsw.writeEndElement(); // Name
                    }

                    xsw.writeStartElement("Info");
                    String msg = eripRequest.getMessage();
                    if (msg != null) {
                        for (String line : msg.split("\n")) {
                            xsw.writeStartElement("InfoLine");
                            xsw.writeCharacters(line);
                            xsw.writeEndElement(); // InfoLine
                        }
                    }
                    xsw.writeEndElement(); // Info
                    xsw.writeEndElement(); // ServiceInfo
                    xsw.writeStartElement("NextRequestType");
                    xsw.writeCharacters("TransactionStart");
                    xsw.writeEndElement(); // NextRequestType
                    xsw.writeEndElement();// ServiceProvider_Response
                    xsw.writeEndDocument();
                    xsw.flush();
                    xsw.close();
                    break;

                case "TransactionStart":
                    if (eripTransaction == null) {
                        eripRequest = commonDAO.get(EripRequest.class, request.getRequestId());
                        BigDecimal amount = request.getAmount();
                        String payerName = null;

                        if ((eripRequest.getServiceFlags() & EripServiceFlagsEnum.PAYMENT_REGISTER_REQUIRED) != 0) {
                            RegisteredPayment rp = commonDAO.get(RegisteredPayment.class, Long.parseLong(paRaw));
                            if (rp == null) {
                                throw new GenericEripException("Номер заказа " + paRaw + ".\nИнформация для оплаты не найдена.\nПроверьте номер заказа.");
                            }
                            if (amount.compareTo(rp.getAmount()) != 0) {
                                throw new GenericEripException("Неверная сумма платежа: " + numberFormatter1.format(amount)
                                    + "руб.\nСумма платежа в заказе № " + rp.getId() + " равна "
                                    + numberFormatter1.format(rp.getAmount()));
                            }
                            if (rp.getLegalType() != 0) {
                                payerName = rp.getName();
                            }
                        }

                        if ((eripRequest.getServiceFlags() & EripServiceFlagsEnum.TARIFF_FIXED) != 0) {
                            tariffsHolder.checkFixedTariffSum(request.getDateTime(), amount, numberFormatter1, serviceNo);
                        }
                        if ((eripRequest.getServiceFlags() & EripServiceFlagsEnum.FULL_NAME_REQUIRED) != 0) {
                            if (!isValidFullName(request)) {
                                throw new GenericEripException("Требуется ввод фамилии и имени (допускаются только буквы, пробел и дефис).");
                            }
                        }

                        eripTransaction = new EripTransaction();
                        eripTransaction.setId(request.getTransactionId());
                        eripTransaction.setAmount(amount);
                        eripTransaction.setOperationDate(request.getDateTime());
                        eripTransaction.setAuthorizationType(request.getAuthorizationType());
                        eripTransaction.setAgent(request.getAgent());
                        eripTransaction.setTransactionstateId(EripTransactionStatesEnum.TRANSACTION_STARTED);
                        eripTransaction.setRequest(eripRequest);

                        if (payerName == null && (eripRequest.getServiceFlags() & EripServiceFlagsEnum.FULL_NAME_REQUIRED) != 0) {
                            payerName = (request.getSurname() + " " + request.getFirstName()
                                + ((request.getPatronymic() == null) ? "" : (" " + request.getPatronymic()))).trim();
                        }

                        eripTransaction.setName(StringUtils.substring(payerName, 0, 150));
                        // history = new History(eripService.getEntity(ActionType.class, EripActionTypesEnum.TRANSACTION_START),
                        //    eripTransaction, null);
                        modifyTransaction(eripTransaction, eripRequest, EripActionTypesEnum.TRANSACTION_START.id);
                    }

                    xsw.writeStartDocument("windows-1251", "1.0");
                    xsw.writeStartElement("ServiceProvider_Response");
                    xsw.writeStartElement("TransactionStart");
                    final String sTrxId = String.valueOf(eripTransaction.getId());
                    xsw.writeStartElement("ServiceProvider_TrxId");
                    xsw.writeCharacters(sTrxId);
                    xsw.writeEndElement();// ServiceProvider_TrxId
                    xsw.writeStartElement("Info");
                    xsw.writeStartElement("InfoLine");
                    xsw.writeCharacters(String.format("Номер операции: %1$s.", sTrxId));
                    xsw.writeEndElement();// InfoLine
                    xsw.writeEndElement();// Info
                    xsw.writeEndElement();// TransactionStart
                    xsw.writeEndElement();// ServiceProvider_Response
                    xsw.writeEndDocument();
                    xsw.flush();
                    xsw.close();
                    break;
                case "TransactionResult":
                    boolean transactionCancelled = false;
                    if (eripTransaction == null) {
                        throw new GenericEripException("Попытка завершить несуществующую транзакцию.");
                    }
                    if (transactionState == EripTransactionStatesEnum.TRANSACTION_STARTED) {
                        if (request.isErrorTextPresent()) {
                            eripTransaction.setTransactionstateId(EripTransactionStatesEnum.TRANSACTION_CANCELLED);
                            actionTypeId = EripActionTypesEnum.TRANSACTION_CANCEL.id;
                            transactionCancelled = true;
                        } else {
                            eripTransaction.setTransactionstateId(EripTransactionStatesEnum.TRANSACTION_COMMITED);
                            actionTypeId = EripActionTypesEnum.TRANSACTION_RESULT.id;

                            if ((eripRequest.getServiceFlags() & EripServiceFlagsEnum.PAYMENT_REGISTER_REQUIRED) != 0) {
                                RegisteredPayment rp = commonDAO.get(RegisteredPayment.class, Long.parseLong(paRaw));
                                OrderInfo oi = new OrderInfo(request.getTransactionId(), rp);
                                commonDAO.saveOrUpdate(oi);
                                commonDAO.delete(rp);
                            }

                        }
                        modifyTransaction(eripTransaction, eripRequest, actionTypeId);
                    }

                    xsw.writeStartDocument("windows-1251", "1.0");
                    xsw.writeStartElement("ServiceProvider_Response");
                    xsw.writeStartElement("TransactionResult");
                    xsw.writeStartElement("Info");
                    xsw.writeStartElement("InfoLine");
                    if (transactionCancelled) {
                        xsw.writeCharacters("Оплата отменена.");
                    } else {
                        xsw.writeCharacters(String.format("Услуга %1$s оплачена на сумму: %2$s. Номер операции: %3$d.",
                            eripTransaction.getRequest().getService().getName(),
                            numberFormatter1.format(eripTransaction.getAmount()),
                            eripTransaction.getId()));
                    }
                    xsw.writeEndElement();// InfoLine
                    xsw.writeEndElement();// Info
                    xsw.writeEndElement();// TransactionResult
                    xsw.writeEndElement();// ServiceProvider_Response
                    xsw.writeEndDocument();
                    xsw.flush();
                    xsw.close();
                    break;
                case "StornStart":
                    if (eripTransaction == null || transactionState == EripTransactionStatesEnum.TRANSACTION_STARTED) {
                        throw new GenericEripException("Попытка сторнировать несуществующую транзакцию.");
                    }

                    if (transactionState == EripTransactionStatesEnum.TRANSACTION_COMMITED) {
                        eripTransaction.setTransactionstateId(EripTransactionStatesEnum.STORN_STARTED);
                        modifyTransaction(eripTransaction, eripRequest, EripActionTypesEnum.STORN_START.id);
                    }
                    if (transactionState == EripTransactionStatesEnum.TRANSACTION_COMMITED
                        || transactionState == EripTransactionStatesEnum.STORN_STARTED
                        || transactionState == EripTransactionStatesEnum.STORN_COMMITED) {
                        makeStornAnswer(xsw);
                    } else {
                        throw new GenericEripException(
                            "Платеж заблокирован или по нему получены услуги. Сторнирование невозможно.");
                    }
                    break;
                case "StornResult":
                    if (eripTransaction == null || transactionState == EripTransactionStatesEnum.TRANSACTION_STARTED
                        || transactionState == EripTransactionStatesEnum.TRANSACTION_COMMITED) {
                        throw new GenericEripException("Попытка сторнироватиь несуществующую транзакцию.");
                    }

                    if (transactionState == EripTransactionStatesEnum.STORN_STARTED) {
                        if (request.isStorned()) {
                            eripTransaction.setTransactionstateId(EripTransactionStatesEnum.STORN_COMMITED);
                            actionTypeId = EripActionTypesEnum.STORN_RESULT.id;
                        } else {
                            eripTransaction.setTransactionstateId(EripTransactionStatesEnum.TRANSACTION_COMMITED);
                            actionTypeId = EripActionTypesEnum.STORN_ROLLBACK.id;
                        }
                        modifyTransaction(eripTransaction, eripRequest, actionTypeId);
                    }
                    if (transactionState == EripTransactionStatesEnum.STORN_STARTED
                        || transactionState == EripTransactionStatesEnum.STORN_COMMITED) {
                        makeStornAnswer(xsw);
                    } else {
                        throw new GenericEripException(
                            "Платеж заблокирован или по нему получены услуги. Сторнирование невозможно.");
                    }
                    break;

                default:
                    throw new GenericEripException("Неизвестный запрос " + request.getRequestType());

            }
        } finally {
            Pools.decimalFormat_Pool.returnObject(numberFormatter1);
        }
        writer.close();
        return writer.toString();
    }

    public static boolean isValidFullName(EripXmlRequest request) {
        String fName = request.getFirstName();
        String lName = request.getSurname();
        String mName = request.getPatronymic();
        String fullName = ((lName != null ? lName : "") + " " + (fName != null ? fName : "") + " " + (mName != null ? mName : "")).trim();
        Matcher m = patternPersonalName.matcher(fullName);
        return m.matches();
    }

    //    /**
    //     * Описывает автомобиль для показа плательщику
    //     * Если удается получить что-то от ГАИ, вставляет марку и госномер.
    //     * Если не удается, просто вставляет номер техпаспорта
    //     *
    //     * @param paRaw - номер техпаспорта в непричёсанном виде
    //     * @return описание автомобиля для показа плательщику
    //     */
    //    @Deprecated
    //    private String findAndDescribeVehicle(String paRaw) {
    //        String result;
    //        String paWithNormalizedSeries = BlankNumberHelpers.normalizeBlankSeries(paRaw);
    //        GaiRegCache gaiRegCache = getRegistrationInfo(paWithNormalizedSeries);
    //        if (gaiRegCache != null) {
    //            String maskedRn = GaiHelpers.getMaskedRegNumber(gaiRegCache.getRnMain());
    //            result = String.format("Техпаспорт: %1$s. \nАвтомобиль: %2$s, %3$s, %4$d г.в. ",
    //                    paWithNormalizedSeries, maskedRn, gaiRegCache.getModel(), gaiRegCache.getYear());
    //        } else {
    //            result = String.format("Техпаспорт: %1$s. ", paWithNormalizedSeries);
    //        }
    //        return result;
    //    }

    /**
     * Описывает автомобиль для показа плательщику, используя новый вебсервис ГАИ.
     * Если удается получить что-то от ГАИ, вставляет марку и госномер.
     * Если не удается, просто вставляет номер техпаспорта
     *
     * @param paRaw - номер техпаспорта в непричёсанном виде
     * @return описание автомобиля для показа плательщику
     */
    private String findAndDescribeVehicleV1(String paRaw) {
        GaiAnswerEntryV1 regInfo = GaiHelpers.getRegistrationInfoFromGaiV1(paRaw, Settings.getGaiServiceV1Url(),
            Settings.getGaiServiceV1EncodedAuth());
        if (regInfo != null) {
            String maskedRn = GaiHelpers.getMaskedRegNumber(regInfo.getRegNumber());
            return String.format("Техпаспорт: %1$s. \nАвтомобиль: %2$s, %3$s, %4$d г.в. ",
                paRaw, maskedRn, regInfo.getModelMarkNameRus(), regInfo.getYear());
        } else {
            return String.format("Техпаспорт: %1$s. ", paRaw);
        }
    }

    @NotNull
    private static List<Long> getTxNumbers(EripOfflineMsg210 msg210) {
        List<Long> listOfTransactions = new ArrayList<>(msg210.getBody().size());
        for (EripOfflineMsg210.BodyLine line : msg210.getBody()) {
            listOfTransactions.add(Long.parseLong(line.getFields()[12]));
        }
        return listOfTransactions;
    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Exception.class})
    public void repayTransactions(EripOfflineMsg210 msg210) {
        List<Long> listOfTransactions = getTxNumbers(msg210);
        Integer payDocNumber = Integer.parseInt(msg210.getHeader()[9]);
        Date payDocDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            payDocDate = sdf.parse(msg210.getHeader()[10]);
        } catch (ParseException e) {
            payDocDate = new Date(0);
        }
        for (Long txId : listOfTransactions) {
            EripTransaction eripTransaction = commonDAO.get(EripTransaction.class, txId);
            if (eripTransaction == null) {
                logger.error("erip transaction " + txId + " not found in DB!");
                continue;
            }

            //                logger.info("210 line " +_210Message2String(fileFields));
            // Бывает, что после Transaction_start не приходит transaction_result. Но если платеж попадает в 210-е сообщение,
            // то он был реально совершен и подтвержден деньгами
            if (eripTransaction.getTransactionstateId() == EripTransactionStatesEnum.TRANSACTION_STARTED) {
                eripTransaction.setTransactionstateId(EripTransactionStatesEnum.TRANSACTION_COMMITED);
            }
            eripTransaction.setPayDocNumber(payDocNumber);
            eripTransaction.setPayDocDate(payDocDate);
            EripRequest request = eripTransaction.getRequest();
            // ActionType actionType = eripService.getEntity(ActionType.class, EripActionTypesEnum.REAL_MONEY_TRANSMISSION);
            // History h = new History(actionType, eripTransaction, now);
            // logger.info("210: about to modify transaction " + eripTransaction.getId());
            modifyTransaction(eripTransaction, request, EripActionTypesEnum.REAL_MONEY_TRANSMISSION.id);
        }
        logger.info("210: transactions modified: " + StringUtils.join(listOfTransactions, ','));

    }

    @Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = {Exception.class})
    public void deleteDeliveryQueue(List<DeliveryQueue> entities) {
        for (DeliveryQueue entity : entities) {
            commonDAO.delete(entity);
        }
    }

    public void updateTariffsFromDB() {
        DecimalFormat numberFormatter1 = null;
        try {
            numberFormatter1 = Pools.decimalFormat_Pool.borrowObject();
            List<Tariff> tariffs = tariffDAO.getAllTariffs();
            Map<Integer, List<TariffSimple>> mainTariffs = new HashMap<>(11);
            Map<Integer, List<TariffSimple>> allTariffs = new HashMap<>(30);
            for (Tariff t : tariffs) {
                Integer serviceId = t.getService().getId();
                List<TariffSimple> main;

                TariffSimple e = new TariffSimple(
                    serviceId, t.getService().getName(), t.getStartDate(), t.getEndDate(), t.getSum(),
                    numberFormatter1.format(t.getSum()), 0, t.isMain());
                if (t.isMain()) {
                    main = mainTariffs.computeIfAbsent(serviceId, k -> new ArrayList<>());
                    main.add(e);
                }

                List<TariffSimple> all = allTariffs.computeIfAbsent(serviceId, k -> new ArrayList<>());
                all.add(e);
            }
            tariffsHolder.setMainTariffs(mainTariffs);
            tariffsHolder.setAllTariffs(allTariffs);
        } finally {
            Pools.decimalFormat_Pool.returnObject(numberFormatter1);
        }
    }

    public void removeOldRegisteredPayments(int registeredPaymentKeepDays) {
        em.createNamedQuery(QueryNames.deleteOldRegisteredPayments)
            .setParameter("date", new Date(System.currentTimeMillis() - registeredPaymentKeepDays * 24L * 60L * 60L * 1000L))
            .executeUpdate();
    }

    private static void makeStornAnswer(XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartDocument("windows-1251", "1.0");
        xsw.writeStartElement("ServiceProvider_Response");
        xsw.writeEndElement();// ServiceProvider_Response
        xsw.writeEndDocument();
        xsw.flush();
        xsw.close();
    }
}
