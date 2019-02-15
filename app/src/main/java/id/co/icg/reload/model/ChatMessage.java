package id.co.icg.reload.model;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable{

    private Long id;
    private String topic;
    private String userId;
    private String message;
    private Date created;
    private Integer readStatus;
    private Long telegramId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", created=" + created +
                ", readStatus=" + readStatus +
                ", telegramId=" + telegramId +
                '}';
    }
}
