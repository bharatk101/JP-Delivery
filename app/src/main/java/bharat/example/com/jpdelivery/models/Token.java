package bharat.example.com.jpdelivery.models;

/**
 * Created by bharat on 3/1/18.
 */

public class Token {

    private String token;
    public boolean isServerToken;
    public boolean isDeliveryToken;

    public Token() {
    }

    public Token(String token, boolean isServerToken, boolean isDeliveryToken) {
        this.token = token;
        this.isServerToken = isServerToken;
        this.isDeliveryToken = isDeliveryToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }


    public boolean isDeliveryToken() {
        return isDeliveryToken;
    }

    public void setDeliveryToken(boolean deliveryToken) {
        isDeliveryToken = deliveryToken;
    }
}
