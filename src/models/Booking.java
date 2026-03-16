package models;

public class Booking {
    private int booking_id;
    private int user_id;
    private int dpt_id;
    private int seat_number;

    public Booking() {}

    public Booking(int booking_id, int user_id, int dpt_id, int seat_number) {
        this.booking_id = booking_id;
        this.user_id = user_id;
        this.dpt_id = dpt_id;
        this.seat_number = seat_number;
    }

    // Getters and Setters
    public int getBookingId() {
        return booking_id;
    }

    public void setBookingId(int booking_id) {
        this.booking_id = booking_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getDptId() {
        return dpt_id;
    }

    public void setDptId(int dpt_id) {
        this.dpt_id = dpt_id;
    }

    public int getSeatNumber() {
        return seat_number;
    }

    public void setSeatNumber(int seat_number) {
        this.seat_number = seat_number;
    }
}