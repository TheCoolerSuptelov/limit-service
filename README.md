# Overview проекта

![alt text](https://downloader.disk.yandex.ru/preview/b38dc06428c285396e67f3de6793552150538171e14a3fc34b062578f43bff8c/62d05af6/QDgCcMoq6FwTg7NWmxkMf1TES5alPAv_Njs3iiHdno83faqoKsc3nZcbmuI8PVo9geDx1mbXr_AKg6n_kP6gJw%3D%3D?uid=0&filename=2022-07-14%2021-03-34%2014.%20Step%2012%20-%20Connecting%20Currency%20Exchange%20Microservice%20with%20Zipkin.mp4%20-%20%D0%9C%D0%B5%D0%B4%D0%B8%D0%B0%D0%BF%D1%80%D0%BE%D0%B8%D0%B3%D1%80%D1%8B%D0%B2%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%20VLC.png&disposition=inline&hash=&limit=0&content_type=image%2Fpng&owner_uid=0&tknv=v2&size=2048x2048)

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
Задать имя файла пропосов можно в конфиге сервиса через: `spring.cloud.config.name`

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
2. По адресу `http://localhost:8080/limits` получаем лимиты, созданные по данным конфига в гите, который сервис получил  
через конфиг сервер

# Profiles props
Можно указывать различные значения пропсов в зависимости от профиля.  
Для этого нужно разместить в репо `limits-service` несколько пропсов, в моем примере это `qa&dev`  
После пуша в репо, сервер перечитает значения и сможет представлять их по урлу: `http://localhost:8888/limits-service/{profileName}`  
Если указать в пропсах сервиса `spring.profiles.active=dev` - сервис будет использовать пропсы, полученные по профилю, 
который предоставил сервер.

# Переход к exchange server

Так-так-так.  
Сделали контроллер для обмена валютов (только показываем).  
URL для запроса http://localhost:8000/currency-exchange/from/USD/to/INR
Добавил репо для хранения.
Из интересного, оказывает не надо прикручивать flyway для контроля ddl, достаточно использовать schema для DDL  
и data для генерации данных.

# Добавлние второго сервиса Currency conversion service.

server.port=8100
Живет тут: https://github.com/TheCoolerSuptelov/currency-convertion-service
D:\java\petProjects\currency-convertion-service

# Реализация OpenFeign

Пишем прокси сервиса, из которого хотим получать данные. В нашем случае это:  
https://github.com/TheCoolerSuptelov/currency-convertion-service/tree/main/src/main/java/com/github/thecoolersuptelov/currencyconvertionservice/proxy
Полностью копируем метод, который будем вызывать.
Готово, каренси конвершн сервис тянет данные из эксчэнж сервиса курсы для конвертации валют

# Использование Eureka для навигации сервисов

В проекте currency-conversion есть реализация OpenFeign клиента, который позволяет "красиво"  
связывать сервисы между собой. До этого момента там указан статический УРЛ сервиса Currency-exachange.  
Так как оба сервиса зарегистрированы в Eureka (через указание в пропсах eureka.client.serviceUrl.defaultZone)  
мы можем в OpenFeign указать не статический адрес сервиса, а имя приложения, с которым хотим взаимодействовать:  
Пример изменения по ссылке:

https://github.com/TheCoolerSuptelov/currency-convertion-service/blob/main/src/main/java/com/github/thecoolersuptelov/currencyconvertionservice/proxy/CurrencyExchange.java

# Api-Gateway

Для использовании api-gateWay нам нужен апи gateWay и прибить его связать его с EUREKA
После чего все сервисы зарегистрированные в Eureka будут доступны по API gateWay
Свойство для связывания gateWay и Eureka: `eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka`  
Обращения к сервисам преобразуется в:  
http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR  
http://localhost:8765/currency-conversion-service/from/USD/to/INR/quantity/1
Где localhost:8765 - api GateWay.

# Строим дороги (routes) сами

Запросы в gateWay можно перехватывать и обрабатывать как нашей душе угодно.  
Пример:  
`https://github.com/TheCoolerSuptelov/currency-convertion-api-gateway/blob/master/src/main/java/com/github/thecoolersuptelov/apigateway/ApiGatewayConfiguration.java`

Так же есть возможность добавлять свои фильтры в запросы:
`https://github.com/TheCoolerSuptelov/currency-convertion-api-gateway/blob/master/src/main/java/com/github/thecoolersuptelov/apigateway/LoggingFilter.java`
По сути развявывает руки и позволяет решать вопросы: метрики, связанности сервисов через модификацию заголовку,
логирование запросом и пр.

# resilience4j

resilience4j - CircuitBreaker - паттерн работы с ошибками:
Circuit Breaker выступает как прокси-сервис между приложением и удаленным сервисом. Прокси-сервис мониторит последние
возникшие ошибки, для определения, можно ли выполнить операцию или просто сразу вернуть ошибку.  
@CircuitBreaker - если сервис лежит, лишь возвращает ответ, без выполнения запроса, иногда пропускаю запросы,  
чтобы удостовериться в "лежании" сервиса (по умолчанию 1 раз в минуту)

**'@Retry'** По сути, resilience4j проксирует запрос, и в зависимости от способа обработки в `@Retry` выбирает стратегию
поведения.    
Количество попыток можно определить через пропсы и прописать соотвествующее значение
в `@Retry(name = "attributeFromProps")`  
`https://github.com/TheCoolerSuptelov/limits-service/blob/master/limits-service.properties`
Можно сказать, что в простом варианте, он заменяет множественный try-catch.  
Так же можно указать способ обработки  `@Retry(,fallback="methodName")`, важно метод должен возвращать то же, что и
оригинальный метод

'@RateLimitter' - позволяет ограничить колво запросов в секунду.  
`@Bulkhead` - max concurrent calls.

# Tracing Zipkin and Sleuth

Файловый путь проектов  
D:\java\petProjects\cloud\limits-service  
D:\java\petProjects\limits-service  
D:\java\petProjects\spring-cloud-config-server
D:\java\petProjects\currency-convertion-service

# Ключевые УРЛЫ проекты

Пример работы CurrencyConversion-->CurrencyExchange через OpenFeign
http://localhost:6001/currency-convertion-feign/from/USD/to/INR/quantity/9

Пример работы CurrencyExchange   
http://localhost:8000/currency-exchange/from/USD/to/INR

Пример работы Config Server  
http://localhost:8888/limits-service/default

Пример работы CurrencyConversion:  
http://localhost:6001/from/USD/to/INR/quantity/9

Пример работы Eureka Server:  
http://localhost:8761/
