package com.sasafashions.report;

import com.sasafashions.security.SessionManager;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.math.BigDecimal;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates customized PDF reports for Sasa Fashions.
 *
 * <p>The class uses OpenPDF to create customer, order, payment,
 * material-stock and purchase reports.</p>
 *
 * @author SASA Group
 */
public class PdfReportGenerator {

    private static final Color PURPLE =
            new Color(74, 45, 125);

    private static final Color LIGHT_PURPLE =
            new Color(226, 218, 240);

    private static final Color LIGHT_GRAY =
            new Color(245, 245, 245);

    private static final Font BUSINESS_FONT =
            new Font(
                    Font.HELVETICA,
                    20,
                    Font.BOLD,
                    PURPLE
            );

    private static final Font REPORT_TITLE_FONT =
            new Font(
                    Font.HELVETICA,
                    15,
                    Font.BOLD,
                    Color.DARK_GRAY
            );

    private static final Font INFORMATION_FONT =
            new Font(
                    Font.HELVETICA,
                    9,
                    Font.NORMAL,
                    Color.DARK_GRAY
            );

    private static final Font HEADER_FONT =
            new Font(
                    Font.HELVETICA,
                    8,
                    Font.BOLD,
                    Color.WHITE
            );

    private static final Font CELL_FONT =
            new Font(
                    Font.HELVETICA,
                    8,
                    Font.NORMAL,
                    Color.BLACK
            );

    private static final Font SUMMARY_FONT =
            new Font(
                    Font.HELVETICA,
                    10,
                    Font.BOLD,
                    PURPLE
            );

    private final ReportDataDAO reportDataDAO =
            new ReportDataDAO();

    private final DecimalFormat moneyFormat =
            new DecimalFormat("#,##0.00");

    private final DecimalFormat numberFormat =
            new DecimalFormat("#,##0.##");

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern(
                    "dd MMMM yyyy HH:mm"
            );

    /**
     * Generates the customer PDF report.
     *
     * @param parent parent form
     */
    public void generateCustomerReport(
            Component parent
    ) {

        try {

            List<LinkedHashMap<String, Object>> rows =
                    reportDataDAO.getCustomerReportData();

            generatePdf(
                    parent,
                    "CUSTOMER REPORT",
                    "Sasa_Fashions_Customer_Report.pdf",
                    rows,
                    null
            );

        } catch (
                SQLException
                | IOException
                | DocumentException exception
        ) {

            showReportError(parent, exception);
        }
    }

    /**
     * Generates the order PDF report.
     *
     * @param parent parent form
     */
    public void generateOrderReport(
            Component parent
    ) {

        try {

            List<LinkedHashMap<String, Object>> rows =
                    reportDataDAO.getOrderReportData();

            generatePdf(
                    parent,
                    "ORDER REPORT",
                    "Sasa_Fashions_Order_Report.pdf",
                    rows,
                    "Total Amount"
            );

        } catch (
                SQLException
                | IOException
                | DocumentException exception
        ) {

            showReportError(parent, exception);
        }
    }

    /**
     * Generates the payment PDF report.
     *
     * @param parent parent form
     */
    public void generatePaymentReport(
            Component parent
    ) {

        try {

            List<LinkedHashMap<String, Object>> rows =
                    reportDataDAO.getPaymentReportData();

            generatePdf(
                    parent,
                    "PAYMENT REPORT",
                    "Sasa_Fashions_Payment_Report.pdf",
                    rows,
                    "Amount"
            );

        } catch (
                SQLException
                | IOException
                | DocumentException exception
        ) {

            showReportError(parent, exception);
        }
    }

    /**
     * Generates the material-stock PDF report.
     *
     * @param parent parent form
     */
    public void generateMaterialStockReport(
            Component parent
    ) {

        try {

            List<LinkedHashMap<String, Object>> rows =
                    reportDataDAO
                            .getMaterialStockReportData();

            generatePdf(
                    parent,
                    "MATERIAL STOCK REPORT",
                    "Sasa_Fashions_Material_Stock_Report.pdf",
                    rows,
                    null
            );

        } catch (
                SQLException
                | IOException
                | DocumentException exception
        ) {

            showReportError(parent, exception);
        }
    }

