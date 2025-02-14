package hotelbookingsystem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.jfree.chart.axis.NumberAxis;

public class Performance extends JFrame {
    private ChartPanel barChartPanel;
    
    public Performance() {
        setTitle("Hotel Performance");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE); // Set background color


        int chartWidth = 400;
        int chartHeight = 300;

        barChartPanel = new ChartPanel(createBarChart("Daily"));
        barChartPanel.setBounds(20, 20, 800, 550);

        ChartPanel pieChartPanel = new ChartPanel(createPieChart());
        pieChartPanel.setBounds(860, 350, chartWidth, chartHeight);

        ChartPanel lineChartPanel = new ChartPanel(createLineChart());
        lineChartPanel.setBounds(860, 20, chartWidth, chartHeight);

        JButton dailyButton = createButton("Daily", 50, 600);
        dailyButton.addActionListener(e -> updateBarChart("Daily"));

        JButton weeklyButton = createButton("Daily", 160, 600);
        dailyButton.addActionListener(e -> updateBarChart("Weekly"));
        
        JButton monthlyButton = createButton("Monthly", 270, 600);
        dailyButton.addActionListener(e -> updateBarChart("Monthly"));

        JButton closeButton = createButton("Close", 550, 620);
        closeButton.addActionListener(e -> dispose());

        add(barChartPanel);
        add(pieChartPanel);
        add(lineChartPanel);
        add(dailyButton);
        add(weeklyButton);
        add(monthlyButton);
        add(closeButton);

        setVisible(true);
    }
    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBounds(x, y, 100, 30);

        // Default colors
        Color defaultBg = new Color(0x393939);
        Color defaultFg = Color.WHITE;
        Color hoverBg = Color.WHITE;
        Color hoverFg = new Color(0x393939);

        // Apply default styling
        button.setBackground(defaultBg);
        button.setForeground(defaultFg);
        button.setBorder(BorderFactory.createLineBorder(defaultBg, 2));
        button.setFocusPainted(false);
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverBg);
                button.setForeground(hoverFg);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(defaultBg);
                button.setForeground(defaultFg);
            }
        });

        add(button);
        return button;
    }

    private JFreeChart createBarChart(String filter) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String query = "";

        switch (filter) {
            case "Daily":
                query = "SELECT DATE(check_in_date) AS date, SUM(rooms.price) AS revenue " +
                        "FROM bookings JOIN rooms ON bookings.room_id = rooms.room_id " +
                        "WHERE status = 'Completed' " +
                        "GROUP BY DATE(check_in_date) " +
                        "ORDER BY DATE(check_in_date);";
                break;
            case "Weekly":
                query = "SELECT YEARWEEK(check_in_date) AS week, SUM(rooms.price) AS revenue " +
                        "FROM bookings JOIN rooms ON bookings.room_id = rooms.room_id " +
                        "WHERE status = 'Completed' " +
                        "GROUP BY YEARWEEK(check_in_date) " +
                        "ORDER BY YEARWEEK(check_in_date);";
                break;
            case "Monthly":
                query = "SELECT DATE_FORMAT(check_in_date, '%Y-%m') AS month, SUM(rooms.price) AS revenue " +
                        "FROM bookings JOIN rooms ON bookings.room_id = rooms.room_id " +
                        "WHERE status = 'Completed' " +
                        "GROUP BY DATE_FORMAT(check_in_date, '%Y-%m') " +
                        "ORDER BY DATE_FORMAT(check_in_date, '%Y-%m');";
                break;
        }

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dataset.addValue(rs.getDouble("revenue"), "Revenue",
                        filter.equals("Daily") ? rs.getString("date") :
                        filter.equals("Weekly") ? "Week " + rs.getString("week") :
                        rs.getString("month"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Revenue by " + filter, filter, "Revenue ($)",
                dataset, org.jfree.chart.plot.PlotOrientation.VERTICAL, false, true, false
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
        return barChart;
    }

    // 1. BAR CHART: Revenue per Date
    private JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy"); // Format: February 2, 2025

         
        String query = "SELECT DATE(check_in_date) AS date, SUM(rooms.price) AS revenue \n" +
                "FROM bookings \n" +
                "JOIN rooms ON bookings.room_id = rooms.room_id \n" +
                "WHERE status = 'Completed' \n" +
                "GROUP BY DATE(check_in_date) \n" +
                "ORDER BY DATE(check_in_date);";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("date"); // Get date from ResultSet
                String formattedDate = dateFormat.format(sqlDate); // Convert to "February 2, 2025"
            dataset.addValue(rs.getDouble("revenue"), "Revenue", formattedDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Revenue by Date", "Date", "Revenue ($)",
                dataset, org.jfree.chart.plot.PlotOrientation.VERTICAL, false, true, false
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
        return barChart;
    }
     private void updateBarChart(String filter) {
        JFreeChart updatedChart = createBarChart(filter);
        barChartPanel.setChart(updatedChart);
    }

    // 2. PIE CHART: Most popular room type
    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        String query = "SELECT room_type, COUNT(*) AS count FROM bookings JOIN rooms ON bookings.room_id = rooms.room_id GROUP BY room_type";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dataset.setValue(rs.getString("room_type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Most Popular Room Types", dataset, true, true, false
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        return pieChart;
    }

    // 3. LINE CHART: Most popular booking dates
    private JFreeChart createLineChart() {
        TimeSeries series = new TimeSeries("Bookings");

        String query = "SELECT check_in_date, COUNT(*) AS count FROM bookings GROUP BY check_in_date ORDER BY check_in_date";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                series.add(new Day(rs.getDate("check_in_date")), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
                "Booking Trends", "Date", "Bookings", dataset, false, true, false
        );

        XYPlot plot = (XYPlot) lineChart.getPlot();

        // Fix date format for x-axis
        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        // Fix y-axis labels to show integers instead of scientific notation
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Force normal integer display

        return lineChart;
    }

    public static void main(String[] args) {
        new Performance();
    }
}
