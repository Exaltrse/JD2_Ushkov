/*1) Вывести список юзеров старше 18 лет и моложе 25 (вычисление возраста сделать без добавления новой колонки age)*/

SELECT name, surname
FROM users
WHERE AGE(birth_date) > INTERVAL '18 year'
  AND AGE(birth_date) <= INTERVAL '25 year'

/*2) Подсчитать число машин у каждого пользователя. Вывести в формате User full name (username + пробел + user surname) | Число машин у пользователя.*/
SELECT CONCAT(users.name, ' ', users.surname) AS User_full_name, COUNT(cars.id)
FROM users
         INNER JOIN cars ON users.id = cars.owner
GROUP BY users.id

/*3) Вывести список самых популярных моделей (топ-3) машин в каждом диллере.*/
SELECT dealer.name, t.model
FROM (
         SELECT dealer_id,
                model,
                rank() over (partition by dealer_id order by count(id) desc) as rank
         FROM cars
         GROUP BY dealer_id, model
     ) t
         INNER JOIN dealer ON t.dealer_id = dealer.id
WHERE rank <= 3
ORDER BY dealer.name, t.model

/*4) Определить логины пользователей, имеющих больше 3 машин.*/
SELECT DISTINCT users.login
FROM users
         INNER JOIN cars ON users.id = cars.owner
GROUP BY users.id
HAVING COUNT(cars.id) > 3

/*5) Вывести уникальных диллеров с подсчитанной суммой стоимостей машин, связанных с ними.*/
SELECT dealer.name, SUM(cars.price)
FROM dealer
         INNER JOIN cars ON dealer.id = cars.dealer_id
GROUP BY dealer.name

/*6) Подсчитать количество уникальных пользователей, владеющих хотя бы одной машиной, стоимость которой превышает среднюю стоимость всех машин (всех записей в таблице).*/

WITH subquery AS (SELECT COUNT(users.id)
                  FROM users
                           INNER JOIN cars ON users.id = cars.owner
                  WHERE cars.price > (SELECT AVG(price) FROM cars)
                  GROUP BY users.id)

SELECT COUNT(*)
FROM subquery