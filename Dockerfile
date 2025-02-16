# Available Ollama docker images https://hub.docker.com/r/ollama/ollama/tags
FROM ollama/ollama:0.5.11

WORKDIR /app

COPY entrypoint.sh /entrypoint.sh
COPY test-project ./test-project

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
