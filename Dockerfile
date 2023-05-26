FROM gradle:jdk17

WORKDIR /journal-action-backend

COPY . /journal-action-backend

CMD ["gradle", "bootRun"]