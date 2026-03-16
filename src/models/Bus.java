package models;

public class Bus {
    private int busId;
    private String plate;
    private String captain;

    public Bus() {
    }

    public Bus(int busId, String plate, String captain) {
        this.busId = busId;
        this.plate = plate;
        this.captain = captain;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    @Override
    public String toString() {
        return plate + " - " + captain;
    }
}