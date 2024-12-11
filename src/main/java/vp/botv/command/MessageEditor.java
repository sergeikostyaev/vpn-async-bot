package vp.botv.command;

import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import vp.botv.common.VpnSubscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;
import static vp.botv.common.VpnSubscription.ERROR;

public abstract class MessageEditor {

    protected SendMessage sendMainMenu(Long chatId, String text, Boolean clientFreeTrialAccessUsed) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableMarkdown(true);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        if (!clientFreeTrialAccessUsed) {
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText("Получить бесплатный VPN");
            button1.setCallbackData("get_free_vpn");
            buttons.add(List.of(button1));
        }

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Приобрести VPN");
        button2.setCallbackData("get_vpn");
        buttons.add(List.of(button2));

        InlineKeyboardButton buttonServer = new InlineKeyboardButton();
        buttonServer.setText("Приобрести собственный VPN-сервер");
        buttonServer.setCallbackData("get_vpn_server");
        buttons.add(List.of(buttonServer));

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Инструкция по подключению");
        button3.setCallbackData("info");
        buttons.add(List.of(button3));


        markup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(markup);

        return sendMessage;
    }

    protected String getRunOutOfFreeSubscriptions() {
        return """
                ⏳ Вы уже приобретали бесплатный подарочный пакет.
                                                 
                ❤️ Мы благодарим вас за доверие и за то, что выбрали наш сервис для безопасного и удобного интернет-соединения.
                            
                🔑 Если вас устроил наш сервис, вы можете приобрести подписку на VPN и продолжать пользоваеться безопасным и 
                анонимным доступом в интернет без лимитов и ограничений скорости.
                """;
    }

    protected SendMessage buildMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        return sendMessage;
    }

    protected SendMessage buildMarkdownMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.enableMarkdown(true);

        return sendMessage;
    }

    protected String getPurchaseText(String serviceName, String endDate) {
        return """
                🎉 Вы успешно приобрели услугу: *%s*.
                            
                ⏳ Дата окончания действия подписки: *%s*.

                ⚠️ *Важно! Один ключ VPN можно использовать только на одном устройстве.* 
                Одновременное подключение двух или более устройств с одним ключом может привести, либо смена Wi-Fi точек без предварительного 
                отключения VPN могут привести к сбоям в работе VPN-соединения. 
                Если хотите подключить другое устройство или сменить Wi-Fi точку/перейти на мобильный интернет - *отключите VPN*. 
                Если вы совершили описанные выше действия, 
                и подключение пропало - отключите VPN и подождите 5 минут, соединение придет в норму.

                🔒 Спасибо за покупку и приятного пользования!
                            
                🔑 Скопируйте ключ, отправленный в следующем сообщении, и активируйте его 
                [согласно инструкции](https://telegra.ph/Instrukciya-po-podklyucheniyu-Prive-VPN-11-13). ⬇️
                """.formatted(serviceName, endDate);
    }

    protected String getWelcomeText() {
        return """
                👋 *Добро пожаловать в Prime VPN!*

                🌐 Мы предлагаем безопасный и анонимный доступ к интернету без лимитов скорости и трафика с возможностью обходить блокировки. Вы можете выбрать один из следующих тарифов:

                🎁 *Бесплатный тариф:*
                - 7 дней бесплатного использования ежедневно для каждого пользователя без лимитов и ограничения скорости.    
                
                📅 *Платные тарифы:*
                - 3 дня за *60 руб*: Получите доступ на несколько дней для краткосрочных нужд.
                - 7 дней за *95 руб*: Экономьте при покупке доступа на неделю.
                - 30 дней за *259 руб*: Идеальный выбор для постоянного использования в течение месяца.
                - 90 дней за *679 руб*: Подходит для более длительного использования в течение трёх месяцев.
                - 180 дней за *1199 руб*: Экономьте ещё больше, купив доступ на полгода.
                - 1 год за *2199 руб*: Наиболее выгодный тариф для доступа на целый год.                       

                ⚡️ *Для подключения VPN достаточно всего 1 минуты!*
                    
                ⚠️ *Важно! Один ключ VPN можно использовать только на одном устройстве.* Если хотите подключить другое устройство - *отключите VPN на остальных*.

                📲 Для оформления подписки или для получения бесплатного доступа, выберите соответствующую опцию в меню ниже.

                💬 Если у вас возникнут вопросы, наша поддержка всегда готова помочь!

                🔒 Приятного пользования!
                """;
    }

    protected SendMessage sendPurchaseMenu(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableMarkdown(true);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        Arrays.stream(VpnSubscription.values()).filter(sub -> !sub.name().equals(ERROR.name())).forEach(sub -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(sub.getDescription());
            button.setCallbackData(sub.name());
            buttons.add(List.of(button));
        });

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("Инструкция по подключению");
        button5.setCallbackData("info");
        buttons.add(List.of(button5));

        markup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(markup);

        return sendMessage;
    }

    protected SendInvoice buildInvoice(long chatId, String paymentToken, String subscription, int price) {
        return SendInvoice.builder()
                .chatId(chatId)
                .currency("RUB")
                .startParameter("start")
                .providerToken(paymentToken)
                .title("Оплата VPN")
                .description("Оплатите покупку чтобы получить доступ к VPN")
                .payload(subscription)
                .price(new LabeledPrice("Тест", 100 * price))
                .build();
    }

    protected String checkCode(String code) {
        if (isNull(code)) {
            return "Во веремя формирования ключа возникла ошибка. Пожалуйста, обратитесь с " +
                    "*идентификатором ключа* в поддержку и вам обязательно помогут, " +
                    "а так же предоставят дополнительные 2 дня подключения бесплатно.";
        } else {
            return code;
        }
    }

    protected String getVpnServerPurchaseText(){
        return "Если вы хотите приобрести собственный защищенный VPN-сервер для своих нужд - напишите нам в поддержку: @Prime_vpn_support_bot";
    }
}
