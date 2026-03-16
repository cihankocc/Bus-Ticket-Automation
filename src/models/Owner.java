package models;

import java.util.Objects;

public class Owner extends User {
    private int ownerId;

    public Owner() {
    }

    public Owner(int userId, String firstName, String lastName, String email, String password, int ownerId) {
        super(userId, firstName, lastName, email, password);
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Owner{ownerId=" + ownerId + ", userId=" + getUserId() +
                ", name='" + getFirstName() + " " + getLastName() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Owner))
            return false;
        if (!super.equals(o))
            return false;
        return ownerId == ((Owner) o).ownerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ownerId);
    }
}