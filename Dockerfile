FROM gradle:jdk17

WORKDIR /PracticeAuthBackend

COPY . /PracticeAuthBackend

CMD ["gradle", "bootRun"]