    /**
     * Generates the purchase PDF report.
     *
     * @param parent parent form
     */
    public void generatePurchaseReport(
            Component parent
    ) {

        try {

            List<LinkedHashMap<String, Object>> rows =
                    reportDataDAO.getPurchaseReportData();

            generatePdf(
                    parent,
                    "PURCHASE REPORT",
                    "Sasa_Fashions_Purchase_Report.pdf",
                    rows,
                    "Total Amount"
            );

        } catch (
                SQLException
                | IOException
                | DocumentException exception
        ) {

            showReportError(parent, exception);
        }
    }

    /**
     * Generates and saves a PDF report.
     *
     * @param parent parent form
     * @param reportTitle report title
     * @param defaultFilename suggested filename
     * @param rows report data
     * @param totalColumn optional column to total
     * @throws IOException when the file cannot be written
     * @throws DocumentException when PDF creation fails
     */
    private void generatePdf(
            Component parent,
            String reportTitle,
            String defaultFilename,
            List<LinkedHashMap<String, Object>> rows,
            String totalColumn
    ) throws IOException, DocumentException {

        if (rows == null || rows.isEmpty()) {

            JOptionPane.showMessageDialog(
                    parent,
                    "There is no information available for "
                            + reportTitle + ".",
                    "No Report Data",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        File outputFile =
                selectOutputFile(
                        parent,
                        defaultFilename
                );

        if (outputFile == null) {
            return;
        }

        Document document = new Document(
                PageSize.A4.rotate(),
                28,
                28,
                45,
                42
        );

        try (
                FileOutputStream outputStream =
                        new FileOutputStream(outputFile)
        ) {

            PdfWriter writer =
                    PdfWriter.getInstance(
                            document,
                            outputStream
                    );

            writer.setPageEvent(
                    new ReportPageFooter()
            );

            document.addTitle(reportTitle);
            document.addAuthor("Sasa Fashions");
            document.addCreator(
                    "Sasa Fashions Management System"
            );
            document.addSubject(reportTitle);

            document.open();

            addReportHeading(
                    document,
                    reportTitle
            );

            PdfPTable table =
                    createReportTable(rows);

            document.add(table);

            document.add(
                    new Paragraph(" ")
            );

            Paragraph recordCount =
                    new Paragraph(
                            "Number of records: "
                                    + rows.size(),
                            SUMMARY_FONT
                    );

            recordCount.setAlignment(
                    Element.ALIGN_RIGHT
            );

            document.add(recordCount);

            if (totalColumn != null) {

                BigDecimal total =
                        calculateTotal(
                                rows,
                                totalColumn
                        );

                Paragraph totalParagraph =
                        new Paragraph(
                                "Report total: UGX "
                                        + moneyFormat.format(
                                                total
                                        ),
                                SUMMARY_FONT
                        );

                totalParagraph.setAlignment(
                        Element.ALIGN_RIGHT
                );

                document.add(totalParagraph);
            }

            document.close();
        }

        JOptionPane.showMessageDialog(
                parent,
                "The report was generated successfully.\n\n"
                        + outputFile.getAbsolutePath(),
                "Report Generated",
                JOptionPane.INFORMATION_MESSAGE
        );

        openGeneratedFile(outputFile);
    }

    /**
     * Adds the business name, report title and generation details.
     */
    private void addReportHeading(
            Document document,
            String reportTitle
    ) {

        Paragraph businessName =
                new Paragraph(
                        "SASA FASHIONS",
                        BUSINESS_FONT
                );

        businessName.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(businessName);

        Paragraph businessDescription =
                new Paragraph(
                        "Tailoring Business Management System",
                        INFORMATION_FONT
                );

        businessDescription.setAlignment(
                Element.ALIGN_CENTER
        );

        document.add(businessDescription);

        Paragraph title =
                new Paragraph(
                        reportTitle,
                        REPORT_TITLE_FONT
                );

        title.setAlignment(
                Element.ALIGN_CENTER
        );

        title.setSpacingBefore(8);
        title.setSpacingAfter(6);

        document.add(title);

        String generatedBy =
                SessionManager.getCurrentUsername();

        if (generatedBy == null
                || generatedBy.isBlank()) {

            generatedBy = "System";
        }

        String generatedAt =
                LocalDateTime.now()
                        .format(dateTimeFormatter);

        Paragraph details =
                new Paragraph(
                        "Generated: "
                                + generatedAt
                                + "     |     Generated by: "
                                + generatedBy,
                        INFORMATION_FONT
                );

        details.setAlignment(
                Element.ALIGN_CENTER
        );

        details.setSpacingAfter(15);

        document.add(details);
    }

    /**
     * Creates a table using the map headings and values.
     */
    private PdfPTable createReportTable(
            List<LinkedHashMap<String, Object>> rows
    ) throws DocumentException {

        LinkedHashMap<String, Object> firstRow =
                rows.getFirst();

        int columnCount =
                firstRow.size();

        PdfPTable table =
                new PdfPTable(columnCount);

        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSplitRows(true);
        table.setSplitLate(false);
        table.setSpacingBefore(5);

        for (String heading : firstRow.keySet()) {

            PdfPCell headerCell =
                    new PdfPCell(
                            new Phrase(
                                    heading,
                                    HEADER_FONT
                            )
                    );

            headerCell.setBackgroundColor(PURPLE);
            headerCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );
            headerCell.setVerticalAlignment(
                    Element.ALIGN_MIDDLE
            );
            headerCell.setPadding(6);

            table.addCell(headerCell);
        }

        int rowNumber = 0;

        for (
                LinkedHashMap<String, Object> row
                : rows
        ) {

            Color rowColor =
                    rowNumber % 2 == 0
                            ? Color.WHITE
                            : LIGHT_GRAY;

            for (
                    Map.Entry<String, Object> entry
                    : row.entrySet()
            ) {

                String formattedValue =
                        formatValue(
                                entry.getKey(),
                                entry.getValue()
                        );

                PdfPCell dataCell =
                        new PdfPCell(
                                new Phrase(
                                        formattedValue,
                                        CELL_FONT
                                )
                        );

                dataCell.setBackgroundColor(rowColor);
                dataCell.setVerticalAlignment(
                        Element.ALIGN_MIDDLE
                );
                dataCell.setPadding(5);

                if (isNumericColumn(
                        entry.getKey()
                )) {

                    dataCell.setHorizontalAlignment(
                            Element.ALIGN_RIGHT
                    );

                } else {

                    dataCell.setHorizontalAlignment(
                            Element.ALIGN_LEFT
                    );
                }

                if (
                        "Stock Condition".equals(
                                entry.getKey()
                        )
                        && "REORDER".equalsIgnoreCase(
                                formattedValue
                        )
                ) {

                    dataCell.setBackgroundColor(
                            new Color(255, 210, 210)
                    );
                }

                table.addCell(dataCell);
            }

            rowNumber++;
        }

        return table;
    }

    /**
     * Formats database values for display in the PDF.
     */
    private String formatValue(
            String columnName,
            Object value
    ) {

        if (value == null) {
            return "";
        }

        if (value instanceof Timestamp timestamp) {

            return new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm"
            ).format(timestamp);
        }

        if (value instanceof java.sql.Date date) {

            return new SimpleDateFormat(
                    "dd-MM-yyyy"
            ).format(date);
        }

        if (value instanceof Number number) {

            if (isMoneyColumn(columnName)) {

                return "UGX "
                        + moneyFormat.format(
                                number
                        );
            }

            return numberFormat.format(number);
        }

        return value.toString();
    }

