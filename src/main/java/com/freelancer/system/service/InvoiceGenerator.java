package com.freelancer.system.service;

import com.freelancer.system.model.Project;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class InvoiceGenerator {

    public static String generateInvoice(Project project) {
        String fileName = "Invoice_" + project.getId() + "_" + project.getName().replaceAll("\\s+", "_") + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            document.add(new Paragraph("Date: " + LocalDate.now(), normalFont));
            document.add(new Paragraph(" ", normalFont)); // Spacer

            document.add(new Paragraph("Bill To:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph("Client: " + project.getClient(), normalFont));
            document.add(new Paragraph(" ", normalFont));

            document.add(new Paragraph("Project Details:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph("Project Name: " + project.getName(), normalFont));
            document.add(new Paragraph("Status: " + project.getStatus(), normalFont));
            document.add(new Paragraph(" ", normalFont));

            document.add(new Paragraph("Payment Details:", new Font(Font.HELVETICA, 12, Font.BOLD)));
            document.add(new Paragraph("Total Amount: $" + String.format("%.2f", project.getPayment()), normalFont));

            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph("Thank you for your business!", new Font(Font.HELVETICA, 10, Font.ITALIC)));

            document.close();
            return fileName;

        } catch (DocumentException | IOException e) {
            System.err.println("Error generating invoice: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
