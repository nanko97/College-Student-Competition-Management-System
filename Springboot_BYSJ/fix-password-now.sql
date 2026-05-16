UPDATE xuesheng SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';
UPDATE jiaoshi SET mima = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';
SELECT xuehao, LEFT(mima, 15) as pwd_prefix FROM xuesheng LIMIT 3;
SELECT gonghao, LEFT(mima, 15) as pwd_prefix FROM jiaoshi LIMIT 3;
