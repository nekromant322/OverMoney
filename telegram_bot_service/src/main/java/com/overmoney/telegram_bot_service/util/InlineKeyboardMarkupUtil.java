package com.overmoney.telegram_bot_service.util;

import com.overmoney.telegram_bot_service.constants.InlineKeyboardCallback;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class InlineKeyboardMarkupUtil {

    public InlineKeyboardMarkup generateMergeRequestMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton defaultIKB = new InlineKeyboardButton();
        defaultIKB.setText(InlineKeyboardCallback.DEFAULT.getText());
        defaultIKB.setCallbackData(InlineKeyboardCallback.DEFAULT.getData());

        InlineKeyboardButton mergeWithCategoriesIKB = new InlineKeyboardButton();
        mergeWithCategoriesIKB.setText(InlineKeyboardCallback.MERGE_CATEGORIES.getText());
        mergeWithCategoriesIKB.setCallbackData(InlineKeyboardCallback.MERGE_CATEGORIES.getData());

        InlineKeyboardButton mergeWithCategoriesAndTransactionsIKB = new InlineKeyboardButton();
        mergeWithCategoriesAndTransactionsIKB.setText(
                InlineKeyboardCallback.MERGE_CATEGORIES_AND_TRANSACTIONS.getText());
        mergeWithCategoriesAndTransactionsIKB.setCallbackData(
                InlineKeyboardCallback.MERGE_CATEGORIES_AND_TRANSACTIONS.getData());

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(defaultIKB);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(mergeWithCategoriesIKB);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(mergeWithCategoriesAndTransactionsIKB);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup generatePaymentKeyboard(String paymentUrl) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton payButton = new InlineKeyboardButton();
        payButton.setText(InlineKeyboardCallback.PAY.getText());
        payButton.setUrl(paymentUrl);

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(payButton);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }
}