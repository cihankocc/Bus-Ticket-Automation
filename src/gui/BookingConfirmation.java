package gui;

import controllers.SeatController;
import utils.DatabaseException;

import javax.swing.*;
import java.awt.*;

public class BookingConfirmation extends JDialog {
    private static final long serialVersionUID = 1L;

    private final SeatController seatController = new SeatController();
    private boolean confirmed = false;

    public BookingConfirmation(Frame parent, int userId, int departureId, int seatNumber,
            String userEmail, String route, String date, String time, double price) {
        super(parent, "Rezervasyon Onayı", true);
        setLayout(new BorderLayout());

        JLabel lblInfo = new JLabel(
                "<html>" +
                        "<b>Kullanıcı:</b> " + userEmail + "<br>" +
                        "<b>Koltuk No:</b> " + seatNumber + "<br>" +
                        "<b>Rota:</b> " + route + "<br>" +
                        "<b>Tarih:</b> " + date + "<br>" +
                        "<b>Saat:</b> " + time + "<br>" +
                        "<b>Fiyat:</b> " + String.format("%.2f ₺", price) +
                        "</html>");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton btnConfirm = new JButton("Onayla");
        btnConfirm.setBackground(new Color(34, 139, 34));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);

        JButton btnCancel = new JButton("İptal");
        btnCancel.setFocusPainted(false);

        // Koltuk zaten doluysa onay butonunu kapat
        try {
            if (seatController.isSeatBooked(seatNumber, departureId)) {
                btnConfirm.setEnabled(false);
                lblInfo.setForeground(Color.RED);
                lblInfo.setText("<html>Koltuk " + seatNumber
                        + " zaten rezerve edilmiş.<br>Lütfen başka bir koltuk seçin.</html>");
            }
        } catch (DatabaseException ignored) {
        }

        // Onayla butonu — atomik bookSeat işlemi
        btnConfirm.addActionListener(e -> {
            try {
                boolean success = seatController.bookSeat(userId, departureId, seatNumber);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Rezervasyon başarıyla tamamlandı!",
                            "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    confirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Bu koltuk az önce başkası tarafından alındı. Lütfen başka koltuk seçin.",
                            "Hata", JOptionPane.ERROR_MESSAGE);
                    btnConfirm.setEnabled(false);
                }
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dispose());

        add(lblInfo, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(300, 200));
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}