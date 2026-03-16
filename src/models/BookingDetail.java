package models;

/**
 * Rezervasyon + Sefer bilgilerini birlikte tutan yardımcı sınıf.
 * Kullanıcının bilet geçmişini göstermek için kullanılır.
 */
public class BookingDetail {
    private final int bookingId;
    private final int seatNumber;
    private final String departureCity;
    private final String arrivalCity;
    private final String date;
    private final String time;
    private final double price;
    private final String createdAt;

    public BookingDetail(int bookingId, int seatNumber, String departureCity, String arrivalCity,
            String date, String time, double price, String createdAt) {
        this.bookingId = bookingId;
        this.seatNumber = seatNumber;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.date = date;
        this.time = time;
        this.price = price;
        this.createdAt = createdAt;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getPrice() {
        return price;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
