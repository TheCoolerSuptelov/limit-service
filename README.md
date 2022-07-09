# Read Me First
На местном сленге пропсы - *.properties
Связанные репо:
    1. Наш богоподоный сервис: [https://github.com/TheCoolerSuptelov/limit-service]
    2. Великолепный клауд конфиг сервер: [https://github.com/TheCoolerSuptelov/limit-service-configServer]
    3. Несравненный файл с пропсами: [https://github.com/TheCoolerSuptelov/limits-service]
Один из сервисов, который зависит от клауд конфиг сервера. 
В пропсах есть запись, указывающая на источников пропсов приложения
spring.config.import=optional:configserver:http://localhost:8888
В приложении есть класс, который в свои атрибуты забирает значения из пропсов
src/main/java/com/github/thecoolersuptelov/limitsservice/configuration/Configuration.java

Описание цепочки:
git repo --> Клауд Конфиг сервер --> Сервис.

Как работает это все:
    1. Мы запускаем клауд конфиг сервер, в котором указываем в пропсах где живут пропсы:
`spring.cloud.config.server.git.uri=https://github.com/TheCoolerSuptelov/limits-service`
    2. Аннотируем `@EnableConfigServer` стартер клауд конфига.
    3. В сервисе указывает в пропсах `spring.config.import=optional:configserver:http://localhost:8888`
    4. В сервисе указываем имя приложения, именно по нему будем искать в пропсах, которые предоставляет конфиг сервер,
    свойства, которые нужны для бина, которые употребит наш класс:
    `src/main/java/com/github/thecoolersuptelov/limitsservice/configuration/Configuration.java`
    5. Переходим по урлу `http://localhost:8080/limits` и видим, как вся цепочка отработала
На практике это означает что:
1. По адресу `URL GET http://localhost:8888/limits-service/default` у нас доступен конфиг, который мы разместили в гите