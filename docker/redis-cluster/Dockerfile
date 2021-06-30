FROM grokzen/redis-cluster:5.0.7

EXPOSE 7000

COPY *.sh /bootstrap/
RUN chmod 755 /bootstrap/*.sh

HEALTHCHECK --start-period=30s --interval=10s --timeout=60s --retries=3\
    CMD /bootstrap/init.sh