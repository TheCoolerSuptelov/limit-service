# Overview проекта

![Описание проекта на схеме]![image](https://user-images.githubusercontent.com/102233851/181576003-57712a5c-1b48-4fca-b0b9-b1efe851e261.png)

# Read Me First

На местном сленге пропсы - *.properties  
Связанные репо:  
1. Наш богоподобный сервис: [https://github.com/TheCoolerSuptelov/limit-service] 
2. Великолепный клауд конфиг сервер: [https://github.com/TheCoolerSuptelov/limit-service-configServer] 
3. Несравненный файл с пропсами: [https://github.com/TheCoolerSuptelov/limits-service] 
Один из сервисов, который зависит от клауд конфиг сервера.  
В пропсах сериса есть запись, указывающая на источников пропсов    
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

Для использовании api-gateWay нам нужен апи gateWay и прибить к EUREKA
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
По сути, развявывает руки и позволяет решать вопросы: метрики, связанности сервисов через модификацию заголовку,
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
Позволяет отслеживать запросы строить графы зависимостей.  
Подключаем зависимости Zipkin and Sleuth, запускаем в докуере zipkin радуемся жизни.  
У нас доступна карта общения сервисов.

# K8S  
$env:KUBECONFIG="C:\Users\supte\.kube\config" - регистрируем конфиг, если сервис предоставляет *.yaml , сносим указание типа и переименовываем  
.\kubectl create deployment hello-world-rest-api --image=in28min/hello-world-rest-api:0.0.1.RELEASE - создаем девлой, с имагем из докер хаба  
.\kubectl expose deployment hello-world-rest-api --type=LoadBalancer --port=8080 - открываем наше чудо миру  
.\kubectl get service/hello-world-rest-api - узнаем на каком порту сидит  
Если это mcs.mail, то надо зайти в "виртуальные машины" и добавить адреса. 
ВУАЛя: http://109.120.190.54:32599/hello-world  
  
  
Требуется исключить cloud зависимости из проекта, так как k8s сам менеджерит это 
![image](https://user-images.githubusercontent.com/102233851/181574944-859bb90e-12cc-4b8d-9ee4-a37039ccd0e7.png)
Ту да же должны отправится zipkin and rabbit.  
По той же причине.

Разворачиваем через команды:
kubectl create deployment currency-conversion --image=in28min/mmv2-currency-conversion-service:0.0.11-SNAPSHOT - создаем деплоймент.  
kubectl expose deployment currency-conversion --type=LoadBalancer --port=8100 - публикуем деплоймент.  
kubectl get svc - запрашиваем список сервисов для получения порта. 
  Связь между сервисами работает, пушто кубер умный и при публикации сервиса сетит в переменные окружения (считай другим сервисам)  
пути.  
сервис: currency-exchange
путь: ${CURRENCY_EXCHANGE_SERVICE_HOST}

После разворачивания деплоймента получаем доступ к нашему сервису через k8s
http://109.120.190.54:32318/currency-exchange/from/USD/to/INR  
http://109.120.190.54:32254/currency-conversion-feign/from/USD/to/INR/quantity/10  
  
   
   
  
  
Мы можем сгенерировать deployment & service команды через:  
kubectl get deployment currency-exchange -o yaml >> deployment.yaml  
kubectl get service currency-exchange -o yaml >> service.yaml  
Смержить их в 1 файл.  
После чего запускать апдейт 1 командой.  
kubectl apply -f deployment.yaml    
  
Можно сгенерировать файл с пропсами командой:  
kubectl create configmap currency-conversion --from-literal=CURRENCY_EXCHANGE_URI=http://currency-exchange  
kubectl.exe get configmap currency-conversion -o yaml >> configmap.yaml  
После чего положить её в deployment и привязать параметры через тег envFrom: к значениям в этой мапе  

Автоскэлинг (горизонтальное масштабирование)  
Можем сделать горизонтальное масштабирование по подам, в по условиям.
  
Файловый путь проектов  
D:\java\petProjects\cloud\limits-service  
D:\java\petProjects\limits-service  
D:\java\petProjects\spring-cloud-config-server
D:\java\petProjects\currency-convertion-service  
  
    
https://hub.docker.com/r/in28min/mmv2-currency-exchange-service/tags
https://github.com/in28minutes/spring-microservices-v2/tree/main/05.kubernetes
http://109.120.190.54:32254/currency-conversion-feign/from/USD/to/INR/quantity/10
http://109.120.190.54:32318/currency-exchange/from/USD/to/INR

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
