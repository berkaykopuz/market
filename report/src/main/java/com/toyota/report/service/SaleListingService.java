package com.toyota.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.toyota.report.entity.Product;
import com.toyota.report.entity.ProductSale;
import com.toyota.report.entity.Sale;
import com.toyota.report.repository.ProductSaleRepository;
import com.toyota.report.repository.SaleRepository;
import jakarta.servlet.http.HttpServletResponse;
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

        return salePage;
    }

    public List<Sale> getAllSortedSales(String sortBy, String sortOrder){
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        return saleRepository.findAll(sort);
    }

    public Sale getSale(String billId){
        Sale sale = saleRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException(""));
        return sale;
    }

    public void createBillForSale(HttpServletResponse response, String billId) throws IOException {
        Sale sale = getSale(billId);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontHeader = FontFactory.getFont(FontFactory.TIMES_BOLD);
        fontHeader.setSize(22);

        Paragraph headerParagraph = new Paragraph(" MARKET\ntoki konutlari sk. akpinar mah.\n05332147878 "
                , fontHeader);
        headerParagraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
        fontParagraph.setSize(14);

        Paragraph pdfParagraph = new Paragraph("", fontParagraph);
        pdfParagraph.setAlignment(Paragraph.ALIGN_LEFT);

        //start access
        pdfParagraph.add("\nDate  :  " + sale.getSaleDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        pdfParagraph.add(
                "                                                                                     " +
                        "Hour  :  "+ sale.getSaleDate().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        pdfParagraph.add("\nBill Id  :  " + billId);
        pdfParagraph.add("                       " +
                "Payment  :  " + sale.getPaymentMethod());
        pdfParagraph.add("\n----------------------------------------------------------------------------" +
                "------------------------------------\n");

        sale.getProductSales().stream().forEach(s -> {
            pdfParagraph.add(s.getProduct().getId().toString() + "    ");
            pdfParagraph.add("("+ s.getAmount() + " ADET X " + s.getProduct().getPrice() + ")");
            pdfParagraph.add("\n" + s.getProduct().getName() +
                    "                                                                      " +
                    s.getProduct().getPrice() * s.getAmount() + "\n");
        });

        pdfParagraph.add("\n----------------------------------------------------------------------------" +
                "------------------------------------\n");

        pdfParagraph.add("ALINAN PARA  :  "+ sale.getTotalPrice());
        pdfParagraph.add("\nINDIRIM  :      " + (sale.getTotalPrice() - sale.getPaidPrice()));
        pdfParagraph.add("\nODENEN PARA  :  " + sale.getPaidPrice());


        document.add(headerParagraph);
        document.add(pdfParagraph);
        document.close();
    }
}
