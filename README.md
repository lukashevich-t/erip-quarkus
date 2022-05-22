# erip-quarkus Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## создание
```bash
#!/bin/bash

mvn io.quarkus.platform:quarkus-maven-plugin:2.9.1.Final:create \
    -DprojectGroupId=by.gto.erip \
    -DprojectArtifactId=erip-quarkus \
    -Dextensions="jdbc-mariadb, hibernate-orm, agroal, mailer, undertow, smallrye-metrics" \
    -DbuildTool=gradle-kotlin-dsl
```

## TODO list
- [x] Принимать данные в кодировке windows-1251 
- [ ] Найти способ заменить org.springframework.cache.annotation.Cacheable в
  - by.gto.erip.dao.hibernate.TariffDAOImpl
- [ ] Запускать задачи по таймеру
- [ ] как запускать профиль %dev без создания контейнеров, на БД разработчика
- [ ] Нужен ли класс by.gto.erip.helpers.hibernate.MySQLDialectExtended
- [ ] отсылать почту
- [ ] чтение настроек из БД
- [ ] перечитывание настроек по таймеру и по запросу
- [ ] Создать точки для управления извне (by.gto.erip.webservices.Srv1)

## Изменения
[+] добавлено [-] удалено [*] изменено [f] исправлено

erip-quarkus 1.0.0-SNAPSHOT 22.05.2022 15:30
[+] Первый коммит


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/erip-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and JPA
- Agroal - Database connection pool ([guide](https://quarkus.io/guides/datasource)): Pool JDBC database connections (included in Hibernate ORM)
- Mailer ([guide](https://quarkus.io/guides/mailer)): Send emails
- SmallRye Metrics ([guide](https://quarkus.io/guides/microprofile-metrics)): Expose metrics for your services

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)

