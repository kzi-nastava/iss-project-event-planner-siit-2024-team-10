INSERT INTO location (id, city, country, street, house_number) VALUES
                                                                   (1, 'New York', 'USA', '5th Avenue', '101'),
                                                                   (2, 'London', 'UK', 'Baker Street', '221B'),
                                                                   (3, 'Berlin', 'Germany', 'Unter den Linden', '50');

INSERT INTO account (id, email, password, role, registration_timestamp, notifications_silenced, status, user_id) VALUES
                                                                                                                     (1, 'john.doe@example.com', 'password123', 0, '2023-12-01 10:00:00', FALSE, 0, null),
                                                                                                                     (2, 'jane.smith@example.com', 'password456', 1, '2023-12-02 11:00:00', TRUE, 0, null);

INSERT INTO users (id, dtype, first_name, last_name, phone_number, profile_photo, location_id, account_id) VALUES
                                                                                                        (1,'Provider', 'John', 'Doe', '123-456-7890', NULL, 1, 1),
                                                                                                        (2,'Organizer', 'Jane', 'Smith', '987-654-3210', NULL, 2, 2);

INSERT INTO company (id, email, name, phone_number, description, location_id) VALUES
                                                                                  (1, 'info@techcorp.com', 'TechCorp', '555-1234', 'Leading tech company.', 1),
                                                                                  (2, 'hello@greenworld.com', 'GreenWorld', '555-5678', 'Sustainable solutions provider.', 2);

INSERT INTO offering_category (id, name, description, is_deleted, pending) VALUES
                                                                               (1, 'Electronics', 'Category for electronic items.', FALSE, FALSE),
                                                                               (2, 'Home Services', 'Category for home-related services.', FALSE, TRUE);

INSERT INTO event_type (id, name, description, is_active) VALUES
                                                              (1, 'Workshop', 'Hands-on learning sessions.', TRUE),
                                                              (2, 'Conference', 'Large gatherings for presentations and discussions.', TRUE);

INSERT INTO event (id, organizer_id, event_type_id, name, description, max_participants, is_open, date, is_deleted, location_id, date_created) VALUES
                                                                                                                                                   (1, 2, 2, 'Tech Workshop', 'Learn about the latest tech trends.', 50, TRUE, '2024-01-15', FALSE, 1, '2024-03-10'),
                                                                                                                                                   (2, 2, 1, 'Business Conference', 'Annual business networking event.', 200, FALSE, '2024-03-10', FALSE, 2, '2024-03-03'),
                                                                                                                                                   (3, 2, 1, 'Music Festival', 'Enjoy live performances from top artists.', 500, TRUE, '2024-07-20', FALSE, 3, '2024-05-01'),
                                                                                                                                                   (4, 2, 2, 'Charity Gala', 'Fundraising dinner for a noble cause.', 150, FALSE, '2024-10-15', FALSE, 2, '2024-08-12'),
                                                                                                                                                   (5, 2, 1, 'Art Exhibition', 'Showcasing modern art pieces.', 100, TRUE, '2024-09-01', FALSE, 1, '2024-07-15');
INSERT INTO agenda_item (id, name, description, location, start_time, end_time, is_deleted) VALUES
                                                                                                (1, 'Opening Session', 'Kick-off of the event.', 'Main Hall', '09:00:00', '10:00:00', FALSE),
                                                                                                (2, 'Keynote Speech', 'Special guest speaker.', 'Conference Room A', '10:30:00', '11:30:00', FALSE);

INSERT INTO budget_item (id, amount, purchase_date, is_deleted, category_id, offering_id) VALUES
                                                                                              (1, 500.00, '2023-11-01 14:00:00', FALSE, 1, NULL),
                                                                                              (2, 200.00, '2023-11-02 15:00:00', FALSE, 2, NULL);

INSERT INTO comment (id, content, status, commenter_id) VALUES
                                                            (1, 'Great event!', 1, 1),
                                                            (2, 'Needs better organization.', 0, 2);

INSERT INTO message (id, content, timestamp, sender_id, receiver_id, is_read) VALUES
                                                                                  (1, 'Hello, when is the event?', '2023-12-05 09:00:00', 1, 2, FALSE),
                                                                                  (2, 'Can you provide more details?', '2023-12-06 10:00:00', 2, 1, TRUE);

INSERT INTO notification (id, is_read, content) VALUES
                                                    (1, FALSE, 'Your account has been updated.'),
                                                    (2, TRUE, 'Event registration confirmed.');

INSERT INTO product_details (id, name, description, price, discount, is_visible, is_available, timestamp) VALUES
                                                                                                              (1, 'Smartphone', 'Latest model smartphone.', 999.99, 0.0, TRUE, TRUE, '2023-10-10 12:00:00'),
                                                                                                              (2, 'Laptop', 'High-performance laptop.', 1499.99, 100.0, TRUE, TRUE, '2023-10-11 13:00:00');

INSERT INTO service_details (id, name, description, specification, price, discount, fixed_time, max_duration, min_duration, cancellation_period, reservation_period, is_visible, is_available, auto_confirm, timestamp) VALUES
    (1, 'Cleaning Service', 'Home cleaning package.', 'Standard package for small apartments.', 50.0, 5.0, TRUE, 4, 2, 24, 48, TRUE, TRUE, FALSE, '2023-10-15 14:00:00'),
    (2, 'Gardening Service', 'Lawn care and garden maintenance.', 'Includes lawn mowing, trimming, and plant care for medium-sized gardens.', 75.0, 6.0, TRUE, 3, 1, 30, 60, TRUE, TRUE, TRUE, '2023-11-20 10:00:00');

INSERT INTO offerings (id, dtype, current_product_details_id, is_deleted, pending) VALUES
                                                         (1, 'Product', 1, FALSE, FALSE),
                                                         (2,'Product', 2, FALSE, FALSE);

INSERT INTO offerings (id,dtype, current_service_details_id, is_deleted, pending) VALUES
                                                         (3,'Service', 1, FALSE, FALSE),
                                                         (4,'Service', 2, FALSE, FALSE);

INSERT INTO rating (id, score, rater_id) VALUES
                                             (1, 5, 1),
                                             (2, 3, 2);

INSERT INTO reservation (id, start_time, end_time, status, event_id, service_id) VALUES
                                                                                     (1, '2024-01-15 09:00:00', '2024-01-15 17:00:00', 1, 1, NULL),
                                                                                     (2, '2024-03-10 08:00:00', '2024-03-10 20:00:00', 0, 2, 1);
