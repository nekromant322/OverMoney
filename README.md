Распределенный трекер финансов **без** интеграций с банками  
Распознавание транзакций из текста и голоса, микро и макро менджмент расходов и доходов  



![orchestrator-coverage](https://raw.githubusercontent.com/nekromant322/OverMoney/badges-branch/.github/badges/jacoco_orchestrator.svg)
![recognizer-coverage](https://raw.githubusercontent.com/nekromant322/OverMoney/badges-branch/.github/badges/jacoco_recognizer.svg)
![telegram-bot-coverage](https://raw.githubusercontent.com/nekromant322/OverMoney/badges-branch/.github/badges/jacoco_telegram_bot.svg)

Если у тебя нет доступа к [jira](https://override-platform.atlassian.net/jira/software/c/projects/OV/boards/2) или [confluence](https://override-platform.atlassian.net/wiki/spaces/O/pages/28278785/Starter+guide) - попроси в [telegram](https://t.me/Marandyuk_Anatolii)

Если заметил баг не в своем коде - смело сам заводи задачку в [jira](https://override-platform.atlassian.net/jira/software/c/projects/OV/boards/2) с подробным описанием и шагами для воспроизведения  

Прежде чем смержить код - не забудь его локально протестировать и **обновить ветку до актуального мастера**

Без строгого соблюдения чекстайла твой код не попадет в мастер - проверить его локально можно с помощью mvn clean verify, как в родительском поме, так и в любом из модулей

После мержа в мастер микросервисы деплоятся overmoney.tech, закрывай задачу после того как убедился, что там все в порядке и ты ничего не сломал  
Полезную информацию о том как поковыряться в кишках тестового окружения можно найти в [confluence](https://override-platform.atlassian.net/wiki/spaces/O/pages/28344321)

Прод
[https://overmoney.tech/overmoney](https://overmoney.tech/overmoney)
Тестовый стенд
[https://overmoneytest.online/overmoney]([https://overmoney.tech/overmoney](https://overmoneytest.online/overmoney))


![Схема инфры](infra.svg)


