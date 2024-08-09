package com.overmoney.telegram_bot_service.constants;

public class MessageConstants {
    public static final String NO_RIGHTS = "Простите, но эта команда доступна только администраторам";
    public static final String TRANSACTION_MESSAGE_INVALID = "Мы не смогли распознать ваше сообщение. " +
            "Убедитесь, что сумма и товар указаны верно и попробуйте еще раз :)";
    public static final String MERGE_REQUEST_TEXT =
            "Привет, ты добавил меня в груповой чат, теперь я буду отслеживать " +
                    "транзакции всех пользователей в этом чате.\n\n" +
                    "Хочешь перенести данные о своих финансах и использовать их совместно?";
    public static final String MERGE_REQUEST_COMPLETED_DEFAULT_TEXT =
            "Удачного совместного использования!";
    public static final String MERGE_REQUEST_COMPLETED_TEXT =
            "Данные аккаунта были перенесены.";
    public static final String REGISTRATION_INFO_TEXT =
            "Для корректной регистрации аккаунта убедитесь, что на момент добавления в чат бота" +
                    " в чате с ботом только вы. После переноса данных можете добавлять других пользователей";
    public static final String INVALID_TRANSACTION_TO_DELETE = "Некорректная транзакция для удаления";
    public static final String SUCCESSFUL_DELETION_TRANSACTION = "Эта запись успешно удалена!";
    public static final String COMMAND_TO_DELETE_TRANSACTION = "удалить";
    public static final String BLANK_MESSAGE = "";
    public static final String SUCCESSFUL_UPDATE_TRANSACTION_TEXT = "Запись успешно изменена.\n";
    public static final String INVALID_UPDATE_TRANSACTION_TEXT = "Некорректная транзакция для изменения.\n" +
            "Возможно, Вы выбрали транзакцию, которая уже была изменена или сообщение бота";
    public static final String NETWORK_ERROR = "сетевая ошибка, функционал временно не доступен";
    public static final String MESSAGE_NOT_REGISTERED = "Возможно, вы не ввели команду /start. " +
            "Пожалуйста, введите /start и повторите вашу попытку :)";
}
