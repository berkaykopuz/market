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
     * Finds sales by the specified sale date.
     *
     * @param year The year of the sale date to search for.
     * @param month The month of the sale date to search for.
     * @param day The day of the sale date to search for.
     * @return A list of Sale objects that match the specified sale date.
     */
    public List<Sale> findBySaleDate(int year, int month, int day){
        return saleRepository.findBySaleDate(year, month, day);
    }
    /**
     * Retrieves a paginated list of all sales.
     *
     * @param pageNo The page number to retrieve.
     * @param pageSize The number of items per page.
     * @return A Page object containing Sale objects for the requested page.
     */
    public Page<Sale> getAllSales(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Sale> salePage = saleRepository.findAll(pageable);

        logger.info("Getting all sales from database");
        return salePage;
    }
    /**
     * Retrieves all sales sorted by the specified field and order.
     *
     * @param sortBy The field to sort by.
     * @param sortOrder The order of sorting, either "ASC" for ascending or "DESC" for descending.
     * @return A sorted list of Sale objects.
     */
    public List<Sale> getAllSortedSales(String sortBy, String sortOrder){
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        logger.info("Getting all sorted sales");
        return saleRepository.findAll(sort);
    }
    /**
     * Retrieves a specific sale by its bill ID.
     *
     * @param billId The bill ID of the sale to retrieve.
     * @return The Sale object with the specified bill ID.
     * @throws SaleNotFoundException if the sale with the given bill ID is not found.
     */
    public Sale getSale(String billId){
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
        Sale sale = getSale(billId);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
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
            pdfParagraph.add("(" + s.getSaledAmount() + " PIECE X " + s.getProduct().getPrice() + ")");
            pdfParagraph.add("\n");
            pdfParagraph.add(s.getProduct().getName());


            int spaceCount = 70 - (s.getProduct().getName().length() + (s.getSaledAmount() + " PIECE X " + s.getProduct().getPrice()).length());
            for (int i = 0; i < spaceCount; i++) {
                pdfParagraph.add(" ");
            }

            pdfParagraph.add((s.getProduct().getPrice() * s.getSaledAmount()) + "\n");
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
