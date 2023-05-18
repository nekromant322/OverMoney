![orchestrator-coverage](https://raw.githubusercontent.com/kyomexd/OverMoneyKyomeFork/badges/jacoco_orchestrator.svg)
![recognizer-coverage](https://raw.githubusercontent.com/kyomexd/OverMoneyKyomeFork/badges/jacoco_recognizer.svg)
![telegram-bot-coverage](https://raw.githubusercontent.com/kyomexd/OverMoneyKyomeFork/badges/jacoco_telegram_bot.svg)

Если у тебя нет доступа к [jira](https://override-platform.atlassian.net/jira/software/c/projects/OV/boards/2) или [confluence](https://override-platform.atlassian.net/wiki/spaces/O/pages/28278785/Starter+guide) - попроси  
Если в аккаунте Atlassian поставишь фотку как в тг - будешь суперчемпионом  
Если заметил баг не в своем коде - смело сам заводи задачку с подробным описанием и шагами для воспроизведения  


Прежде чем смержить код - не забудь его локально протестировать

Без строгого соблюдения чекстайла твой код не попадет в мастер (и вообще за границы ветки не выйдет) - проверить его локально можно с помощью mvn clean verify, как в родительском поме, так и в любом из модулей

После мержа в мастер микросервисы деплоятся на тестовую виртуалку overridetech.ru, закрывай задачу после того как убедился, что там все в порядке и ты ничего не сломал  
Полезную информацию о том как поковыряться в кишках тестового окружения можно найти в [confluence](https://override-platform.atlassian.net/wiki/spaces/O/pages/28344321)






