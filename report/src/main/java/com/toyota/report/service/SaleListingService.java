package com.toyota.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.toyota.report.constant.Constant;
import com.toyota.report.entity.Product;
import com.toyota.report.entity.ProductSale;
import com.toyota.report.entity.Sale;
import com.toyota.report.exception.SaleNotFoundException;
import com.toyota.report.repository.ProductSaleRepository;
import com.toyota.report.repository.SaleRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class SaleListingService {
    private static final Logger logger = LogManager.getLogger(SaleListingService.class);
    private final ProductSaleRepository productSaleRepository;
    private final SaleRepository saleRepository;


    public SaleListingService(ProductSaleRepository productSaleRepository, SaleRepository saleRepository) {
        this.productSaleRepository = productSaleRepository;
        this.saleRepository = saleRepository;
    }

    /**
     * Retrieves a page of sales made on a specific date, sorted by a specified field.
     *
     * @param pageNumber The number of the page to retrieve.
     * @param pageSize The number of sales per page.
     * @param field The field to sort the sales by.
     * @param year The year of the sale date.
     * @param month The month of the sale date.
     * @param day The day of the sale date.
     * @return A page of sales made on the specified date, sorted by the specified field.
     */
    public Page<Sale> getSalesWithPaginationAndSortingByDate(int pageNumber, int pageSize, String field,
                                                       int year, int month, int day){
        logger.debug("Received request to get sales with pagination and sorting by date");

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(field));

        Page<Sale> sales = saleRepository.findBySaleDate(year, month, day, pageable);

        logger.info("Returning sales for the specified date");
        return sales;
    }

    /**
     * Retrieves a page of sales made by a specific cashier, sorted by a specified field.
     *
     * @param pageNumber The number of the page to retrieve.
     * @param pageSize The number of sales per page.
     * @param field The field to sort the sales by.
     * @param cashierName The name of the cashier.
     * @return A page of sales made by the specified cashier, sorted by the specified field.
     */
    public Page<Sale> getSalesWithPaginationAndSortingByCashierName(int pageNumber, int pageSize, String field,
                                                             String cashierName){
        logger.debug("Received request to get sales with pagination and sorting by cashier name");

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(field));

        Page<Sale> sales = saleRepository.findByCashierName(cashierName, pageable);

        logger.info("Returning sales for the specified cashier");
        return sales;
    }

    /**
     * Retrieves all sales, paginated and sorted by a specified field...
     *
     * @param pageNumber The number of the page to retrieve.
     * @param pageSize The number of sales per page.
     * @param field The field to sort the sales by.
     * @return A page of all sales, sorted by the specified field.
     */
    public Page<Sale> getAllSalesWithPaginationAndSorting(int pageNumber, int pageSize, String field){

        logger.debug("Received request to get all sales with pagination and sorting");

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(field));

        Page<Sale> sales = saleRepository.findAll(pageable);

        logger.info("Returning all sales");
        return sales;
    }
    /**
     * Retrieves a specific sale by its bill ID.
     *
     * @param billId The bill ID of the sale to retrieve.
     * @return The Sale object with the specified bill ID.
     * @throws SaleNotFoundException if the sale with the given bill ID is not found.
     */
    public Sale getSale(String billId){
        logger.debug("Received request to get sale with id: " + billId);

        Sale sale = saleRepository.findById(billId)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found with id: " + billId));

        logger.info("Getting specific sale with id:" + billId);
        return sale;
    }

    /**
     * Generates a PDF bill for a specific sale identified by the bill ID.
     *
     * @param response The HttpServletResponse to which content will be written.
     * @param billId The ID of the bill for which the PDF is generated.
     * @throws IOException If there is an input/output error during the PDF creation process.
     */
    public void createBillForSale(HttpServletResponse response, String billId) throws IOException {
        logger.debug("Received request to create bill for sale with id: " + billId);

        Sale sale = getSale(billId);

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Image image = Image.getInstance("./app/images/background.jpg");
        image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        image.setAbsolutePosition(0, 0);
        writer.getDirectContentUnder().addImage(image);

        Font fontHeader = FontFactory.getFont(FontFactory.TIMES_BOLD);
        fontHeader.setSize(22);

        Paragraph headerParagraph = new Paragraph(Constant.COMPANY_NAME +
                Constant.ADDRESS
                , fontHeader);
        headerParagraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
        fontParagraph.setSize(14);

        Paragraph pdfParagraph = new Paragraph("", fontParagraph);
        pdfParagraph.setAlignment(Paragraph.ALIGN_LEFT);

        pdfParagraph.add("\nCashier: " + sale.getCashierName());
        pdfParagraph.add("               " +
                "               Date: " + sale.getSaleDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        pdfParagraph.add("               " +
                "               Hour: " + sale.getSaleDate().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        pdfParagraph.add("\n");


        pdfParagraph.add("Bill Id: " + billId);
        pdfParagraph.add("                             Payment: " + sale.getPaymentMethod());
        pdfParagraph.add("\n");

        pdfParagraph.add("\n----------------------------------------------------------------------------" +
                "------------------------------------\n");

        sale.getProductSales().forEach(s -> {
            pdfParagraph.add(s.getProduct().getId().toString());
            pdfParagraph.add("    ");
            pdfParagraph.add("(" + s.getSaledAmount() + " PIECE X " + s.getSaledPrice() + ")");
            pdfParagraph.add("\n");
            pdfParagraph.add(s.getProduct().getName());




            int spaceCount = 70 - (s.getProduct().getName().length() + (s.getSaledAmount() + " PIECE X " + s.getSaledPrice())
                    .length());
            for (int i = 0; i < spaceCount; i++) {
                pdfParagraph.add(" ");
            }

            pdfParagraph.add((s.getSaledPrice() * s.getSaledAmount()) + "\n");
        });

        pdfParagraph.add("----------------------------------------------------------------------------" +
                "------------------------------------\n");

        pdfParagraph.add("\nTotal Price: " + sale.getTotalPrice());
        pdfParagraph.add("\nDiscount:    " + (sale.getTotalPrice() - sale.getPaidPrice()));
        pdfParagraph.add("\nPaid Price:  " + sale.getPaidPrice());



        document.add(headerParagraph);
        document.add(pdfParagraph);
        document.close();

        logger.info("Creating bill for requested sale with id: " + billId);
    }
}
