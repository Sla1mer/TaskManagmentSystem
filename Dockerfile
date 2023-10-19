FROM gradle:jdk17

WORKDIR /CRMAuthService

COPY . /CRMAuthService

CMD ["gradle", "bootRun"]