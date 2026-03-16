package models;

/**
 * Otobüs koltuğunu temsil eden model.
 * Not: bus_id kaldırıldı — koltuk zaten dpt_id üzerinden sefere bağlı,
 * sefer de zaten bus_id içeriyor.
 */
public class Seat {
    private int seatId;
    private int seatNumber;
    private String status; // "available", "booked", "reserved"
    private int dptId;

    public Seat() {
    }

    public Seat(int seatId, int seatNumber, String status, int dptId) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.status = status;
        this.dptId = dptId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDptId() {
        return dptId;
    }

    public void setDptId(int dptId) {
        this.dptId = dptId;
    }

    @Override
    public String toString() {
        return "Seat{seatId=" + seatId + ", seatNumber=" + seatNumber +
                ", status='" + status + "', dptId=" + dptId + "}";
    }
}