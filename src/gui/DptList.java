package gui;

import controllers.DptController;
import controllers.UserController;
import models.Dpt;
import models.User;
import utils.DatabaseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DptList extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private final DptController dptController = new DptController();

    public DptList() {
        setTitle("Sefer Listesi");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 580);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setContentPane(contentPane);

        // Üst panel: Başlık
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(" Mevcut Seferler", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Üst sağ: Butonlar (Rezervasyonlarım ve Çıkış)
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton myBookingsBtn = new JButton("📋 Rezervasyonlarım");
        myBookingsBtn.setBackground(new Color(23, 145, 255));
        myBookingsBtn.setForeground(Color.WHITE);
        myBookingsBtn.setFocusPainted(false);
        myBookingsBtn.addActionListener(e -> openMyBookings());
        topRightPanel.add(myBookingsBtn);

        JButton logoutBtn = new JButton("🚪 Çıkış Yap");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            LoginGui.loggedInEmail = null; // Session temizle
            this.dispose();
            new LoginGui().setVisible(true);
        });
        topRightPanel.add(logoutBtn);

        topPanel.add(topRightPanel, BorderLayout.EAST);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // Orta: Tablo
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        // Alt: İpucu + Yenile butonu
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel hint = new JLabel("İpucu: Koltuk seçimi için bir sefere çift tıklayın.", SwingConstants.LEFT);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        bottomPanel.add(hint, BorderLayout.WEST);

        JButton refreshBtn = new JButton("↻ Yenile");
        refreshBtn.addActionListener(e -> loadDptList());
        bottomPanel.add(refreshBtn, BorderLayout.EAST);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        loadDptList();

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int departureId = (int) table.getValueAt(row, 0);
                        new SeatSelection(departureId).setVisible(true);
                    }
                }
            }
        });
    }

    private void loadDptList() {
        try {
            List<Dpt> dptList = dptController.getAllDpts();
            DefaultTableModel model = new DefaultTableModel(
                    new String[] { "ID", "Kalkış", "Varış", "Tarih", "Saat", "Fiyat (₺)", "Otobüs ID", "Boş Koltuk",
                            "Rota" },
                    0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            for (Dpt dpt : dptList) {
                model.addRow(new Object[] {
                        dpt.getDepartureId(), dpt.getDepartureCity(), dpt.getArrivalCity(),
                        dpt.getDate(), dpt.getTime(),
                        String.format("%.2f", dpt.getPrice()),
                        dpt.getBusId(), dpt.getAvailableSeats(), dpt.getRouteInfo()
                });
            }
            table.setModel(model);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMyBookings() {
        try {
            UserController uc = new UserController();
            User user = uc.getUserByEmail(LoginGui.loggedInEmail);
            if (user != null) {
                new MyBookingsGui(user.getUserId(), user.getEmail()).setVisible(true);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}