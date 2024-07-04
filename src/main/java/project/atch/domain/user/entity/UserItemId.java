package project.atch.domain.user.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserItemId implements Serializable {
    private Long user;
    private Long item;

    public UserItemId() {}

    @Override
    public int hashCode() {
        return Objects.hash(user, item);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserItemId that = (UserItemId) obj;
        return Objects.equals(user, that.user) && Objects.equals(item, that.item);
    }
}
