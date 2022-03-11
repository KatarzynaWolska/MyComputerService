package pl.wolskak.mycomputerservice.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.Customer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Stream;

public class InvoiceBuilder {

    private BaseFont font;

    public InvoiceBuilder() {
        try {
            this.font = BaseFont.createFont("ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] createInvoice(ComputerDamage computerDamage) throws IOException, DocumentException, URISyntaxException {
        Document document = new Document();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        addLeftHeader(document);
        addTitle(document);
        addCustomerInfo(document, computerDamage.getComputer().getCustomer());

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        addTableHeader(table);
        addTableRows(table, computerDamage);
        table.setSpacingAfter(50f);
        document.add(table);

        addSummary(document, computerDamage);
        addSignaturesArea(document);

        document.close();

        return baos.toByteArray();
    }

    private void addLeftHeader(Document document) throws DocumentException {
        document.add(createParagraph("My Computer Service", Element.ALIGN_LEFT, 10.0f, 12));

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();

        document.add(createParagraph("ul. Przykładowa 1", Element.ALIGN_LEFT, 10.0f, 12));
        document.add(createParagraph("01-001 Przykładowo", Element.ALIGN_LEFT, 10.0f, 12));
        document.add(createParagraph("Tel: 111 222 333", Element.ALIGN_LEFT, 10.0f, 12));
        document.add(createParagraph("Data wystawienia: " + dateFormat.format(cal.getTime()) + "r.", Element.ALIGN_LEFT, 50.0f, 12));
    }

    private void addCustomerInfo(Document document, Customer customer) throws DocumentException {
        Chunk titleChunk = new Chunk("Nabywca:", new Font(font, 12));
        titleChunk.setBackground(BaseColor.LIGHT_GRAY, 2f, 5f, 10f, 5f);
        Paragraph title = new Paragraph(titleChunk);
        title.setAlignment(Element.ALIGN_LEFT);
        title.setSpacingAfter(10.0f);
        document.add(title);

        document.add(createParagraph(customer.getName() + " " + customer.getSurname(), Element.ALIGN_LEFT, 10.0f, 12));
        document.add(createParagraph("E-mail: " + customer.getEmail(), Element.ALIGN_LEFT, 10.0f, 12));
        document.add(createParagraph("Tel: " + customer.getPhoneNumber(), Element.ALIGN_LEFT, 50.0f, 12));
    }

    private void addTitle(Document document) throws DocumentException, IOException {
        document.add(createParagraph("Faktura za usługi naprawy", Element.ALIGN_CENTER, 50.0f, 24));
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("L.p.", "Nazwa", "Sprzęt", "Serwisant", "Cena za usługę")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, new Font(font, 12)));
                    table.addCell(header);
                });
    }

    private void addTableRows(PdfPTable table, ComputerDamage computerDamage) {
        table.addCell(createParagraph("1.", Element.ALIGN_CENTER, 5.0f, 12));
        table.addCell(createParagraph(computerDamage.getTopic(), Element.ALIGN_CENTER, 5.0f, 12));
        table.addCell(createParagraph(computerDamage.getComputer().getBrand() + " " + computerDamage.getComputer().getModel(),
                Element.ALIGN_CENTER, 5.0f, 12));
        table.addCell(createParagraph(computerDamage.getRepairer().getName() + " " + computerDamage.getRepairer().getSurname(),
                Element.ALIGN_CENTER, 5.0f, 12));
        table.addCell(createParagraph(computerDamage.getRepair().getPrice(), Element.ALIGN_CENTER, 5.0f, 12));
    }

    private void addSummary(Document document, ComputerDamage computerDamage) throws DocumentException {
        Chunk summaryChunk = new Chunk("Razem:          " + computerDamage.getRepair().getPrice(), new Font(font, 12));
        summaryChunk.setBackground(BaseColor.LIGHT_GRAY, 5f, 5f, 5f, 5f);
        Paragraph summary = new Paragraph(summaryChunk);
        summary.setAlignment(Element.ALIGN_RIGHT);
        summary.setSpacingBefore(30.0f);
        summary.setSpacingAfter(50.0f);
        document.add(summary);
    }

    private void addSignaturesArea(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(150f);
        table.addCell(createTableCell("Wystawił:"));
        table.addCell(createTableCell("Odebrał:"));
        table.addCell(createTableCell("......................................."));
        table.addCell(createTableCell("......................................."));

        document.add(table);
    }

    private Paragraph createParagraph(String content, int alignment, float spacingAfter, int fontSize) {
        Paragraph paragraph = new Paragraph(new Phrase(content, new Font(font, fontSize)));
        paragraph.setAlignment(alignment);
        paragraph.setSpacingAfter(spacingAfter);
        return paragraph;
    }

    private PdfPCell createTableCell(String content) {
        PdfPCell cell = new PdfPCell(createParagraph(content, Element.ALIGN_CENTER, 50.0f, 12));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
