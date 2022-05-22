package by.gto.erip.parse;

import by.gto.erip.model.bft.EripXmlRequest;
import by.gto.erip.pools.Pools;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EripParser extends DefaultHandler {
    private final char[] charsArray = new char[200];
    private int charsPos = 0;

    private final EripXmlRequest eripXmlRequest = new EripXmlRequest();
    private String currElement;
    private static final SAXException illegalDoc = new SAXException("illegal document structure");
    private final SimpleDateFormat formatYYYYMMDDhhmmss = Pools.formatYYYYMMDDhhmmss_Pool.borrowObject();
    private static final Logger log = Logger.getLogger(EripParser.class.getName());

    @Override
    public void startDocument() throws SAXException {
        currElement = "";
    }

    @Override
    public void endDocument() throws SAXException {
        Pools.formatYYYYMMDDhhmmss_Pool.returnObject(formatYYYYMMDDhhmmss);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currElement = qName;
        charsPos = 0;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String chars = new String(charsArray, 0, charsPos);
        switch (currElement) {
            case "ServiceProvider_Request":
                break;
            case "Version":
//                if (!chars.equals("1")) {
//                    throw new SAXException("EripXmlRequest version can only be equals to 1");
//                }
                eripXmlRequest.setVersion(chars);
                break;
            case "TransactionId":
                eripXmlRequest.setTransactionId(Long.parseLong(chars));
                break;
            case "AuthorizationType":
                eripXmlRequest.setAuthorizationType(chars);
                break;
            case "Amount":
                if (!"ServiceInfo".equals(eripXmlRequest.getRequestType())) {
                    eripXmlRequest.setAmount(new BigDecimal(chars.replace(',', '.')));
                }
                break;
            case "AmountPrecision":
                //eripXmlRequest.setAmount(new BigDecimal(chars.replace(',', '.')));
                break;
            case "Debt": // ServiceInfo/Amount/Debt
                //eripXmlRequest.setDebt(new BigDecimal(chars.replace(',', '.')));
                break;
            case "Penalty": // ServiceInfo/Amount/Penalty
                //eripXmlRequest.setPenalty(new BigDecimal(chars.replace(',', '.')));
                break;
            case "RequestType":
                eripXmlRequest.setRequestType(chars);
                break;
            case "DateTime":
                Date d = formatYYYYMMDDhhmmss.parse(chars, new ParsePosition(0));
                if (d == null) {
                    throw illegalDoc;
                }
                eripXmlRequest.setDateTime(d);
                break;
            case "ServiceNo":
                eripXmlRequest.setServiceNo(Integer.parseInt(chars));
                break;
            case "PersonalAccount": {
                    eripXmlRequest.setPersonalAccount(chars);
                break;
            }
            case "Currency":
                eripXmlRequest.setCurrency(Short.parseShort(chars));
                break;
            case "RequestId":
                eripXmlRequest.setRequestId(Long.parseLong(chars));
                break;
            case "Agent":
                eripXmlRequest.setAgent(Short.parseShort(chars));
                break;
            case "TransactionStart":
                if (!eripXmlRequest.getRequestType().equals("TransactionStart")) {
                    throw illegalDoc;
                }
                break;
            case "TransactionResult":
                if (!eripXmlRequest.getRequestType().equals("TransactionResult")) {
                    throw illegalDoc;
                }
                break;
            case "StornStart":
                if (!eripXmlRequest.getRequestType().equals("StornStart")) {
                    throw illegalDoc;
                }
                break;
            case "StornResult":
                if (!eripXmlRequest.getRequestType().equals("StornResult")) {
                    throw illegalDoc;
                }
                break;
            case "Surname":
                eripXmlRequest.setSurname(chars);
                break;
            case "FirstName":
                eripXmlRequest.setFirstName(chars);
                break;
            case "Patronymic":
                eripXmlRequest.setPatronymic(chars);
                break;
            case "City":
                eripXmlRequest.setCity(chars);
                break;
            case "Street":
                eripXmlRequest.setStreet(chars);
                break;
            case "House":
                eripXmlRequest.setHouse(chars);
                break;
            case "Building":
                eripXmlRequest.setBuilding(chars);
                break;
            case "Apartment":
                eripXmlRequest.setApartment(chars);
                break;
            case "ServiceProvider_TrxId":
                eripXmlRequest.setServiceProvider_TrxId(chars);
                break;
            case "ErrorText":
                eripXmlRequest.setErrorText(chars);
                eripXmlRequest.setErrorTextPresent(true);
                break;
            case "Storned":
                eripXmlRequest.setStorned(charsArray[0] == 'Y');
                break;
            default:
                break;
        }
        this.currElement = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException, IllegalArgumentException {
        System.arraycopy(ch, start, charsArray, charsPos, length);
        charsPos += length;
    }

    public EripXmlRequest getEripXmlRequest() {
        return eripXmlRequest;
    }

    public static EripXmlRequest parseRequest(String strRequest) {
        StringReader sr = new StringReader(strRequest);
        SAXParserFactory factory = null;
        try {
            factory = Pools.saxParserFactoryPool.borrowObject();
            SAXParser saxParser = factory.newSAXParser();
            EripParser p = new EripParser();
            saxParser.parse(new InputSource(sr), p);
            return p.getEripXmlRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            Pools.saxParserFactoryPool.returnObject(factory);
        }
    }

}
