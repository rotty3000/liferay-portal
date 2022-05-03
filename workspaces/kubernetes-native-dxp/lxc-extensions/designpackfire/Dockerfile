FROM caddy:2.4.6

COPY build/libs/designpackfire-1.0.0.jar /static/designpackfire.jar
COPY Caddyfile /etc/caddy/Caddyfile

RUN \
  unzip /static/designpackfire.jar -d /static && \
  rm /static/designpackfire.jar

RUN \
	caddy fmt --overwrite /etc/caddy/Caddyfile && \
	caddy validate --config /etc/caddy/Caddyfile && \
	cat /etc/caddy/Caddyfile

EXPOSE 8080/tcp