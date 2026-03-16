package gui;

import controllers.BusController;
import controllers.DptController;
import controllers.PassengerController;
import models.Bus;
import models.Dpt;
import models.Passenger;
import utils.DatabaseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminPanel extends JFrame {
    private static final long serialVersionUID = 1L;

    private final BusController busController = new BusController();
    private final DptController dptController = new DptController();
    private final PassengerController passengerController = new PassengerController();

    // Bus fields & table
    private JTextField plateField, captainField;
    private JTable busTable;
    private JComboBox<Bus> busComboBox;

    // Departure fields & table
    private JTextField departureIdField, departureCityField, arrivalCityField,
            dateField, timeField, ticketPriceField, availableSeatsField, routeInfoField;
    private JTable dptTable;

    // Passenger fields & table
    private JTextField passengerIdField, firstNameField, lastNameField, emailField, passwordField;
    private JTable passTable;

    public AdminPanel() {
        setTitle("Yönetici Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 700);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JTabbedPane tabs = new JTabbedPane();
        contentPane.add(tabs, BorderLayout.CENTER);
        tabs.addTab("Otobüs Yönetimi", null, createBusPanel(), "Otobüs ekle, günncele, sil, listele");
        tabs.addTab("Sefer Yönetimi", null, createDptPanel(), "Sefer ekle, güncelle, sil, listele");
        tabs.addTab("Yolcu Yönetimi", null, createPassPanel(), "Yolcu ekle, güncelle, sil, listele");

        // Üst panel: Başlık ve Çıkış Butonu
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(" Yönetici Paneli", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("🚪 Çıkış Yap");
        logoutBtn.setBackground(new Color(220, 53, 69)); // Kırmızı
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginGui().setVisible(true);
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // Paneller oluştuktan sonra tabloları doldur
        refreshBusTable();
        refreshDptTable();
        refreshPassTable();
        loadBusesToCombo();
    }

    // ── Yardımcı metotlar ──────────────────────────────────────────────────────

    private JTextField field() {
        return new JTextField(15);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component cmp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(cmp, gbc);
    }

    private JButton btn(String text, Color bg, Runnable action) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.addActionListener(e -> action.run());
        return b;
    }

    private GridBagConstraints newGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Hata", JOptionPane.ERROR_MESSAGE);
    }

    // ── Panel Oluşturucular ────────────────────────────────────────────────────

    private JPanel createBusPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Otobüs Bilgileri"));
        GridBagConstraints gbc = newGbc();

        plateField = field();
        captainField = field();
        addRow(formPanel, gbc, 0, "Plaka:", plateField);
        addRow(formPanel, gbc, 1, "Kaptan:", captainField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btn("Ekle", new Color(34, 139, 34), this::handleBusAdd));
        btnPanel.add(btn("Güncelle", new Color(23, 145, 255), this::handleBusUpdate));
        btnPanel.add(btn("Sil", new Color(220, 53, 69), this::handleBusDelete));
        btnPanel.add(btn("Temizle", Color.GRAY, () -> {
            plateField.setText("");
            captainField.setText("");
        }));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        busTable = new JTable();
        busTable.setRowHeight(24);
        busTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = busTable.getSelectedRow();
                if (row >= 0) {
                    plateField.setText(busTable.getValueAt(row, 1).toString());
                    captainField.setText(busTable.getValueAt(row, 2).toString());
                }
            }
        });

        mainPanel.add(formPanel, BorderLayout.WEST);
        mainPanel.add(new JScrollPane(busTable), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createDptPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Sefer Bilgileri"));
        GridBagConstraints gbc = newGbc();

        departureIdField = field();
        departureIdField.setEditable(false);
        departureIdField.setToolTipText("Otomatik atanır");
        departureCityField = field();
        arrivalCityField = field();
        dateField = field();
        timeField = field();
        ticketPriceField = field();
        availableSeatsField = field();
        availableSeatsField.setText("40");
        routeInfoField = field();
        busComboBox = new JComboBox<>();

        addRow(formPanel, gbc, 0, "Sefer ID (Sil/Güncelle için):", departureIdField);
        addRow(formPanel, gbc, 1, "Kalkış:", departureCityField);
        addRow(formPanel, gbc, 2, "Varış:", arrivalCityField);
        addRow(formPanel, gbc, 3, "Tarih (Y-A-G):", dateField);
        addRow(formPanel, gbc, 4, "Saat (S:D):", timeField);
        addRow(formPanel, gbc, 5, "Fiyat:", ticketPriceField);
        addRow(formPanel, gbc, 6, "Boş Koltuk:", availableSeatsField);
        addRow(formPanel, gbc, 7, "Rota:", routeInfoField);
        addRow(formPanel, gbc, 8, "Otobüs:", busComboBox);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btn("Ekle", new Color(34, 139, 34), this::handleDptAdd));
        btnPanel.add(btn("Güncelle", new Color(23, 145, 255), this::handleDptUpdate));
        btnPanel.add(btn("Sil", new Color(220, 53, 69), this::handleDptDelete));
        btnPanel.add(btn("Temizle", Color.GRAY, () -> {
            departureIdField.setText("");
            departureCityField.setText("");
            arrivalCityField.setText("");
            dateField.setText("");
            timeField.setText("");
            ticketPriceField.setText("");
            availableSeatsField.setText("40");
            routeInfoField.setText("");
        }));

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        dptTable = new JTable();
        dptTable.setRowHeight(24);
        dptTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = dptTable.getSelectedRow();
                if (row >= 0) {
                    departureIdField.setText(dptTable.getValueAt(row, 0).toString());
                    departureCityField.setText(dptTable.getValueAt(row, 1).toString());
                    arrivalCityField.setText(dptTable.getValueAt(row, 2).toString());
                    dateField.setText(dptTable.getValueAt(row, 3).toString());
                    timeField.setText(dptTable.getValueAt(row, 4).toString());
                    ticketPriceField.setText(dptTable.getValueAt(row, 5).toString());
                    availableSeatsField.setText(dptTable.getValueAt(row, 7).toString());
                    routeInfoField
                            .setText(dptTable.getValueAt(row, 8) != null ? dptTable.getValueAt(row, 8).toString() : "");
                    // İlgili otobüsü seç
                    int bId = Integer.parseInt(dptTable.getValueAt(row, 6).toString());
                    for (int i = 0; i < busComboBox.getItemCount(); i++) {
                        if (busComboBox.getItemAt(i).getBusId() == bId) {
                            busComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        mainPanel.add(formPanel, BorderLayout.WEST);
        mainPanel.add(new JScrollPane(dptTable), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createPassPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Yolcu Bilgileri"));
        GridBagConstraints gbc = newGbc();

        passengerIdField = field();
        passengerIdField.setEditable(false);
        passengerIdField.setToolTipText("Otomatik atanır");
        firstNameField = field();
        lastNameField = field();
        emailField = field();
        passwordField = field();

        addRow(formPanel, gbc, 0, "Yolcu ID (Sil/Güncelle için):", passengerIdField);
        addRow(formPanel, gbc, 1, "Ad:", firstNameField);
        addRow(formPanel, gbc, 2, "Soyad:", lastNameField);
        addRow(formPanel, gbc, 3, "E-posta:", emailField);
        addRow(formPanel, gbc, 4, "Şifre (Değişecekse):", passwordField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btn("Ekle", new Color(34, 139, 34), this::handlePassengerAdd));
        btnPanel.add(btn("Güncelle", new Color(23, 145, 255), this::handlePassengerUpdate));
        btnPanel.add(btn("Sil", new Color(220, 53, 69), this::handlePassengerDelete));
        btnPanel.add(btn("Temizle", Color.GRAY, () -> {
            passengerIdField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            passwordField.setText("");
        }));

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        passTable = new JTable();
        passTable.setRowHeight(24);
        passTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = passTable.getSelectedRow();
                if (row >= 0) {
                    passengerIdField.setText(passTable.getValueAt(row, 0).toString());
                    firstNameField.setText(passTable.getValueAt(row, 1).toString());
                    lastNameField.setText(passTable.getValueAt(row, 2).toString());
                    emailField.setText(passTable.getValueAt(row, 3).toString());
                    passwordField.setText(""); // Güvenlik gereği şifreyi geri doldurma
                }
            }
        });

        mainPanel.add(formPanel, BorderLayout.WEST);
        mainPanel.add(new JScrollPane(passTable), BorderLayout.CENTER);
        return mainPanel;
    }

    // ── Table Yükleyicileri ───────────────────────────────────────────────────

    private void refreshBusTable() {
        try {
            List<Bus> buses = busController.getAllBuses();
            DefaultTableModel model = new DefaultTableModel(new String[] { "ID", "Plaka", "Kaptan" }, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            for (Bus b : buses)
                model.addRow(new Object[] { b.getBusId(), b.getPlate(), b.getCaptain() });
            busTable.setModel(model);
        } catch (DatabaseException e) {
            showErr("Otobüsler yüklenemedi: " + e.getMessage());
        }
    }

    private void loadBusesToCombo() {
        try {
            busComboBox.removeAllItems();
            for (Bus bus : busController.getAllBuses())
                busComboBox.addItem(bus);
        } catch (DatabaseException ignored) {
        }
    }

    private void refreshDptTable() {
        try {
            List<Dpt> dpts = dptController.getAllDpts();
            DefaultTableModel model = new DefaultTableModel(
                    new String[] { "ID", "Kalkış", "Varış", "Tarih", "Saat", "Fiyat", "Bus ID", "Koltuk", "Rota" }, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            for (Dpt d : dpts) {
                model.addRow(new Object[] { d.getDepartureId(), d.getDepartureCity(), d.getArrivalCity(),
                        d.getDate(), d.getTime(), d.getPrice(), d.getBusId(), d.getAvailableSeats(),
                        d.getRouteInfo() });
            }
            dptTable.setModel(model);
        } catch (DatabaseException e) {
            showErr("Seferler yüklenemedi: " + e.getMessage());
        }
    }

    private void refreshPassTable() {
        try {
            List<Passenger> passes = passengerController.getAllPassengers();
            DefaultTableModel model = new DefaultTableModel(new String[] { "ID", "Ad", "Soyad", "E-posta" }, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            for (Passenger p : passes) {
                model.addRow(new Object[] { p.getUserId(), p.getFirstName(), p.getLastName(), p.getEmail() });
            }
            passTable.setModel(model);
        } catch (DatabaseException e) {
            showErr("Yolcular yüklenemedi: " + e.getMessage());
        }
    }

    // ── İşleyiciler (Handlers) ────────────────────────────────────────────────

    private void handleBusAdd() {
        try {
            showMsg(busController.addBus(plateField.getText(), captainField.getText()));
            refreshBusTable();
            loadBusesToCombo();
        } catch (DatabaseException e) {
            showErr(e.getMessage());
        }
    }

    private void handleBusUpdate() {
        try {
            showMsg(busController.updateBus(plateField.getText(), captainField.getText()));
            refreshBusTable();
            loadBusesToCombo();
        } catch (DatabaseException e) {
            showErr(e.getMessage());
        }
    }

    private void handleBusDelete() {
        try {
            showMsg(busController.deleteBus(plateField.getText()));
            refreshBusTable();
            loadBusesToCombo();
        } catch (DatabaseException e) {
            showErr(e.getMessage());
        }
    }

    private void handleDptAdd() {
        try {
            Bus bus = (Bus) busComboBox.getSelectedItem();
            if (bus == null) {
                showErr("Lütfen bir otobüs seçin.");
                return;
            }
            
            String formattedDate = utils.ValidationUtils.formatDateForDB(dateField.getText());
            if (formattedDate == null) {
                showErr("Geçersiz tarih formatı. Lütfen YYYY-AA-GG veya GG-AA-YYYY olarak girin.");
                return;
            }

            showMsg(dptController.addDpt(departureCityField.getText(), arrivalCityField.getText(),
                    formattedDate, timeField.getText(), Double.parseDouble(ticketPriceField.getText()),
                    bus.getBusId(), Integer.parseInt(availableSeatsField.getText()), routeInfoField.getText()));
            refreshDptTable();
        } catch (Exception e) {
            showErr(e.getMessage());
        }
    }

    private void handleDptUpdate() {
        try {
            Bus bus = (Bus) busComboBox.getSelectedItem();
            if (bus == null) {
                showErr("Seçili otobüs yok.");
                return;
            }
            
            String formattedDate = utils.ValidationUtils.formatDateForDB(dateField.getText());
            if (formattedDate == null) {
                showErr("Geçersiz tarih formatı. Lütfen YYYY-AA-GG veya GG-AA-YYYY olarak girin.");
                return;
            }

            showMsg(dptController.updateDpt(Integer.parseInt(departureIdField.getText()),
                    departureCityField.getText(), arrivalCityField.getText(), formattedDate, timeField.getText(),
                    Double.parseDouble(ticketPriceField.getText()), bus.getBusId(),
                    Integer.parseInt(availableSeatsField.getText()), routeInfoField.getText()));
            refreshDptTable();
        } catch (Exception e) {
            showErr("Geçersiz giriş: " + e.getMessage());
        }
    }

    private void handleDptDelete() {
        try {
            showMsg(dptController.deleteDpt(Integer.parseInt(departureIdField.getText())));
            refreshDptTable();
        } catch (Exception e) {
            showErr(e.getMessage());
        }
    }

    private void handlePassengerAdd() {
        try {
            showMsg(passengerController.addPassenger(firstNameField.getText(), lastNameField.getText(),
                    emailField.getText(), passwordField.getText()));
            refreshPassTable();
        } catch (Exception e) {
            showErr(e.getMessage());
        }
    }

    private void handlePassengerUpdate() {
        try {
            showMsg(passengerController.updatePassenger(Integer.parseInt(passengerIdField.getText()),
                    firstNameField.getText(), lastNameField.getText(), emailField.getText(), passwordField.getText()));
            refreshPassTable();
        } catch (Exception e) {
            showErr("Geçersiz giriş: " + e.getMessage());
        }
    }

    private void handlePassengerDelete() {
        try {
            showMsg(passengerController.deletePassenger(Integer.parseInt(passengerIdField.getText())));
            refreshPassTable();
        } catch (Exception e) {
            showErr(e.getMessage());
        }
    }
}