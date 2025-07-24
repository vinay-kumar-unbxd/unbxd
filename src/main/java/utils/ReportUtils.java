package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class ReportUtils {
    
    /**
     * Opens the latest ExtentReport in the default browser
     */
    public static void openLatestReport() {
        String reportPath = ExtentManager.getReportPath();
        if (reportPath != null && !reportPath.isEmpty()) {
            openReport(reportPath);
        } else {
            System.out.println("❌ No report path found. Please run tests first.");
        }
    }
    
    /**
     * Opens a specific report file in the default browser
     * @param reportPath Path to the HTML report file
     */
    public static void openReport(String reportPath) {
        try {
            File reportFile = new File(reportPath);
            if (reportFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(reportFile);
                    System.out.println("📄 Report opened in default browser: " + reportPath);
                } else {
                    System.out.println("🖥️ Desktop not supported. Report saved at: " + reportPath);
                }
            } else {
                System.out.println("❌ Report file not found: " + reportPath);
            }
        } catch (IOException e) {
            System.out.println("❌ Error opening report: " + e.getMessage());
            System.out.println("📁 Report available at: " + reportPath);
        }
    }
    
    /**
     * Get summary of the latest test execution
     * @return Summary string
     */
    public static String getTestSummary() {
        return "📊 ExtentReport Summary:\n" +
               "📁 Report Location: " + ExtentManager.getReportPath() + "\n" +
               "🕒 Generated: " + java.time.LocalDateTime.now() + "\n" +
               "🎯 Framework: Unbxd Product Search Automation\n" +
               "🔧 Tools: Selenium + TestNG + ExtentReports + REST Assured";
    }
} 