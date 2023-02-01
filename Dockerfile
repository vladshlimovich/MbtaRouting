#FROM alpine as release
FROM adoptopenjdk/openjdk8:jdk8u292-b10-alpine
RUN mkdir /usr/local/deployments
RUN cd /usr/local/deployments
WORKDIR /usr/local/deployments
COPY target/runtime* ./
EXPOSE 1984
CMD "./startup.sh"
