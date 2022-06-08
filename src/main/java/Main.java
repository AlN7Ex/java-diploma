import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        // создаём конфиг
        LinksSuggester linksSuggester = new LinksSuggester(new File("data/config"));
        linksSuggester.check();

        int count = 0;
        var dir = new File("data/pdfs");
        String[] list = dir.list();

        if (list != null) {

            // перебираем пдфки в data/pdfs

            for (var fileIn : dir.listFiles()) {

                // для каждой пдфки создаём новую в data/converted

                var doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter("data/converted/"+ list[count]));
                ++count;
                int numberOfPages = doc.getNumberOfPages();

                // перебираем страницы pdf

                for (int i = 1; i <= numberOfPages; ++i) {
                    String textFromPage = PdfTextExtractor.getTextFromPage(doc.getPage(i));

                    // если в странице есть неиспользованные ключевые слова, создаём новую страницу за ней

                    List<Suggest> suggest = linksSuggester.suggest(textFromPage);
                    if (suggest.isEmpty()) {
                        continue;
                    }
                    var newPage = doc.addNewPage(++i);
                    numberOfPages++;
                    var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
                    Canvas canvas = new Canvas(newPage, rect);
                    Paragraph paragraph = new Paragraph("Suggestions:\n");
                    paragraph.setFontSize(25);
                        // сюда вставтье логика добавления нужных ссылок
                    for (Suggest sug : suggest) {
                        PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
                        PdfAction action = PdfAction.createURI(sug.getUrl());
                        annotation.setAction(action);
                        Link link = new Link(sug.getTitle(), annotation);
                        paragraph.add(link.setUnderline());
                        paragraph.add("\n");
                    }
                    canvas.add(paragraph);
                }
                doc.close();
            }
        }
    }
}
