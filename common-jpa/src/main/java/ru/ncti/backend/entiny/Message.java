package ru.ncti.backend.entiny;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "text")
    private String text;

    @ManyToOne(targetEntity = User.class, cascade = {CascadeType.ALL})
    private User sender;

    @ManyToOne(targetEntity = Chat.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Chat chat;

    @Column(name = "createdAt")
    Instant createdAt;
}
