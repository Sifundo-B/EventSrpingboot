package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.TicketHistory;
import com.TeamC.Eventiefy.repository.TicketHistoryRepo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

@Service
public class TicketService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private TicketHistoryRepo ticketHistoryRepo;

    public String generateAndUploadTicketPDF(TicketHistory ticketHistory) throws IOException, WriterException {
        if (ticketHistory.getCloudinaryUrl() != null && !ticketHistory.getCloudinaryUrl().isEmpty()) {
            return ticketHistory.getCloudinaryUrl();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Event: " + ticketHistory.getEventName());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Ticket Number: " + ticketHistory.getTicketNumber());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Attendee: " + ticketHistory.getAttendeeName());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Event Date: " + ticketHistory.getEventDate());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Location: " + ticketHistory.getLocation());
                contentStream.endText();
            }

            document.save(byteArrayOutputStream);
        }

        File tempFile = File.createTempFile("ticket_" + ticketHistory.getTicketNumber(), ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(byteArrayOutputStream.toByteArray());
        }

        Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                "resource_type", "auto",
                "public_id", "tickets/" + ticketHistory.getAttendeeName(),
                "type", "upload",
                "access_mode", "public"
        ));

        tempFile.delete();

        String cloudinaryUrl = (String) uploadResult.get("secure_url");

        // Generate QR Code using ZXing
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode("Ticket ID: " + ticketHistory.getTicketNumber(), BarcodeFormat.QR_CODE, 250, 250);

        Path tempQrFile = FileSystems.getDefault().getPath("temp_qr_" + ticketHistory.getTicketNumber() + ".png");
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", tempQrFile);

        // Upload QR Code to Cloudinary
        Map qrUploadResult = cloudinary.uploader().upload(tempQrFile.toFile(), ObjectUtils.asMap(
                "resource_type", "auto",
                "public_id", "qr_codes/" + ticketHistory.getTicketNumber(),
                "type", "upload",
                "access_mode", "public"
        ));

        // Clean up temporary QR code file
        tempQrFile.toFile().delete();

        String qrCodeUrl = (String) qrUploadResult.get("secure_url");

        // Save the URLs to the database
        ticketHistory.setCloudinaryUrl(cloudinaryUrl);
        ticketHistory.setQrCodeUrl(qrCodeUrl);
        ticketHistoryRepo.save(ticketHistory);

        return cloudinaryUrl;
    }

    public String generateSignedTicketURL(String cloudinaryUrl) throws Exception {
        try {
            // Log the incoming Cloudinary URL
            System.out.println("Generating signed URL for Cloudinary URL: " + cloudinaryUrl);

            // Extract the public ID from the Cloudinary URL
            String[] urlParts = cloudinaryUrl.split("/upload/");
            if (urlParts.length < 2) {
                throw new Exception("Invalid Cloudinary URL format.");
            }

            String publicId = urlParts[1].replace(".pdf", "");

            // Log the extracted public ID
            System.out.println("Extracted public ID: " + publicId);

            // Generate the signed URL
            String signedUrl = cloudinary.url()
                    .resourceType("image")
                    .type("authenticated")
                    .secure(true)
                    .signed(true)
                    .publicId(publicId)
                    .generate();

            // Log the generated signed URL
            System.out.println("Generated signed URL: " + signedUrl);

            return signedUrl;

        } catch (Exception e) {
            throw new Exception("Error generating signed URL: " + e.getMessage(), e);
        }
    }
}
