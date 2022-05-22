package by.gto.erip.listen;

import by.gto.erip.exceptions.AccountFormatException;
import by.gto.erip.helpers.Settings;
import by.gto.erip.model.bft.EripXmlRequest;
import by.gto.erip.parse.EripParser;
import by.gto.erip.service.EripServiceImpl;
import by.gto.library.helpers.AutoCloseableHelper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

@MultipartConfig(fileSizeThreshold = 20000)
@WebServlet(urlPatterns = "/", loadOnStartup = 1)
public class EripListener extends HttpServlet {
    private static final long serialVersionUID = -2234136904762317772L;
    private static final AtomicLong uniqueIdGenerator = new AtomicLong(System.currentTimeMillis());
    private static final Logger log = Logger.getLogger(EripListener.class.getName());

    @Inject
    EripServiceImpl eripService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String path = req.getServletPath();
        if (path.startsWith("/4440f8d9-4849-4134-a3f6-bb8bbd517f6c/")) {
            String mimeType = Files.probeContentType(new File(path).toPath());
            if (mimeType == null)
                mimeType = "text/plain";
            resp.setContentType(mimeType);
            try (var ach = new AutoCloseableHelper()) {
                OutputStream sout = ach.add(resp.getOutputStream());
                InputStream is = ach.add(EripListener.class.getClassLoader().getResourceAsStream(path));
                IOUtils.copy(is, sout);
            }
        }
    }

    /**
     * Достать из данных переданной формы XML-посылку
     *
     * @param rq Объект запроса сервлета
     * @return текст посылки или null при неудаче
     */
    private String extractMessage(HttpServletRequest rq) throws IOException, FileUploadException {
        if (!ServletFileUpload.isMultipartContent(rq)) {
            return null;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request
        for (FileItem item : upload.parseRequest(rq)) {
            if (!(item.isFormField() && "XML".equals(item.getFieldName()))) {
                continue;
            }
            // System.out.println("contentType: " + item.getContentType());
            // System.out.println("name: " + item.getName());
            // System.out.println("fieldName: " + item.getFieldName());
            // final byte[] bytes = item.get();
            // System.out.println("content: " + Arrays.toString(bytes));
            // System.out.println("size: " + item.getSize());
            // System.out.println("in memory: " + item.isInMemory());
            // System.out.println("getString: " + item.getString());
            // System.out.println("string, using windows-1251: " + new String(bytes, "windows-1251"));
            return new String(item.get(), "windows-1251");
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // req.setCharacterEncoding("windows-1251");
        resp.setContentType("application/xml");
        resp.setCharacterEncoding("windows-1251");
        resp.setStatus(200);
        resp.setHeader("Access-Control-Allow-Origin", "*");
        OutputStream outputStream = resp.getOutputStream();
        // String strRequest = req.getParameter("XML");
        String fileName = Settings.getRequestLogPath() + "/err.txt";
        String result;
        try (var ach = new AutoCloseableHelper()) {
            String strRequest = extractMessage(req);
            if (strRequest == null) {
                final Writer wr = ach.add(new OutputStreamWriter(outputStream, "windows-1251"));
                wr.write(ResponseCreator.formatError("Parameter 'XML' not found"));
                return;
            }

            EripXmlRequest r = EripParser.parseRequest(strRequest);
            log.info("parsed EripXmlRequest: " + ((r == null) ? "null" : r));
            fileName = makeFileName(r);
            FileWriter wr = ach.add(new FileWriter(fileName + ".xml", StandardCharsets.UTF_8));
            wr.write(strRequest);
            if (r == null) {
                result = ResponseCreator.formatError("Request parsing unsuccessfull");
            } else {
                result = eripService.createResponse(r);
            }
        } catch (Exception ex) {
            if (ex instanceof AccountFormatException) {
                log.error(ex.getMessage());
            } else {
                log.error(ex.getMessage(), ex);
            }
            result = ResponseCreator.formatError(ex.getMessage());
        }
        try (final FileWriter logWriter = new FileWriter(fileName + "-a.xml", StandardCharsets.UTF_8);
             final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "windows-1251")) {
            outputStreamWriter.write(result);
            logWriter.write(result);
        }
    }

    private String makeFileName(EripXmlRequest parsedRequest) {
        String pathToFile, fileName;
        String requestLogRoot = Settings.getRequestLogPath();
        final long eripRequestId = uniqueIdGenerator.incrementAndGet();
        if (parsedRequest == null) {
            Date dt = new Date();
            pathToFile = String.format("%1$s/err/%2$tY-%2$tm-%2$td/%2$tH", requestLogRoot, dt);
            fileName = String.format("%1$tH-%1$tM-%1$tS-%2$d", dt, eripRequestId);
        } else {
            long transactionId = parsedRequest.getTransactionId();
            if (transactionId != 0L) {
                pathToFile = getRequestLogPath(requestLogRoot + "/tx", transactionId);
            } else {
                pathToFile = getRequestLogPath(requestLogRoot + "/rq", parsedRequest.getRequestId());
            }
            fileName = String.valueOf(eripRequestId);
        }

        File f = new File(pathToFile);
        f.mkdirs();
        return pathToFile + "/" + fileName;
    }

    private String getRequestLogPath(String prefix, long requestId) {
        long p1 = requestId % 10000L;
        requestId /= 10000L;
        long p2 = requestId % 10000L;
        requestId /= 10000L;
        return String.format("%s/%d/%04d/%04d", prefix, requestId, p2, p1);
    }
}
