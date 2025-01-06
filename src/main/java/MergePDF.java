import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MergePDF {

    public static void main(String[] args) throws IOException {
        System.out.println("Start, MergePDF!");

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String outDir = System.getenv("PDF_OUT_DIR");
        String outFilePath = outDir + "/merged_" + now + ".pdf";

        String inputDir = System.getenv("PDF_MERGE_INPUT_DIR");

        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDestinationFileName(outFilePath);

        File directory = new File(inputDir);
        if (!directory.isDirectory()) {
            System.err.println("指定したパスはディレクトリではありません: " + inputDir);
        }

        File[] pdfFiles = directory.listFiles((_, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfFiles == null || pdfFiles.length == 0) {
            System.err.println("ディレクトリにPDFファイルが存在しません: " + inputDir);

        } else {

            ArrayList<File> pdfFilesList = new ArrayList<>(Arrays.asList(pdfFiles));

            pdfFilesList.sort(Comparator.comparing(File::getName));

            for (File pdfFile : pdfFilesList) {
                System.out.println("結合対象のPDF: " + pdfFile.getAbsolutePath());
                pdfMerger.addSource(pdfFile);
            }

            pdfMerger.mergeDocuments(null);
            System.out.println("すべてのPDFファイルが結合されました: " + outFilePath);
        }
    }
}
