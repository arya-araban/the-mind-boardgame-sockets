package sockets;

public class Message {

    private String content;

    private String authToken;

    private String specialUseCase;

    public Message(String content, String authToken) {

        this.content = content;
        this.authToken = authToken;
    }

    public Message(String content, String authToken, String specialUseCase) {

        this.content = content;
        this.authToken = authToken;
        this.specialUseCase = specialUseCase;
    }

    public String getContent() {
        return content;
    }


    public String getAuthToken() {
        return authToken;
    }

    public String getSpecialUseCase() {
        return specialUseCase;
    }

    @Override
    public String toString() {
        if (this.specialUseCase == null)
            return (this.content + "~" + this.authToken);

        return (this.content + "~" + this.authToken + "~" + this.specialUseCase);

    }

}
