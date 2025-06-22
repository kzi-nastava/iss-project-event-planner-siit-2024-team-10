INSERT INTO location ( city, country, street, house_number) VALUES
                                                                   ( 'New York', 'USA', '5th Avenue', '101'),
                                                                   ( 'London', 'UK', 'Baker Street', '221B'),
                                                                   ( 'Berlin', 'Germany', 'Unter den Linden', '50');

INSERT INTO account (email, password, role, last_password_reset_date,notifications_silenced, status, user_id) VALUES
                                                                                                                     ( 'auth@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 0,'2023-12-01 10:00:00', FALSE, 0, null),
                                                                                                                     ( 'organizer@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 1,'2023-12-01 10:00:00', TRUE, 0, null),
                                                                                                                     ( 'provider@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 2,'2023-12-01 10:00:00', TRUE, 0, null),
                                                                                                                     ( 'admin@mail.com', '$2a$10$ViS.UAa9KZMu4luKceTt8OP9z6Y35SKmBlE.CxCbGEejVWuDfXpuC', 3,'2023-12-01 10:00:00', TRUE, 0, null);


INSERT INTO company ( email, name, phone_number, description, location_id) VALUES
                                                                                  ( 'info@techcorp.com', 'TechCorp', '555-1234', 'Leading tech company.', 1);

INSERT INTO users ( dtype, first_name, last_name, phone_number, profile_photo, location_id, account_id, company_id) VALUES
                                                                                                                           ('Provider', 'John', 'Doe', '123-456-7890', NULL, 1, 3,1),
                                                                                                                           ('Organizer', 'Jane', 'Smith', '987-654-3210', NULL, 2, 2, NULL);

UPDATE account SET user_id = 2 WHERE id = 2;
UPDATE account SET user_id = 1 WHERE id = 3;

INSERT INTO offering_category (name, description, is_deleted, pending) VALUES
                                                                               ('Electronics', 'Category for electronic items.', FALSE, FALSE),
                                                                               ( 'Home Services', 'Category for home-related services.', FALSE, TRUE);

INSERT INTO event_type ( name, description, is_active) VALUES
                                                              ( 'Workshop', 'Hands-on learning sessions.', TRUE),
                                                              ( 'Conference', 'Large gatherings for presentations and discussions.', TRUE);


INSERT INTO event_stats (one_star_count, two_star_count, three_star_count, four_star_count, five_star_count, participants_count, average_rating) VALUES
                                                                                                                                     (2,1,5,2,4,14, 3.3),
                                                                                                                                     (4,2,6,1,1,14, 2.8),
                                                                                                                                     (1,2,1,0,0,4, 2.0),
                                                                                                                                     (0,0,2,3,7,12, 4.4),
                                                                                                                                     (4,2,1,7,1,15, 3.2);

INSERT INTO event (organizer_id, event_type_id, name, description, max_participants, is_open, date, is_deleted, location_id, date_created, stats_id) VALUES
                                                                                                                                                   ( 2, 2, 'Tech Workshop', 'Learn about the latest tech trends.', 50, TRUE, '2026-01-15 12:00', FALSE, 1, '2025-03-10', 1),
                                                                                                                                                   ( 2, 1, 'Business Conference', 'Annual business networking event.', 200, FALSE, '2026-03-10 15:00', FALSE, 2, '2025-03-03',2),
                                                                                                                                                   ( 2, 1, 'Music Festival', 'Enjoy live performances from top artists.', 500, TRUE, '2025-07-20 09:00', FALSE, 3, '2025-05-01',3),
                                                                                                                                                   ( 2, 2, 'Charity Gala', 'Fundraising dinner for a noble cause.', 150, TRUE, '2025-10-15 08:30', FALSE, 2, '2025-08-12',4),
                                                                                                                                                   ( 2, 1, 'Art Exhibition', 'Showcasing modern art pieces.', 100, TRUE, '2025-09-01 18:00', FALSE, 1, '2025-07-15',5);
INSERT INTO agenda_item (name, description, location, start_time, end_time, is_deleted) VALUES
                                                                                                ( 'Opening Session', 'Kick-off of the event.', 'Main Hall', '09:00:00', '10:00:00', FALSE),
                                                                                                ( 'Keynote Speech', 'Special guest speaker.', 'Conference Room A', '10:30:00', '11:30:00', FALSE);

INSERT INTO budget_item (amount, purchase_date, is_deleted, category_id, offering_id) VALUES
                                                                                              ( 500.00, '2023-11-01 14:00:00', FALSE, 1, NULL),
                                                                                              ( 200.00, '2023-11-02 15:00:00', FALSE, 2, NULL);

INSERT INTO comment (content, status, commenter_id, rating) VALUES
                                                                ('Great event!', 1, 1, 5),
                                                                ('Could be better.', 1, 2, 3),
                                                                ('Excellent organization!', 1, 3, 5),
                                                                ('Not satisfied with the service.', 0, 1, 2),
                                                                ('Highly recommend.', 1, 2, 4),
                                                                ('Average experience.', 1, 2, 3),
                                                                ('Loved the venue!', 0, 1, 4),
                                                                ('Staff was very helpful.', 1, 1, 5),
                                                                ('Disappointed with the food.', 1, 1, 2),
                                                                ('Best event I’ve attended!', 1, 2, 5),
                                                                ('The decorations were stunning.', 1, 3, 4),
                                                                ('The sound system was poor.', 1, 4, 2),
                                                                ('Loved the music!', 1, 1, 5),
                                                                ('The event was well organized.', 1, 2, 5),
                                                                ('Seating arrangement could be better.', 1, 3, 3),
                                                                ('Venue was difficult to find.', 0, 2, 2),
                                                                ('The host was amazing.', 1, 1, 5),
                                                                ('Food was excellent.', 1, 4, 5),
                                                                ('Average decoration.', 1, 3, 3),
                                                                ('Not worth the price.', 1, 4, 2),
                                                                ('Would love to attend again.', 1, 2, 5),
                                                                ('The event started late.', 1, 3, 2),
                                                                ('The location was convenient.', 1, 1, 4),
                                                                ('The organizers were friendly.', 1, 4, 5),
                                                                ('There was a delay in service.', 0, 2, 3),
                                                                ('Everything was perfect.', 1, 1, 5),
                                                                ('The food was too spicy.', 1, 3, 2),
                                                                ('The lighting was great.', 1, 2, 4),
                                                                ('The stage design was creative.', 1, 4, 5),
                                                                ('The event exceeded my expectations.', 1, 1, 5),
                                                                ('The parking area was small.', 0, 3, 3),
                                                                ('Good value for money.', 1, 4, 4),
                                                                ('The event was too crowded.', 1, 2, 2),
                                                                ('I had a wonderful time.', 1, 1, 5);
INSERT INTO message (content, timestamp, sender_id, receiver_id, is_read) VALUES
                                                                                  ( 'Hello, when is the event?', '2023-12-05 09:00:00', 1, 2, FALSE),
                                                                                  ( 'Can you provide more details?', '2023-12-06 10:00:00', 2, 1, TRUE);

INSERT INTO notification (read, content, title, date) VALUES
                                                    ( FALSE, 'Your account has been updated.','Account Updated','2025-02-08 09:00:00'),
                                                    ( FALSE, 'The reserved service is in 1 hour.','Service Reservation','2025-02-05 09:00:00'),
                                                    ( TRUE, 'Event registration confirmed.', 'Event Confirmation', '2025-01-04 09:00:00');


INSERT INTO product_details (name, description, price, discount, is_visible, is_available, timestamp) VALUES
                                                                                                              ( 'Wedding Decoration Set', 'Complete set of decorations for weddings, including table centerpieces and backdrops.', 200.00, 10.0, TRUE, FALSE, '2023-10-20 15:00:00'),
                                                                                                              ( 'Conference Projector', 'High-resolution projector for conferences and business meetings.', 1200.00, 10.0, TRUE, TRUE, '2023-10-25 10:30:00'),
                                                                                                              ( 'Sound System', 'Professional sound system for parties and celebrations.', 850.00, 50.0, TRUE, TRUE, '2023-10-28 11:45:00'),
                                                                                                              ( 'Banquet Chairs', 'Comfortable and elegant chairs for formal events.', 25.00, 20.0, TRUE, FALSE, '2023-11-01 09:15:00'),
                                                                                                              ( 'Table Linens', 'High-quality linens for event tables.', 15.00, 10.0, TRUE, TRUE, '2023-11-05 14:00:00'),
                                                                                                              ( 'Stage Lighting Kit', 'Adjustable lighting kit for stage events.', 500.00, 0.0, TRUE, TRUE, '2023-11-08 16:30:00'),
                                                                                                              ( 'Wedding Cake Topper', 'Customizable cake toppers for weddings.', 50.00, 50.0, TRUE, TRUE, '2023-11-12 13:20:00'),
                                                                                                              ( 'Portable Dance Floor', 'Easy-to-assemble dance floor for events.', 700.00, 70.0, TRUE, FALSE, '2023-11-15 17:00:00'),
                                                                                                              ( 'Photo Booth', 'Self-service photo booth with props for celebrations.', 1500.00, 10.0, TRUE, TRUE, '2023-11-18 12:00:00'),
                                                                                                              ( 'Funeral Memorial Kit', 'Set of candles, floral arrangements, and remembrance cards.', 120.00, 10.0, TRUE, TRUE, '2023-11-20 18:45:00');


INSERT INTO service_details (name, description, specification, price, discount, fixed_time, max_duration, min_duration, cancellation_period, reservation_period, is_visible, is_available, auto_confirm, timestamp) VALUES
                                                                                                                                                                                                                            ( 'Wedding Photography', 'Professional photography for weddings.', 'Includes pre-event shoot, event coverage, and photo album.', 1200.00, 10.0, TRUE, 6, 6, 48, 72, FALSE, TRUE, TRUE, '2023-10-30 09:00:00'),
                                                                                                                                                                                                                            ( 'Event Catering', 'Complete catering service for events.', 'Includes setup, serving, and cleanup for up to 200 guests.', 5000.00, 50.0, TRUE, 10, 4, 72, 96, TRUE, TRUE, FALSE, '2023-11-03 11:30:00'),
                                                                                                                                                                                                                            ( 'DJ Service', 'Professional DJ for weddings and parties.', 'Includes sound system and personalized playlist.', 700.00, 50.0, TRUE, 6, 3, 24, 48, TRUE, TRUE, TRUE, '2023-11-06 19:00:00'),
                                                                                                                                                                                                                            ( 'Conference Setup', 'Venue setup for business conferences.', 'Includes table arrangements, projector setup, and refreshments.', 1500.00, 15.0, TRUE, 5, 3, 48, 72, TRUE, TRUE, FALSE, '2023-11-10 08:00:00'),
                                                                                                                                                                                                                            ( 'Event Security', 'Security personnel for large events.', 'Up to 5 guards equipped for crowd management.', 1000.00, 10.0, TRUE, 8, 4, 24, 48, TRUE, TRUE, TRUE, '2023-11-14 20:15:00'),
                                                                                                                                                                                                                            ( 'Floral Arrangement', 'Custom floral arrangements for weddings or funerals.', 'Includes bouquets, table arrangements, and venue decor.', 300.00, 25.0, TRUE, 6, 2, 24, 48, TRUE, TRUE, FALSE, '2023-11-16 10:10:00'),
                                                                                                                                                                                                                            ( 'Master of Ceremonies', 'Experienced MC for formal events.', 'Includes script preparation and event hosting.', 400.00, 40.0, TRUE, 6, 3, 24, 48, FALSE, TRUE, TRUE, '2023-11-19 14:25:00'),
                                                                                                                                                                                                                            ( 'Funeral Planning', 'Comprehensive funeral arrangement services.', 'Includes venue setup, catering, and floral decor.', 2000.00, 20.0, TRUE, 10, 6, 72, 96, TRUE, TRUE, FALSE, '2023-11-22 09:50:00'),
                                                                                                                                                                                                                            ( 'Party Balloon Setup', 'Balloon decorations for birthdays and celebrations.', 'Includes customized balloon arches and centerpieces.', 250.00, 20.0, TRUE, 4, 2, 24, 48, TRUE, TRUE, TRUE, '2023-11-25 13:40:00'),
                                                                                                                                                                                                                            ( 'Event Clean-Up', 'Post-event cleanup service.', 'Includes garbage disposal and venue tidying.', 500.00, 50.0, TRUE, 5, 2, 24, 48, TRUE, TRUE, FALSE, '2023-11-28 18:00:00');


INSERT INTO offerings (dtype, category_id, provider_id, current_product_details_id, is_deleted, pending) VALUES
                                                                                       ('Product',1 ,1, 1, FALSE, FALSE),
                                                                                       ('Product',2 ,1,  2, FALSE, FALSE),
                                                                                       ('Product',1 ,1,  3, FALSE, FALSE),
                                                                                       ('Product',1 ,1,  4, FALSE, FALSE),
                                                                                       ('Product',1 ,1,  5, FALSE, FALSE),
                                                                                       ( 'Product',2 ,1,  6, FALSE, FALSE),
                                                                                       ( 'Product',1 ,1,  7, FALSE, FALSE),
                                                                                       ( 'Product',1 ,1,  8, FALSE, FALSE),
                                                                                       ( 'Product',2 ,1,  9, FALSE, FALSE),
                                                                                       ( 'Product',1 ,1,  10, FALSE, FALSE);
INSERT INTO offerings (dtype, category_id, provider_id, current_service_details_id, is_deleted, pending) VALUES
                                                                                       ( 'Service',1 ,1,  1, FALSE, FALSE),
                                                                                       ( 'Service',2 ,1,  2, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  3, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  4, FALSE, FALSE),
                                                                                       ( 'Service',2 ,1,  5, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  6, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  7, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  8, FALSE, FALSE),
                                                                                       ( 'Service',1 ,1,  9, FALSE, FALSE),
                                                                                       ( 'Service',2 ,1,  10, FALSE, FALSE);

INSERT INTO reservation (start_time, end_time, status, event_id, service_id, timestamp) VALUES
                                                                                     ( '2025-01-15 09:00:00', '2025-01-15 17:00:00', 1, 1, 13,'2025-12-12 08:00:00'),
                                                                                     ( '2025-03-10 08:00:00', '2025-03-10 20:00:00', 0, 2, 11,'2025-12-12 08:00:00');

INSERT INTO offerings_comments (offering_id, comments_id) VALUES
                                                             (1, 1),
                                                             (1, 2),
                                                             (2, 3),
                                                             (2, 4),
                                                             (3, 5),
                                                             (3, 6),
                                                             (4, 7),
                                                             (5, 8),
                                                             (5, 9),
                                                             (6, 10),
                                                             (7, 11),
                                                             (7, 12),
                                                             (8, 13),
                                                             (8, 14),
                                                             (9, 15),
                                                             (9, 16),
                                                             (10, 17),
                                                             (11, 18),
                                                             (12, 19),
                                                             (13, 20),
                                                             (14, 21),
                                                             (14, 22),
                                                             (15, 23),
                                                             (16, 24),
                                                             (16, 25),
                                                             (17, 26),
                                                             (18, 27),
                                                             (19, 28),
                                                             (20, 29);
INSERT INTO account_notifications (account_id, notifications_id) VALUES
                                                                     (2, 1),
                                                                     (2, 2),
                                                                     (2,3);