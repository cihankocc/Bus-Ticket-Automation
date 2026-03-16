package gui;

import controllers.BookingController;
import models.BookingDetail;
import utils.DatabaseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Giriş yapmış kullanıcının bilet geçmişini gösteren ve
 * rezervasyon iptali yapmasına olanak tanıyan ekran.
 */
public class MyBookingsGui extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private List<BookingDetail> bookings;
    private final int userId;
    private final BookingController bookingController = new BookingController();

    public MyBookingsGui(int userId, String userEmail) {
        this.userId = userId;
        setTitle("Rezervasyonlarım — " + userEmail);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 450);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // Başlık
        JLabel titleLabel = new JLabel("Rezervasyonlarım", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        // Tablo
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        // Alt panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshBtn = new JButton("Yenile");
        refreshBtn.addActionListener(e -> loadBookings());

        JButton cancelBtn = new JButton("Seçili Rezervasyonu İptal Et");
        cancelBtn.setBackground(new Color(220, 53, 69));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> cancelSelectedBooking());

        bottomPanel.add(refreshBtn);
        bottomPanel.add(cancelBtn);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        loadBookings();
    }

    private void loadBookings() {
        try {
            bookings = bookingController.getUserBookings(userId);
            DefaultTableModel model = new DefaultTableModel(
                    new String[] { "Rezervasyon ID", "Kalkış", "Varış", "Tarih", "Saat", "Koltuk No", "Fiyat (₺)",
                            "Tarih (Alınış)" },
                    0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            for (BookingDetail b : bookings) {
                model.addRow(new Object[] {
                        b.getBookingId(),
                        b.getDepartureCity(),
                        b.getArrivalCity(),
                        b.getDate(),
                        b.getTime(),
                        b.getSeatNumber(),
                        String.format("%.2f", b.getPrice()),
                        b.getCreatedAt()
                });
            }
            table.setModel(model);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelectedBooking() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Lütfen iptal etmek istediğiniz rezervasyonu seçin.");
            return;
        }

        BookingDetail selected = bookings.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "\"" + selected.getDepartureCity() + " → " + selected.getArrivalCity() +
                        "\" seferindeki " + selected.getSeatNumber()
                        + " no'lu koltuk rezervasyonunu iptal etmek istiyor musunuz?",
                "Rezervasyon İptali",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // BookingDetail'de dpt_id lazım — bu yüzden booking_id ile sorgulayıp alıyoruz
                // Basit çözüm: cancelBooking direkt burada işlem yapıyor
                String result = bookingController.cancelBooking(
                        selected.getBookingId(),
                        selected.getSeatNumber(),
                        // dpt_id'ye ihtiyacımız var — BookingDetail'e ekleyelim
                        getDptIdFromBooking(selected.getBookingId()));
                JOptionPane.showMessageDialog(this, result, "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                loadBookings(); // Tabloyu yenile
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getDptIdFromBooking(int bookingId) throws DatabaseException {
        var booking = bookingController.getBookingById(bookingId);
        return booking != null ? booking.getDptId() : -1;
    }
}