    /**
     * Identifies monetary columns.
     */
    private boolean isMoneyColumn(
            String columnName
    ) {

        String normalized =
                columnName.toLowerCase();

        return normalized.contains("amount")
                || normalized.contains("price")
                || normalized.contains("cost")
                || normalized.contains("subtotal");
    }

    /**
     * Identifies numeric columns for right alignment.
     */
    private boolean isNumericColumn(
            String columnName
    ) {

        String normalized =
                columnName.toLowerCase();

        return isMoneyColumn(columnName)
                || normalized.contains("quantity")
                || normalized.contains("level");
    }

    /**
     * Calculates the total of a numeric report column.
     */
    private BigDecimal calculateTotal(
            List<LinkedHashMap<String, Object>> rows,
            String columnName
    ) {

        BigDecimal total =
                BigDecimal.ZERO;

        for (
                LinkedHashMap<String, Object> row
                : rows
        ) {

            Object value =
                    row.get(columnName);

            if (value instanceof BigDecimal decimal) {

                total = total.add(decimal);

            } else if (value instanceof Number number) {

                total = total.add(
                        BigDecimal.valueOf(
                                number.doubleValue()
                        )
                );
            }
        }

        return total;
    }

    /**
     * Allows the user to choose the PDF save location.
     */
    private File selectOutputFile(
            Component parent,
            String defaultFilename
    ) {

        JFileChooser fileChooser =
                new JFileChooser();

        fileChooser.setDialogTitle(
                "Save Sasa Fashions Report"
        );

        fileChooser.setFileFilter(
                new FileNameExtensionFilter(
                        "PDF Documents (*.pdf)",
                        "pdf"
                )
        );

        File documentsDirectory =
                new File(
                        System.getProperty("user.home"),
                        "Documents"
                );

        if (documentsDirectory.exists()) {

            fileChooser.setCurrentDirectory(
                    documentsDirectory
            );
        }

        fileChooser.setSelectedFile(
                new File(defaultFilename)
        );

        int result =
                fileChooser.showSaveDialog(parent);

        if (result
                != JFileChooser.APPROVE_OPTION) {

            return null;
        }

        File selectedFile =
                fileChooser.getSelectedFile();

        if (!selectedFile
                .getName()
                .toLowerCase()
                .endsWith(".pdf")) {

            selectedFile = new File(
                    selectedFile.getParentFile(),
                    selectedFile.getName() + ".pdf"
            );
        }

        if (selectedFile.exists()) {

            int overwrite =
                    JOptionPane.showConfirmDialog(
                            parent,
                            "The selected PDF already exists.\n"
                                    + "Do you want to replace it?",
                            "Confirm Replace",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (overwrite
                    != JOptionPane.YES_OPTION) {

                return null;
            }
        }

        return selectedFile;
    }

    /**
     * Opens the completed report using the computer's PDF reader.
     */
    private void openGeneratedFile(
            File outputFile
    ) {

        if (!Desktop.isDesktopSupported()) {
            return;
        }

        try {

            Desktop.getDesktop().open(outputFile);

        } catch (IOException exception) {

            /*
             * The report was saved successfully even if the
             * computer could not open it automatically.
             */
            System.err.println(
                    "Unable to open report automatically: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Displays a reporting error.
     */
    private void showReportError(
            Component parent,
            Exception exception
    ) {

        JOptionPane.showMessageDialog(
                parent,
                "Unable to generate the report.\n"
                        + exception.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Adds the business name and page number to every PDF page.
     */
    private static class ReportPageFooter
            extends PdfPageEventHelper {

        private final Font footerFont =
                new Font(
                        Font.HELVETICA,
                        8,
                        Font.NORMAL,
                        Color.GRAY
                );

        @Override
        public void onEndPage(
                PdfWriter writer,
                Document document
        ) {

            PdfPTable footer =
                    new PdfPTable(2);

            float availableWidth =
                    document.getPageSize().getWidth()
                            - document.leftMargin()
                            - document.rightMargin();

            footer.setTotalWidth(availableWidth);
            footer.setLockedWidth(true);

            PdfPCell businessCell =
                    new PdfPCell(
                            new Phrase(
                                    "Sasa Fashions",
                                    footerFont
                            )
                    );

            businessCell.setBorder(Rectangle.TOP);
            businessCell.setPaddingTop(5);

            PdfPCell pageCell =
                    new PdfPCell(
                            new Phrase(
                                    "Page "
                                            + writer.getPageNumber(),
                                    footerFont
                            )
                    );

            pageCell.setBorder(Rectangle.TOP);
            pageCell.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );
            pageCell.setPaddingTop(5);

            footer.addCell(businessCell);
            footer.addCell(pageCell);

            footer.writeSelectedRows(
                    0,
                    -1,
                    document.leftMargin(),
                    28,
                    writer.getDirectContent()
            );
        }
    }
}