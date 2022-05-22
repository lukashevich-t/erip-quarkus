package by.gto.erip.pools;

import by.gto.library.helpers.ObjectPool;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.xml.parsers.SAXParserFactory;

public class Pools {
    public static final ObjectPool<SAXParserFactory> saxParserFactoryPool = new ObjectPool<SAXParserFactory>(10) {
        @Override
        protected SAXParserFactory createObject() {
            return SAXParserFactory.newInstance();
        }
    };

    public static final ObjectPool<SimpleDateFormat> formatYYYYMMDDhhmmss_Pool = new ObjectPool<SimpleDateFormat>(10) {
        @Override
        protected SimpleDateFormat createObject() {
            return new SimpleDateFormat("yyyyMMddHHmmss");
        }
    };

    public static final ObjectPool<DecimalFormat> decimalFormat_Pool = new ObjectPool<DecimalFormat>(20) {

        @Override
        protected DecimalFormat createObject() {
            Locale l = new Locale("ru");
            DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols(l);
            unusualSymbols.setDecimalSeparator(',');
            return new DecimalFormat("#####0.00", unusualSymbols);
        }
    };
}

