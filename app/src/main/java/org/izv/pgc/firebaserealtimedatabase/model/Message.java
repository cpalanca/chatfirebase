package org.izv.pgc.firebaserealtimedatabase.model;

public class Message {
    private boolean from;
    private String message, time;

    public Message(Boolean from, String message, String time) {
        this.from = from;
        this.message = message;
        this.time = time;
    }

    public Message() {
    }

    public boolean getFrom() {
        return from;
    }

    public void setFrom(boolean from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                "message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}
