-- Final SQL: Highest salaried employee per department excluding payments on the 1st day
-- Dialect: PostgreSQL-compatible

WITH filtered_payments AS (
    SELECT p.emp_id, p.amount
    FROM PAYMENTS p
    WHERE EXTRACT(DAY FROM p.payment_time) <> 1
),
employee_totals AS (
    SELECT e.department,
           e.emp_id,
           e.first_name,
           e.last_name,
           e.dob,
           COALESCE(SUM(fp.amount), 0) AS salary
    FROM EMPLOYEE e
    LEFT JOIN filtered_payments fp ON fp.emp_id = e.emp_id
    GROUP BY e.department, e.emp_id, e.first_name, e.last_name, e.dob
),
ranked AS (
    SELECT et.*, ROW_NUMBER() OVER (
        PARTITION BY et.department
        ORDER BY et.salary DESC, et.emp_id
    ) AS rn
    FROM employee_totals et
)
SELECT d.department_name AS department_name,
       r.salary AS salary,
       (r.first_name || ' ' || r.last_name) AS employee_name,
       DATE_PART('year', AGE(CURRENT_DATE, r.dob)) AS age
FROM ranked r
JOIN DEPARTMENT d ON d.department_id = r.department
WHERE r.rn = 1
ORDER BY d.department_name;