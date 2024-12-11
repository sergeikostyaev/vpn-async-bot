package vp.botv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchase")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_type")
    private String subscriptionType;

    @Column(name = "subscription_price")
    private Long subscriptionPrice;

    @Column(name = "client_telegram_id")
    private Long clientTelegramId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "telegram_link")
    private String telegramLink;

    @Column(name = "telegram_name")
    private String telegramName;

    @Column(name = "key_id")
    private String keyId;

    @Column(name = "exp_time")
    private LocalDateTime expTime;

    @Column(name = "purchase_date_time")
    private LocalDateTime purchaseDateTime;

    @Column(name = "promocode")
    private String promocode;

    @Column(name = "integration_link")
    private String integrationLink;

    @Setter
    @Column(name = "is_client_warned")
    private Boolean isClientWarned;

    @Setter
    @Column(name = "success")
    private Boolean success;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
