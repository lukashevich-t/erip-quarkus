package by.gto.erip.listen;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class ResponseCreator {

    public static final String ERIP_MESSAGE_FOOTER = StringEscapeUtils.escapeHtml4("УП \"Белтехосмотр\"\n+37517 311-09-81 доб. 135");
    private static final String ERIP_MESSAGE_FOOTER_WITH_TAGS =
            "<ErrorLine>" +
                    StringUtils.join(ERIP_MESSAGE_FOOTER.split("\n+"), "</ErrorLine><ErrorLine>") +
                    "</ErrorLine>";

    public static String formatError(String error) {
        StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='windows-1251'?>"
                + "<ServiceProvider_Response><Error>");
        if (null == error) {
            sb.append("<ErrorLine>null</ErrorLine>");
        } else {
            for (String line : error.split("\n")) {
                sb.append("<ErrorLine>").append(StringEscapeUtils.escapeHtml4(line)).append("</ErrorLine>");
            }
            sb.append(ERIP_MESSAGE_FOOTER_WITH_TAGS);
        }
        sb.append("</Error></ServiceProvider_Response>");
        return sb.toString();
    }
}
