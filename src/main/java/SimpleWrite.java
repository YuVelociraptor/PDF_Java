import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleWrite {

    public static void main(String[] args) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String now = LocalDateTime.now().format(format);

        String outDir = System.getenv("PDF_OUT_DIR");
        File outFile = new File(outDir + "/out_" + now + ".pdf");

        String fontPath = System.getenv("PDF_FONT");
        System.out.println(fontPath);

        try (PDDocument document = new PDDocument()) {

            // PDFにFont設定
            PDFont font = PDType0Font.load(document, new File(fontPath));

            // A4
            PDPage page = new PDPage(PDRectangle.A4);

            // 90°回転して横にする
            page.setRotation(90);
            document.addPage(page);

            // 書き込むコンテンツを90°回転対応
            try (PDPageContentStream content = new PDPageContentStream(document, page, AppendMode.APPEND, false)) {
                content.transform(new Matrix(0, 1, -1, 0, page.getMediaBox().getWidth(), 0));
            }

            float TOP_MERGING = 10;
            float TEXT_MARGIN_POINT = 1;
            float Y_DELTA = 150;

            float LEFT_MERGING = 10;
            float X_DELTA = 100;

            try (PDPageContentStream content = new PDPageContentStream(document, page, AppendMode.APPEND, false)) {

                // 高さ（90°回転しているのでgetWidth）
                float height = page.getMediaBox().getWidth();
                System.out.println("Height" + height);

                float width = page.getMediaBox().getHeight();
                System.out.println(width);

                for(int y = 0; y < 4; y++){
                    for(int x = 0; x < 8; x++){

                        content.beginText();
                        content.setFont(font, 10);
                        content.newLineAtOffset(LEFT_MERGING + x * X_DELTA, height - TOP_MERGING -TEXT_MARGIN_POINT * 10 - y * Y_DELTA);
                        content.showText("Hello " + y + " : " + x);
                        content.endText();
                    }
                }
            }

            document.save(outFile);

        }catch (Exception e){

            e.printStackTrace();
        }
    }
}
