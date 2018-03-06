package eu.z3r0byteapps.posterroaster.API;

/**
 * Created by basva on 27-2-2018.
 */

public class Registration {
    String poster;
    User user;
    String vote;

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}
