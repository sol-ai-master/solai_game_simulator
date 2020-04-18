FROM gradle:jdk11
WORKDIR ./
ADD --chown=gradle:gradle ./ ./
