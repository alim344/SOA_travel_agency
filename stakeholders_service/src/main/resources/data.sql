INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_TOURIST') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_GUIDE') ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, email, password, enabled, role_id, last_password_reset_date)
VALUES ('admin123', 'admin@gmail.com', '$2a$10$.ijdXADqb6w7MOU8iipjFujSQXDkW5HPvhmDFXxbWjhUi5QYePyiO', true, 1, now());
INSERT INTO users (username, email, password, enabled, role_id, last_password_reset_date)
VALUES ('mila123', 'mila@gmail.com', '$2a$10$.ijdXADqb6w7MOU8iipjFujSQXDkW5HPvhmDFXxbWjhUi5QYePyiO', true, 2, now());
INSERT INTO users (username, email, password, enabled, role_id, last_password_reset_date)
VALUES ('sara123', 'sara@gmail.com', '$2a$10$.ijdXADqb6w7MOU8iipjFujSQXDkW5HPvhmDFXxbWjhUi5QYePyiO', true, 3, now());

INSERT INTO profiles (first_name, last_name, profile_photo, biography, motto, user_id)
VALUES ('Tanja', 'Bajnunovic', 'slika_tanje.jpg', null, null, 1);
INSERT INTO profiles (first_name, last_name, profile_photo, biography, motto, user_id)
VALUES ('Mila', 'Budimirovic', 'slika_mile.jpg', 'biografija_mile', 'motto_mile', 2);
INSERT INTO profiles (first_name, last_name, profile_photo, biography, motto, user_id)
VALUES ('Sara', 'Sapundzija', 'slika_sare.jpg', 'biografija_sare', 'motto_sare', 3);