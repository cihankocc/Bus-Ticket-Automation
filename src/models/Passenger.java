package models;

import java.util.Objects;

public class Passenger extends User {
    private int passengerId;

    public Passenger() {
    }

    public Passenger(int userId, String firstName, String lastName, String email, String password, int passengerId) {
        super(userId, firstName, lastName, email, password);
        this.passengerId = passengerId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    @Override
    public String toString() {
        return "Passenger{passengerId=" + passengerId + ", userId=" + getUserId() +
                ", name='" + getFirstName() + " " + getLastName() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Passenger))
            return false;
        if (!super.equals(o))
            return false;
        return passengerId == ((Passenger) o).passengerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), passengerId);
    }
}