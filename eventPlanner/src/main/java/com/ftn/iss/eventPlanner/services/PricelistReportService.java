package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.GetPricelistItemDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@Service
public class PricelistReportService {
    @Autowired
    OfferingService offeringService;
    public List<GetPricelistItemDTO> getPricelist() {
        List<GetOfferingDTO> offerings = offeringService.findAll();
        List<GetPricelistItemDTO> pricelist = new ArrayList<>();

        for (GetOfferingDTO offering : offerings) {
            GetPricelistItemDTO item = new GetPricelistItemDTO();
            item.setId(offering.getId());
            item.setOfferingId(offering.getId());
            item.setName(offering.getName());
            item.setPrice(offering.getPrice());
            item.setDiscount(offering.getDiscount());
            double priceWithDiscount = offering.getPrice() * (1 - offering.getDiscount() / 100.0);
            item.setPriceWithDiscount(priceWithDiscount);
            pricelist.add(item);
        }

        return pricelist;
    }

    public byte[] generateReport(List<GetPricelistItemDTO> pricelistItems) throws JRException {
        // Load the JRXML template from classpath
        String reportPath = "template/pricelist_report.jrxml";  // Note: no leading slash
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream(reportPath);
        if (reportStream == null) {
            throw new JRException("Could not find report template: " + reportPath);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Prepare the data for the report
        List<HashMap<String, Object>> reportData = new ArrayList<>();
        for (GetPricelistItemDTO item : pricelistItems) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("serialNumber", item.getId());
            data.put("itemName", item.getName());
            data.put("price", item.getPrice());
            data.put("discount", item.getDiscount());
            data.put("priceWithDiscount", item.getPriceWithDiscount());
            reportData.add(data);
        }

        // Convert data into JRDataSource
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(reportData);

        // Fill the report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), jrDataSource);

        // Export to PDF
        byte[] pdfReport = JasperExportManager.exportReportToPdf(jasperPrint);
        return pdfReport;
    }
}
