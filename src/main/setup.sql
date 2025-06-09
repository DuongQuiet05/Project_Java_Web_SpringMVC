create database intern_ship;
use intern_ship;
#dữ liệu mẫu cho tech
insert into technology
    (name, status, image, description, is_featured, createdAt, updatedAt)
values ('JavaScript', 'ENABLED', 'javascript.png', null, false, '2025-06-12 11:02:16', '2025-06-12 22:29:29'),

       ('C++', 'ENABLED', 'ISO_C++_Logo.png', null, false, '2025-06-12 11:02:16', '2025-06-12 22:29:01'),

       ('PHP', 'ENABLED', 'tech_php.png', null, false, '2025-06-12 11:02:16', '2025-06-13 13:14:48'),

       ('ReactJS', 'ENABLED', '1d949589-afdd-4a1e-b77f-c53fdaf8af13.webp', null, false, '2025-06-12 22:21:35',
        '2025-06-12 22:21:35'),

       ('CSS', 'ENABLED', 'logo-2582747_960_720.webp', null, false, '2025-06-13 13:12:26', '2025-06-13 13:12:26'),

       ('HTML', 'ENABLED', 'logo-2582748_960_720.webp', null, false, '2025-06-13 13:12:42', '2025-06-13 14:35:27');


#dữ liệu mẫu cho vị trí tuyển dụng
insert into recruitment_position
(id, createdAt, description, expiredDate, location, maxSalary, minExperience, minSalary, name, numberOfVacancies, field,
 formOfWork, hot, status, updatedAt)
values (1, '2025-06-16 15:06:30', 'Build responsive UIs with HTML, CSS, and ReactJS.', '2025-07-20', 'Hanoi', 1200, 1,
        700, 'Frontend Developer', 5, 'IT', 'FULL_TIME', false, 'ENABLED', '2025-06-16 15:06:30'),

       (2, '2025-06-16 15:06:30', 'Maintain legacy systems and develop new features using PHP and JavaScript.',
        '2025-07-10', 'Hanoi', 1000, 2, 800, 'PHP Web Developer', 3, 'IT', 'FULL_TIME', false, 'ENABLED',
        '2025-06-16 15:06:30'),

       (3, '2025-06-16 15:06:30', 'Work with C++ on embedded systems, performance critical.', '2025-08-05', 'Hanoi',
        1600, 3, 1000, 'Embedded C++ Developer', 2, 'IT', 'FULL_TIME', false, 'ENABLED', '2025-06-16 15:06:30'),

       (4, '2025-06-16 15:27:28', 'Develop both frontend and backend using JavaScript and PHP.', '2025-07-25', 'Hanoi',
        1400, 2, 900, 'Fullstack Developer', 4, 'IT', 'FULL_TIME', false, 'ENABLED', '2025-06-16 15:27:28'),

       (5, '2025-06-16 15:27:28',
        'Design intuitive user interfaces with HTML, CSS and collaborate with frontend teams.', '2025-07-30', 'Hanoi',
        900, 1, 600, 'UI/UX Designer', 3, 'IT', 'PART_TIME', false, 'ENABLED', '2025-06-16 15:27:28'),

       (6, '2025-06-16 15:27:28', 'Build scalable SPAs using ReactJS and modern JS libraries.', '2025-08-01', 'Hanoi',
        1500, 2, 1000, 'ReactJS Engineer', 2, 'IT', 'REMOTE', false, 'ENABLED', '2025-06-16 15:27:28'),

       (7, '2025-06-16 15:27:28', 'Learn and contribute to real projects using HTML, CSS and JavaScript.', '2025-07-18',
        'Hanoi', 400, 0, 200, 'Frontend Intern', 1, 'IT', 'HYBRID', false, 'ENABLED', '2025-06-16 15:27:28'),

       (8, '2025-06-16 15:27:28', 'Maintain and debug PHP-based legacy systems.', '2025-07-22', 'Hanoi', 1000, 3, 700,
        'Legacy System Maintainer', 2, 'IT', 'FULL_TIME', false, 'ENABLED', '2025-06-16 15:27:28');

#dữ liệu cho bảng tech_pos
insert into recruitment_position_technology (position_id, technology_id)
values (1, 2),
       (1, 5),
       (1, 6),

       (2, 3),
       (2, 1),

       (3, 4),

       (4, 3),
       (4, 1),
       (4, 2),
       (4, 6),
       (4, 5),

       (5, 5),
       (5, 6),

       (6, 2),
       (6, 1),

       (7, 1),
       (7, 6),
       (7, 5),

       (8, 3),
       (8, 1);

# data demo cho location

INSERT INTO location (name)
VALUES ('Tokyo'),
       ('Ha Noi'),
       ('New York'),
       ('London'),
       ('Paris'),
       ('Mumbai'),
       ('São Paulo'),
       ('Cairo'),
       ('Moscow'),
       ('Beijing'),
       ('Sydney');

