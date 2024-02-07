
server {
        listen 80;
        listen [::]:80;

      
        root /var/www/overrideTech.ru/html;
        index index.html index.htm index.nginx-debian.html;


        server_name overrideTech.ru www.overrideTech.ru;
        return 301 https://$host$request_uri;

        error_log  /var/log/nginx/error_log  crit;
}

server {
        listen 443 ssl http2;
        listen [::]:443 ssl http2;
        server_name overridetech.ru www.overridetech.ru;

        server_tokens off;

        ssl_certificate /etc/letsencrypt/live/overridetech.ru/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/overridetech.ru/privkey.pem;


        resolver 8.8.8.8;


        location /eureka/ {
                proxy_pass http://localhost:8761/;  # HTTP

        }
        location /invest/ {
                proxy_pass http://localhost:8087/;  # HTTP
        }
        location /twitch-bot/ {
                proxy_pass http://localhost:9000/;
        }
        location /labyrinth-challenge/ {
                proxy_pass http://localhost:3000/;
        }

        location / {
                proxy_pass http://localhost:8081/;  # orchestrator
        }

        location /twitch-bot/ws/ {
                proxy_pass                              http://localhost:9000;
                proxy_http_version                      1.1;
                proxy_set_header Upgrade                $http_upgrade;
                proxy_set_header Connection             "upgrade";
                proxy_read_timeout                      600;
        }
        location /review-bot/ {
            proxy_pass http://localhost:9100/;
        }

        location /prometheus/ {
            proxy_pass http://localhost:9090/;
        }
}
