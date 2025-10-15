import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SplitPDF {

    public static void main(String[] args) throws IOException {

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String outDir = System.getenv("PDF_OUT_DIR");
        File outputDir = new File(outDir);

        String inputDir = System.getenv("PDF_SPLIT_INPUT_DIR");

        File directory = new File(inputDir);
        if (!directory.isDirectory()) {
            System.err.println("指定したパスはディレクトリではありません: " + inputDir);
        }

        File[] pdfFiles = directory.listFiles((_, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfFiles == null || pdfFiles.length == 0) {
            System.err.println("ディレクトリにPDFファイルが存在しません: " + inputDir);

        }else{

            for (File inputFile : pdfFiles) {

                try (PDDocument document = Loader.loadPDF(inputFile)) {
                    int totalPages = document.getNumberOfPages();
                    Splitter splitter = new Splitter();
                    List<PDDocument> pages = splitter.split(document);

                    DecimalFormat df = new DecimalFormat(String.join("", java.util.Collections.nCopies(Math.max(3, String.valueOf(totalPages).length()), "0")));

                    String baseName = stripPdfExtension(inputFile.getName());

                    System.out.println(baseName);

                    int pageNum = 0;
                    for (PDDocument pageDoc : pages) {
                        String fileName = String.format("Split%s_%s_page%s.pdf",now, baseName, df.format(pageNum));
                        File outputFile = new File(outputDir, fileName);
                        try (pageDoc) {
                            pageDoc.save(outputFile);
                        }
                        pageNum++;
                    }

                    System.out.println("終了");
                    System.out.println(pageNum);
                }
            }

        }
    }

    private static String stripPdfExtension(String name) {
        int dot = name.toLowerCase().lastIndexOf(".pdf");
        return (dot > 0) ? name.substring(0, dot) : name;
    }
}
