package vp.botv.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VpnSubscription {

    THREE_DAYS(60, "VPN на 3 дня за 60 руб", 3),
    SEVEN_DAYS(95, "VPN на 7 дней за 95 руб", 7),
    ONE_MONTH(259, "VPN на 30 дней за 259 руб", 30),
    THREE_MONTHS(679, "VPN на 90 дней за 679 руб", 90),
    SIX_MONTHS(1199, "VPN на 180 дней за 1199 руб", 180),
    ONE_YEAR(2199, "VPN на 1 год за 2199 руб", 365),
    ERROR(0, "ERROR", 0);


    private final Integer price;

    private final String description;

    private final Integer days;

}
