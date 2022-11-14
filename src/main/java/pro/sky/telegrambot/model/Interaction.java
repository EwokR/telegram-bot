package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Interaction {

    @Id
    @GeneratedValue
    private String request;
    private String response;


    public Interaction(String request, String response) {
        this.request = request;
        this.response = response;
    }

    public Interaction() {
        this.request = "";
        this.response = "";
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interaction that = (Interaction) o;
        return Objects.equals(request, that.request) && Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, response);
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "request='" + request + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
