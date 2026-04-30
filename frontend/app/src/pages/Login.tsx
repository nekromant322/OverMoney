import { useEffect, useRef, useState } from 'react';
import './Landing.css';

type TelegramUser = {
  id: number;
  first_name: string;
  last_name?: string;
  username?: string;
  photo_url?: string;
  auth_date: number;
  hash: string;
};

declare global {
  interface Window {
    onTelegramAuth: (user: TelegramUser) => void;
  }
}

export default function Login() {
  const widgetSlot = useRef<HTMLDivElement>(null);
  const [widgetLoaded, setWidgetLoaded] = useState(false);

  useEffect(() => {
    window.onTelegramAuth = async (user) => {
      try {
        const response = await fetch('/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json; charset=utf-8' },
          credentials: 'include',
          body: JSON.stringify(user),
        });
        if (!response.ok) {
          throw new Error(`Auth failed: ${response.status}`);
        }
        window.location.href = `${import.meta.env.BASE_URL}operations`;
      } catch (e) {
        console.error('ERROR! Something wrong happened', e);
      }
    };

    let cancelled = false;

    fetch('/login/bot-login', { credentials: 'include' })
      .then((r) => r.text())
      .then((botName) => {
        if (cancelled || !widgetSlot.current) return;
        const script = document.createElement('script');
        script.src = 'https://telegram.org/js/telegram-widget.js?22';
        script.async = true;
        script.setAttribute('data-telegram-login', botName.trim());
        script.setAttribute('data-size', 'large');
        script.setAttribute('data-onauth', 'onTelegramAuth(user)');
        script.setAttribute('data-request-access', 'write');
        script.onload = () => { if (!cancelled) setWidgetLoaded(true); };
        widgetSlot.current.appendChild(script);
      })
      .catch((e) => console.error('Failed to load bot name', e));

    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="landing__wrapper">
      <img className="landing__top-background" src={`${import.meta.env.BASE_URL}images/background.svg`} alt="" aria-hidden="true" loading="eager" decoding="async" />
      <img className="landing__bottom-background" src={`${import.meta.env.BASE_URL}images/background.svg`} alt="" aria-hidden="true" loading="eager" decoding="async" />

      <header className="landing__header header">
        <a href="/">
          <img src={`${import.meta.env.BASE_URL}images/logo.svg`} width="32" height="32" alt="OverMoney logo" />
        </a>
      </header>

      <main className="landing__content">
        <section className="landing__hero">
          <h1>OverMoney</h1>
          <h2 className="landing__subtitle">Твой трекер финансов</h2>
          <div className="landing__widget-slot" ref={widgetSlot}>
            {!widgetLoaded && (
              <div className="tg-connecting">
                <span className="tg-connecting__dot" />
                <span className="tg-connecting__dot" />
                <span className="tg-connecting__dot" />
                <span className="tg-connecting__text">Подключение к Telegram</span>
              </div>
            )}
          </div>
        </section>

        <div className="landing__features">
          <div className="feature-card">
            <div className="feature-card__title">Удобный</div>
            <div className="feature-card__description">
              Распознавание текстовых и голосовых сообщений в Telegram боте и создание операций на их основе.
            </div>
            <a href="#comfortable" className="feature-card__link">Узнать больше -›</a>
          </div>
          <div className="feature-card">
            <div className="feature-card__title">Функциональный</div>
            <div className="feature-card__description">
              Подробная аналитика финансов. Распределение операций по категориям на основе ML.
            </div>
            <a href="#functional" className="feature-card__link">Узнать больше -›</a>
          </div>
          <div className="feature-card">
            <div className="feature-card__title">Безопасный</div>
            <div className="feature-card__description">
              Не требует доступа к SMS на вашем устройстве и не отправляет запросов к API банков.
            </div>
            <a href="#safe" className="feature-card__link">Узнать больше -›</a>
          </div>
        </div>

        <section className="landing__info">
          <div id="comfortable" className="landing__detail feature-details">
            <div className="feature-details__content-container">
              <h3>Легко записывайте свои доходы и расходы</h3>
              <div className="feature-details__description">
                С OverMoney контролировать финансы проще: записывайте операции текстом или голосом через наш
                Telegram-бот, указав сумму и описание в свободном формате. Приложение автоматически добавит новую
                операцию и определит категорию к которой она относится или предоставит возможность указать ее
                самостоятельно через удобный web интерфейс.
              </div>
            </div>
            <div className="feature-details__image-container">
              <img className="feature-details__image" src={`${import.meta.env.BASE_URL}images/comfortable.svg`} width="320" height="320" alt="comfortable illustration" loading="lazy" />
            </div>
          </div>

          <div id="functional" className="landing__detail feature-details">
            <div className="feature-details__content-container">
              <h3>Группируйте траты по категориям и следите за динамикой</h3>
              <div className="feature-details__description">
                OverMoney использует технологии машинного обучения (ML), адаптируясь под ваши привычки и автоматизируя
                рутинные действия. Встроенные средства аналитики позволяют легко отслеживать динамику, вовремя
                выявлять ненужные траты и концентировать усилия на достижении ваших финансовых целей.
              </div>
            </div>
            <div className="feature-details__image-container">
              <img className="feature-details__image" src={`${import.meta.env.BASE_URL}images/functional.svg`} width="320" height="320" alt="functional illustration" loading="lazy" />
            </div>
          </div>

          <div id="safe" className="landing__detail feature-details">
            <div className="feature-details__content-container">
              <h3>Не беспокойтесь за безопасность своих данных</h3>
              <div className="feature-details__description">
                Конфиденциальность – наш приоритет. В OverMoney все данные шифруются и доступны только вам. Сервис не
                запрашивает доступ к SMS и банковским API, не передает информацию третьим лицам и не использует её
                для рекламы.
              </div>
            </div>
            <div className="feature-details__image-container">
              <img className="feature-details__image" src={`${import.meta.env.BASE_URL}images/safe.svg`} width="320" height="320" alt="safe illustration" loading="lazy" />
            </div>
          </div>

          <div id="subscription" className="landing__detail feature-details">
            <div className="feature-details__content-container">
              <h3>Следите за наличием долгов</h3>
              <div className="feature-details__description">
                Забываете возвращать друзьям деньги? Получите доступ к вкладке Учёт долгов, оформив подписку
                OverMoney за 300 рублей в месяц. Вы получите неограниченный функционал аналитики долгов,
                возможность добавить, удалить, изменить каждый долг.
              </div>
            </div>
            <div className="feature-details__image-container">
              <img className="feature-details__image" src={`${import.meta.env.BASE_URL}images/subscription.svg`} width="320" height="320" alt="subscription illustration" loading="lazy" />
            </div>
          </div>
        </section>

        <section className="landing__feedback">
          <h3>Напишите нам</h3>
          <div className="landing__feedback-info">
            Нашли баг или хотите предложить новую фичу? Расскажите нам чтобы мы могли сделать приложение лучше.
          </div>
          <a className="landing__send-feedback link-button" href="mailto:shamaneeel881@gmail.com">Отправить email</a>
        </section>
      </main>

      <footer className="landing__footer footer">
        <a href="/">
          <img src={`${import.meta.env.BASE_URL}images/logo.svg`} width="32" height="32" alt="OverMoney logo" />
        </a>
        <strong className="landing__footer-name">Самозанятая Марандюк Анастасия Александровна</strong>
        <p><strong className="landing__footer-name">ИНН: 324107164320</strong></p>
        <a href="mailto:shamaneeel881@gmail.com">shamaneeel881@gmail.com</a>
        <div className="footer__copyright">
          © {new Date().getFullYear()}{' '}
          <a href={`${import.meta.env.BASE_URL}offer`}>Договор-оферта</a>
        </div>
      </footer>
    </div>
  );
}
