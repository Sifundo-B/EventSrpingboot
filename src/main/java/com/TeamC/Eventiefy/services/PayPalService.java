package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.Ticket;
import com.TeamC.Eventiefy.entity.TicketHistory;
import com.TeamC.Eventiefy.enums.TicketStatus;
import com.TeamC.Eventiefy.repository.EventRepository;
import com.TeamC.Eventiefy.repository.TicketHistoryRepo;
import com.TeamC.Eventiefy.repository.TicketRepository;
import com.TeamC.Eventiefy.user.User;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PayPalService {

    @Autowired
    private APIContext apiContext;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketHistoryRepo ticketHistoryRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserServiceImpl userService;

    private static final Logger LOGGER = Logger.getLogger(PayPalService.class.getName());

    public String createPayment(Double total, Long eventId) throws PayPalRESTException {
        String formattedTotal = String.format(Locale.US, "%.2f", total);

        Amount amount = new Amount();
        amount.setCurrency("USD");  // Change to a supported currency, e.g., USD or EUR
        amount.setTotal(formattedTotal);

        LOGGER.info("Formatted Payment amount: " + formattedTotal);

        Transaction transaction = new Transaction();
        transaction.setDescription("Event Ticket Purchase");
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(getRedirectURLs(eventId));

        Payment createdPayment = payment.create(apiContext);

        for (Links link : createdPayment.getLinks()) {
            if (link.getRel().equals("approval_url")) {
                return link.getHref();
            }
        }

        return null;
    }

    private RedirectUrls getRedirectURLs(Long eventId) {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:4200/payment-cancel?eventId=" + eventId);  // Updated cancel URL
        // Use a fixed URL for return_url and handle the parameters within the frontend
        redirectUrls.setReturnUrl("http://localhost:4200/payment-success?eventId=" + eventId);
        return redirectUrls;
    }
    @Transactional
    public void handlePaymentSuccess(String paymentId, String payerId, Long eventId) {
        try {
            LOGGER.log(Level.INFO, "Handling payment success: paymentId={0}, payerId={1}, eventId={2}", new Object[]{paymentId, payerId, eventId});

            Payment payment = Payment.get(apiContext, paymentId);
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            Payment executedPayment = payment.execute(apiContext, paymentExecution);

            if ("approved".equals(executedPayment.getState())) {
                Optional<Event> eventOptional = eventRepository.findById(eventId);
                if (eventOptional.isPresent()) {
                    Event event = eventOptional.get();
                    if (event.getNumberOfTickets() > 0) {
                        event.setNumberOfTickets(event.getNumberOfTickets());

                        User user = userService.getCurrentUser();
                        if (user != null) {
                            Ticket ticket = new Ticket();
                            ticket.setEvent(event);
                            ticket.setStatus(TicketStatus.SOLD);
                            ticket.setPrice(event.getPrice());
                            ticket.setUser(user);

                            ticketRepository.save(ticket);
                            eventRepository.save(event);

                            // Save ticket history
                            TicketHistory ticketHistory = new TicketHistory();
                            ticketHistory.setEventName(event.getName());
                            ticketHistory.setImage(event.getImageUrl());
                            ticketHistory.setAttendeeName(user.getFirstName() + " " + user.getLastName());
                            ticketHistory.setTicketNumber(UUID.randomUUID().toString()); // Generate unique ticket number
                            ticketHistory.setPurchaseDate(LocalDate.now());
                            ticketHistory.setPrice(event.getPrice());
                            ticketHistory.setEventDate(event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                            ticketHistory.setLocation(event.getAddress());
                            ticketHistory.setTicketType("Regular"); // Customize as needed
                            ticketHistory.setUser(user);

                            ticketHistoryRepository.save(ticketHistory);

                            // Generate ticket as PDF
                            generateTicketPDF(ticketHistory);

                            LOGGER.log(Level.INFO, "Ticket purchased and history saved successfully.");
                        } else {
                            throw new RuntimeException("User not found");
                        }
                    } else {
                        throw new RuntimeException("No tickets available");
                    }
                } else {
                    throw new RuntimeException("Event not found");
                }
            } else {
                throw new RuntimeException("Payment not approved");
            }
        } catch (PayPalRESTException | IOException | WriterException e) {
            LOGGER.log(Level.SEVERE, "Error handling payment success: ", e);
            throw new RuntimeException("Error handling payment success", e);
        }
    }

    private void generateTicketPDF(TicketHistory ticketHistory) throws IOException, WriterException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                BufferedImage bufferedImage = null;
                if (ticketHistory.getImage() != null) {
                    bufferedImage = ImageIO.read(new URL(ticketHistory.getImage()));
                } else {
                    // Optionally use a default image
                    bufferedImage = ImageIO.read(new File("path/to/default/image.png"));
                }

                if (bufferedImage != null) {
                    // Save the BufferedImage to a temporary file
                    File tempImageFile = File.createTempFile("event_image", ".png");
                    ImageIO.write(bufferedImage, "png", tempImageFile);

                    // Create a PDImageXObject from the temporary file
                    PDImageXObject pdImage = PDImageXObject.createFromFileByContent(tempImageFile, document);

                    // Draw the image on the PDF
                    float imageHeight = 200;
                    float imageWidth = pdImage.getWidth() * (imageHeight / pdImage.getHeight());
                    float xPosition = (page.getMediaBox().getWidth() - imageWidth) / 2;
                    float yPosition = page.getMediaBox().getHeight() - imageHeight - 50;

                    contentStream.drawImage(pdImage, xPosition, yPosition, imageWidth, imageHeight);

                    // Delete the temporary image file
                    tempImageFile.delete();
                } else {
                    LOGGER.log(Level.WARNING, "Image could not be loaded for ticket generation.");
                }

                // Set text position below the image
                float textYPosition = page.getMediaBox().getHeight() - 250;

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(50, textYPosition);
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

                // Generate QR code for the ticket details URL
                String ticketDetailsUrl = "http://localhost:8080/api/v1/tickets/details/" + ticketHistory.getTicketNumber();
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(ticketDetailsUrl, BarcodeFormat.QR_CODE, 150, 150);
                BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                // Save QR code to temporary file
                File qrImageFile = File.createTempFile("qr_code", ".png");
                ImageIO.write(qrImage, "png", qrImageFile);

                // Add QR code to the PDF
                PDImageXObject qrPdImage = PDImageXObject.createFromFileByContent(qrImageFile, document);
                contentStream.drawImage(qrPdImage, 50, textYPosition - 200, 150, 150);

                // Delete the temporary QR image file
                qrImageFile.delete();
            }

            // Save the document to a temporary file
            File tempFile = File.createTempFile("ticket_" + ticketHistory.getTicketNumber(), ".pdf");
            document.save(tempFile);

            // Upload the PDF to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap("resource_type", "auto"));

            // Set the URL in the TicketHistory
            String cloudinaryUrl = (String) uploadResult.get("url");
            ticketHistory.setCloudinaryUrl(cloudinaryUrl);

            // Clean up the temporary file
            tempFile.delete();

            LOGGER.log(Level.INFO, "Ticket PDF generated and uploaded to Cloudinary successfully at: " + cloudinaryUrl);
        }
    }
}
