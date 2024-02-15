package com.toyota.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.toyota.report.entity.Product;
import com.toyota.report.entity.ProductSale;
import com.toyota.report.entity.Sale;
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

    public List<Sale> findBySaleDate(int year, int month, int day){
        return saleRepository.findBySaleDate(year, month, day);
    }

    public Page<Sale> getAllSales(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Sale> salePage = saleRepository.findAll(pageable);

        logger.info("Getting all sales from database");
        return salePage;
    }

    public List<Sale> getAllSortedSales(String sortBy, String sortOrder){
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        logger.info("Getting all sorted sales");
        return saleRepository.findAll(sort);
    }

    public Sale getSale(String billId){
        Sale sale = saleRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException(""));
        logger.info("Getting specific sale with id:" + billId);
        return sale;
    }

    public void createBillForSale(HttpServletResponse response, String billId) throws IOException {
        Sale sale = getSale(billId);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontHeader = FontFactory.getFont(FontFactory.TIMES_BOLD);
        fontHeader.setSize(22);

        Paragraph headerParagraph = new Paragraph(" MARKET\n\n" +
                "toki konutlari sk. akpinar mah.\n05332147878\nKocaeli "
                , fontHeader);
        headerParagraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
        fontParagraph.setSize(14);

        Paragraph pdfParagraph = new Paragraph("", fontParagraph);
        pdfParagraph.setAlignment(Paragraph.ALIGN_LEFT);


        pdfParagraph.add("\nDate: " + sale.getSaleDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        pdfParagraph.add("               Hour: " + sale.getSaleDate().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        pdfParagraph.add("\n");


        pdfParagraph.add("Bill Id: " + billId);
        pdfParagraph.add("                             Payment: " + sale.getPaymentMethod());
        pdfParagraph.add("\n");

        pdfParagraph.add("\n----------------------------------------------------------------------------" +
                "------------------------------------\n");

        sale.getProductSales().forEach(s -> {
            pdfParagraph.add(s.getProduct().getId().toString());
            pdfParagraph.add("    ");
            pdfParagraph.add("(" + s.getAmount() + " PIECE X " + s.getProduct().getPrice() + ")");
            pdfParagraph.add("\n");
            pdfParagraph.add(s.getProduct().getName());


            int spaceCount = 70 - (s.getProduct().getName().length() + (s.getAmount() + " PIECE X " + s.getProduct().getPrice()).length());
            for (int i = 0; i < spaceCount; i++) {
                pdfParagraph.add(" ");
            }

            pdfParagraph.add((s.getProduct().getPrice() * s.getAmount()) + "\n");
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
