
server {
        listen 80;
        listen [::]:80;

        root /var/www/overrideTech.ru/html;
        index index.html index.htm index.nginx-debian.html;

        server_name overrideTech.ru www.overrideTech.ru;
        return 301 https://$host$request_uri;

        error_log  /var/log/nginx/error_log  crit;

        location / {
                proxy_pass http://localhost:8081/;  # HTTP orchestrator
        }

        location /kibana/ {
            proxy_pass http://localhost:5601/;
        }
        location /eureka/ {
            proxy_pass http://localhost:8761/;  # HTTP

        }
        location /telegram/ {
                proxy_pass http://localhost:8082/;  # HTTP

        }
        location /twitch-bot/ {
                proxy_pass https://localhost:9000/;
        }

        location ~ \.css {
                    add_header  Content-Type    text/css;
        }
        location ~ \.js {
                    add_header  Content-Type    application/x-javascript;
        }
}

server {
        listen 443 ssl http2;
        listen [::]:443 ssl http2;
        server_name overridetech.ru www.overridetech.ru;

        server_tokens off;

        ssl_certificate /etc/letsencrypt/live/overridetech.ru/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/overridetech.ru/privkey.pem;


        resolver 8.8.8.8;

        location / {
                proxy_pass http://localhost:8081/;  # HTTP orchestrator
        }
        location /eureka/ {
                proxy_pass http://localhost:8761/;  # HTTP

        }
        location /twitch-bot/ {
                proxy_pass http://localhost:9000/;
        }

        location /twitch-bot/ws/ {
                proxy_pass                              http://localhost:9000;
                proxy_http_version                      1.1;
                proxy_set_header Upgrade                $http_upgrade;
                proxy_set_header Connection             "upgrade";
                proxy_read_timeout                      600;
#                proxy_set_header Host                   $host;
#                proxy_set_header X-Forwarded-For        $proxy_add_x_forwarded_for;
#                proxy_set_header X-Real-IP              $remote_addr;
        }

        location /telegram/ {
                proxy_pass http://localhost:8082/;  # HTTP

        }
        location /kibana/ {
            proxy_pass http://localhost:5601/;
        }
        location /review-bot/ {
            proxy_pass http://localhost:9100/;
        }
#        location ~ \.css {
#                    add_header  Content-Type    text/css;
#        }
#        location ~ \.js {
#                    add_header  Content-Type    application/x-javascript;
#        }

}
