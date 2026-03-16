package gui;

import controllers.DptController;
import controllers.SeatController;
import controllers.UserController;
import models.Dpt;
import models.User;
import utils.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelection extends JFrame {
    private static final long serialVersionUID = 1L;
    private final SeatController seatController;
    private final UserController userController;
    private final DptController dptController;
    private final int departureId;
    private final List<JButton> seatButtons;

    public SeatSelection(int departureId) {
        this.departureId = departureId;
        this.seatController = new SeatController();
        this.userController = new UserController();
        this.dptController = new DptController();
        this.seatButtons = new ArrayList<>();

        setTitle("Otobüs Yerleşim Planı");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JLabel header = new JLabel("Otobüs Yerleşim Planı", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(header, BorderLayout.NORTH);

        // Arka planı otobüs zemini hissiyatı vermek için koyu gri yapalım
        JPanel busContainer = new JPanel(new BorderLayout(5, 5));
        busContainer.setBackground(Color.DARK_GRAY);
        busContainer.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        // Şoför Mahalli (Ön Kısım)
        JPanel frontPanel = new JPanel(new BorderLayout());
        frontPanel.setBackground(Color.DARK_GRAY);
        JLabel driverLabel = new JLabel("Şoför Mahalli 🛞", SwingConstants.LEFT);
        driverLabel.setForeground(Color.WHITE);
        driverLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel doorLabel = new JLabel("🚪 Ön Kapı", SwingConstants.RIGHT);
        doorLabel.setForeground(Color.WHITE);
        doorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        frontPanel.add(driverLabel, BorderLayout.WEST);
        frontPanel.add(doorLabel, BorderLayout.EAST);
        frontPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        busContainer.add(frontPanel, BorderLayout.NORTH);

        // Koltuklar Paneli: 10 Satır, 5 Sütun (3. sütun koridor)
        JPanel seatPanel = new JPanel(new GridLayout(10, 5, 10, 10));
        seatPanel.setBackground(Color.DARK_GRAY); // Zemin rengi

        int seatIndex = 1;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 5; col++) {
                if (col == 2) {
                    // Koridor boşluğu
                    JLabel corridor = new JLabel("");
                    seatPanel.add(corridor);
                } else {
                    // Koltuk butonu
                    final int seatNumber = seatIndex++;
                    JButton seatBtn = new JButton("K-" + seatNumber);
                    seatBtn.setFont(new Font("Arial", Font.BOLD, 12));
                    seatBtn.setBackground(new Color(40, 167, 69)); // Yeşil (Boş)
                    seatBtn.setForeground(Color.WHITE);
                    seatBtn.setFocusPainted(false);
                    // Hafif yuvarlak köşeler ve belirginleşmiş sınırlar (Nimbus destekler)
                    seatBtn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.BLACK, 1),
                            BorderFactory.createEmptyBorder(8, 2, 8, 2)));

                    seatBtn.addActionListener(e -> handleSeatSelection(seatBtn, seatNumber));
                    seatButtons.add(seatBtn);
                    seatPanel.add(seatBtn);
                }
            }
        }

        busContainer.add(seatPanel, BorderLayout.CENTER);
        updateSeatStatuses();
        mainPanel.add(busContainer, BorderLayout.CENTER);

        // Renk Açıklaması (Alt Kısım)
        JPanel legendPanel = new JPanel(new FlowLayout());
        JLabel availableLabel = new JLabel("  Boş Koltuk  ");
        availableLabel.setOpaque(true);
        availableLabel.setBackground(new Color(40, 167, 69));
        availableLabel.setForeground(Color.WHITE);
        availableLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel bookedLabel = new JLabel("  Dolu Koltuk  ");
        bookedLabel.setOpaque(true);
        bookedLabel.setBackground(new Color(220, 53, 69));
        bookedLabel.setForeground(Color.WHITE);
        bookedLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        legendPanel.add(availableLabel);
        legendPanel.add(bookedLabel);
        mainPanel.add(legendPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void handleSeatSelection(JButton seatBtn, int seatNumber) {
        try {
            User user = userController.getUserByEmail(LoginGui.loggedInEmail);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Kullanıcı bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Dpt dpt = dptController.getDptById(departureId);
            if (dpt == null) {
                JOptionPane.showMessageDialog(this, "Sefer bilgisi bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BookingConfirmation dialog = new BookingConfirmation(
                    this, user.getUserId(), departureId, seatNumber, user.getEmail(),
                    dpt.getDepartureCity() + " → " + dpt.getArrivalCity(),
                    dpt.getDate(), dpt.getTime(), dpt.getPrice());
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                seatBtn.setBackground(Color.RED);
                seatBtn.setEnabled(false);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "İşlem sırasında hata oluştu: " + e.getMessage(), "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSeatStatuses() {
        try {
            for (int i = 0; i < seatButtons.size(); i++) {
                if (seatController.isSeatBooked(i + 1, departureId)) {
                    seatButtons.get(i).setBackground(Color.RED);
                    seatButtons.get(i).setEnabled(false);
                }
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Koltuk durumları yüklenirken hata oluştu.", "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}