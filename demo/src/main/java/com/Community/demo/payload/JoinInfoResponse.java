package com.Community.demo.payload;

public class JoinInfoResponse {
    private String room;
    private String url;

    public JoinInfoResponse() {}
    public JoinInfoResponse(String room, String url) { this.room = room; this.url = url; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
