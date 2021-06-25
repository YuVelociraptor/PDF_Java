import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleWrite {

    public static void main(String[] args) throws IOException {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String now = LocalDateTime.now().format(format);
        System.out.println(now);

        String outDir = System.getenv("PDF_OUT_DIR");
        File outFile = new File(outDir + "/out_" + now + ".pdf");

        String fontPath = System.getenv("PDF_FONT");
        System.out.println(fontPath);

        try (PDDocument document = new PDDocument()) {

            PDFont font = PDType0Font.load(document, new File(fontPath));

            PDPage page = new PDPage(PDRectangle.A4);
            page.setRotation(90);
            document.addPage(page);

            document.save(outFile);

        }
    }
}
